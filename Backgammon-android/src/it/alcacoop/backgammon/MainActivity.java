/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.NativeFunctions;
import it.alcacoop.backgammon.utils.MatchRecorder;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;



public class MainActivity extends AndroidApplication implements NativeFunctions {
  
  private String data_dir;
  TextToSpeech tts;
  protected AdView adView;
  private final int SHOW_ADS = 1;
  private final int HIDE_ADS = 0;

  protected Handler handler = new Handler()
  {
      @SuppressLint("HandlerLeak")
      @Override
      public void handleMessage(Message msg) {
          switch(msg.what) {
              case SHOW_ADS:
              {
                  adView.setVisibility(View.VISIBLE);
                  break;
              }
              case HIDE_ADS:
              {
                  adView.setVisibility(View.GONE);
                  break;
              }
          }
      }
  };
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;
    
    data_dir = getBaseContext().getApplicationInfo().dataDir+"/gnubg/";
    
    copyAssetsIfNotExists();
    GnubgAPI.InitializeEnvironment(data_dir);
    
    // Create the layout
    RelativeLayout layout = new RelativeLayout(this);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    View gameView = initializeForView(new GnuBackgammon(this), cfg);
    adView = new AdView(this, AdSize.BANNER, "XXXXXXXXXXXXXXX");
    adView.loadAd(new AdRequest());
    adView.setVisibility(View.GONE);
    
    layout.addView(gameView);
    RelativeLayout.LayoutParams adParams = 
        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

    layout.addView(adView, adParams);
    setContentView(layout);
  }
  
  
  //Load library
  static {
    System.loadLibrary("glib-2.0");
    System.loadLibrary("gthread-2.0");
    System.loadLibrary("gnubg");
  }   

  private void copyAssetsIfNotExists() {
    File a1 = new File(data_dir+"g11.xml");
    File a2 = new File(data_dir+"gnubg_os0.bd");
    File a3 = new File(data_dir+"gnubg_ts0.bd");
    File a4 = new File(data_dir+"gnubg.weights");
    File a5 = new File(data_dir+"gnubg.wd");
    
    //Asset already presents
    if (a1.exists()&&a2.exists()&&a3.exists()&&a4.exists()&&a5.exists()) return;
    
    File assetDir = new File(data_dir);
    assetDir.mkdirs();
    
    AssetManager assetManager = getAssets();
    String[] files = null;
    try {
      files = assetManager.list("gnubg");
    } catch (IOException e) {
      Log.e("tag", "Failed to get asset file list.", e);
    }
    for(String filename : files) {
      InputStream in = null;
      OutputStream out = null;
      try {
        Log.e("MINE", filename);
        in = assetManager.open("gnubg/"+filename);
        out = new FileOutputStream(data_dir + filename);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
      } catch(IOException e) {
        Log.e("tag", "Failed to copy asset file: " + filename, e);
      }       
    }
  }
  
  private void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
      out.write(buffer, 0, read);
    }
  }

  @Override
  public void showAds(boolean show) {
    handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
  }

  @Override
  public void openURL(String url) {
    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivity(myIntent);
  }

  @Override
  public String getDataDir() {
    return data_dir;
  }

  @Override
  public void shareMatch(MatchRecorder rec) {
    final Intent intent = new Intent(Intent.ACTION_SEND);
    
    intent.setType("text/plain");
    intent.setType("message/rfc822");
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    Date date = new Date();
    String d = dateFormat.format(date);
    intent.putExtra(Intent.EXTRA_SUBJECT, "Backgammon Mobile exported Match (Played on "+d+")");
    intent.putExtra(Intent.EXTRA_TEXT, "You can analize attached file with desktop version of GNU Backgammon\nNOTE: GNU Backgammon is available for Windows, MacOS and Linux\n\nIf you enjoyed Backgammon Mobile please help us rating it on the market");
    
    try {
      dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
      d = dateFormat.format(date);
      File dir = new File(Environment.getExternalStorageDirectory(), "gnubg-sgf");
      dir.mkdir();
      final File data = new File(dir, "match-"+d+".sgf");
      
      FileOutputStream out = new FileOutputStream(data);
      out.write(rec.saveSGF().getBytes());
      out.close();
      
      Uri uri = Uri.fromFile(data);
      intent.putExtra(Intent.EXTRA_STREAM, uri);
      
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          startActivity(Intent.createChooser(intent, "Send email..."));
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setInstance(GnuBackgammon gb) {
    // TODO Auto-generated method stub
    
  }
  
}
