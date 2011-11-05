package com.tapink.midpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.maps.MapActivity;

public class PickVenueActivity extends MapActivity {

  private ListView mListView;

  private static final String[] VENUES = new String[] {
    "Pizza Hut",
    "Burger King",
    "Starbucks",
    "McDonalds",
  };
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pick_venue);

    final Button actionConfirmButton = (Button) findViewById(R.id.button);
    actionConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(PickVenueActivity.this, ConfirmVenueActivity.class);
        startActivity(i);
      }
    });

    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new ArrayAdapter<String>(this, 
                                                  //R.layout.list_item, 
                                                  android.R.layout.simple_list_item_1,
                                                  VENUES));
    
  }
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

}
