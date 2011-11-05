package com.tapink.midpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.MapActivity;

public class LocationActivity extends MapActivity {

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
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }


}