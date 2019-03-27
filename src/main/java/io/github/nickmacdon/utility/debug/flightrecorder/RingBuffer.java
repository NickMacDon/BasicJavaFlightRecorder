package io.github.nickmacdon.utility.debug.flightrecorder;

/*
 * This code is part of https://github.com/NickMacDon/BasicJavaFlightRecorder
 * This code is licensed with the Apache License 2.0  https://www.apache.org/licenses/LICENSE-2.0
 * Written by Nick MacDonald (C) 2019
 *
 * This is a pretty simple little array based ring buffer.  When full, new entries will
 * overwrite the oldest entry.  There's not much tricky here, but maybe the way the generic
 * array has to be built because of Java's type erasure.
 */
import java.lang.reflect.Array;

final class RingBuffer<T>
{
  private final long maxNumberofEnties;
  private final T[] entries;
  private final T UNDEFINED_ENTRY;
  
  private int numberOfEntries     = 0;
  private int startOfRing         = 0;
  private int endOfRing           = 0;
  private int numberOfLostEntries = 0;

  public RingBuffer(final int maxNumberOfEntriesInRingBuffer, final T valueToUseForUndefinedEntries, final Class<T> classOfElements)
  {
    UNDEFINED_ENTRY = valueToUseForUndefinedEntries;
    maxNumberofEnties = maxNumberOfEntriesInRingBuffer;
    // This line didn't work because of requiring the exact type of the default not being the interface, so added classOfElements
    // entries = (T[]) Array.newInstance(UNDEFINED_ENTRY.getClass(), maxNumberOfEntriesInRingBuffer);
    entries = (T[]) Array.newInstance(classOfElements, maxNumberOfEntriesInRingBuffer);
    clearAllEntries();
  }
  
  public void put(final T itemToPut)
  {
    if (numberOfEntries == 0)
    {
      //System.out.println(UNDEFINED_ENTRY.getClass());
      //System.out.println(itemToPut.getClass());
      numberOfEntries = 1;
      entries[startOfRing] = itemToPut;
      return;
    }

    if (numberOfEntries == maxNumberofEnties)
    {
      startOfRing++;
      if (startOfRing >= maxNumberofEnties)  startOfRing = 0;
      numberOfLostEntries++;
    }
    else
    {
      numberOfEntries++;
    }

    endOfRing++;
    if (endOfRing >= maxNumberofEnties)  endOfRing = 0;

    entries[endOfRing] = itemToPut;
  }
  
  public int getNumberOfEntries()
  {
    return numberOfEntries;
  }
  
  public int getNumberOfLostEntries()
  {
    return numberOfLostEntries;
  }
  
  public void clearNumberOfLostEntries()
  {
    numberOfLostEntries = 0;
  }

  public T peekAtEntry(final int entryNumberToPeekAt)
  {
    if ((entryNumberToPeekAt < 0) || (entryNumberToPeekAt >= numberOfEntries))
    {
      throw new IllegalArgumentException(String.format("Attempt to peek at item number %d when there are %d items", entryNumberToPeekAt, numberOfEntries));
    }
    
    int arrayIndex = startOfRing + entryNumberToPeekAt;
    if (arrayIndex >= maxNumberofEnties) arrayIndex -= maxNumberofEnties;

    return entries[arrayIndex];
  }
  
  public T get()
  {
    if (numberOfEntries <= 0)
    {
      throw new IllegalStateException("Attempt to get() when empty");
    }
    numberOfEntries--;

    final T result = entries[startOfRing];

    startOfRing++;
    if (startOfRing >= maxNumberofEnties)  startOfRing = 0;
    
    return result;
  }
  
  public void clearAllEntries()
  {
    for (int i=0; i<maxNumberofEnties; i++)  entries[i] = UNDEFINED_ENTRY;
    numberOfEntries     = 0;
    startOfRing         = 0;
    endOfRing           = 0;
    numberOfLostEntries = 0;
  }
}
