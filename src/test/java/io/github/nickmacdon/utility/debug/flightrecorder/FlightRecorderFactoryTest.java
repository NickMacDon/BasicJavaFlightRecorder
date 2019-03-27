package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * This is a simple example of using the tool.  Get a FlightRecorder from the factory, start it
 * recording, throw in some events, stop it and dump it to a string.  The only mildly tricky
 * thing is the static import of the FlightRecorderTestEvents enum to save some typing in
 * the calls like:  fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
 */
import static io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderTestEvents.*;
import static org.junit.Assert.*;
import org.junit.Test;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorder;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderEventDumper;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderFactory;

public class FlightRecorderFactoryTest
{
  @Test
  public void test()
  {
    final FlightRecorder fr = FlightRecorderFactory.getFlightRecorder(10).startRecording();
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    final FlightRecorderEventDumper frd = FlightRecorderFactory.getStringDumper("Test Dump");
    fr.stopRecording().dumpEntriesTo(frd);
    final String dumpReport = frd.toString();
    // System.out.println(dumpReport);
    assertTrue(dumpReport.contains("1:"));
    assertTrue(dumpReport.contains("2:"));
    assertTrue(dumpReport.contains("EVENT1"));
    assertTrue(dumpReport.contains("EVENT2"));
  }
}
