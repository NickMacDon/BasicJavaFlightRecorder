package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * This is the actual interface for the events that get recorded.  You probably
 * won't need to implement one of these yourself, you can just use the default
 * implementation in DefaultFlightRecorderEvent
 * 
 * Please use FlightRecorderEvent.INVALID in place of using null to initialize any variable... or
 * as a return type when you might have been considering returning null instead.
 */
public interface FlightRecorderEvent extends FlightRecorderEventID
{
  long getEventTimestamp();
  long getSequenceNumber();
  
  public static final FlightRecorderEvent INVALID = new FlightRecorderEvent()
  {
    @Override
    public String getEventName()
    {
      throw makeException("getEventName()");
    }
    
    @Override
    public long getEventID()
    {
      throw makeException("getEventID()");
    }
    
    @Override
    public long getSequenceNumber()
    {
      throw makeException("getSequenceNumber()");
    }
    
    @Override
    public long getEventTimestamp()
    {
      throw makeException("getEventTimestamp()");
    }

    @Override
    public FlightRecorderEvent getEvent(final long sequenceNumber)
    {
      throw makeException(String.format("getEvent(%d)", sequenceNumber));
    }
    
    private RuntimeException makeException(final String caller)
    {
      return new IllegalStateException("Use of FlightRecorderEvent.INVALID: " + caller);
    }
  };
}
