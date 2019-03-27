package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * This is an example of code you will actually have to write for yourself.  These are the
 * actual events that will be logged.  They need to be assigned unique IDs or else you
 * won't be able to analyze the log meaningfully later.  Your enum will likely be
 * virtually identical in structure otherwise.  You can use DefaultFlightRecorderEvent to
 * save having to make your own implementation of FlightRecorderEvent.
 */
import io.github.nickmacdon.utility.debug.flightrecorder.DefaultFlightRecorderEvent;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderEvent;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderEventID;

enum FlightRecorderTestEvents implements FlightRecorderEventID
{
  FLIGHT_RECORDER_TEST_EVENT1(1),
  FLIGHT_RECORDER_TEST_EVENT2(2),
  FLIGHT_RECORDER_TEST_EVENT3(3)
  ;
  
  private final long eventID;
  FlightRecorderTestEvents(final long eventID)
  {
    this.eventID = eventID;
  }

  @Override
  public String getEventName()
  {
    return toString();
  }

  @Override
  public long getEventID()
  {
    return eventID;
  }
  
  public FlightRecorderEvent getEvent(final long sequenceNumber)
  {
    final long timeStamp = System.currentTimeMillis();
    return new DefaultFlightRecorderEvent(timeStamp, sequenceNumber, this);
  }
}
