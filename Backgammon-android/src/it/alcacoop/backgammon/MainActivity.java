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
import it.alcacoop.backgammon.fsm.MenuFSM;
import it.alcacoop.backgammon.fsm.MenuFSM.States;
import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.layers.FibsScreen;
import it.alcacoop.backgammon.layers.SplashScreen;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.util.GServiceApplication;
import it.alcacoop.backgammon.util.GServiceGameHelper;
import it.alcacoop.backgammon.utils.AppDataManager;
import it.alcacoop.backgammon.utils.ELORatingManager;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.SubmitScoreResult;


@SuppressLint({ "SimpleDateFormat", "HandlerLeak", "TrulyRandom" })
public class MainActivity extends GServiceApplication implements NativeFunctions, OnEditorActionListener, SensorEventListener {

  private String data_dir;
  protected AdView adView;
  private View chatBox;
  private View gameView;

  private boolean mInitialized;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private int rotation;

  private InterstitialAd interstitial;
  private Timer adsTimer;
  private TimerTask adsTask;

  private int appVersionCode = 0;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;

    data_dir = getBaseContext().getApplicationInfo().dataDir + "/gnubg/";

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    RelativeLayout layout = new RelativeLayout(this);
    gameView = initializeForView(new GnuBackgammon(this), cfg);

    /** SENSOR INITIALIZATION **/
    mInitialized = false;
    mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    /** SENSOR INITIALIZATION **/

    /** ADS INITIALIZATION **/
    PrivateDataManager.initData();
    adView = new AdView(this);
    adView.setAdUnitId(PrivateDataManager.ads_id);

    if (isTablet(this))
      adView.setAdSize(AdSize.FULL_BANNER);
    else
      adView.setAdSize(AdSize.BANNER);
    adView.setVisibility(View.GONE);

    if (!isProVersion())
      adView.loadAd(new AdRequest.Builder().build());
    // Create the interstitial
    interstitial = new InterstitialAd(this);
    interstitial.setAdUnitId(PrivateDataManager.int_id);

    interstitial.setAdListener(new AdListener() {
      @Override
      public void onAdClosed() {
        GnuBackgammon.Instance.interstitialVisible = false;
        GnuBackgammon.Instance.currentScreen.resume();
        super.onAdClosed();
      }
    });
    /** ADS INITIALIZATION **/

    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layout.addView(gameView);
    layout.addView(adView, adParams);

    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    chatBox = inflater.inflate(R.layout.chat_box, null);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    chatBox.setVisibility(View.GONE);
    layout.addView(chatBox, params);

    setContentView(layout);

    /** CHATBOX DIMS **/
    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);
    int width = metrics.widthPixels;

    View s1 = findViewById(R.id.space1);
    View s2 = findViewById(R.id.space2);
    View s3 = findViewById(R.id.chat_content);
    ViewGroup.LayoutParams pars = s1.getLayoutParams();
    pars.width = Math.round(width * 0.15f) + 7;
    s1.setLayoutParams(pars);
    pars = s2.getLayoutParams();
    pars.width = Math.round(width * 0.15f) + 7;
    s2.setLayoutParams(pars);
    pars = s3.getLayoutParams();
    GnuBackgammon.chatHeight = pars.height;
    pars.width = Math.round(width * 0.7f) - 14;
    s3.setLayoutParams(pars);
    EditText target = (EditText)findViewById(R.id.message);
    target.setOnEditorActionListener(this);
    /** CHATBOX DIMS **/

    /** GOOGLE API INITIALIZATION **/
    PrivateDataManager.createBillingData(this);
    prefs = Gdx.app.getPreferences("GameOptions");
    gHelper = new GServiceGameHelper(this, prefs.getBoolean("ALREADY_SIGNEDIN", false));
    gHelper.setup(this, GServiceGameHelper.CLIENT_APPSTATE | GServiceGameHelper.CLIENT_GAMES);

    ActivityManager actvityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
    List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(3);
    for (RunningTaskInfo runningTaskInfo : taskInfos) {
      if (runningTaskInfo.baseActivity.getPackageName().contains("gms")) {
        gserviceSignIn();
        break;
      }
    }
    /** GOOGLE API INITIALIZATION **/

    /** APP VERSION **/
    PackageInfo pInfo;
    try {
      pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      appVersionCode = pInfo.versionCode;
    } catch (NameNotFoundException e) {}
    /** APP VERSION **/
  }


  public boolean isTablet(Context context) {
    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    return (xlarge || large);
  }


  private void copyAssetsIfNotExists() {
    File a1 = new File(data_dir + "g11.xml");
    File a2 = new File(data_dir + "gnubg_os0.bd");
    File a3 = new File(data_dir + "gnubg_ts0.bd");
    File a4 = new File(data_dir + "gnubg.weights");
    File a5 = new File(data_dir + "gnubg.wd");

    // Asset already presents
    if (a1.exists() && a2.exists() && a3.exists() && a4.exists() && a5.exists())
      return;

    File assetDir = new File(data_dir);
    assetDir.mkdirs();

    AssetManager assetManager = getAssets();
    String[] files = null;
    try {
      files = assetManager.list("gnubg");
    } catch (IOException e) {}
    for (String filename : files) {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = assetManager.open("gnubg/" + filename);
        out = new FileOutputStream(data_dir + filename);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
      } catch (IOException e) {}
    }
  }

  private void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }

  @Override
  public void showAds(final boolean show) {
    if (isProVersion())
      return;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (show) {
          // adView.loadAd(new AdRequest.Builder().build());
          adView.setVisibility(View.VISIBLE);
        } else {
          adView.setVisibility(View.GONE);
        }
      }
    });
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
    try {
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
    intent.putExtra(Intent.EXTRA_SUBJECT, "Backgammon Mobile exported Match (Played on " + d + ")");
    intent
        .putExtra(
            Intent.EXTRA_TEXT,
            "You can analize attached file with desktop version of GNU Backgammon\nNOTE: GNU Backgammon is available for Windows, MacOS and Linux\n\nIf you enjoyed Backgammon Mobile please help us rating it on the market");

    try {
      dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
      d = dateFormat.format(date);
      File dir = new File(Environment.getExternalStorageDirectory(), "gnubg-sgf");
      dir.mkdir();
      final File data = new File(dir, "match-" + d + ".sgf");

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
  public void injectBGInstance() {}


  @Override
  public void fibsSignin() {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_signin, null);
        alert.setView(myView).setTitle("Login to server...").setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
          }
        });

        if (!GnuBackgammon.Instance.server.equals("fibs.com"))
          alert.setNeutralButton("Create Account", new DialogInterface.OnClickListener() {
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
            final int min_chars;
            if (GnuBackgammon.Instance.server.equals("fibs.com")) {
              usr = GnuBackgammon.Instance.fibsPrefs.getString("fusername");
              pwd = GnuBackgammon.Instance.fibsPrefs.getString("fpassword");
              min_chars = 0;
            } else {
              usr = GnuBackgammon.Instance.fibsPrefs.getString("tusername");
              pwd = GnuBackgammon.Instance.fibsPrefs.getString("tpassword");
              min_chars = 3;
            }
            ((EditText)myView.findViewById(R.id.username)).setText(usr);
            ((EditText)myView.findViewById(R.id.password)).setText(pwd);
            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                String username = ((EditText)myView.findViewById(R.id.username)).getText().toString();
                String password = ((EditText)myView.findViewById(R.id.password)).getText().toString();
                if (username.length() > 3 && password.length() > 3) {
                  GnuBackgammon.Instance.commandDispatcher.sendLogin(username, password);
                  d.dismiss();
                } else {
                  Context context = getApplicationContext();
                  CharSequence text = "";
                  if (username.length() <= min_chars)
                    text = "Username must be at least " + (min_chars + 1) + "-chars length";
                  else if (password.length() <= 3)
                    text = "Password must be at least 4-chars length";
                  else
                    text = "Generic error, please retype username and password";

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
        alert.setView(myView).setCancelable(false).setTitle("Create new account...").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
          }
        }).setPositiveButton("Create", null);

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
                if (username.length() > 3 && password.length() > 3 && password2.length() > 3 && password.equals(password2)) {
                  GnuBackgammon.Instance.FibsUsername = username;
                  GnuBackgammon.Instance.FibsPassword = password;
                  GnuBackgammon.Instance.commandDispatcher.createAccount();
                  d.dismiss();
                } else {
                  Context context = getApplicationContext();
                  CharSequence text = "";
                  if (username.length() <= 3)
                    text = "Username must be at least 4-chars length";
                  else if (password.length() <= 3)
                    text = "Password must be at least 4-chars length";
                  else if (!password.equals(password2))
                    text = "Provided passwords don't match";
                  else
                    text = "Generic error, please retype username and password";

                  int duration = Toast.LENGTH_SHORT;
                  Toast toast = Toast.makeText(context, text, duration);
                  toast.setGravity(Gravity.TOP, 0, 0);
                  toast.show();
                }
              }
            });
          }
        });


        myMsg.setText("\nYou are creating new account...\n\n" + "Available chars for username are: A-Z,a-z,_\n" + "Available chars for password are: A-Z,a-z,0-9,_\n\n"
            + "Note: username and password must be\n minimum 4-chars length\n");
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        popupBuilder.setCancelable(false).setView(myMsg).setTitle("Info").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }


  @Override
  public void showChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (chatBox.getVisibility() != View.VISIBLE)
          chatBox.setVisibility(View.VISIBLE);
      }
    });
  }

  @Override
  public void hideChatBox() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (chatBox.getVisibility() != View.GONE) {
          EditText chat = (EditText)findViewById(R.id.message);
          InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
          chatBox.setVisibility(View.GONE);
        }
      }
    });
  }


  public void clearMessage(View v) {
    EditText chat = (EditText)findViewById(R.id.message);
    chat.setText("");
  }

  public void sendMessage(View v) {
    EditText chat = (EditText)findViewById(R.id.message);
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(chat.getWindowToken(), 0);
    Editable msg = chat.getText();
    if (msg.toString().length() > 0) {
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
    if ((GnuBackgammon.Instance == null) || (GnuBackgammon.Instance.currentScreen == null) || (GnuBackgammon.Instance.getScreen() == null)
        || (GnuBackgammon.Instance.getScreen() instanceof SplashScreen))
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

    if (adView != null)
      adView.resume();

    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    if (!isProVersion()) {
      adsTimer = new Timer();
      adsTask = new TimerTask() {
        @Override
        public void run() {
          runOnUiThread(new Runnable() {
            public void run() {
              if ((!isProVersion()) && (!interstitial.isLoaded())) {
                interstitial.loadAd(new AdRequest.Builder().build());
              }
            }
          });
        }
      };
      adsTimer.schedule(adsTask, 0, 15000);
    }
  }

  @Override
  protected void onPause() {
    if (adView != null)
      adView.pause();

    mSensorManager.unregisterListener(this);
    if (adsTimer != null) {
      adsTimer.cancel();
      adsTimer.purge();
    }

    if ((GnuBackgammon.Instance != null) && (GnuBackgammon.Instance.currentScreen instanceof FibsScreen) && (!GnuBackgammon.Instance.interstitialVisible)) {
      GnuBackgammon.Instance.commandDispatcher.send("BYE");
      GnuBackgammon.Instance.fibsScreen.fibsInvitations.clear();
      GnuBackgammon.Instance.fibsScreen.fibsPlayers.clear();
      GnuBackgammon.Instance.setFSM("MENU_FSM");
    }
    super.onPause();
  }


  @Override
  public void onAccuracyChanged(Sensor arg0, int arg1) {}


  private final float NOISE = 0.5f;

  @Override
  public void onSensorChanged(SensorEvent event) {
    float x = event.values[1];
    if (rotation == 3)
      x = -x;
    if (!mInitialized) {
      mInitialized = true;
    } else {
      if (Math.abs(x) < NOISE)
        return;
      if (GnuBackgammon.Instance != null)
        if (GnuBackgammon.Instance.currentScreen != null)
          GnuBackgammon.Instance.currentScreen.moveBG(x);
    }
  }

  @Override
  public void showInterstitial() {
    if (isProVersion())
      return;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (interstitial.isLoaded()) {
          GnuBackgammon.Instance.currentScreen.pause();
          GnuBackgammon.Instance.interstitialVisible = true;
          synchronized (this) {
            try {
              wait(700);
            } catch (InterruptedException e) {}
            interstitial.show();
          }
        }
      }
    });
  }


  @Override
  public void initEngine() {
    Gdx.app.log("INITIALIZATION", "LOADING..");
    System.loadLibrary("glib-2.0");
    System.loadLibrary("gthread-2.0");
    System.loadLibrary("gnubg");
    copyAssetsIfNotExists();
    GnubgAPI.InitializeEnvironment(data_dir);
  }


  public boolean isProVersion() {
    // TODO: NEED FIX WITH LOCAL PROPERTIES
    return PrivateDataManager.msIsPremium;
  }


  @Override
  protected void onDestroy() {
    if (adView != null)
      adView.destroy();
    PrivateDataManager.destroyBillingData();
    super.onDestroy();
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
    if (mRoomId != null) {
      GServiceClient.getInstance().leaveRoom(10000);
    }
    gHelper.onStop();
    super.onStop();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // System.out.println("---> ONACTIVITY RESULT");
    hideProgressDialog();
    if (requestCode == PrivateDataManager.RC_REQUEST) {
      if (resultCode != 10000) {
        if (isProVersion()) {
          adView.setVisibility(View.GONE);
          if (adsTimer != null) {
            adsTimer.cancel();
            adsTimer.purge();
            PrivateDataManager.destroyBillingData(); // Memory Optimization!
          }
          GnuBackgammon.Instance.menuScreen.redraw();
        }
      } else { // ERROR!
        System.out.println("BILLING: 10000");
        PrivateDataManager.destroyBillingData();
        PrivateDataManager.createBillingData(this);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }


  // GSERVICE STUFF...


  @Override
  public int getAppVersionCode() {
    return appVersionCode;
  }


  @Override
  protected boolean shouldShowInvitationDialog() {
    return true;
  }


  @Override
  protected void onRoomConnectedBehaviour() {
    MatchState.matchType = 3;
    GnuBackgammon.fsm.state(States.GSERVICE);
  }


  @Override
  protected void onLeftRoomBehaviour(int reason) {
    GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
    GServiceClient.getInstance().reset();
    hideProgressDialog();
    if (GnuBackgammon.fsm instanceof MenuFSM)
      GnuBackgammon.fsm.state(States.TWO_PLAYERS);
  }


  @Override
  protected void onRTMessageReceivedBehaviour(String msg) {
    GServiceClient.getInstance().processReceivedMessage(msg);
  }


  @Override
  protected void onErrorBehaviour(String msg) {
    UIDialog.getFlashDialog(Events.NOOP, msg);
  }


  @Override
  protected void onStateLoadedBehaviour(byte[] data) {
    AppDataManager.getInstance().loadState(data);
    ELORatingManager.getInstance().syncLeaderboards();
  }


  @Override
  protected byte[] onStateConflictBehaviour(byte[] localData, byte[] serverData) {
    return AppDataManager.getInstance().resolveConflict(localData, serverData);
  }


  @Override
  protected void onDismissProgressDialogBehaviour() {
    gserviceResetRoom();
    GnuBackgammon.fsm.state(MenuFSM.States.TWO_PLAYERS);
    System.out.println("QUI CI SON PASSATO! " + GnuBackgammon.fsm);
  }


  @Override
  protected void onScoreSubmittedBehaviour(String board_id, SubmitScoreResult arg1) {
    // FIX: SYNC WITH LEADERBOARD VALUE
    long local_score = 0;
    // TODO: NULLPOINTEREXCEPTION IF NO NETWORK
    long score = arg1.getScoreResult(LeaderboardVariant.TIME_SPAN_ALL_TIME).rawScore;
    String board = "SINGLEBOARD";
    if (board_id.equals(ELORatingManager.MULTI_BOARD))
      board = "MULTIBOARD";
    if (board_id.equals(ELORatingManager.TIGA_BOARD))
      board = "TIGABOARD";
    if (board_id.equals(ELORatingManager.FIBS_BOARD2))
      board = "FIBSBOARD2";
    local_score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString(board, "0")) * 100);

    System.out.println("===> " + board + ": " + GnuBackgammon.Instance.optionPrefs.getString(board) + " " + local_score + " " + score);
    System.out.println("===> " + (double)(score / 100.00));

    if (local_score < score) {
      GnuBackgammon.Instance.optionPrefs.putString(board, ((double)(score / 100.00)) + "");
      GnuBackgammon.Instance.optionPrefs.flush();
      gserviceUpdateState();
    }
  }


  @Override
  protected void onLeaveRoomBehaviour(int reason) {}

  @Override
  protected void onResetRoomBehaviour() {}

}
