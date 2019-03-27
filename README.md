# Basic Java Flight Recorder
A simple/basic little flight recorder (code path tracing) framework for [remote] end users to generate tracing info for a developer debug investigation

## When would I need this?
The basic idea of this tool is to support an end user (potentially remote/offsite) in gathering data for the developer to debug a reported problem.  It requires the developer to instrument their code with calls to record when important events occur.  The basic version uses a circular buffer that keeps up to the last 1000 events.  This has some memory impact, but operationally it is pretty light on the system.  This way, logging can remain on until the failure [re]occurs and then the data can be dumped and transmitted to the developer to aid the debugging.

__(Note this framework does not include any means to communicate anything... it's not inherently any sort of a spy... nor inherently any sort of instrumentation... but it could be developed into either, I suppose.)__

## What does the developer need to do to use it?
The developer needs to know what events in their software they wish to monitor for.  An Enum should be created for those events, and each event should be given a unique ID (a long.)  The Enum should implement the FlightRecorderEventID interface and part of implementing that interface will require it to be able to turn a FlightRecorderEventID into a FlightRecorderEvent when requested by the framework.

The FlightRecorderEventID interface contains a few methods and a special case constant to represent an undefined event ID:
```
public interface FlightRecorderEventID
{
  String getEventName();
  long getEventID();
  FlightRecorderEvent getEvent(long sequenceNumber);
  
  public static final FlightRecorderEventID INVALID
...
}
```

Here is a sample of the code from the unit tests, with three recordable events:
```
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
```

Having created the necessary Enum, it is then time to set up your application to use it.  Somewhere in the startup of your program you'll need to use the FlightRecorderFactory to get a FlightRecorder instance.  The FlightRecorder interface contains a small complement of methods related to starting, stopping, recording events, and dumping the event recording:
```
public interface FlightRecorder
{
  FlightRecorder recordEvent(FlightRecorderEventID eventToRecord);
  FlightRecorder startRecording();
  FlightRecorder stopRecording();
  FlightRecorder clearAllRecordedEvents();
  int getNumberOfEntriesRecorded();
  FlightRecorder dumpEntriesTo(FlightRecorderEventDumper eventDumper);
}
```
The use of FlightRecorder as a return type allows for method chaining.  Your startup code could make a series of calls such as:
```
final FlightRecorder fr = FlightRecorderFactory.getFlightRecorder().startRecording();
```
It will depend on the design of the application how best to share the FlightRecorder instance amongst its various classes... some sort of globally accessible class might be useful but since you would normally only want one instance of it for your entire application, you can use the factory method getFlightRecorderAgain() to get it anywhere it ends up being needed.

The flight recorder can be started and stopped and restarted by the program as necessary, to prevent collecting too much data or to allow the user to control the enabling of the recording.  If the recording is not started/enabled, then any call to recordEvent() will have minimal impact and will not store anything in recording.

Throughout your program you will need to use your Enum in calls to FlightRecorder's recordEvent() method, such as this:
```
  void sampleMethod1()
  {
    if (someCondition)
    {
      fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
      ... do something application specific ...
    }
    else
    {
      fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
      ... do something application specific ...
    }
  }
```
As you can see from such a simple example, this could let you know which path through your code a specific execution took... in essence whether "someCondition" was true or false.

When the user informs the application it wants to extract the recording, the application will need to use an instance of the interface FlightRecorderEventDumper.  The factory provides one such instance, which is very simplistic, and dumps into a Java String by way of a StringBuilder.

Here is a sample usage from the unit tests:
```
public class FlightRecorderFactoryTest
{
  @Test
  public void test()
  {
    final FlightRecorder fr = FlightRecorderFactory.getFlightRecorder().startRecording();
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT1);
    fr.recordEvent(FLIGHT_RECORDER_TEST_EVENT2);
    final FlightRecorderEventDumper frd = FlightRecorderFactory.getStringDumper("Test Dump");
    fr.stopRecording().dumpEntriesTo(frd);
    final String dumpReport = frd.toString();
    System.out.println(dumpReport);
  }
}
```

and here is sample output from it:
```
Flight Recorder Dump: Test Dump
Dump created: Tue Mar 26 22:06:37 EDT 2019
Initial dump timestamp=1553652397455, final dump timestamp=1553652397456
number of events: 2
   1:    1(FLIGHT_RECORDER_TEST_EVENT1)               1 +0.001
   2:    2(FLIGHT_RECORDER_TEST_EVENT2)               2 +0.001
```
