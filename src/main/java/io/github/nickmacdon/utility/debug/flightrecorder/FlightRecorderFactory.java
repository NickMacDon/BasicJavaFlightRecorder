package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * Use the FlightRecorderFactory to make your life easier.  Note you can avoid
 * making a global object or passing the FlightRecorder instance around by using
 * the getFlightRecorderAgain() method.
 */
public final class FlightRecorderFactory
{
  // private FlightRecorderFactory INSTANCE = new FlightRecorderFactory();
  private FlightRecorderFactory( ) {}  // No instantiation / Singleton
  
  private static FlightRecorder lastGivenFlightRecorder = FlightRecorder.INVALID;

  public static final int DEFAULT_NUMBER_OF_EVENTS = 1_000;

  public static FlightRecorder getFlightRecorder()
  {
    lastGivenFlightRecorder = new InMemoryRingBufferFlightRecorder(DEFAULT_NUMBER_OF_EVENTS);
    return lastGivenFlightRecorder;
  }

  public static FlightRecorder getFlightRecorder(final int numberOfEvents)
  {
    lastGivenFlightRecorder = new InMemoryRingBufferFlightRecorder(numberOfEvents);
    return lastGivenFlightRecorder;
  }
  
  public static FlightRecorderEventDumper getStringDumper(final String dumpTitle)
  {
    return new FlightRecorderEventDumperToString(dumpTitle); 
  }
 
  public static FlightRecorder getFlightRecorderAgain()
  {
    if (lastGivenFlightRecorder != FlightRecorder.INVALID)
      return lastGivenFlightRecorder;
    throw new IllegalStateException("No previous call to getFlightRecorder()");
  }
}
