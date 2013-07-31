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
import it.alcacoop.backgammon.fsm.MenuFSM.States;
import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.layers.FibsScreen;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.layers.SplashScreen;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.util.GServiceGameHelper;
import it.alcacoop.backgammon.utils.AchievementsManager;
import it.alcacoop.backgammon.utils.AppDataManager;
import it.alcacoop.backgammon.utils.ELORatingManager;
import it.alcacoop.backgammon.utils.MatchRecorder;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateLoadedListener;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.achievement.OnAchievementUpdatedListener;
import com.google.android.gms.games.leaderboard.OnScoreSubmittedListener;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeReliableMessageSentListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;



@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class MainActivity extends AndroidApplication 
implements NativeFunctions, OnEditorActionListener, SensorEventListener, AdListener, 
GServiceGameHelper.GameHelperListener, RealTimeMessageReceivedListener,
RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener, RealTimeReliableMessageSentListener,
OnStateLoadedListener
{
  private String data_dir;
  protected AdView adView;
  private final int SHOW_ADS = 1;
  private final int HIDE_ADS = 0;
  private View chatBox;
  private View gameView;
  private GServiceGameHelper gHelper;

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


  private boolean mInitialized;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private int rotation;

  private InterstitialAd interstitial;

  private Timer adsTimer;
  private TimerTask adsTask;
  private Timer tping;
  private TimerTask pingtask;
  
  private String mRoomId = null;
  private String mMyId = null;
  ArrayList<Participant> mParticipants = null;
  private Preferences prefs;
  private boolean meSentInvitation;


  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;

    data_dir = getBaseContext().getApplicationInfo().dataDir+"/gnubg/";

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    RelativeLayout layout = new RelativeLayout(this);
    gameView = initializeForView(new GnuBackgammon(this), cfg);


    /** SENSOR INITIALIZATION **/
    mInitialized = false;
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    /** SENSOR INITIALIZATION **/


    /** ADS INITIALIZATION **/
    PrivateDataManager.initData();
    if (isTablet(this))
      adView = new AdView(this, AdSize.IAB_BANNER, PrivateDataManager.ads_id);
    else
    adView = new AdView(this, AdSize.BANNER, PrivateDataManager.ads_id);
    adView.setVisibility(View.GONE);
    
    if (!isProVersion())
      adView.loadAd(new AdRequest());
    //Create the interstitial
    interstitial = new InterstitialAd(this, PrivateDataManager.int_id);
    interstitial.setAdListener(this);
    /** ADS INITIALIZATION **/


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

    /** CHATBOX DIMS **/
    Display display = getWindowManager().getDefaultDisplay();
    rotation = display.getRotation();
    Point size = new Point();
    try {
      display.getSize(size);
    } catch (java.lang.NoSuchMethodError ignore) { // Older device
      size.x = display.getWidth();
      size.y = display.getHeight();
    }
    int width = size.x;
    View s1 = findViewById(R.id.space1);
    View s2 = findViewById(R.id.space2);
    View s3 = findViewById(R.id.chat_content);
    ViewGroup.LayoutParams pars = s1.getLayoutParams();
    pars.width = Math.round(width*0.15f)+7;
    s1.setLayoutParams(pars);
    pars = s2.getLayoutParams();
    pars.width = Math.round(width*0.15f)+7;
    s2.setLayoutParams(pars);
    pars = s3.getLayoutParams();
    GnuBackgammon.chatHeight = pars.height;
    pars.width = Math.round(width*0.7f)-14;
    s3.setLayoutParams(pars);
    EditText target = (EditText) findViewById(R.id.message);
    target.setOnEditorActionListener(this);
    /** CHATBOX DIMS **/


    /** GOOGLE API  INITIALIZATION **/
    PrivateDataManager.createBillingData(this);
    prefs = Gdx.app.getPreferences("GameOptions");
    gHelper = new GServiceGameHelper(this, prefs.getBoolean("ALREADY_SIGNEDIN", false));
    gHelper.setup(this, GServiceGameHelper.CLIENT_APPSTATE|GServiceGameHelper.CLIENT_GAMES);

    ActivityManager actvityManager = (ActivityManager) this.getSystemService( ACTIVITY_SERVICE );
    List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(3);
    for(RunningTaskInfo runningTaskInfo:taskInfos){
      if (runningTaskInfo.baseActivity.getPackageName().contains("gms")) {
        gserviceSignIn();
        break;
      }
    }
    /** GOOGLE API  INITIALIZATION **/
  }


  public boolean isTablet(Context context) {
    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    return (xlarge || large);
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
    }
    for(String filename : files) {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = assetManager.open("gnubg/"+filename);
        out = new FileOutputStream(data_dir + filename);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
      } catch(IOException e) {
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
    if (isProVersion()) return;
    handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
  }

  @Override
  public void openURL(String url) {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivityForResult(myIntent, 1000);
  }
  
  @Override
  public void openURL(String url, String fallback) {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    try  {
    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    startActivityForResult(myIntent, 1000);
    } catch (Exception e) {
      Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fallback));
      startActivityForResult(myIntent, 1000);
    }
  }

  @Override
  public String getDataDir() {
    return data_dir;
  }

  @Override
  public void shareMatch(MatchRecorder rec) {
    final Intent intent = new Intent(Intent.ACTION_SEND);

    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();

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
          startActivityForResult(Intent.createChooser(intent, "Send email..."), 1001);
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
        });

        if (!GnuBackgammon.Instance.server.equals("fibs.com"))
          alert.setNeutralButton("Create Account",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
              fibsRegistration();
            }
          });

        alert.setPositiveButton("Login", null);

        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            String usr = "";
            String pwd = "";
            if (GnuBackgammon.Instance.server.equals("fibs.com")) {
              usr = GnuBackgammon.Instance.fibsPrefs.getString("fusername");
              pwd = GnuBackgammon.Instance.fibsPrefs.getString("fpassword");
            } else {
              usr = GnuBackgammon.Instance.fibsPrefs.getString("tusername");
              pwd = GnuBackgammon.Instance.fibsPrefs.getString("tpassword");
            }
            ((EditText)myView.findViewById(R.id.username)).setText(usr);
            ((EditText)myView.findViewById(R.id.password)).setText(pwd);
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String username = ((EditText)myView.findViewById(R.id.username)).getText().toString();
                String password = ((EditText)myView.findViewById(R.id.password)).getText().toString();
                if (username.length()>3&&password.length()>3) {
                  GnuBackgammon.Instance.commandDispatcher.sendLogin(username, password);
                  d.dismiss();
                } else {
                  Context context = getApplicationContext();
                  CharSequence text = "";
                  if (username.length()<=3) text = "Username must be at least 4-chars length";
                  else if (password.length()<=3) text = "Password must be at least 4-chars length";
                  else text = "Generic error, please retype username and password";

                  int duration = Toast.LENGTH_SHORT;
                  Toast toast = Toast.makeText(context, text, duration);
                  toast.setGravity(Gravity.TOP, 0, 0);
                  toast.show();
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
    final AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
    final TextView myMsg = new TextView(this);

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
                } else {
                  Context context = getApplicationContext();
                  CharSequence text = "";
                  if (username.length()<=3) text = "Username must be at least 4-chars length";
                  else if (password.length()<=3) text = "Password must be at least 4-chars length";
                  else if (!password.equals(password2)) text = "Provided passwords don't match";
                  else text = "Generic error, please retype username and password";

                  int duration = Toast.LENGTH_SHORT;
                  Toast toast = Toast.makeText(context, text, duration);
                  toast.setGravity(Gravity.TOP, 0, 0);
                  toast.show();
                }
              }
            });
          }
        });


        myMsg.setText("\nYou are creating new account...\n\n" +
            "Available chars for username are: A-Z,a-z,_\n" +
            "Available chars for password are: A-Z,a-z,0-9,_\n\n" +
            "Note: username and password must be\n minimum 4-chars length\n");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        popupBuilder.setCancelable(false)
        .setView(myMsg)
        .setTitle("Info")
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            d.show();
          }
        });
        popupBuilder.show();

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
  public void showChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        chatBox.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override
  public void hideChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        EditText chat = (EditText) findViewById(R.id.message);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
        chatBox.setVisibility(View.GONE);
      }
    });
  }


  public void clearMessage(View v) {
    EditText chat = (EditText) findViewById(R.id.message);
    chat.setText("");
  }

  public void sendMessage(View v) {
    EditText chat = (EditText) findViewById(R.id.message);
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
    Editable msg = chat.getText();
    if (msg.toString().length()>0) {
      chat.setText("");
      GnuBackgammon.Instance.appendChatMessage(msg.toString(), true);
    }
    adjustFocus();
  }

  @Override
  public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
    sendMessage(null);
    return false;
  }

  public void adjustFocus() {
    gameView.setFocusableInTouchMode(true);
    gameView.requestFocus();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if ((GnuBackgammon.Instance==null)||
        (GnuBackgammon.Instance.currentScreen==null)||
        (GnuBackgammon.Instance.getScreen()==null)||
        (GnuBackgammon.Instance.getScreen() instanceof SplashScreen))
      return super.onKeyDown(keyCode, event);
    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
      adjustFocus();
      GnuBackgammon.Instance.gameScreen.chatBox.hide();
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    if (!isProVersion()) {
      adsTimer = new Timer();
      adsTask = new TimerTask() {
        @Override
        public void run() {
          runOnUiThread(new Runnable() {
            public void run() {
              if ((!isProVersion())&&(!interstitial.isReady())) {
                interstitial.loadAd(new AdRequest());
              }
            }
          });
        }
      };
      adsTimer.schedule(adsTask, 0,15000);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSensorManager.unregisterListener(this);
    if (adsTimer!=null) {
      adsTimer.cancel();
      adsTimer.purge();
    }

    if ((GnuBackgammon.Instance!=null)&&(GnuBackgammon.Instance.currentScreen instanceof FibsScreen)&&(!GnuBackgammon.Instance.interstitialVisible)) {
      GnuBackgammon.Instance.commandDispatcher.send("BYE");
      GnuBackgammon.Instance.fibsScreen.fibsInvitations.clear();
      GnuBackgammon.Instance.fibsScreen.fibsPlayers.clear();
      GnuBackgammon.Instance.setFSM("MENU_FSM");
    }
  }



  @Override
  public void onAccuracyChanged(Sensor arg0, int arg1) {
  }


  private final float NOISE = 0.5f;
  @Override
  public void onSensorChanged(SensorEvent event) {
    float x = event.values[1];
    if (rotation==3) x=-x;
    if (!mInitialized) {
      mInitialized = true;
    } else { 
      if (Math.abs(x) < NOISE) return;
      if (GnuBackgammon.Instance!=null)
        if (GnuBackgammon.Instance.currentScreen!=null)
          GnuBackgammon.Instance.currentScreen.moveBG(x);
    }
  }

  @Override
  public void showInterstitial() {
    if (isProVersion()) return;
    if (interstitial.isReady()) {
      GnuBackgammon.Instance.currentScreen.pause();
      GnuBackgammon.Instance.interstitialVisible = true;
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          synchronized (this) {
            try {
              wait(700);
            } catch (InterruptedException e) {}
            interstitial.show();  
          }
        }
      });
    }
  }

  @Override
  public void onDismissScreen(Ad arg0) {
    GnuBackgammon.Instance.interstitialVisible = false;
    GnuBackgammon.Instance.currentScreen.resume();
  }

  @Override
  public void onReceiveAd(Ad ad) {}
  @Override
  public void onLeaveApplication(Ad arg0) {}
  @Override
  public void onPresentScreen(Ad arg0) {}
  @Override
  public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {}



  @Override
  public void initEngine() {
    Gdx.app.log("INITIALIZATION","LOADING..");
    System.loadLibrary("glib-2.0");
    System.loadLibrary("gthread-2.0");
    System.loadLibrary("gnubg");
    copyAssetsIfNotExists();
    GnubgAPI.InitializeEnvironment(data_dir);    
  }


  public boolean isProVersion() {
    return PrivateDataManager.msIsPremium;
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    PrivateDataManager.destroyBillingData();
  }


  @Override
  public void inAppBilling() {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    Intent myIntent = new Intent(this, PurchaseActivity.class);
    startActivityForResult(myIntent, PrivateDataManager.RC_REQUEST);
  }


  @Override
  protected void onStart() {
    super.onStart();
    gHelper.onStart(this);
  }


  @Override
  protected void onStop() {
    super.onStop();
    if (mRoomId != null) {
      GServiceClient.getInstance().leaveRoom(10000);
    }
    gHelper.onStop();
  }

  

  private static int RC_SELECT_PLAYERS = 6000;
  private static int RC_WAITING_ROOM = 6001;
  private static int RC_LEADERBOARD = 6002;
  private static int RC_ACHIEVEMENTS = 6003;
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PrivateDataManager.RC_REQUEST) {
      if (resultCode!=10000) { 
        if (isProVersion()) {
          adView.setVisibility(View.GONE);
          if (adsTimer!=null) {
            adsTimer.cancel();
            adsTimer.purge();
            PrivateDataManager.destroyBillingData();  //Memory Optimization!
          }
          GnuBackgammon.Instance.menuScreen.redraw();
        }
      } else { //ERROR!
         System.out.println("BILLING: 10000");
         PrivateDataManager.destroyBillingData();
         PrivateDataManager.createBillingData(this);
      }
    } else if (requestCode==RC_SELECT_PLAYERS) {
      if (resultCode == RESULT_OK) {
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(GamesClient.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
          autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        final ArrayList<String> invitees = data.getStringArrayListExtra(GamesClient.EXTRA_PLAYERS);
        // create the room
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
          rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        gHelper.getGamesClient().createRoom(rtmConfigBuilder.build());
      } else {
        hideProgressDialog();
      }
    } else if (requestCode==RC_WAITING_ROOM) {
      if (resultCode!=RESULT_OK) {
        gserviceResetRoom();
        hideProgressDialog();
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
      gHelper.onActivityResult(requestCode, resultCode, data);
    }
    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }





  //GSERVICE STUFF...
  @Override
  public void onSignInFailed() {
  }

  @Override
  public void onSignInSucceeded() {
    prefs.putBoolean("ALREADY_SIGNEDIN", true);
    prefs.flush();
    gHelper.getGamesClient().registerInvitationListener(MainActivity.this);
    gHelper.getAppStateClient().loadState(MainActivity.this, APP_DATA_KEY);

    if (gHelper.getInvitationId()!=null && gHelper.getGamesClient().isConnected()) {
      GnuBackgammon.Instance.invitationId = gHelper.getInvitationId();
    }
  }


  @Override
  public void gserviceSignIn() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        gHelper.beginUserInitiatedSignIn();
      }
    });
  }


  @Override
  public boolean gserviceIsSignedIn() {
    return gHelper.isSignedIn();
  }


  @Override
  public void onInvitationReceived(Invitation invitation) {
    if (GnuBackgammon.Instance.currentScreen instanceof GameScreen) {
  	  gHelper.getGamesClient().declineRoomInvitation(invitation.getInvitationId());
  	  return;
  	}
    gserviceInvitationReceived(invitation.getInviter().getIconImageUri(), 
        invitation.getInviter().getDisplayName(), invitation.getInvitationId());
  }


  @Override
  public void onJoinedRoom(int arg0, Room room) {
    if (room==null) {
      hideProgressDialog();
      UIDialog.getFlashDialog(Events.NOOP, "Invalid invitation");
    } else {
      updateRoom(room);
      gConnecting = true;
    }
  }

  
  @Override
  public void onLeftRoom(int statusCode, String roomId) {
    System.out.println("---> P2P LEFT ROOM");
  }


  private boolean gConnecting = false;
  @Override
  public void onRoomConnected(int arg0, Room room) {
    updateRoom(room);
    MatchState.matchType = 3;
    GnuBackgammon.fsm.state(States.GSERVICE);
    pingtask = new TimerTask() {
      @Override
      public void run() {
        gserviceSendReliableRealTimeMessage("70 PING");
      }
    };
    tping = new Timer();
    tping.schedule(pingtask, 0, 5000);
    gConnecting = false;
  }


  private void updateRoom(Room room) {
    if (room!=null) {
      mRoomId = room.getRoomId();
      mParticipants = room.getParticipants();
    }
  }


  @Override
  public void onRoomCreated(int statusCode, Room room) {
    if (statusCode != GamesClient.STATUS_OK) {
      hideProgressDialog();
      UIDialog.getFlashDialog(Events.NOOP, "Unknown error");
      return;
    }
    mRoomId = room.getRoomId();
    meSentInvitation = true;
    Intent i = gHelper.getGamesClient().getRealTimeWaitingRoomIntent(room, Integer.MAX_VALUE);
    startActivityForResult(i, RC_WAITING_ROOM);
  }


  @Override
  public void onConnectedToRoom(Room room) {
    mParticipants = room.getParticipants();
    mMyId = room.getParticipantId(gHelper.getGamesClient().getCurrentPlayerId());
    updateRoom(room);
    String me, opponent, opponent_player_id;
    
    SecureRandom rdm = new SecureRandom();
    String sRdm = new BigInteger(130, rdm).toString(32);
    
    if (mParticipants.get(0).getParticipantId()==mMyId) {
      me = mParticipants.get(0).getDisplayName();
      opponent = mParticipants.get(1).getDisplayName();
      
      if (mParticipants.get(1).getPlayer()==null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(1).getPlayer().getPlayerId();
      
    } else {
      me = mParticipants.get(1).getDisplayName();
      opponent = mParticipants.get(0).getDisplayName();
      
      if (mParticipants.get(0).getPlayer()==null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(0).getPlayer().getPlayerId();
      
    }
    GnuBackgammon.Instance.gameScreen.updatePInfo(opponent, me);
    if (meSentInvitation) AchievementsManager.getInstance().checkSocialAchievements(opponent_player_id);
  }


  @Override
  public void onDisconnectedFromRoom(Room room) {
    mRoomId = null;
    lastReceptionTime = 0;
  }


  @Override
  public void onPeerDeclined(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerInvitedToRoom(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerJoined(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeerLeft(Room room, List<String> arg1) {
    System.out.println("---> P2P PEER LEFT");
    if (gConnecting) {
      hideProgressDialog();
      gserviceResetRoom();
      UIDialog.getFlashDialog(Events.NOOP, "Error: peer left the room");
      updateRoom(room);
    }
  }

  @Override
  public void onPeersConnected(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeersDisconnected(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onRoomAutoMatching(Room room) {
    updateRoom(room);
  }

  @Override
  public void onRoomConnecting(Room room) {
    updateRoom(room);
  }




  public void gserviceInvitationReceived(final Uri imagesrc, final String username, final String invitationId) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_invitation, null);
        alert.setView(myView).
        setTitle("Invitation received").
        setCancelable(false).
        setNegativeButton("Decline", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            gHelper.getGamesClient().declineRoomInvitation(invitationId);

          }
        });
        alert.setPositiveButton("Accept", null);

        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            ImageManager im = ImageManager.create(MainActivity.this);
            im.loadImage(((ImageView)myView.findViewById(R.id.image)), imagesrc);
            TextView tv = (TextView)myView.findViewById(R.id.text);
            tv.setText(username+" wants to play with you...");
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.requestFocus();
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                gserviceAcceptInvitation(invitationId);
                d.dismiss();
              }
            });
          }
        });
        d.show();
      }
    });
  }


  @Override
  public void gserviceAcceptInvitation(String invitationId) {
    RoomConfig.Builder roomConfigBuilder  = RoomConfig.builder(MainActivity.this);
    roomConfigBuilder.setInvitationIdToAccept(invitationId);
    roomConfigBuilder.setMessageReceivedListener(MainActivity.this);
    roomConfigBuilder.setRoomStatusUpdateListener(MainActivity.this);
    gHelper.getGamesClient().joinRoom(roomConfigBuilder.build());
    showProgressDialog();
  }


  ProgressDialog mProgressDialog = null;
  void showProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressDialog == null) {
          if (MainActivity.this == null)
            return;
          mProgressDialog = new ProgressDialog(MainActivity.this){
            int clickCount=0;
            
            @Override
            public void dismiss() {
              super.dismiss();
              clickCount = 0;
            }
            
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
              clickCount++;
              if (clickCount==7) {
                GnuBackgammon.Instance.nativeFunctions.gserviceResetRoom();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
                dismiss();
              }
              return false;
            }
          };
        }
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
      }
    });
  }

  @Override
  public void hideProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressDialog!=null) {
          mProgressDialog.dismiss();
          mProgressDialog=null;
        }
      }
    });
  }

  long lastReceptionTime = 0;
  @Override
  public void onRealTimeMessageReceived(RealTimeMessage rtm) {
    lastReceptionTime = System.currentTimeMillis();
    byte[] buf = rtm.getMessageData();
    String s = new String(buf);
    System.out.println("GSERVICE RECEIVED: "+s);
    GServiceClient.getInstance().precessReceivedMessage(s);
  }


  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
    System.out.println("GSERVICE SEND: "+msg);
    if ((lastReceptionTime!=0) && ((System.currentTimeMillis() - lastReceptionTime) > 8000)) {
      GServiceClient.getInstance().leaveRoom(0);
    } else {
      if ((mRoomId==null)||(mRoomId=="")) {
        GServiceClient.getInstance().leaveRoom(GamesClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
      } else {
        for (Participant p : mParticipants) {
          if (p.getParticipantId().equals(mMyId))
            continue;
          if (p.getStatus() != Participant.STATUS_JOINED) {
            continue;
          }
          
          gHelper.getGamesClient().sendReliableRealTimeMessage(this, msg.getBytes(), mRoomId, p.getParticipantId());   //.sendReliableRealTimeMessage(this, msg.getBytes(), mRoomId, p.getParticipantId());
        }
      }
    }
  }


  @Override
  public void onRealTimeMessageSent(int statusCode, int token, String recipientParticipantId) {
    if ((statusCode != GamesClient.STATUS_OK)&&(lastReceptionTime!=0)) {
      GServiceClient.getInstance().leaveRoom(GamesClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    }
  }


  @Override
  public void gserviceStartRoom() {
    if (gHelper.getGamesClient().isConnected()) {
      showProgressDialog();
      Intent intent = gHelper.getGamesClient().getSelectPlayersIntent(1, 1);
      startActivityForResult(intent, RC_SELECT_PLAYERS);
    } else {
      gserviceGetSigninDialog(-1);
    }
  }

  @Override
  public void gserviceResetRoom() {
    GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
    gConnecting = false;
    meSentInvitation = false;
    if (mRoomId != null) {
      gHelper.getGamesClient().leaveRoom(this, mRoomId);
      mRoomId = null;
      lastReceptionTime = 0;
      gserviceStopPing();
    }
  }

  @Override
  public void gserviceStopPing() {
    System.out.println("GSERVICE: STOP PING");
    if (tping!=null) {
      tping.cancel();
      tping.purge();
    }
    lastReceptionTime=0;
  }
  
  @Override
  public void gserviceSubmitRating(long score, String board_id) {
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false)) return;
    gHelper.getGamesClient().submitScoreImmediate(new OnScoreSubmittedListener() {
      
      @Override
      public void onScoreSubmitted(int arg0, SubmitScoreResult arg1) {
      }
    }, board_id, score);
    
  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id=="") return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn())) return;
    gHelper.getGamesClient().incrementAchievementImmediate(new OnAchievementUpdatedListener() {

      @Override
      public void onAchievementUpdated(int statusCode, String achievement_id) {
      }
    }, achievement_id, increment);
  }

  @Override
  public void gserviceUnlockAchievement(String achievement_id) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id=="") return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn())) return;
    gHelper.getGamesClient().unlockAchievementImmediate(new OnAchievementUpdatedListener() {

      @Override
      public void onAchievementUpdated(int statusCode, String arg1) {
      }
    }, achievement_id);
  }

  @Override
  public void gserviceOpenLeaderboards() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(gHelper.getGamesClient().getAllLeaderboardsIntent(), RC_LEADERBOARD);
    } else {
      gserviceGetSigninDialog(FROM_SCOREBOARDS);
    }
  }

  @Override
  public void gserviceOpenAchievements() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(gHelper.getGamesClient().getAchievementsIntent(), RC_ACHIEVEMENTS);
    } else {
      gserviceGetSigninDialog(FROM_ACHIEVEMENTS);
    }
  }

  
  private static int APP_DATA_KEY = 0;
  @Override
  public void onStateLoaded(int statusCode, int stateKey, byte[] data) {

      if (statusCode == AppStateClient.STATUS_OK) {
        AppDataManager.getInstance().loadState(data);
        ELORatingManager.getInstance().syncLeaderboards();
      } else if (statusCode == AppStateClient.STATUS_NETWORK_ERROR_STALE_DATA) {
      } else {
      }
  }

  @Override
  public void onStateConflict(int stateKey, String ver, byte[] localData, byte[] serverData) {
    gHelper.getAppStateClient().resolveState(this, APP_DATA_KEY, ver, AppDataManager.getInstance().resolveConflict(localData, serverData));
  }

  @Override
  public void gserviceUpdateState() {
    if (gHelper.isSignedIn()) {
//      deleteAppState();
      gHelper.getAppStateClient().updateState(APP_DATA_KEY, AppDataManager.getInstance().getBytes());
    }
  }

/*  
  private void deleteAppState() {
    if (gHelper.isSignedIn()) {
      gHelper.getAppStateClient().deleteState(new OnStateDeletedListener() {

        @Override
        public void onStateDeleted(int arg0, int arg1) {
          System.out.println("GSERVICE STATE DELETED");
        }
      }, APP_DATA_KEY);
    }
  }
*/


  private static int FROM_ACHIEVEMENTS = 1;
  private static int FROM_SCOREBOARDS = 2;
  public void trySignIn(final int from) {
    if ((from==FROM_ACHIEVEMENTS)||(from==FROM_SCOREBOARDS)) {
      gHelper.setListener(new GServiceGameHelper.GameHelperListener() {
        @Override
        public void onSignInSucceeded() {
          gHelper.setListener(MainActivity.this);
          MainActivity.this.onSignInSucceeded();
          if (from==FROM_ACHIEVEMENTS)
            startActivityForResult(gHelper.getGamesClient().getAchievementsIntent(), RC_ACHIEVEMENTS);
          else
            startActivityForResult(gHelper.getGamesClient().getAllLeaderboardsIntent(), RC_LEADERBOARD);
        }
        @Override
        public void onSignInFailed() {
          UIDialog.getFlashDialog(Events.NOOP, "Login error");
        }
      });
    }
    gserviceSignIn();
  }

  
  @Override
  public void gserviceGetSigninDialog(final int from) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_gplus, null);
        alert.setView(myView).
        setTitle("Signin").
        setCancelable(true);
        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            String msg = "";
            TextView v = (TextView) d.findViewById(R.id.login_text);
            if (prefs.getBoolean("ALREADY_SIGNEDIN", false)) {
              msg = "Please sign in on Google Games Services to enable this feature";
              //v.setVisibility(View.GONE);
            } else {
              msg = "Please sign in, Google will ask you to accept requested permissions and configure " +
              "sharing settings up to two times. This may take few minutes..";
            }
            v.setText(msg);
            com.google.android.gms.common.SignInButton b = (com.google.android.gms.common.SignInButton) d.findViewById(R.id.sign_in_button);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                d.dismiss();
                trySignIn(from);
              }
            });
          }
        });
        d.show();
      }
    });
  }
}
