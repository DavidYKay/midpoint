package com.tapink.midpoint;

import android.content.Intent;
import android.location.Location;
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
  private EditText mMyLocation;
  private EditText mTheirLocation;

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
        Intent i = new Intent(LocationActivity.this, PickVenueActivity.class);

        // If the user didn't enter anything:
        String userInput = mMyLocation.getText().toString();
        Location location = null;
        if (!TextHelper.isEmptyString(userInput)) {
          i.putExtra("address", userInput);
        } else {
          location = mLastKnownLocation;
          i.putExtra("location", location);
        }

        i.putExtra("event", mEvent);

        startActivity(i);
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
    Attendee attendee = i.getParcelableExtra("attendee");
    Log.v(TAG, "Event: " + mEvent);
    Log.v(TAG, "attendee: " + attendee);

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
  // UI Management
  ////////////////////////////////////////

}
