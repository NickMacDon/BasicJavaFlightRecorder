package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * Your own Enum of events will implement this.  Look at FlightRecorderTestEvents for
 * a simple example.  Basically this allows you to choose your own event names to be
 * meaningful for your app... and you can choose the values for the IDs to suit your
 * needs or desires.  You will probably want to "import static" your Enum to make using
 * the flight recorder less verbose. 
 * 
 * Please use FlightRecorderEventID.INVALID in place of using null to initialize any variable... or
 * as a return type when you might have been considering returning null instead.
 */
public interface FlightRecorderEventID
{
  String getEventName();
  long getEventID();
  FlightRecorderEvent getEvent(long sequenceNumber);
  
  public static final FlightRecorderEventID INVALID = new FlightRecorderEventID()
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
    public FlightRecorderEvent getEvent(final long sequenceNumber)
    {
      throw makeException(String.format("getEvent(%d)", sequenceNumber));
    }
    
    private RuntimeException makeException(final String caller)
    {
      return new IllegalStateException("Use of FlightRecorderEventID.INVALID: " + caller);
    }
  };
}
