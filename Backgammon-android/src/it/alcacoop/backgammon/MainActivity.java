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
import it.alcacoop.backgammon.gservice.GServiceApplication;
import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.helpers.ADSHelpers;
import it.alcacoop.backgammon.helpers.AccelerometerHelpers;
import it.alcacoop.backgammon.helpers.AndroidHelpers;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.layers.SplashScreen;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.AppDataManager;
import it.alcacoop.backgammon.utils.ELORatingManager;
import it.alcacoop.backgammon.utils.MatchRecorder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.text.Editable;
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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.android.gms.games.leaderboard.ScoreSubmissionData.Result;


@SuppressLint("InflateParams")
public class MainActivity extends GServiceApplication implements NativeFunctions, OnEditorActionListener {

  private View chatBox;
  private View gameView;

  private AndroidHelpers androidHelpers;
  private ADSHelpers adsHelpers;
  private AccelerometerHelpers accelerometerHelpers;

  private ImageManager imgMgr;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    RelativeLayout layout = new RelativeLayout(this);
    gameView = initializeForView(new GnuBackgammon(this), cfg);

    super.onCreate(savedInstanceState);

    // HELPERS INITIALIZATION
    PrivateDataManager.createBillingData(this);
    androidHelpers = new AndroidHelpers(this);
    accelerometerHelpers = new AccelerometerHelpers(this);
    adsHelpers = new ADSHelpers(this, androidHelpers.isTablet());

    RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    layout.addView(gameView);
    View adv = adsHelpers.getAdView();
    if (adv != null)
      layout.addView(adv, adParams);

    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    chatBox = inflater.inflate(R.layout.chat_box, null);
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    chatBox.setVisibility(View.GONE);
    layout.addView(chatBox, params);

    setContentView(layout);

    /** CHATBOX DIMS **/
    int width = androidHelpers.getScreenWidth();
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

    imgMgr = ImageManager.create(getApplicationContext());
  }


  @Override
  public void showAds(final boolean show) {
    adsHelpers.showAds(show);
  }


  @Override
  public void openURL(String... urls) {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    androidHelpers.openURL(urls);
  }

  @Override
  public String getDataDir() {
    return androidHelpers.getDataDir();
  }

  @Override
  public void shareMatch(MatchRecorder rec) {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    androidHelpers.sendFile(rec.saveSGF().getBytes());
  }

  @Override
  public void injectBGInstance() {}


  @SuppressLint("NewApi")
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

        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            enterImmersiveMode();
          }
        });

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

        if (android.os.Build.VERSION.SDK_INT >= 11) {
          d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
          d.show();
          d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
          d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
          d.show();
        }
      }
    });
  }

  @SuppressLint("NewApi")
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
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            enterImmersiveMode();
          }
        });
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
            if (android.os.Build.VERSION.SDK_INT >= 11) {
              d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
              d.show();
              d.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
              d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            } else {
              d.show();
            }
          }
        });
        AlertDialog popup = popupBuilder.create();
        if (android.os.Build.VERSION.SDK_INT >= 11) {
          popup.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
          popup.show();
          popup.getWindow().getDecorView().setSystemUiVisibility(MainActivity.this.getWindow().getDecorView().getSystemUiVisibility());
          popup.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
          d.show();
        }
      }
    });
  }

  @Override
  public boolean isNetworkUp() {
    return androidHelpers.isNetworkUp();
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
        adjustFocus();
        enterImmersiveMode();
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
    enterImmersiveMode();
  }

  @Override
  public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
    sendMessage(null);
    return false;
  }

  @SuppressLint({ "InlinedApi", "NewApi" })
  public void enterImmersiveMode() {
    if (android.os.Build.VERSION.SDK_INT < 11)
      return;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.INVISIBLE);
      }
    });

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
      enterImmersiveMode();
      adjustFocus();
      GnuBackgammon.Instance.gameScreen.chatBox.hide();
    }
    return super.onKeyDown(keyCode, event);
  }


  @Override
  protected void onResume() {
    super.onResume();
    accelerometerHelpers.onResume();
    adsHelpers.onResume();
    enterImmersiveMode();
  }

  @Override
  protected void onPause() {
    adsHelpers.onPause();
    accelerometerHelpers.onPause();
    super.onPause();
  }


  @Override
  public void showInterstitial() {
    adsHelpers.showInterstitial();
  }


  @Override
  public void initEngine() {
    Gdx.app.log("INITIALIZATION", "LOADING..");
    System.loadLibrary("gnubg");
    androidHelpers.copyAssetsIfNotExists();
    AICalls.Locking.InitializeEnvironment(androidHelpers.getDataDir());
    enterImmersiveMode();
  }


  public boolean isProVersion() {
    return (PrivateDataManager.msIsPremium) || (PrivateDataManager.billingPrefs.getBoolean("msIsPremium", false));
  }


  @Override
  protected void onDestroy() {
    adsHelpers.onDestroy();
    PrivateDataManager.destroyBillingData();
    super.onDestroy();
  }


  @Override
  public void inAppBilling() {
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
    Intent myIntent = new Intent(this, PurchaseActivity.class);
    startActivityForResult(myIntent, PrivateDataManager.INAPP_BILLING_REQUEST);
  }


  @Override
  protected void onStart() {
    super.onStart();
    enterImmersiveMode();
  }


  @Override
  protected void onStop() {
    if (mRoomId != null) {
      GServiceClient.getInstance().leaveRoom(10000);
    }
    super.onStop();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode != RESULT_OK)
      hideProgressDialog();

    switch (requestCode) {
      case PrivateDataManager.INAPP_BILLING_REQUEST: // RETURN FROM IN APP BILLING
        if (resultCode != 10000) {
          adsHelpers.disableAllAds();
          GnuBackgammon.Instance.menuScreen.redraw();
        } else { // ERROR!
          PrivateDataManager.destroyBillingData();
          PrivateDataManager.createBillingData(this);
        }
        break;
    }

    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
    enterImmersiveMode();
  }


  @Override
  public int getAppVersionCode() {
    return androidHelpers.getAppVersionCode();
  }


  @Override
  protected boolean shouldShowInvitationDialog() {
    if (GnuBackgammon.Instance.currentScreen instanceof GameScreen) // PLAYING A GAME
      return false;
    return true;
  }


  @Override
  protected void onRoomConnectedBehaviour() {
    MatchState.matchType = 3;
    GnuBackgammon.fsm.state(MenuFSM.States.GSERVICE);
  }


  @Override
  protected void onLeftRoomBehaviour() {
    GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
    GServiceClient.getInstance().reset();
    hideProgressDialog();
    if (GnuBackgammon.fsm instanceof MenuFSM)
      GnuBackgammon.fsm.state(MenuFSM.States.TWO_PLAYERS);
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
  }


  @Override
  protected void onScoreSubmittedBehaviour(String board_id, SubmitScoreResult ssr) {
    long local_score = 0;
    if (ssr == null)
      return; // NO NETWORK OR APPSTATE ERROR

    Result sr = ssr.getScoreData().getScoreResult(LeaderboardVariant.TIME_SPAN_ALL_TIME);

    if (sr == null) // NO NETWORK OR APPSTATE ERROR
      return;

    long score = sr.rawScore;

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

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    adjustFocus();
    if (hasFocus)
      enterImmersiveMode();
  }


  @Override
  public void beginGoogleSignIn() {
    gHelper.setConnectOnStart(prefs.getBoolean("WANTS_GOOGLE_SIGNIN", true));
    gHelper.onStart(this);
    enterImmersiveMode();
  }


  @Override
  public void loadImageFromIconURI(Object iconURI, final int playerIndex) {

    System.out.println("===> URI: " + iconURI);

    Uri uri = (Uri)iconURI;
    imgMgr.loadImage(new ImageManager.OnImageLoadedListener() {
      @Override
      public void onImageLoaded(Uri uri, final Drawable drawable, boolean arg2) {
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            Texture tex = new Texture(128, 128, Format.RGBA8888);
            tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            if (playerIndex == 0)
              GnuBackgammon.Instance.iconMe = new Image(tex);
            else
              GnuBackgammon.Instance.iconOpponent = new Image(tex);
          }
        });
      }
    }, uri, R.drawable.gplayer);
  }

}
