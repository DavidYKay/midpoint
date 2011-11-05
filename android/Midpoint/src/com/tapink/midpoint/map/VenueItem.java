package com.tapink.midpoint.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.tapink.midpoint.util.GeoHelper;

public class VenueItem extends OverlayItem {

  public static class Factory {

    public static VenueItem VenueItemFromJSONObject(JSONObject json) {
      String name    = "Venue";
      String snippet = "Snippet";
      try {
        name = json.getString("display_name");
        snippet = json.getString("phone_number");
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }

      JSONArray locations;
      double lat = 0.0;
      double lon = 0.0;
      try {
        locations = json.getJSONArray("locations");
        JSONObject location = locations.getJSONObject(0);

        lat = location.getDouble("lat");
        lon = location.getDouble("lon");
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      //VenueItem sampleItem = new VenueItem(
      //    getPoint(40.77765, -73.951704),
      //    "Test Item",
      //    "Snippet"
      //    );
      return new VenueItem(
          GeoHelper.getPoint(lat, lon),
          name,
          snippet,
          new Venue(json)
          );
    }
  }

  private Venue mVenue;
  public VenueItem(GeoPoint point, String title, String snippet, Venue venue) {
    super(point, title, snippet);

    mVenue = venue;
  }
  
  public Venue getVenue() {
    return mVenue;
  }

}
