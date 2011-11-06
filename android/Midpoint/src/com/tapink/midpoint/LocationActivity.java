package com.tapink.midpoint;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationActivity extends MapActivity {

  private static final String TAG = "LocationActivity";
  private MyLocationOverlay me;
  private MapView mMapView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.location);

    final Button actionConfirmButton = (Button) findViewById(R.id.button);
    actionConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(LocationActivity.this, PickVenueActivity.class);
        startActivity(i);
      }
    });

    mMapView = (MapView) findViewById(R.id.mapview);
    me = new MyLocationOverlay(this, mMapView);
    mMapView.getOverlays().add(me);

  }
  
  @Override
  public void onResume() {
    super.onResume();

    //me.enableCompass();
    me.enableMyLocation();

    Location pos = me.getLastFix();
    if (pos != null) {
      Log.v(TAG, "Pos found: " + pos);
      //mMapView.getController().setCenter(
      //    pos
      //);
    }
    GeoPoint loc = me.getMyLocation();
    if (loc != null) {
      mMapView.getController().setCenter(
          loc
      );
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


}
