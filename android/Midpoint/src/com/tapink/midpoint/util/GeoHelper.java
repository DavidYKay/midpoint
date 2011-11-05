package com.tapink.midpoint.util;

import com.google.android.maps.GeoPoint;

public class GeoHelper {

  public static GeoPoint getPoint(double lat, double lon) {
    return(new GeoPoint((int)(lat*1000000.0),
                        (int)(lon*1000000.0)));
  }

}
