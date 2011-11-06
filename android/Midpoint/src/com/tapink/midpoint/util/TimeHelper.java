package com.tapink.midpoint.util;

public class TimeHelper {
  
  private static final long SECOND = 1000;
  private static final long MINUTE = 60 * SECOND;
  private static final long HOUR   = 60 * MINUTE;
  private static final long DAY    = 24 * HOUR;
  
  public static long getTimeNow() {
    return System.currentTimeMillis();
  }
  
  public static long timePlusHours(long time, int hours, int minutes, int seconds) {
    time += seconds * SECOND;
    time += minutes * MINUTE;
    time += hours * HOUR;
    return time;
  }
  
  public static long oneHourFromNow() {
    return getTimeNow() + 1 * HOUR;
  }
}
