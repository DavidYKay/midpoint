package com.tapink.midpoint.util;

import android.text.format.DateUtils;
import android.text.format.Time;

public class TextHelper {
  
  public static boolean isEmptyString(String string) {
    if (string == null || string.length() == 0) {
      return true;
    }
    return false;
  }

  public static String unixTimeToNiceTime(long unixTime) {
    String niceTime;
    if (unixTime == 0) {
      niceTime = "Time Unknown.";
    } else {

      Time time = new Time();
      time.set(unixTime);

      Time now = new Time();
      now.setToNow();

      //long delta = now.toMillis() - time.toMillis();

      niceTime = DateUtils.getRelativeTimeSpanString (
          time.toMillis(false),
          now.toMillis(false),
          DateUtils.HOUR_IN_MILLIS,
          0
          ).toString();
    }
    return niceTime;
  }
  public static String unixTimeToString(long unixTime) {
    //Time time = new Time(unixTime);
    Time time = new Time();

    time.set(unixTime);

    //return time.format2445();
    //return time.toString();
    //return time.format(
    //    //"%a, %b %d, %C"
    //    //"%F"
    //    //"%c"  //Preferred date & time
    //                   "%x"  //Preferred date
    //);

    return time.format3339(true);
  }
}
