package com.tapink.midpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
      initViewsFromVenue(mVenue);
      
      
    }
    Log.v(TAG, "Venue is now: " + venue);

    mListView = (ListView) findViewById(R.id.list);
    mListView.setAdapter(new ArrayAdapter<String>(this, 
                                                  android.R.layout.simple_list_item_1,
                                                  VENUE_DATA));

  }

  private void initViewsFromVenue(Venue venue) {
    mVenueName.setText(venue.getName());
    
    DownloadBitmapTask task = new DownloadBitmapTask();

    String imageUrl = venue.getLargeImageUrl();

    task.execute(imageUrl);

  }

  class DownloadBitmapTask extends AsyncTask<String, Integer, Bitmap> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
      //Log.v(TAG, "doInBackground: " + urls);
      Log.v(TAG, "doInBackground: " + urls[0]);
      return loadBitmap(urls[0]);
    }

    //protected void onProgressUpdate(Integer... progress) {
    //  setProgressPercent(progress[0]);
    //}

    protected void onPostExecute(Bitmap bitmap) {
      Log.v(TAG, "onPostExecute: " + bitmap);
      if (bitmap != null) {
        mImageView.setImageDrawable(
            new BitmapDrawable(bitmap)
            );
      }
    }
    
  }

  private Bitmap loadBitmap(String strUrl) {
    URL url = null;
    Bitmap bitmap = null;
    try {
      url = new URL(strUrl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Must be a properly formed URL. Received: " + strUrl);
    }
    URLConnection connection;
    try {
      connection = url.openConnection();
      connection.setUseCaches(true);
      Object response = connection.getContent();
      if (response instanceof Bitmap) {
        bitmap = (Bitmap)response;        
      } else {
        Log.e(TAG, "Object received: " + response);
        InputStream in = connection.getInputStream();           
        //saveStreamToDisk(in);
        bitmap = BitmapFactory.decodeStream(in);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  
    return bitmap;
  }

  private void saveStreamToDisk(InputStream in) {
    File sdCard = Environment.getExternalStorageDirectory();
    File dir = new File (sdCard.getAbsolutePath() + "/localhack/images");
    dir.mkdirs();
    File file = new File(dir, "downloaded-image.png");

    FileOutputStream f;
    try {
      f = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new IllegalStateException("Couldn't open file: " + file);
    }
    
    try {
      while (in.available() > 0) {
        f.write(in.read());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
