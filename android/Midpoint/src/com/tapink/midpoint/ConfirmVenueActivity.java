package com.tapink.midpoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ConfirmVenueActivity extends Activity {
  
  private static final String[] VENUE_DATA = new String[] {
    "$20 for lunch for two!",
    "This is a great venue. Really",
    "Great tex-mex.",
  };
  private ListView mListView;

    
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.confirm_venue);

    final Button actionConfirmButton = (Button) findViewById(R.id.button_confirm);
    actionConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent(ConfirmVenueActivity.this, CalendarListActivity.class);
        startActivity(i);
      }
    });
    
    final Button actionCancelButton = (Button) findViewById(R.id.button_cancel);
    actionCancelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new ArrayAdapter<String>(this, 
                                                  //R.layout.list_item, 
                                                  android.R.layout.simple_list_item_1,
                                                  VENUE_DATA));

  }

}
