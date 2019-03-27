package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * You can implement one of these to dump the output to whatever storage you need.
 * Perhaps, for example, you want one that composes an email for the user to send.
 * Probably use FlightRecorderEventDumperToString as a prototype.
 * 
 * Please use FlightRecorderEventDumper.INVALID in place of using null to initialize any variable... or
 * as a return type when you might have been considering returning null instead.
 */
public interface FlightRecorderEventDumper
{
  void setDumpTimestamps(long startTimeStamp, long endTimeStamp);
  void setNumberOfEventsToBeDumped(int numberOfEvents);
  void dumpBegins();
  void dumpEvent(FlightRecorderEvent eventToDump);
  void dumpComplete();
  
  public static final FlightRecorderEventDumper INVALID = new FlightRecorderEventDumper()
  {
    @Override
    public void setNumberOfEventsToBeDumped(final int numberOfEvents)
    {
      throw makeException(String.format("setNumberOfEventsToBeDumped(%d)", numberOfEvents));
    }
    
    @Override
    public void setDumpTimestamps(final long startTimeStamp, final long endTimeStamp)
    {
      throw makeException(String.format("setDumpTimestamps(%d, %d)", startTimeStamp, endTimeStamp));
    }
    
    @Override
    public void dumpEvent(FlightRecorderEvent eventToDump)
    {
      throw makeException("dumpComplete()");
    }
    
    @Override
    public void dumpComplete()
    {
      throw makeException("dumpComplete()");
    }
    
    @Override
    public void dumpBegins()
    {
      throw makeException("dumpBegins()");
    }
    
    private RuntimeException makeException(final String caller)
    {
      return new IllegalStateException("Use of FlightRecorderEventDumper.INVALID: " + caller);
    }
  };
}
