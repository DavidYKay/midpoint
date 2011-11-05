package com.tapink.midpoint.map;

import org.json.JSONObject;

public class Venue {

  private JSONObject mJson;
  
  public Venue(JSONObject json) {
    super();
    this.mJson = json;
  }

  public JSONObject getJson() {
    return mJson;
  }
}
