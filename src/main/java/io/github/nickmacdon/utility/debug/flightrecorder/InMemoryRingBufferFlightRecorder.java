package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * This FlightRecorder implementation uses a ring buffer to store events.  (Basically thus using
 * a largish array in memory.)  This means the oldest events will get overwritten when the ring
 * buffer gets full.  This allows you to leave the flight recorder in record mode as long as
 * you want while you wait to catch a record of the problem when it eventually occurs.
 */
final class InMemoryRingBufferFlightRecorder implements FlightRecorder
{
  private final RingBuffer<FlightRecorderEvent> ringBuffer;
  private boolean amRecording = false;
  private long sequenceNumber = 0;
  private long startTimeStamp = 0;
  private long endTimeStamp   = 0;
  
  public InMemoryRingBufferFlightRecorder(final int numberOfEventsInRingBuffer)
  {
    ringBuffer = new RingBuffer<>(numberOfEventsInRingBuffer, FlightRecorderEvent.INVALID, FlightRecorderEvent.class);
  }

  @Override
  public FlightRecorder recordEvent(final FlightRecorderEventID eventToRecord)
  {
    sequenceNumber++;  // non recorded events still consume sequence numbers (by design)
    if (amRecording)
    {
      endTimeStamp = System.currentTimeMillis();

      final FlightRecorderEvent eventToStore = eventToRecord.getEvent(sequenceNumber);
      ringBuffer.put(eventToStore);
    }
    return this;
  }

  @Override
  public FlightRecorder startRecording()
  {
    if (startTimeStamp == 0) startTimeStamp = System.currentTimeMillis();
    //TODO ringBuffer.put(SPECIAL_EVENTS_STARTED_RECORDING);
    amRecording = true;
    return this;
  }

  @Override
  public FlightRecorder stopRecording()
  {
    amRecording = false;
    //TODO ringBuffer.put(SPECIAL_EVENTS_STOPPED_RECORDING);
    return this;
  }

  @Override
  public FlightRecorder clearAllRecordedEvents()
  {
    ringBuffer.clearAllEntries();
    return this;
  }

  @Override
  public int getNumberOfEntriesRecorded()
  {
    return ringBuffer.getNumberOfEntries();
  }

  @Override
  public FlightRecorder dumpEntriesTo(final FlightRecorderEventDumper eventDumper)
  {
    eventDumper.setDumpTimestamps(startTimeStamp, endTimeStamp);
    eventDumper.dumpBegins();
    final int numberOfEntries = ringBuffer.getNumberOfEntries();
    eventDumper.setNumberOfEventsToBeDumped(numberOfEntries);
    if (numberOfEntries > 0)
    {
      for (int i=0; i<numberOfEntries; i++)
        eventDumper.dumpEvent(ringBuffer.peekAtEntry(i));
    }
    eventDumper.dumpComplete();
    return this;
  }
}
