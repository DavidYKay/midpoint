package com.tapink.midpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.tapink.midpoint.util.DummyDataHelper;

public class PickVenueActivity extends MapActivity {

  private Context mContext = this;
  private ListView mListView;

  private static final String[] VENUES = new String[] {
    "Pizza Hut",
    "Burger King",
    "Starbucks",
    "McDonalds",
  };
  
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
//    mListView.setAdapter(new ArrayAdapter<String>(this, 
//                                                  //R.layout.list_item, 
//                                                  android.R.layout.simple_list_item_1,
//                                                  VENUES));
    populateSampleData();
  }

  ////////////////////////////////////////
  // MapActivity
  ////////////////////////////////////////
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  ////////////////////////////////////////
  // JSON Parsing
  ////////////////////////////////////////

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
