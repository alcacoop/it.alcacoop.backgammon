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

import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.utils.MatchRecorder;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;



@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class MainActivity extends AndroidApplication implements NativeFunctions {
  
  private String data_dir;
  protected AdView adView;
  private final int SHOW_ADS = 1;
  private final int HIDE_ADS = 0;
  private View chatBox;
  private boolean chatVisible = false;

  
  protected Handler handler = new Handler()
  {
    @SuppressLint("HandlerLeak")
    @Override
    public void handleMessage(Message msg) {
      switch(msg.what) {
      case SHOW_ADS:
        adView.setVisibility(View.VISIBLE);
        break;
      case HIDE_ADS:
        adView.setVisibility(View.GONE);
        break;
      }
    }
  };
  
  @SuppressLint("NewApi")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;
    
    data_dir = getBaseContext().getApplicationInfo().dataDir+"/gnubg/";
    
    copyAssetsIfNotExists();
    GnubgAPI.InitializeEnvironment(data_dir);
        
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

    RelativeLayout layout = new RelativeLayout(this);
    View gameView = initializeForView(new GnuBackgammon(this), cfg);
    adView = new AdView(this, AdSize.BANNER, "XXXXXXXXXXXXXXX");
    adView.loadAd(new AdRequest());
    adView.setVisibility(View.GONE);
    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.WRAP_CONTENT, 
      RelativeLayout.LayoutParams.WRAP_CONTENT
    );
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layout.addView(gameView);
    layout.addView(adView, adParams);
    
    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    chatBox = inflater.inflate(R.layout.chat_box, null);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT, 
        RelativeLayout.LayoutParams.WRAP_CONTENT
      );
    chatBox.setVisibility(View.GONE);
    layout.addView(chatBox, params);
    
    setContentView(layout);
    
    /* CHATBOX DIMS */
    View s1 = findViewById(R.id.space1);
    View s2 = findViewById(R.id.space2);
    View s3 = findViewById(R.id.chat_content);
    ViewGroup.LayoutParams pars = s1.getLayoutParams();
    params.width = Math.round(GnuBackgammon.Instance.chatWidth*0.15f)+7;
    s1.setLayoutParams(pars);
    pars = s2.getLayoutParams();
    pars.width = Math.round(GnuBackgammon.Instance.chatWidth*0.15f)+7;
    s2.setLayoutParams(params);
    pars = s3.getLayoutParams();
    pars.width = Math.round(GnuBackgammon.Instance.chatWidth*0.7f)-14;
    s3.setLayoutParams(params);
    GnuBackgammon.Instance.chatHeight = pars.height;
    /* CHATBOX DIMS */
    
    chatBox.requestLayout();
    
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
  public void injectBGInstance() {
  }


  @Override
  public void fibsSignin() {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_signin, null);
        alert.setView(myView).
          setTitle("Login to server...").
          setCancelable(false).
          setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
            }
          }).
          setNeutralButton("Create Account",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              fibsRegistration();
            }
          }).
          setPositiveButton("Login", null);
        
        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            ((EditText)myView.findViewById(R.id.username)).setText(GnuBackgammon.Instance.fibsPrefs.getString("username"));
            ((EditText)myView.findViewById(R.id.password)).setText(GnuBackgammon.Instance.fibsPrefs.getString("password"));
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String username = ((EditText)myView.findViewById(R.id.username)).getText().toString();
                String password = ((EditText)myView.findViewById(R.id.password)).getText().toString();
                if (username.length()>3&&password.length()>3) {
                  GnuBackgammon.Instance.commandDispatcher.sendLogin(username, password);
                  d.dismiss();
                }
              }
            });
          }
        });
        d.show();
      }
    });
  }

  @Override
  public void fibsRegistration() {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();
    
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_register, null);
        alert.setView(myView).
          setCancelable(false).
          setTitle("Create new account...").
          setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
            }
          }).
          setPositiveButton("Create", null);
        
        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String username = ((EditText)myView.findViewById(R.id.username)).getText().toString();
                String password = ((EditText)myView.findViewById(R.id.password)).getText().toString();
                String password2 = ((EditText)myView.findViewById(R.id.password2)).getText().toString();
                if (username.length()>3&&password.length()>3&&password2.length()>3&&password.equals(password2)) {
                  GnuBackgammon.Instance.FibsUsername = username;
                  GnuBackgammon.Instance.FibsPassword = password;
                  GnuBackgammon.Instance.commandDispatcher.createAccount();
                  d.dismiss();
                }
              }
            });
          }
        });
        d.show();
      }
    });
  }

  @Override
  public boolean isNetworkUp() {
    ConnectivityManager connectivityManager = 
      (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = 
      connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }

  @Override
  public void toggleChatBox() {
    if (chatVisible)
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          EditText chat = (EditText) findViewById(R.id.message);
          InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
          chatBox.setVisibility(View.GONE);
        }
      });
      
    else
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          chatBox.setVisibility(View.VISIBLE);
        }
      });
    chatVisible = !chatVisible;
  }
  
  public void clearMessage(View v) {
    EditText chat = (EditText) findViewById(R.id.message);
    chat.setText("");
  }
  
  public void sendMessage(View v) {
    //EditText chat = (EditText) findViewById(R.id.message);
  }
}
