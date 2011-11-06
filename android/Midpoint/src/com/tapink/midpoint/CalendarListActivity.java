package com.tapink.midpoint;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.tapink.midpoint.calendar.Event;
import com.tapink.midpoint.map.Venue;
import com.tapink.midpoint.util.TextHelper;

public class CalendarListActivity extends Activity {

  private static final String[] MEETINGS = new String[] {
    "Lunch with Fred Wilson",
    "Meeting with DFJ Gotham",
    "Holiday Party",
    "Meeting with Alice",
  };
  protected static final int MENU_PICK_CALENDAR = 1;

  protected static final int NEW_CALENDAR_EVENT = 1;
  private static final String TAG = "CalendarListActivity";

  private ListView mListView;
  private ContentResolver mContentResolver;
  private Context mContext = this;

  private Uri mCalendarUri;
  private Uri mEventUri;
  private Uri mCalendarsUri;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_list);

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

    mListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {

        Intent i = new Intent(CalendarListActivity.this, LocationActivity.class);

        Event event = (Event) mListView.getAdapter().getItem(position);

        i.putExtra("event", event);

        startActivity(i);

      }
    });

    mCalendarUri  = getCalendarUri();
    mEventUri     = mCalendarUri.buildUpon().appendPath("events").build();
    mCalendarsUri = mCalendarUri.buildUpon().appendPath("calendars").build();
    Log.v(TAG, "eventsUri: " + mEventUri);
    Log.v(TAG, "calendarsUri: " + mCalendarsUri);


    Intent intent = getIntent();
    Event event = intent.getParcelableExtra("event");
    Venue venue = intent.getParcelableExtra("venue");

    if (event != null && venue != null) {
      Log.v(TAG, "Sweet! Received both an event and a venue.");
      // Sweet!
      // Let's update the calendar event.

      updateEvent(
          event.getDatabaseId(),
          venue.getAddress()
          );
    } else {
      Log.v(TAG, "Aw snap. No event received.");
    }
    Log.v(TAG, "Event: " + event);
    Log.v(TAG, "Venue: " + venue);

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

  ////////////////////////////////////////
  // Options Menu
  ////////////////////////////////////////

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    menu.add(0, MENU_PICK_CALENDAR, 0, R.string.pick_calendar)
    .setShortcut('3', 'i')
    .setIcon(android.R.drawable.ic_menu_add);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_PICK_CALENDAR:

      showCalendarPicker();

      return true;
    }
    return super.onOptionsItemSelected(item);
  }



  ////////////////////////////////////////
  // Calendar Queries
  ////////////////////////////////////////

  private Cursor getSystemCalendars() {
    String[] calendarsProjection = new String[]{ "_id", "name" };

    Cursor cursor = managedQuery(mCalendarsUri,
                                 calendarsProjection,
                                 null,
                                 null,
                                 null
                                );

    return cursor;
  }

  private int updateEvent(long eventId, String eventLocation) {
    ContentValues values = new ContentValues();
    values.put("eventLocation", eventLocation);

    //sUriMatcher.addURI(Calendar.AUTHORITY, "events/#", EVENTS_ID);
    //Uri eventUri = mCalendarUri.buildUpon().appendPath("events").build();

    //String idString = Long.toString(eventId);
    String idString = Integer.toString((int) eventId);
    Uri eventUri = mEventUri.buildUpon().appendPath(idString).build();
    Log.v(TAG, "UPDATING eventUri: " + eventUri);
    int result = mContentResolver.update(
        eventUri,
        values,
        null, null
        //"_id = ?",
        //new String[] { Long.toString(eventId) }
        );
    return result;

  }

  private void readCalendar() {
    final SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
    readCalendar(
        System.currentTimeMillis(),
        preferences.getInt("calendar_id", 1)
        );
  }

  private void readCalendar(long time, int calendarId) {
    String[] eventProjection = new String[]{ "_id", "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" };
    String[] calendarsProjection = new String[]{ "_id", "name" };

    //int calendarId = 1; // Target calendar

    //int attendeeStatus = getAttendeeAccepted();
    int attendeeStatus = 1; // Yes?
    //int attendeeStatus = 2; // No
    //int attendeeStatus = 3; // Maybe

    Cursor cursor = getContentResolver().query(mEventUri,
                                               eventProjection,
      //"dtstart > ? AND calendar_id = ? AND selfAttendeeStatus > ? AND eventLocation IS NULL",  //selection
      "dtstart > ? AND calendar_id = ? AND selfAttendeeStatus = ? AND eventLocation IS NULL",  //selection
      new String[] { Long.toString(time), Integer.toString(calendarId) , Integer.toString(attendeeStatus)},  //2 is no
      "dtstart ASC"   //sort order
      );

    cursor.moveToFirst();

    ArrayList<Event> events = new ArrayList<Event>();
    String[] CalNames = new String[cursor.getCount()];
    int[] CalIds = new int[cursor.getCount()];
    for (int i = 0; i < CalNames.length; i++) {
      Event event = new Event(
        cursor.getLong(0),
        cursor.getString(2),
        cursor.getString(3),
        cursor.getString(6),
        new Date(cursor.getLong(4)),
        new Date(cursor.getLong(5))
      );
      events.add(event);
      cursor.moveToNext();
    }
    cursor.close();

    Event[] eventArray = new Event[events.size()];
    events.toArray(eventArray);
    mListView.setAdapter(new EventArrayAdapter(
        eventArray
    ));
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


  ////////////////////////////////////////
  // Data
  ////////////////////////////////////////

  private static final long SECOND = 1000;
  private static final long MINUTE = 60;
  private static final long HOUR   = 60;
  private static final long DAY    = 24;

  private Intent launchCalendarIntent() {
    long eventStartInMillis = System.currentTimeMillis() + 5 * MINUTE * SECOND;
    //long eventStartInMillis = System.currentTimeMillis() + 1 * HOUR * MINUTE * SECOND;
    long eventEndInMillis = eventStartInMillis + HOUR * MINUTE * SECOND;

    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.setType("vnd.android.cursor.item/event");
    intent.putExtra("title", "Some title");
    intent.putExtra("description", "Some description");
    intent.putExtra("beginTime", eventStartInMillis);
    intent.putExtra("endTime", eventEndInMillis);
    return intent;
  }

  ////////////////////////////////////////
  // Reflection
  ////////////////////////////////////////

  private Uri getCalendarUri() {
    Class<?> calendarProviderClass = null;
    try {
      calendarProviderClass = Class.forName("android.provider.Calendar");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Field uriField = null;
    try {
      uriField = calendarProviderClass.getField("CONTENT_URI");
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    Uri calendarUri = null;
    try {
      calendarUri = (Uri) uriField.get(null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assert calendarUri != null;
    Log.v(TAG, "Calendar URI: " + calendarUri);
    return calendarUri;
  }

  private int getAttendeeAccepted() {
    Class<?> calendarProviderClass = null;
    try {
      //calendarProviderClass = Class.forName("android.provider.Calendar");
      calendarProviderClass = Class.forName("android.provider.Calendar.Attendees");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Field uriField = null;
    try {
      uriField = calendarProviderClass.getField("ATTENDEE_STATUS_ACCEPTED");
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
    int attendeeStatus = -1;
    try {
      attendeeStatus = (Integer) uriField.get(null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    assert attendeeStatus != -1;
    Log.v(TAG, "Attendee status: " + attendeeStatus);
    return attendeeStatus;
  }

  ////////////////////////////////////////
  // ArrayAdapter
  ////////////////////////////////////////

  private class EventArrayAdapter extends BaseAdapter {

    private Event[] mEvents;

    public EventArrayAdapter(Event[] events) {
      mEvents = events;
    }

    @Override
    public int getCount() {
      return mEvents.length;
    }

    @Override
    public Object getItem(int index) {
      return mEvents[index];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      // Kennedy, this is where you supply an XML file to base it on.
      View view = inflater.inflate(R.layout.event_list_item, null);

      final TextView name = (TextView) view.findViewById(R.id.name);
      final TextView date = (TextView) view.findViewById(R.id.date);

      Event event = mEvents[position];

      name.setText(event.getName());
      //date.setText(event.getDescription());
      date.setText(
          TextHelper.unixTimeToNiceTime(event.getStartTime().getTime())
      );

      return view;
    }
  }

  ////////////////////////////////////////
  // View Management
  ////////////////////////////////////////

  private void showCalendarPicker() {
    final Cursor cursor = getSystemCalendars();

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        cursor.moveToFirst();
        cursor.moveToPosition(item);
        String name = cursor.getString(
            cursor.getColumnIndex("name")
            );
        long id = cursor.getLong(
            cursor.getColumnIndex("_id")
            );
        Log.v(TAG,
              String.format("Clicked: %s, %d",
                            name,
                            id));

        // Update our preferences


        final SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("calendar_id", (int) id);
        editor.commit();

        // TODO: refresh
        readCalendar();
        //readCalendar(
        //    System.currentTimeMillis(),
        //    item
        //    );

        dialog.dismiss();
      }
    };

    final SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        
    int calendarId = preferences.getInt("calendar_id", -1);
    int positionId = calendarId - 1;

    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle(R.string.pick_calendar);
    builder.setSingleChoiceItems(
        cursor,
        positionId,
        "name",
        listener
        );

    AlertDialog alert = builder.create();
    alert.show();
  }
}
