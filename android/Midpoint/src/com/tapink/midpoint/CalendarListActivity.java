package com.tapink.midpoint;

import java.lang.reflect.Field;
import java.sql.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
  private ContentResolver mContentResolver;
  
  private Uri mCalendarUri;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    mContentResolver = getContentResolver();

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


    mCalendarUri = getCalendarUri();
  }

  @Override
  protected void onResume() {
    super.onResume();
    //mEventLoader.startBackgroundThread();
    //eventsChanged();
    //CalendarView view = (CalendarView) mViewSwitcher.getNextView();
    //view.updateIs24HourFormat();
    //view.updateView();

    //view = (CalendarView) mViewSwitcher.getCurrentView();
    //view.updateIs24HourFormat();
    //view.updateView();

    // Register for Intent broadcasts
    //IntentFilter filter = new IntentFilter();

    //filter.addAction(Intent.ACTION_TIME_CHANGED);
    //filter.addAction(Intent.ACTION_DATE_CHANGED);
    //filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    //registerReceiver(mIntentReceiver, filter);

    mContentResolver.registerContentObserver(
        //Calendar.Events.CONTENT_URI,
        mCalendarUri,
        true,
        mObserver);
    
    readCalendar();
  }
    
  @Override
  protected void onPause() {
    super.onPause();
    mContentResolver.unregisterContentObserver(mObserver);
    //unregisterReceiver(mIntentReceiver);

    //CalendarView view = (CalendarView) mViewSwitcher.getCurrentView();
    //view.cleanup();
    //view = (CalendarView) mViewSwitcher.getNextView();
    //view.cleanup();
    //mEventLoader.stopBackgroundThread();
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

  private void readCalendar() {
    Uri baseUri = mCalendarUri;
    Uri eventUri = baseUri.buildUpon().appendPath("events").build();
    Uri calendarsUri = baseUri.buildUpon().appendPath("calendars").build();
    Log.v(TAG, "eventsUri: " + eventUri);
    Log.v(TAG, "calendarsUri: " + calendarsUri);
    Cursor cursor = getContentResolver().query(eventUri, new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);
    //Cursor cursor = getContentResolver().query(Uri.parse("content://calendar/events"), new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, null, null, null);
    //Cursor cursor = getContentResolver().query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);

    String add = null;
    cursor.moveToFirst();
    String[] CalNames = new String[cursor.getCount()];
    int[] CalIds = new int[cursor.getCount()];
    for (int i = 0; i < CalNames.length; i++) {
      CalIds[i] = cursor.getInt(0);
      CalNames[i] = "Event" + cursor.getInt(0) + ": \nTitle: " + cursor.getString(1) + "\nDescription: " + cursor.getString(2) + "\nStart Date: " + new Date(cursor.getLong(3)) + "\nEnd Date : " + new Date(cursor.getLong(4)) + "\nLocation : " + cursor.getString(5);
      //CalNames[i] = "Event" + cursor.getInt(0) + ": \nTitle: " + cursor.getString(1);
      if(add == null)
        add = CalNames[i];
      else{
        add += CalNames[i];
      }
      Log.v(TAG, "Calendar events: " + add);
      Log.v(TAG, "-----");

      //((TextView)findViewById(R.id.calendars)).setText(add);

      cursor.moveToNext();
    }
    cursor.close();

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


  // Create an observer so that we can update the views whenever a
  // Calendar event changes.
  private ContentObserver mObserver = new ContentObserver(new Handler())
  {
    private static final String TAG = "ContentObserver";
    @Override
    public boolean deliverSelfNotifications() {
      return true;
    }

    @Override
    public void onChange(boolean selfChange) {
      Log.v(TAG, "onChange");
      //eventsChanged();
    }
  };

  private Uri getCalendarUri() {
    Class<?> calendarProviderClass = null;
    try {
      calendarProviderClass = Class.forName("android.provider.Calendar");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Field uriField = null;
    try {
      uriField = calendarProviderClass.getField("CONTENT_URI");
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Uri calendarUri = null;
    try {
      calendarUri = (Uri) uriField.get(null);
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //if (calendarUri == null) {
    //  throw new IllegalStateException("Couldn't find calendar URI!");
    //}
    assert calendarUri != null;
    Log.v(TAG, "Calendar URI: " + calendarUri);
    return calendarUri;
  }
  
}
