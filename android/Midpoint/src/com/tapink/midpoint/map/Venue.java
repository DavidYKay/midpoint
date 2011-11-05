package com.tapink.midpoint.map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {
  private static final String TAG = "Venue";

  private JSONObject mJson;
  
  ////////////////////////////////////////
  // Constructors
  ////////////////////////////////////////
  
  public Venue(String jsonString) throws JSONException {
    this(new JSONObject(jsonString));
  }
  
  public Venue(JSONObject json) {
    super();
    if (json == null) {
      throw new IllegalArgumentException("JSON cannot be null!");
    }
    this.mJson = json;
  }
  
  ////////////////////////////////////////
  // Convenience methods
  ////////////////////////////////////////

  public String getName() {
    try {
      return mJson.getString("display_name");
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }
  
  public String getLargeImageUrl() {
    JSONArray imageArray;
    try {
      //Log.v(TAG, "images object found: " + testObject);
      imageArray = mJson.getJSONArray("images");
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }

    JSONObject images;
    try {
      images = imageArray.getJSONObject(0);
    } catch (JSONException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return null;
    }

    String urlString = null;
    try {
      urlString = images.getString("src_large");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    if (urlString == null) {
      try {
        urlString = images.getString("src_small");
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    if (urlString == null) {
      try {
        urlString = images.getString("src_thumb");
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return urlString;
  }
  
  ////////////////////////////////////////
  // Accessors
  ////////////////////////////////////////
  
  public JSONObject getJson() {
    return mJson;
  }
  
  ////////////////////////////////////////
  // Object
  ////////////////////////////////////////

  public String toString() {
    return mJson.toString();
  }

  ////////////////////////////////////////
  // Parcelable
  ////////////////////////////////////////

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(mJson.toString());
  }

  public static final Parcelable.Creator<Venue> CREATOR
      = new Parcelable.Creator<Venue>() {
        public Venue createFromParcel(Parcel in) {
          return new Venue(in);
        }

        public Venue[] newArray(int size) {
          return new Venue[size];
        }
      };

  private Venue(Parcel in) {
    String jsonString = in.readString();
    try {
      mJson = new JSONObject(jsonString);
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new IllegalStateException("Failed to load Venue from Parcelled JSON!");
    }
  }

}
