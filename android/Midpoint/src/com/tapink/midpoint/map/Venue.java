package com.tapink.midpoint.map;

import org.json.JSONException;
import org.json.JSONObject;

public class Venue {

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
}
