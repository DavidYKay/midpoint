package com.tapink.midpoint.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class TheirOverlay extends ItemizedOverlay<OverlayItem> {

  private static final String TAG = "TheirOverlay";

  private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
  private Context mContext;
  private Delegate mDelegate;

  public interface Delegate {
    public void theirOverlayTappedItem(OverlayItem item);
  }

  public TheirOverlay(Drawable defaultMarker, Context context) {
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
  protected OverlayItem createItem(int index) {
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
      mDelegate.theirOverlayTappedItem(mOverlays.get(index));
    }

    CharSequence text = "Their loctaion!";
    Toast toast = Toast.makeText(
        mContext,
        text,
        Toast.LENGTH_SHORT);
    toast.show();

    return true;
  }

  ////////////////////////////////////////
  // Public Methods
  ////////////////////////////////////////

  public void addItemList(List<OverlayItem> items) {
    for (OverlayItem item : items) {
      mOverlays.add(item);
    }
    populate();
  }

  public void addItem(OverlayItem overlay) {
    mOverlays.add(overlay);
    populate();
  }

  public void clearItems() {
    mOverlays.clear();
    populate();
  }

}
