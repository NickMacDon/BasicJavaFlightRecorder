package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 * 
 * A unit test of the RingBuffer used to implement the default FlightRecorder.  This did
 * help find a bug or two, so it goes to show how useful unit tests can be even when you
 * think the code under test is so easy as to be a cakewalk...
 * (More specifically it indicated that an empty ring buffer is a special case.)
 */
import static org.junit.Assert.*;
import org.junit.Test;
import io.github.nickmacdon.utility.debug.flightrecorder.RingBuffer;

public class RingBufferTest
{
  @Test
  public void testRingBuffer()
  {
    final RingBuffer<Integer> rb = new RingBuffer<>(3, 0, Integer.class);
    assertEquals(0, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.put(1);
    assertEquals(1, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.put(2);
    assertEquals(2, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    assertEquals((Integer)1, rb.peekAtEntry(0));
    assertEquals((Integer)2, rb.peekAtEntry(1));
    assertEquals((Integer)1, rb.get());
    assertEquals(1, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.put(3);
    assertEquals(2, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.put(4);
    assertEquals(3, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.put(5);
    assertEquals(3, rb.getNumberOfEntries());
    assertEquals(1, rb.getNumberOfLostEntries());
    assertEquals((Integer)3, rb.peekAtEntry(0));
    assertEquals((Integer)4, rb.peekAtEntry(1));
    assertEquals((Integer)5, rb.peekAtEntry(2));
    assertEquals((Integer)3, rb.get());
    assertEquals(2, rb.getNumberOfEntries());
    assertEquals(1, rb.getNumberOfLostEntries());
    rb.clearNumberOfLostEntries();
    assertEquals(0, rb.getNumberOfLostEntries());
    rb.clearAllEntries();
    assertEquals(0, rb.getNumberOfEntries());
    assertEquals(0, rb.getNumberOfLostEntries());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPeekAtEntryWhenNone()
  {
    final RingBuffer<Integer> rb = new RingBuffer<>(3, 0, Integer.class);
    rb.peekAtEntry(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPeekAtEntryWhenNone2()
  {
    final RingBuffer<Integer> rb = new RingBuffer<>(3, 0, Integer.class);
    rb.put(1);
    assertEquals((Integer)1, rb.peekAtEntry(0));
    rb.peekAtEntry(1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPeekAtEntryWhenNone3()
  {
    final RingBuffer<Integer> rb = new RingBuffer<>(3, 0, Integer.class);
    rb.peekAtEntry(-1);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWhenEmpty()
  {
    final RingBuffer<Integer> rb = new RingBuffer<>(3, 0, Integer.class);
    rb.get();
  }
}
