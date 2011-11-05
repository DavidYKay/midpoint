package com.tapink.midpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.tapink.midpoint.map.Venue;
import com.tapink.midpoint.map.VenueItem;
import com.tapink.midpoint.map.VenueOverlay;
import com.tapink.midpoint.util.DummyDataHelper;

public class PickVenueActivity extends MapActivity implements VenueOverlay.Delegate {

  private static final String TAG = "PickVenueActivity";

  private Context mContext = this;
  private ListView mListView;
  private MapView mMapView;
  private VenueOverlay mVenueOverlay;
  private MyLocationOverlay me;
  private JSONVenueAdapter mAdapter;

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
    me = new MyLocationOverlay(this, mMapView);
    mMapView.getOverlays().add(me);

    Drawable pin = this.getResources().getDrawable(R.drawable.marker);
    mVenueOverlay = new VenueOverlay(pin, mContext);
    mVenueOverlay.setDelegate(this);
    mMapView.getOverlays().add(mVenueOverlay);

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
  // VenueOverlay.Delegate
  ////////////////////////////////////////

  @Override
  //public void venueOverlayTappedItem(int index) {
  public void venueOverlayTappedItem(final VenueItem item) {
    Log.v(TAG, String.format("venueOverlayTappedItem(%s)", item.toString()));

    // TODO Auto-generated method stub
    
    //OverlayItem item = mOverlays.get(index);
//    OverlayItem item = mVenueOverlay.createItem(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());

    dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        // No need to do anything. Just dismiss the view.
      }

    });

    dialog.setPositiveButton(R.string.view_venue, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // Navigate to the next screen.

        // Pass in our venue data.
        Intent i = new Intent(PickVenueActivity.this, ConfirmVenueActivity.class);

        //JSONObject json = (JSONObject) mAdapter.getItem(which);
        //JSONObject json = (JSONObject) mAdapter.getItem(which);
        Venue venue = item.getVenue();
        JSONObject json = venue.getJson();
        if (json != null) {
          String jsonString = json.toString();
          i.putExtra("venue", jsonString);
        }

        startActivity(i);
      }

    });

    dialog.show();

  }

  ////////////////////////////////////////
  // Data
  ////////////////////////////////////////


  private void populateMapFromListAdapter() {

    int count = mAdapter.getCount();

    for (int i = 0; i < count; i++) {
      JSONObject json = (JSONObject) mAdapter.getItem(i);
      mVenueOverlay.addItem(
          VenueItem.Factory.VenueItemFromJSONObject(json)
      );
    }
  }

  private void populateSampleData() {
    DummyDataHelper helper = new DummyDataHelper(mContext);
    JSONArray venues = helper.getSampleVenues();
    JSONVenueAdapter adapter = new JSONVenueAdapter(venues);
    mListView.setAdapter(adapter);
    mAdapter = adapter;
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
