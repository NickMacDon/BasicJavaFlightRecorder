package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * Your Enum of events should implement FlightRecorderEventID, for an example see
 * FlightRecorderTestEvents in the unit tests.
 * Use this class in your Enum to return a FlightRecorderEvent from the getEvent()
 * method in the interface.
 */

public final class DefaultFlightRecorderEvent implements FlightRecorderEvent
{
  private final long timeStamp;
  private final long sequenceNumber;
  private final FlightRecorderEventID eventID;
  
  public DefaultFlightRecorderEvent(
    final long timeStamp,
    final long sequenceNumber,
    final FlightRecorderEventID eventID
  )
  {
    this.timeStamp      = timeStamp;
    this.sequenceNumber = sequenceNumber;
    this.eventID        = eventID;
  }

  @Override
  public String getEventName()
  {
    return eventID.getEventName();
  }

  @Override
  public long getEventID()
  {
    return eventID.getEventID();
  }

  @Override
  public long getEventTimestamp()
  {
    return timeStamp;
  }

  @Override
  public long getSequenceNumber()
  {
    return sequenceNumber;
  }

  @Override
  public FlightRecorderEvent getEvent(final long sequenceNumber)
  {
    return new DefaultFlightRecorderEvent(System.currentTimeMillis(), sequenceNumber, eventID);
  }
}
