package com.tapink.midpoint.map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Venue implements Parcelable {

  private JSONObject mJson;
  
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

  public JSONObject getJson() {
    return mJson;
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
