package com.tapink.midpoint.map;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.tapink.midpoint.R;

public class VenueOverlay extends ItemizedOverlay<VenueItem> {

  private ArrayList<VenueItem> mOverlays = new ArrayList<VenueItem>();
  private Context mContext;

  public VenueOverlay(Drawable defaultMarker, Context context) {
    super(boundCenterBottom(defaultMarker));

    mContext = context;
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

    OverlayItem item = mOverlays.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());

    dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        // TODO Auto-generated method stub
        
      }

    });

    dialog.setPositiveButton(R.string.view_venue, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
        
      }

    });

    dialog.show();

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
