package com.tapink.midpoint.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;

public class VenueOverlay extends ItemizedOverlay<VenueItem> {

  private static final String TAG = "VenueOverlay";

  private ArrayList<VenueItem> mOverlays = new ArrayList<VenueItem>();
  private Context mContext;
  private Delegate mDelegate;

  public interface Delegate {
    public void venueOverlayTappedItem(VenueItem item);
    //public void venueOverlayTappedItemAtIndex(int index);
  }

  public VenueOverlay(Drawable defaultMarker, Context context) {
    super(boundCenterBottom(defaultMarker));

    mContext = context;
  }
  
  ////////////////////////////////////////
  // Accessor / Mutator
  ////////////////////////////////////////

  public Delegate getDelegate() {
    return mDelegate;
  }

  public void setDelegate(Delegate mDelegate) {
    this.mDelegate = mDelegate;
  }

  ////////////////////////////////////////
  // ItemizedOverlay Methods
  ////////////////////////////////////////

  @Override
  protected VenueItem createItem(int index) {
    return mOverlays.get(index);
  }

  @Override
  public int size() {
    return mOverlays.size();
  }

  ////////////////////////////////////////
  // Touch Handling
  ////////////////////////////////////////
  @Override
  protected boolean onTap(int index) {
    Log.v(TAG, "onTap(" + index + ")");
    if (mDelegate != null) {
      //mDelegate.venueOverlayTappedItem(index);
      mDelegate.venueOverlayTappedItem(mOverlays.get(index));
    }
    return true;
  }

  ////////////////////////////////////////
  // Public Methods
  ////////////////////////////////////////

  public void addItemList(List<VenueItem> items) {
    for (VenueItem item : items) {
      mOverlays.add(item);
    }
    populate();
  }

  public void addItem(VenueItem overlay) {
    mOverlays.add(overlay);
    populate();
  }

  public void clearItems() {
    mOverlays.clear();
    populate();
  }

}
