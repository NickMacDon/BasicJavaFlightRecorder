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

### My PGP Key
```
-----BEGIN PGP PUBLIC KEY BLOCK-----
Comment: User-ID:	Nick MacDonald (https://github.com/nickmacdon)
Comment: Created:	2019-Mar-26 10:57 PM
Comment: Expires:	2025-Dec-31 12:00 PM
Comment: Type:	4096-bit RSA
Comment: Usage:	Signing, Encryption, Certifying User-IDs
Comment: Fingerprint:	3F49F981717BA7C8B6008D62FFF542A67F7C51FF


mQINBFya5qYBEACY0RMytiTWtRxMvJYLDatWkZl2TvcSJN0b//Uqit/rqS/IvGI1
B/bOEp2DZg7RwLzPMYuQ3nOFaigF1p6mLIKP+WPtj/2npws558Njs3rPr4HxhdUZ
uBIjveQgfxTlW9XZIGTfHBG+eP6weRZBvg+m+DozyX4e99arw92Qx5+5Jo8Mze9T
yddZscFSpC920dxH50F/DGwjHOgXaQBnU7tZoGtYlyS4lkKG35HoEAq4AfP5BjkQ
pTPDrE/xWKih+QvS2v0svziZFyiZTrtvEb9mcG6YpVyCpbZyYLLCtIQiqLeM2XKn
1ooqqpfhTnm0fB/KfyJA5e7nc1irEQIVRfCIfdMBGfvZmTaGCAzn+QQqJKMVhGxv
XYEq5IqQsqvCHl2gIRvHPlasfu+UHWkg3zAPuG7tHuFzAYbs/cndDRFztZ4zgZd5
KsPWU3RSf8mFZ6KIJcGc5D2DdZJMeBaG9JmAdr56DQvM3IMr5NvnDkfWtiGGdekj
1Kjw+NGOXNcyJSIF1dLkc/w8BxXVbs4SOZ71G7S3vyHvFgtUVjxa816ck3xz891f
xXDRZqN9qdjdJIiWp/W3Hsoc3F8osqWXlM9wZOkBn2joAVQ5BZhSvNlAI3xOMA98
AHu1tMiVz1UI6XeS5AsW2CNrc9eUIeyiHdTI2zSq4YCZ+VZS8x3trQiduwARAQAB
tC5OaWNrIE1hY0RvbmFsZCAoaHR0cHM6Ly9naXRodWIuY29tL25pY2ttYWNkb24p
iQJUBBMBCAA+FiEEP0n5gXF7p8i2AI1i//VCpn98Uf8FAlya5qYCGwMFCQy6b+oF
CwkIBwIGFQoJCAsCBBYCAwECHgECF4AACgkQ//VCpn98Uf92bA/7BmMX5zHh0XPk
eTYYSpaOYTumwztuQVmKAYQaiFdyfIDzATfosWNUOYwjviHsDxDOLXDn4ZKh2Hbz
9uARrp0TO7RyLqGb/BidA37PyGBR0El5Uij6yUpJiiAUdDcZH3jw10Sr4P+CsuzB
QQ3Rj7jlqA9ayKpVbF6nTfsj18kLTLFZAZNSoZQJvBvkJz/XilK6l9/wlchptqOC
pZ1yjR7xWgosUYyOkjLnKJ/Xr4BREffzDl+vbAHVsZQvLcRr8PDJmLq5MevGsFdB
LlkzlgUPmhgySHsXbUs1HLEBSlgztnYPJWbIoDA7ltwDMaSZAxTBlgq6IRdmeVtB
CTmCsqZ73Fmiuva7fXhEdJuZg1ac4pufXMJZUikSNBN5PP26CyhN6rjdkWv3W/lQ
AKuPYLLRvLIihkX2Z45Ke/FY3hk8H63moBX58E6UIhpA63bYWGjssK+aRbJtaNZS
qpg26VFwgH/7PMWMyckoMto2Gt4Zb5tdi3h1ZTXdgsV4DxK3QR7wuYBra/M87thP
Xz1W46T3gVZx6AEPy34VvuadtD28ST/akuo3wsJDREkVA4qDm4bJ2kO0orthWy4P
Dk7yYHVQpRuHbiwkQZGjRrMUcDtAPduaEyZ2ItaRF+FFdi98Kr3uaLzxhRqLnIqJ
H4DXDT1dpwZa0z70vYNRuKnB00zgNsS5Ag0EXJrmpgEQAN7MP6efTIAhsmMSNBkm
HAM1gu4kHOothhuf/RTlVp41kg42wgeUuWi68BBlUOQgH5zx227UXtJ+7gVlst+N
04SAi3OWNWGGzf/xr3mjFwGw3CcvcDjGBX7Bz08DVwzcqUbkMFsc61rgVQq16EAk
tAzocb/SBJd1C/qZLDYcxkneTdVaYG2EOCvsFYFqCAQxjHFDu4gJejJrhfY04YH4
IQZYB2pkF9sWeNXCsRfmKhw+yHPlRXNuX7Nvecrp9UgL7C+jJA79CqECRX0aAzpW
SBXvCXrfgPPBkELyhpwQu0eGvKcroVI6x8cmKVIjRllJpDWd8cB5lbspgsmM5M20
jJ5UqACnD9PgUpH34Flby5vvM67LSnpDM/AWZniZT+khQPPdMcMw3iiZDNNKuQtX
S26t+B1sQm71a86wwQrPmB4X3ICBiqxqXVRz6H7egPMoSs8opvZP/1xE0bE4hFk8
evJB72g4/JQKHFxcwwmREVc9mGb2/plKN+sEQkcCR/XiyMlmVR3dACfusXtYKIl9
FqKWkpl8r/zMHciWErtwVyRzRvNtYeVOXLv13mvh4lsTqOEzJCsNc6lqbGe5qD81
KmaQtaJ/TNih2jEVqDaM+8IVGZCO438PrD4SQj51pyuYaXf6yTJEdBn0iXHwpSQO
Tvtigb94ctwgJZRHHDCOiidjABEBAAGJAjwEGAEIACYWIQQ/SfmBcXunyLYAjWL/
9UKmf3xR/wUCXJrmpgIbDAUJDLpv6gAKCRD/9UKmf3xR/1Z0D/9+Hry6lxEVptgL
lurrylCITrCApi7MZVLZc1XQs1LgXvCLW7o4cnP0rKchdwkoSya2kLAIrtY7CVtW
OjAFLo1Ic0Qkz7gOB/fFY/OXGa2mSDTnHLVkWY8HsMchtCVegt/8Eg7fgbFeU8Z2
hjDblTeYfqzhFVeSFTEEXppRTzbLyDlcbkarMmPQw5qjzw+uNzrmMsrTwIAz4lV+
VfTzo3wNk3L2uRrCxHjxHvtaNQjJMrkw2apVbK7mMa7YpAR9x7Udl1rcdqoJI3b3
jSLx8fPUxoxrIxEzzpNRkHKz2wf/thUNzlzCEh06eMBZHLo8RT5OAYMRr7x0GbOh
jFS8k194anIuMZPFj5BEM34OyLAADUnw6LgSZVlVkmrZXyOJaV0waJBIAwczhRqk
FKG6Omn6LfbaJ9tipjMnFtXozx9g5ZOCC844+kdWMJZ2gb31vr/eZ/+yHLmjZieW
GtKZEeJuxAKLN89FTQQ+sVpqmS/RmnfCCex/4BtReoYY29pRgdMNDMw/yr2LeQVm
vM7ATm8ObygL2sJOkOoLviLAiFW5AlLekzmV2KUzlm9dURQgfXpZ5cCa7EyQZX/L
R6yTBuzUfWtUKkJczdexcuUWvaAi5QfC4uYQu8JZUhOMdHTcwmlCRYyaA7K9KfvI
Mzdkoq82KnrKGBnp92lWM/vFmrYFxA==
=pOUZ
-----END PGP PUBLIC KEY BLOCK-----
```
