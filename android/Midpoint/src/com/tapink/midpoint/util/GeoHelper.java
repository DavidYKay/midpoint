package com.tapink.midpoint.util;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class GeoHelper {

  public static final String LOCATION_PROVIDER = "com.tapink.midpoint.FAKE";

  public static GeoPoint getPoint(double lat, double lon) {
    return(new GeoPoint((int)(lat*1000000.0),
                        (int)(lon*1000000.0)));
  }

  public static Location geoPointToLocation(GeoPoint geoPoint) {
    double latitude = geoPoint.getLatitudeE6() / 1000000F;
    double longitude = geoPoint.getLongitudeE6() / 1000000F;
    Location loc = new Location(LOCATION_PROVIDER);
    loc.setLatitude(latitude);
    loc.setLongitude(longitude);

    return loc;
  }

  public static double microDegreesToDegrees(int microDegrees) {
    return microDegrees / 1E6;
  }

  public static String locationToHumanReadable(GeoPoint geo) {
    return locationToHumanReadable(
        geoPointToLocation(geo)
        );
  }

  public static String locationToHumanReadable(Location loc) {
    return String.format("(%s, %s)",
      Location.convert(loc.getLatitude() , Location.FORMAT_DEGREES),
      Location.convert(loc.getLongitude() , Location.FORMAT_DEGREES)
    );
  }

}
