package com.tapink.midpoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MidpointActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private void launchCalendar() {
      long eventStartInMillis = System.currentTimeMillis();
      long eventEndInMillis = eventStartInMillis + 60 * 60 * 1000;

      Intent intent = new Intent(Intent.ACTION_EDIT);  
      intent.setType("vnd.android.cursor.item/event");
      intent.putExtra("title", "Some title");
      intent.putExtra("description", "Some description");
      intent.putExtra("beginTime", eventStartInMillis);
      intent.putExtra("endTime", eventEndInMillis);
      startActivity(intent);
    }

}