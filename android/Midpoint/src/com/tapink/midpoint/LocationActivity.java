package com.tapink.midpoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.tapink.midpoint.calendar.Attendee;
import com.tapink.midpoint.calendar.Event;
import com.tapink.midpoint.util.GeoHelper;
import com.tapink.midpoint.util.TextHelper;

public class LocationActivity extends MapActivity {

  private static final String TAG = "LocationActivity";

  // Model
  private Location mLastKnownLocation;
  //private GeoPoint mLastKnownLocation;

  // Mapping
  private MyLocationOverlay me;
  private MapView mMapView;

  private Event mEvent;
  private Attendee mAttendee;

  private EditText mMyLocation;
  private EditText mTheirLocation;

  private Context mContext = this;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.location);

    mMyLocation = (EditText) findViewById(R.id.my_location);
    mTheirLocation = (EditText) findViewById(R.id.their_location);

    final Button actionConfirmButton = (Button) findViewById(R.id.button);
    actionConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        findMidPointAndFinish();
        //navigateToVenuePicker();
      }
    });

    mMapView = (MapView) findViewById(R.id.mapview);
    mMapView.setBuiltInZoomControls(true);

    me = new MyLocationOverlay(this, mMapView) {
      public void onLocationChanged(android.location.Location location) {
          super.onLocationChanged(location);
          updateLocation(location);
        }
    };
    mMapView.getOverlays().add(me);

    Intent i = getIntent();
    mEvent = i.getParcelableExtra("event");
    Log.v(TAG, "Event: " + mEvent);

    //Attendee attendee = i.getParcelableExtra("attendee");
    mAttendee = i.getParcelableExtra("attendee");
    Log.v(TAG, "attendee: " + mAttendee);
    if (mAttendee != null) {
      // Set address as default
      mTheirLocation.setText(
          mAttendee.getAddress()
      );
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    //me.enableCompass();
    me.enableMyLocation();

    Location pos = me.getLastFix();
    if (pos != null) {
      Log.v(TAG, "Pos found: " + pos);
    }
    GeoPoint loc = me.getMyLocation();
    if (loc != null) {
      mMapView.getController().setCenter(
          loc
      );
      updateLocation(loc);
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    //me.disableCompass();
    me.disableMyLocation();
  }

  ////////////////////////////////////////
  // MapActivity
  ////////////////////////////////////////

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  ////////////////////////////////////////
  // Location Management
  ////////////////////////////////////////

 private void updateLocation(GeoPoint loc) {
   updateLocation(
     GeoHelper.geoPointToLocation(loc)
   );
 }

 private void updateLocation(Location loc) {
   mLastKnownLocation = loc;
    if (mMyLocation.getText() == null) {
      updateLocationText(loc);
    }
 }

  private void updateLocationText(GeoPoint loc) {
    updateLocationText(
        GeoHelper.geoPointToLocation(loc)
        );
  }

  private void updateLocationText(Location loc) {
    mMyLocation.setText(
        GeoHelper.locationToHumanReadable(
            loc
            ));
  }

  ////////////////////////////////////////
  // Foreign APIs
  ////////////////////////////////////////

  private Address geocodeAddress(String address) {
    final Locale locale = Locale.getDefault();

    final Geocoder geocoder = new Geocoder(mContext, locale);

    final int maxResults = 5;
    List<Address> addresses = null;
    try {
      addresses = geocoder.getFromLocationName(address, maxResults);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    //return addresses;
    return addresses.get(0);
  }

  class GeocodeTask extends AsyncTask<String, Integer, Address[]> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Address[] doInBackground(String... locationNames) {
      //Log.v(TAG, "doInBackground: " + addresses);
      Log.v(TAG, "doInBackground: " + locationNames[0]);
      
      int count = locationNames.length;
      Address[] addresses = new Address[count];
      //ArrayList<Address> addresses = new ArrayList<Address>(); 
      //for (String location : locationNames) {
      for (int i = 0; i < count; i++) {
        String location = locationNames[i];
        Address address = geocodeAddress(location);
        addresses[i] = address;
      }
      return addresses;
    }

    //protected void onProgressUpdate(Integer... progress) {
    //  setProgressPercent(progress[0]);
    //}

    protected void onPostExecute(Address[] addresses) {
      Log.v(TAG, "onPostExecute: " + addresses);

      double avgLat = 0.0;
      double avgLon = 0.0;

      for (Address address : addresses) {
        avgLat += address.getLatitude();
        avgLon += address.getLongitude();
      }
      int count = addresses.length;
      avgLat /= count;
      avgLon /= count;

      Location midpoint = new Location(GeoHelper.LOCATION_PROVIDER);
      midpoint.setLongitude(avgLon);
      midpoint.setLatitude(avgLat);

      Location theirLoc = null;
      if (addresses.length > 1) {
        theirLoc = GeoHelper.addressToLocation(addresses[1]);
      }

      navigateToVenuePicker(
          midpoint,
          theirLoc
      );

      //navigateToVenuePicker();
    }
  }
  
  ////////////////////////////////////////
  // Navigation
  ////////////////////////////////////////
  
  private void findMidPointAndFinish() {
    // If the user didn't enter anything:
    String userInput = mMyLocation.getText().toString();

    String userLocation = null;
    Location location   = null;

    if (!TextHelper.isEmptyString(userInput)) {
      userLocation = userInput;
    } else {
      location = mLastKnownLocation;
      userLocation = GeoHelper.locationToHumanReadable(location);
      //public static String locationToHumanReadable(GeoPoint geo) {
      //i.putExtra("location", location);
    }

    String theirLocation = null;
    String theirInput = mTheirLocation.getText().toString();
    if (!TextHelper.isEmptyString(theirInput)) {
      theirLocation = theirInput;
    } else {
      //theirlocation = mLastKnownLocation;
      //userLocation = 
          //public static String locationToHumanReadable(GeoPoint geo) {
          //i.putExtra("location", location);
    }
    
    GeocodeTask task = new GeocodeTask();
    task.execute(userLocation, theirLocation);
  }

  private void navigateToVenuePicker(Location midpoint, Location theirLocation) {
    Intent i = new Intent(LocationActivity.this, PickVenueActivity.class);

    if (theirLocation != null) {
      i.putExtra("their_location", theirLocation);
    }

    i.putExtra("midpoint", midpoint);
    i.putExtra("event", mEvent);

    startActivity(i);
  }

}
