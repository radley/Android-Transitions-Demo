package com.radleymarx.imagegallerydemo.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtil {
  
  
  public static Bitmap getBipmapFromURL(String surl){
    try {
      URL url = new URL(surl);
      HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
      urlcon.setDoInput(true);
      urlcon.connect();
      InputStream in = urlcon.getInputStream();
      Bitmap mIcon = BitmapFactory.decodeStream(in);
      return  mIcon;
    } catch (Exception e) {
      Log.e("Error", e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
  
  
}
