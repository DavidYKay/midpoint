package com.tapink.midpoint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class CalendarListActivity extends Activity {

  private static final String[] MEETINGS = new String[] {
    "Lunch with Fred Wilson",
    "Meeting with DFJ Gotham",
    "Holiday Party",
    "Meeting with Alice",
  };
  protected static final int NEW_CALENDAR_EVENT = 1;
  private static final String TAG = "CalendarListActivity";
  
  private ListView mListView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    final Button actionConfirmButton = (Button) findViewById(R.id.button);
    actionConfirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = launchCalendarIntent();
        startActivityForResult(i, NEW_CALENDAR_EVENT);
      }
    });

    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new ArrayAdapter<String>(this,
                                                  R.layout.list_item,
                                                  MEETINGS));
    mListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {

        Intent i = new Intent(CalendarListActivity.this, LocationActivity.class);

        // TODO: Pass in the calendar event ID

        startActivity(i);
        
      }
    });
  }

//  private class CalendarAdapter extends ArrayAdapter<String> {
//
//  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.v(TAG,
          String.format(
              "onActivityResult(%d, %d, %s)",
              requestCode,
              resultCode,
              data
              )
         );

    if (requestCode == NEW_CALENDAR_EVENT) {
      Log.v(TAG, "Wahoo! calendar event received.");

    }

  }


  private Intent launchCalendarIntent() {
    long eventStartInMillis = System.currentTimeMillis();
    long eventEndInMillis = eventStartInMillis + 60 * 60 * 1000;

    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra("title", "Some title");
    intent.putExtra("description", "Some description");
    intent.putExtra("beginTime", eventStartInMillis);
    intent.putExtra("endTime", eventEndInMillis);
    return intent;
  }


}
