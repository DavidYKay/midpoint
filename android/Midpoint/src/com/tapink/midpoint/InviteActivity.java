package com.tapink.midpoint;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.util.Rfc822Tokenizer;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;

import com.android.calendar.EmailAddressAdapter;
import com.android.common.Rfc822InputFilter;
import com.android.common.Rfc822Validator;

public class InviteActivity extends Activity {

  static final int START_DATE_DIALOG_ID = 0;
  static final int END_DATE_DIALOG_ID   = 1;
  static final int START_TIME_DIALOG_ID = 2;
  static final int END_TIME_DIALOG_ID   = 3;

  private Button mStartTimeButton;
  private Button mStartDateButton;

  private Button mEndTimeButton;
  private Button mEndDateButton;

  private EditText mTitle;
  private EditText mLocation;
  private EditText mDescription;
  private EditText mGuests;

  private Button mDoneButton;
  private Button mCancelButton;

  private DateWrapper mStartDate = new DateWrapper();
  private DateWrapper mEndDate   = new DateWrapper();

  private TimeWrapper mStartTime = new TimeWrapper();
  private TimeWrapper mEndTime   = new TimeWrapper();
  private EmailAddressAdapter mAddressAdapter;
  private Rfc822Validator mEmailValidator;
  private MultiAutoCompleteTextView mAttendeesList;


  private class DateWrapper {
    public int year;
    public int month;
    public int day;
  }

  private class TimeWrapper {
    public int hour;
    public int minute;
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.invite);


    mAddressAdapter = new EmailAddressAdapter(this);
    
    mDoneButton = (Button) findViewById(R.id.save);
    mDoneButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        saveFieldsAndExit();
      }
    });
    
    mCancelButton = (Button) findViewById(R.id.discard);
    mCancelButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });

    mStartDateButton = (Button) findViewById(R.id.start_date);
    mStartDateButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(START_DATE_DIALOG_ID);
      }
    });
    mEndDateButton = (Button) findViewById(R.id.end_date);
    mEndDateButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(END_DATE_DIALOG_ID);
      }
    });

    mStartTimeButton = (Button) findViewById(R.id.start_time);
    mStartTimeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(START_TIME_DIALOG_ID);
      }
    });
    mEndTimeButton = (Button) findViewById(R.id.end_time);
    mEndTimeButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        showDialog(END_TIME_DIALOG_ID);
      }
    });

    mAttendeesList = initMultiAutoCompleteTextView(R.id.attendees);

    // get the current date
    final Calendar c = Calendar.getInstance();

    mStartDate.year  = c.get(Calendar.YEAR);
    mStartDate.month = c.get(Calendar.MONTH);
    mStartDate.day   = c.get(Calendar.DAY_OF_MONTH);

    mEndDate.year  = c.get(Calendar.YEAR);
    mEndDate.month = c.get(Calendar.MONTH);
    mEndDate.day   = c.get(Calendar.DAY_OF_MONTH);

    mStartTime.hour   = c.get(Calendar.HOUR_OF_DAY);
    mStartTime.minute = c.get(Calendar.MINUTE);

    mEndTime.hour   = c.get(Calendar.HOUR_OF_DAY) + 1;
    mEndTime.minute = c.get(Calendar.MINUTE);

    updateStartDate();
    updateEndDate();
    updateStartTime();
    updateEndTime();
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case START_DATE_DIALOG_ID:
        return new DatePickerDialog(this,
                                    mStartDateSetListener,
                                    mStartDate.year, 
                                    mStartDate.month, 
                                    mStartDate.day
                                   );
      case END_DATE_DIALOG_ID:
        return new DatePickerDialog(this,
                                    mEndDateSetListener,
                                    mEndDate.year, 
                                    mEndDate.month, 
                                    mEndDate.day
                                   );
      case START_TIME_DIALOG_ID:
        return new TimePickerDialog(this,
                                    mStartTimeSetListener, 
                                    mStartTime.hour, 
                                    mStartTime.minute, 
                                    false);
      case END_TIME_DIALOG_ID:
        return new TimePickerDialog(this,
                                    mEndTimeSetListener, 
                                    mEndTime.hour, 
                                    mEndTime.minute, 
                                    false);

    }
    return null;
  }

  // updates the date in the TextView
  private void updateStartDate() {
    mStartDateButton.setText(
        new StringBuilder()
        // Month is 0 based so add 1
        .append(mStartDate.month + 1).append("-")
        .append(mStartDate.day).append("-")
        .append(mStartDate.year).append(" "));
  }

  private void updateEndDate() {
    mEndDateButton.setText(
        new StringBuilder()
        // Month is 0 based so add 1
        .append(mEndDate.month + 1).append("-")
        .append(mEndDate.day).append("-")
        .append(mEndDate.year).append(" "));
  }

  // updates the time in the TextView
  private void updateStartTime() {
    mStartTimeButton.setText(
        new StringBuilder()
        .append(mStartTime.hour).append("-")
        .append(mStartTime.minute).append(" "));
  }

  private void updateEndTime() {
    mEndTimeButton.setText(
        new StringBuilder()
        .append(mEndTime.hour).append("-")
        .append(mEndTime.minute).append(" "));
  }


  private DatePickerDialog.OnDateSetListener mStartDateSetListener =
      new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, 
                              int monthOfYear, int dayOfMonth) {
          mStartDate.year  = year;
          mStartDate.month = monthOfYear;
          mStartDate.day   = dayOfMonth;
          updateStartDate();
        }
      };

  private TimePickerDialog.OnTimeSetListener mStartTimeSetListener =
      new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
          mStartTime.hour = hourOfDay;
          mStartTime.minute = minute;
          updateStartTime();
        }
      };

  private DatePickerDialog.OnDateSetListener mEndDateSetListener =
      new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, 
                              int monthOfYear, int dayOfMonth) {
          mEndDate.year  = year;
          mEndDate.month = monthOfYear;
          mEndDate.day   = dayOfMonth;
          updateEndDate();
        }
      };

  private TimePickerDialog.OnTimeSetListener mEndTimeSetListener =
      new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
          mEndTime.hour = hourOfDay;
          mEndTime.minute = minute;
          updateEndTime();
        }
      };

  // Autocomplete emails

  private MultiAutoCompleteTextView initMultiAutoCompleteTextView(int res) {
    MultiAutoCompleteTextView list = (MultiAutoCompleteTextView) findViewById(res);
    list.setAdapter(mAddressAdapter);
    list.setTokenizer(new Rfc822Tokenizer());
    //list.setValidator(mEmailValidator);

    // NOTE: assumes no other filters are set
    list.setFilters(sRecipientFilters);

    return list;
  }

  private static String extractDomain(String email) {
    int separator = email.lastIndexOf('@');
    if (separator != -1 && ++separator < email.length()) {
      return email.substring(separator);
    }
    return null;
  }

  private static InputFilter[] sRecipientFilters = new InputFilter[] { new Rfc822InputFilter() };

  ////////////////////////////////////////
  // 
  ////////////////////////////////////////
        
  private void saveFieldsAndExit() {
    GregorianCalendar startDate = new GregorianCalendar(
        mStartDate.year,
        mStartDate.month,
        mStartDate.day,
        mStartTime.hour,
        mStartTime.minute
        );
    
    GregorianCalendar endDate = new GregorianCalendar(
        mEndDate.year,
        mEndDate.month,
        mEndDate.day,
        mStartTime.hour,
        mStartTime.minute
        );

    Intent i = new Intent();

    i.putExtra("end_date", endDate.getTimeInMillis());
    i.putExtra("start_date", startDate.getTimeInMillis());

    i.putExtra("description" , mDescription.getText().toString());
    i.putExtra("guests"      , mGuests.getText().toString());
    i.putExtra("location"    , mLocation.getText().toString());
    i.putExtra("title"       , mTitle.getText().toString());

    setResult(
        Activity.RESULT_OK,
        i
    );
    finish();
  }
}
