package com.tapink.midpoint;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tapink.midpoint.map.Venue;

public class ConfirmVenueActivity extends Activity {
  
  private static final String[] VENUE_DATA = new String[] {
    "$20 for lunch for two!",
    "This is a great venue. Really",
    "Great tex-mex.",
  };
  private static final String TAG = "ConfirmVenueActivity";

  private ListView mListView;
  private ImageView mImageView;
  private TextView mVenueName;

  private Venue mVenue;
    
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.confirm_venue);
    
    mImageView = (ImageView) findViewById(R.id.image);
    mVenueName = (TextView) findViewById(R.id.venue_name);

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

    // Grab venue data from intent

    Intent i = getIntent();
    Venue venue = i.getParcelableExtra("venue");
    if (venue != null) {
      mVenue = venue;
      
      mVenueName.setText(mVenue.getName());
      
    }
    Log.v(TAG, "Venue is now: " + venue);

    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new ArrayAdapter<String>(this, 
                                                  android.R.layout.simple_list_item_1,
                                                  VENUE_DATA));

  }

  class DownloadBitmapTask extends AsyncTask<String, Integer, Bitmap> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
      return loadBitmap(urls[0]);
    }

    //protected void onProgressUpdate(Integer... progress) {
    //  setProgressPercent(progress[0]);
    //}

    protected void onPostExecute(Bitmap bitmap) {
      mImageView.setImageDrawable(
          new BitmapDrawable(bitmap)
      );
    }
    
  }

  private Bitmap loadBitmap(String strUrl) {
    URL url = null;
    try {
      url = new URL(strUrl);
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    URLConnection connection;
    try {
      connection = url.openConnection();
      connection.setUseCaches(true);
      Object response = connection.getContent();
      if (response instanceof Bitmap) {
        Bitmap bitmap = (Bitmap)response;
        return bitmap;
      } 
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  
    return null;
  }

}
