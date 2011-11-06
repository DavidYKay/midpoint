package com.tapink.midpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tapink.midpoint.calendar.Event;
import com.tapink.midpoint.map.Venue;

public class ConfirmVenueActivity extends Activity {
  
  private static final String[] VENUE_DATA = new String[] {
    "$20 for lunch for two!",
    "This is a great venue. Really",
    "Great tex-mex.",
  };
  private static final String TAG = "ConfirmVenueActivity";
  
  private Context mContext = this;

  private ListView mListView;
  private ImageView mImageView;
  private TextView mVenueName;

  private Event mEvent;
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

        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.putExtra("event", mEvent);
        i.putExtra("venue", mVenue);

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
    
    mEvent = i.getParcelableExtra("event");
    Log.v(TAG, "Event is now: " + mEvent);

    mVenue = i.getParcelableExtra("venue");
    if (mVenue != null) {
      initViewsFromVenue(mVenue);
    }
    Log.v(TAG, "Venue is now: " + mVenue);

    mListView = (ListView) findViewById(R.id.list);

    JSONObject json = mVenue.getJson();
    JSONArray properties = null;
    try {
      properties = json.getJSONArray("properties");
      mListView.setAdapter(new JSONPropertiesAdapter(properties));
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      mListView.setAdapter(new ArrayAdapter<String>(this, 
          R.layout.confirm_list_item,
          VENUE_DATA));
    }
  

  

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

  ////////////////////////////////////////
  // JSONVenueAdapter
  ////////////////////////////////////////
  
  private class JSONPropertiesAdapter extends BaseAdapter {

    private JSONArray mProperties;

    public JSONPropertiesAdapter(JSONArray properties) {
      this.mProperties = properties;
    }

    @Override
    public int getCount() {
      return mProperties.length();
    }

    @Override
    public Object getItem(int index) {
      try {
        return mProperties.getJSONObject(index);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      // Kennedy, this is where you supply an XML file to base it on.
      View view = inflater.inflate(R.layout.confirm_list_item, null);
      TextView test = (TextView) view;

      JSONObject json = (JSONObject) getItem(position);

      // properties: [
      // Key: value
      // ]
      String key = "";
      String value = "";
      try {
        value = json.getString("value");
        key = json.getString("key");
      } catch (JSONException e) {
        e.printStackTrace();
      }

      String hybrid = String.format(
          "%s : %s",
          key,
          value
          );
      test.setText(
          hybrid
          );

      return test;
    }
  }

}
