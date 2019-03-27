package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * A unit test of the ring buffer based FlightRecorder.
 */
import static io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorderTestEvents.*;
import static org.junit.Assert.*;
import org.junit.Test;
import io.github.nickmacdon.utility.debug.flightrecorder.FlightRecorder;
import io.github.nickmacdon.utility.debug.flightrecorder.InMemoryRingBufferFlightRecorder;

public class InMemoryRingBufferFlightRecorderTest
{
  @Test
  public void testInMemoryRingBufferFlightRecorder()
  {
    final FlightRecorder fr = new InMemoryRingBufferFlightRecorder(3);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1); // Not recording yet
    assertEquals(0, fr.getNumberOfEntriesRecorded());
    fr.startRecording();
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    assertEquals(1, fr.getNumberOfEntriesRecorded());
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT3);
    assertEquals(2, fr.getNumberOfEntriesRecorded());
    fr.clearAllRecordedEvents();
    assertEquals(0, fr.getNumberOfEntriesRecorded());
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT3);
    assertEquals(3, fr.getNumberOfEntriesRecorded());
  }

  @Test
  public void testDumpEntriesTo()
  {
    final FlightRecorder fr = FlightRecorderFactory.getFlightRecorder().startRecording();
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT3);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT3);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    final FlightRecorderEventDumper frd = FlightRecorderFactory.getStringDumper("Test Dump");
    fr.stopRecording().dumpEntriesTo(frd);
    final String dumpReport = frd.toString();
    // System.out.println(dumpReport);
    assertTrue(dumpReport.contains("number of events: 6"));
    assertTrue(dumpReport.contains("1:"));
    assertTrue(dumpReport.contains("2:"));
    assertTrue(dumpReport.contains("3:"));
    assertTrue(dumpReport.contains("4:"));
    assertTrue(dumpReport.contains("5:"));
    assertTrue(dumpReport.contains("6:"));
    assertTrue(dumpReport.contains("EVENT1"));
    assertTrue(dumpReport.contains("EVENT2"));
    assertTrue(dumpReport.contains("EVENT3"));
  }
}
