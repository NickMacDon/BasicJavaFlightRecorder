package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * Get your FlightRecorder instances from the FlightRecorderFactory.  You could, in theory,
 * implement your own version of this interface if you didn't find the InMemoryRingBufferFlightRecorder
 * meeting your needs.
 * 
 * Most methods allow chaining, and since FlightRecorders don't start automatically, you may
 * make calls like this:
 * 
 * final FlightRecorder fr = FlightRecorderFactory.getFlightRecorder().startRecording();
 * 
 * Please use FlightRecorder.INVALID in place of using null to initialize any variable... or
 * as a return type when you might have been considering returning null instead.
 */

public interface FlightRecorder
{
  FlightRecorder recordEvent(FlightRecorderEventID eventToRecord);
  FlightRecorder startRecording();
  FlightRecorder stopRecording();
  FlightRecorder clearAllRecordedEvents();
  int getNumberOfEntriesRecorded();
  FlightRecorder dumpEntriesTo(FlightRecorderEventDumper eventDumper);

  public static final FlightRecorder INVALID = new FlightRecorder()
  {
    @Override
    public FlightRecorder recordEvent(final FlightRecorderEventID eventToRecord)
    {
      throw makeException(String.format("recordEvent(%s)", eventToRecord.getEventName()));
    }
    
    @Override
    public FlightRecorder startRecording()
    {
      throw makeException("startRecording()");
    }

    @Override
    public FlightRecorder stopRecording()
    {
      throw makeException("stopRecording()");
    }
   
    @Override
    public int getNumberOfEntriesRecorded()
    {
      throw makeException("getNumberOfEntriesRecorded()");
    }
    
    @Override
    public FlightRecorder dumpEntriesTo(final FlightRecorderEventDumper eventDumper)
    {
      throw makeException(String.format("dumpEntriesTo(%s)", eventDumper));
    }
    
    @Override
    public FlightRecorder clearAllRecordedEvents()
    {
      throw makeException("clearAllRecordedEvents()");
    }
    
    private RuntimeException makeException(final String caller)
    {
      return new IllegalStateException("Use of FlightRecorder.INVALID: " + caller);
    }
  };
}
