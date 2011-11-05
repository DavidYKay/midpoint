package com.tapink.midpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.tapink.midpoint.map.VenueItem;
import com.tapink.midpoint.map.VenueOverlay;
import com.tapink.midpoint.util.DummyDataHelper;

public class PickVenueActivity extends MapActivity {

  private Context mContext = this;
  private ListView mListView;
  private MapView mMapView;
  private VenueOverlay mVenueOverlay;
  private MyLocationOverlay me;
  
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
    
    mMapView = (MapView) findViewById(R.id.mapview);

    populateSampleData();
    populateMapFromListAdapter();
  }

  ////////////////////////////////////////
  // MapActivity
  ////////////////////////////////////////

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

  ////////////////////////////////////////
  // Data
  ////////////////////////////////////////

  private GeoPoint getPoint(double lat, double lon) {
    return(new GeoPoint((int)(lat*1000000.0),
                        (int)(lon*1000000.0)));
  }

  private void populateMapFromListAdapter() {
    me = new MyLocationOverlay(this, mMapView);
    mMapView.getOverlays().add(me);

    Drawable pin = this.getResources().getDrawable(R.drawable.marker);
    mVenueOverlay = new VenueOverlay(pin, mContext);

    mVenueOverlay.addItem(
      new VenueItem(
                    getPoint(40.77765, -73.951704),
                    "Test Item",
                    "Snippet"
                   )
      );

    mMapView.getOverlays().add(mVenueOverlay);
  }

  private void populateSampleData() {
    DummyDataHelper helper = new DummyDataHelper(mContext);
    JSONArray venues = helper.getSampleVenues();
    JSONVenueAdapter adapter = new JSONVenueAdapter(venues);
    mListView.setAdapter(adapter);
  }
  
  private class JSONVenueAdapter extends BaseAdapter {
    
    private JSONArray mVenues;

    public JSONVenueAdapter(JSONArray venues) {
      this.mVenues = venues;
    }

    @Override
    public int getCount() {      
      return mVenues.length();
    }

    @Override
    public Object getItem(int index) {     
      try {
        return mVenues.getJSONObject(index);
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
      TextView test = new TextView(mContext);

      JSONObject json = (JSONObject) getItem(position);
      String name = "Venue";
      try {
        name = json.getString("display_name");
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
      test.setText(name);

      return test;
    }
    
  }

}