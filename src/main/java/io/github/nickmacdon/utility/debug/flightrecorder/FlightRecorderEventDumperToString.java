package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * This is a simple little dumper that outputs to a Java String.  You use toString()
 * on it to get the final result.  See an example usage in FlightRecorderFactoryTest.
 */

import java.util.Date;

final class FlightRecorderEventDumperToString implements FlightRecorderEventDumper
{
  private final StringBuilder sb = new StringBuilder();

  private long startTimeStamp = 0;
  private int eventNumber     = 0;

  FlightRecorderEventDumperToString(final String dumpTitle)
  {
    sb.append("Flight Recorder Dump: ");
    sb.append(dumpTitle);
    sb.append("\n");
    sb.append("Dump created: ");
    sb.append(new Date());
    sb.append("\n");
  }

  @Override
  public void setDumpTimestamps(final long startTimeStamp, final long endTimeStamp)
  {
    this.startTimeStamp = startTimeStamp;
    sb.append(String.format("Initial dump timestamp=%d, final dump timestamp=%d%n", startTimeStamp, endTimeStamp));
  }

  @Override
  public void setNumberOfEventsToBeDumped(int numberOfEvents)
  {
    sb.append(String.format("number of events: %d%n", numberOfEvents));
  }

  @Override
  public void dumpBegins()
  {
    eventNumber = 0;
  }

  @Override
  public void dumpEvent(final FlightRecorderEvent eventToDump)
  {
    sb.append(String.format("%4d: %4d%-40s %4d +%.3f%n", ++eventNumber, eventToDump.getEventID(), "("+eventToDump.getEventName()+")", eventToDump.getSequenceNumber(), (eventToDump.getEventTimestamp()-startTimeStamp)/1000.0));
  }

  @Override
  public void dumpComplete()
  {
    // Nothing to do, but a file based one would close the file at this point, for example
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }
}
