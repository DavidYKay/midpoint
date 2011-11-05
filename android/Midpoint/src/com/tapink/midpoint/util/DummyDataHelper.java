package com.tapink.midpoint.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.tapink.midpoint.R;

public class DummyDataHelper {
  
  private static final String TAG = "DummyDataHelper";
  private Context mContext;
  
  public DummyDataHelper(Context context) {
    mContext = context;
  }

  public JSONArray getSampleVenues() {
    String text = readTxt(R.raw.sample_venues);
    JSONArray json = null;
    try {
      json = new JSONArray(text);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.v(TAG, "json: " + json);
    return json;
  }
  
  private String readTxt(int resourceId){
    InputStream inputStream = mContext.getResources().openRawResource(resourceId);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    int i;
    try {
      i = inputStream.read();
      while (i != -1) {
        byteArrayOutputStream.write(i);
        i = inputStream.read();
      }
      inputStream.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return byteArrayOutputStream.toString();
  }

}
