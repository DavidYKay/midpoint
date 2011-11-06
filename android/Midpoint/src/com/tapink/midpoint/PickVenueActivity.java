package com.tapink.midpoint;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.tapink.midpoint.calendar.Event;
import com.tapink.midpoint.map.MidpointOverlay;
import com.tapink.midpoint.map.TheirOverlay;
import com.tapink.midpoint.map.Venue;
import com.tapink.midpoint.map.VenueItem;
import com.tapink.midpoint.map.VenueOverlay;
import com.tapink.midpoint.util.DummyDataHelper;
import com.tapink.midpoint.util.GeoHelper;

public class PickVenueActivity extends MapActivity implements VenueOverlay.Delegate {

  private static final String TAG = "PickVenueActivity";

  private Context mContext = this;
  private ListView mListView;

  private MapView mMapView;
  private VenueOverlay mVenueOverlay;
  private MidpointOverlay mMidpointOverlay;
  private TheirOverlay mTheirOverlay;
  private MyLocationOverlay me;

  private VenueAdapter mAdapter;
  private Button mButton;
  
  // Model
  private Event mEvent;
  //private Location mLocation;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pick_venue);

    mButton = (Button) findViewById(R.id.button);
    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleViews();
      }
    });

    mListView = (ListView) findViewById(R.id.list);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
                              long id) {
        Venue venue = (Venue) mAdapter.getItem(position);
        navigateToConfirmWithVenue(venue);
      }
    });

    mMapView = (MapView) findViewById(R.id.mapview);
    mMapView.setBuiltInZoomControls(true);

    me = new MyLocationOverlay(this, mMapView);
    mMapView.getOverlays().add(me);

    Drawable pin = this.getResources().getDrawable(R.drawable.marker);
    mVenueOverlay = new VenueOverlay(pin, mContext);
    mVenueOverlay.setDelegate(this);
    mMapView.getOverlays().add(mVenueOverlay);
    
    Intent i = getIntent();
    mEvent            = i.getParcelableExtra("event");
    Location midpoint = i.getParcelableExtra("midpoint");
    Location theirLocation = i.getParcelableExtra("their_location");
    String address    = i.getStringExtra("address");
    
    Log.v(TAG, "Event: " + mEvent);
    Log.v(TAG, "Midpoint: " + midpoint);
    Log.v(TAG, "Address: " + address);

    if (theirLocation != null) {
      Drawable theirMarker = this.getResources().getDrawable(R.drawable.marker_inverse);
      mTheirOverlay = new TheirOverlay(theirMarker, mContext);
      mMapView.getOverlays().add(mTheirOverlay);
      mTheirOverlay.addItem(
          new OverlayItem(
              GeoHelper.locationToGeoPoint(theirLocation),
              "Their Location",
              "Brett is here."
              )
          );
    }

    if (midpoint != null) {
      Drawable midpointMarker = this.getResources().getDrawable(R.drawable.marker_grey);
      mMidpointOverlay = new MidpointOverlay(midpointMarker, mContext);
      mMapView.getOverlays().add(mMidpointOverlay);

      mMidpointOverlay.addItem(
          new OverlayItem(
              GeoHelper.locationToGeoPoint(midpoint),
              "Midpoint",
              "Halfway point"
              )
          );

      // Make a query using the location
    } else if (address != null) {
      // Make a query using the address

    } else {
      throw new IllegalStateException("Need to have either a location or an address!");
    }

    populateSampleData();
    populateMapFromListAdapter();
  }

  @Override
  public void onResume() {
    super.onResume();

    //me.enableCompass();
    me.enableMyLocation();

    Location pos = me.getLastFix();
    if (pos != null) {
      Log.v(TAG, "Pos found: " + pos);
      //mMapView.getController().setCenter(
      //    pos
      //);
    }
    GeoPoint loc = me.getMyLocation();
    if (loc != null) {
      mMapView.getController().setCenter(
          loc
      );
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    //me.disableCompass();
    me.disableMyLocation();
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
  public void venueOverlayTappedItem(final VenueItem item) {
    Log.v(TAG, String.format("venueOverlayTappedItem(%s)", item.toString()));

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
        // Pass in our venue data.
        Venue venue = item.getVenue();
        navigateToConfirmWithVenue(venue);
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
      //JSONObject json = (JSONObject) mAdapter.getItem(i);
      JSONObject json = ((Venue) mAdapter.getItem(i)).getJson();
      mVenueOverlay.addItem(
          VenueItem.Factory.VenueItemFromJSONObject(json)
      );
    }
  }

  private void populateSampleData() {
    DummyDataHelper helper = new DummyDataHelper(mContext);
    JSONArray venues = helper.getSampleVenues();

    //ArrayList<String> list = new ArrayList<String>();     
    //ArrayList<JSONObject> list = new ArrayList<JSONObject>();     
    ArrayList<Venue> list = new ArrayList<Venue>();     
    if (venues != null) { 
      for (int i=0;i<venues.length();i++){ 
        //list.add(venues.get(i).toString());
        try {
          //list.add((JSONObject) venues.get(i));
          Venue venue = new Venue((JSONObject) venues.get(i));
          list.add(venue);
        } catch (JSONException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      } 
    } 

    //VenueAdapter adapter = new VenueAdapter(venues);
    Venue[] venueArray = new Venue[list.size()];
    list.toArray(venueArray);
    VenueAdapter adapter = new VenueAdapter(venueArray);
    mListView.setAdapter(adapter);
    mAdapter = adapter;
  }
  
  ////////////////////////////////////////
  // JSONVenueAdapter
  ////////////////////////////////////////

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
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      // Kennedy, this is where you supply an XML file to base it on.
      View view = inflater.inflate(R.layout.venue_list_item, null);
      TextView test = (TextView) view;
      
      JSONObject json = (JSONObject) getItem(position);
      String name = "Venue";
      try {
        name = json.getString("display_name");
      } catch (JSONException e) {
        e.printStackTrace();
      }

      test.setText(name);

      return test;
    }
  }
  
  private class VenueAdapter extends BaseAdapter {

    private Venue[] mVenues;

    public VenueAdapter(Venue[] venues) {
      this.mVenues = venues;
    }

    @Override
    public int getCount() {
      return mVenues.length;
    }

    @Override
    public Object getItem(int index) {
      return mVenues[index];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      // Kennedy, this is where you supply an XML file to base it on.
      View view = inflater.inflate(R.layout.venue_list_item, null);
      TextView test = (TextView) view;
      
      Venue venue = mVenues[position];
      String name = venue.getName();
      test.setText(name);
      return test;
    }
  }
  
  ////////////////////////////////////////
  // View Management
  ////////////////////////////////////////

  private void toggleViews() {
    if (mMapView.getVisibility() == View.VISIBLE) {
      mMapView.setVisibility(View.GONE);
      mListView.setVisibility(View.VISIBLE);
      mButton.setText(R.string.map);
    } else {
      mMapView.setVisibility(View.VISIBLE);
      mListView.setVisibility(View.GONE);
      mButton.setText(R.string.list);
    }
  }

//  // Minimum & maximum latitude so we can span it
//  // The latitude is clamped between -80 degrees and +80 degrees inclusive
//  // thus we ensure that we go beyond that number
//  private final int minLatitude = (int)(+81 * 1E6);
//  private final int maxLatitude = (int)(-81 * 1E6);
//  // Minimum & maximum longitude so we can span it
//  // The longitude is clamped between -180 degrees and +180 degrees inclusive
//  // thus we ensure that we go beyond that number
//  private final int minLongitude = (int)(+181 * 1E6);
//  private final int maxLongitude = (int)(-181 * 1E6);
  
  private void setupMap() {
    MapController controller = mMapView.getController();
    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
    
    GeoPoint current = me.getMyLocation();
    if (current != null) {
      points.add(current);
    }

    for (int i = 0; i < mVenueOverlay.size(); i++) {
      GeoPoint point = mVenueOverlay.getItem(i).getPoint();
      if (point != null) {
        points.add(point);
      }
    }
    
    for (int i = 0; i < mTheirOverlay.size(); i++) {
      GeoPoint point = mTheirOverlay.getItem(i).getPoint();
      if (point != null) {
        points.add(point);
      }
    }

    setupMap(
        points,
        controller
        );
  }

  private void setupMap(Iterable<GeoPoint> points, MapController mapController) {
    // Minimum & maximum latitude so we can span it
    // The latitude is clamped between -80 degrees and +80 degrees inclusive
    // thus we ensure that we go beyond that number
    int minLatitude = (int)(+81 * 1E6);
    int maxLatitude = (int)(-81 * 1E6);
    // Minimum & maximum longitude so we can span it
    // The longitude is clamped between -180 degrees and +180 degrees inclusive
    // thus we ensure that we go beyond that number
    int minLongitude = (int)(+181 * 1E6);
    int maxLongitude = (int)(-181 * 1E6);

    // Holds all the picture location as Point
    //List<Point> mPoints = new ArrayList<Point>();
    for (GeoPoint point : points) {

      //You get the latitude/longitude as you want, here I take it from the db
      int latitude   = point.getLatitudeE6();
      int longitude  = point.getLongitudeE6();

      // Sometimes the longitude or latitude gathering
      // did not work so skipping the point
      // doubt anybody would be at 0 0
      if (latitude != 0 && longitude !=0)  {

        // Sets the minimum and maximum latitude so we can span and zoom
        minLatitude = (minLatitude > latitude) ? latitude : minLatitude;
        maxLatitude = (maxLatitude < latitude) ? latitude : maxLatitude;               
        // Sets the minimum and maximum latitude so we can span and zoom
        minLongitude = (minLongitude > longitude) ? longitude : minLongitude;
        maxLongitude = (maxLongitude < longitude) ? longitude : maxLongitude;

        //mPoints.add(new Point(latitude, longitude));
      }
    }

    // Zoom to span from the list of points
    mapController.zoomToSpan(
        (maxLatitude - minLatitude),
        (maxLongitude - minLongitude));
    // Animate to the center cluster of points
    mapController.animateTo(new GeoPoint(
        (maxLatitude + minLatitude)/2,
        (maxLongitude + minLongitude)/2 ));

    // Add all the point to the overlay
    //mMapOverlay = new MyPhotoMapOverlay(mPoints);
    //mMapOverlayController = mMapView.createOverlayController();
    //// Add the overlay to the mapview
    //mMapOverlayController.add(mMapOverlay, true);
  }
  
  ////////////////////////////////////////
  // Navigation
  ////////////////////////////////////////
  
  private void navigateToConfirmWithVenue(Venue venue) {
    Intent i = new Intent(PickVenueActivity.this, ConfirmVenueActivity.class);
    
    JSONObject json = venue.getJson();
    if (json == null) {
      // Shit hit the fan!
      Log.e(TAG, "Illegal venue!");
      throw new IllegalStateException("Venue didn't have any underlying JSON object!");
    } else {
      i.putExtra("event", mEvent);
      i.putExtra("venue", venue);

      startActivity(i);
    }
  }
}
