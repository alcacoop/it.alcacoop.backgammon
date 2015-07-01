/**
 ##################################################################
 #                       BACKGAMMON MOBILE                        #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Francesco Valente                #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   18/10/2013                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2013   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of BACKGAMMON MOBILE.                       #
 #  FOUR IN A LINE MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  BACKGAMMON MOBILE is distributed in the hope that it          #
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
 **/

package it.alcacoop.backgammon.gservice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.common.images.ImageManager.OnImageLoadedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.R;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.MenuFSM;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.AchievementsManager;
import it.alcacoop.backgammon.utils.AppDataManager;

@SuppressLint({ "InflateParams", "NewApi" })
public abstract class BaseGServiceApplication extends AndroidApplication
    implements GServiceGameHelper.GameHelperListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener,
    RoomUpdateListener, OnInvitationReceivedListener {

  protected Preferences prefs;
  protected GServiceGameHelper gHelper;
  protected static int APP_DATA_KEY = 0;
  private boolean gConnecting = false;
  private boolean gServiceGameCanceled = false;
  private AlertDialog invitationDialog;
  private SecureRandom rnd;

  protected String mRoomId = null;
  protected String mMyId = null;
  protected ArrayList<Participant> mParticipants = null;
  protected boolean meSentInvitation;
  protected String snapshotName = "snapshotPrefs";

  ProgressDialog mProgressDialog = null;
  protected final static int RC_SELECT_PLAYERS = 6000;
  protected final static int RC_WAITING_ROOM = 6001;
  protected final static int RC_LEADERBOARD = 6002;
  protected final static int RC_ACHIEVEMENTS = 6003;
  protected final static int RC_RESOLVE = GServiceGameHelper.RC_RESOLVE;

  public final static int FROM_ACHIEVEMENTS = 1;
  public final static int FROM_SCOREBOARDS = 2;
  public final static int FROM_NEWGAME = 3;

  protected abstract boolean shouldShowInvitationDialog();
  protected abstract void onRoomConnectedBehaviour();
  protected abstract void onLeaveRoomBehaviour(int reason);
  protected abstract void onLeftRoomBehaviour();
  protected abstract void onRTMessageReceivedBehaviour(String msg);
  protected abstract void onErrorBehaviour(String msg);
  protected abstract void onStateLoadedBehaviour(byte[] data);
  protected abstract byte[] onStateConflictBehaviour(byte[] localData, byte[] serverData);
  protected abstract void onResetRoomBehaviour();
  protected abstract void onScoreSubmittedBehaviour(String board_id, SubmitScoreResult arg1);
  protected abstract void onDismissProgressDialogBehaviour();


  @SuppressLint("TrulyRandom")
  public BaseGServiceApplication() {
    try {
      rnd = SecureRandom.getInstance("SUN");
    } catch (NoSuchAlgorithmException e) {
      rnd = new SecureRandom();
    }
  }

  private void migrateFromAppState() {
    AsyncTask<Void, Void, Boolean> migrateTask = new AsyncTask<Void, Void, Boolean>() {
      @Override
      protected void onPreExecute() {
        showProgressDialog(false);
      }

      @Override
      protected Boolean doInBackground(Void... params) {
        // Load AppState data
        AppStateManager.StateResult load = AppStateManager
            .load(getApiClient(), APP_DATA_KEY)
            .await();
        if (!load.getStatus().isSuccess()) {
          // Create the snapshot and return without errors
          System.out.println("GSERVICE: Could not load App State for migration. Creating empty snapshot.");
          Snapshots.OpenSnapshotResult open = Games.Snapshots
              .open(gHelper.getApiClient(), snapshotName, true)
              .await();
          Games.Snapshots.discardAndClose(gHelper.getApiClient(), open.getSnapshot());
          return true;
        }

        // Save locally with AppDataManager
        byte[] data = load.getLoadedResult().getLocalData();
        AppDataManager.getInstance().loadState(data);

        // Create snapshot
        Snapshots.OpenSnapshotResult open = Games.Snapshots
            .open(getApiClient(), snapshotName, true)
            .await();

        // Write data and commit
        Snapshot snapshot = open.getSnapshot();
        snapshot.getSnapshotContents().writeBytes(data);
        Snapshots.CommitSnapshotResult commit = Games.Snapshots
            .commitAndClose(getApiClient(), snapshot, SnapshotMetadataChange.EMPTY_CHANGE)
            .await();
        if (!commit.getStatus().isSuccess()) {
          GnuBackgammon.out.println("GSERVICE: COMMIT Could not open Snapshot for migration.");
          return false;
        }

        // No failures
        GnuBackgammon.out.println("GSERVICE: migration ok!");
        return true;
      }

      @Override
      protected void onPostExecute(Boolean aBoolean) {
        hideProgressDialog();
      }
    };

    migrateTask.execute();
  }

  private void onStateConflict(Snapshots.OpenSnapshotResult result) {
    final Snapshot remoteSnapshot = result.getSnapshot();
    final Snapshot localSnapshot = result.getConflictingSnapshot();

    // Resolve with one of the snapshot (temporarily)
    Games.Snapshots.resolveConflict(getApiClient(), result.getConflictId(), remoteSnapshot);

    // Reopen the snapsot and write bytes on the result snapshot
    Games.Snapshots.open(getApiClient(), snapshotName, true).setResultCallback(
        new ResultCallback<Snapshots.OpenSnapshotResult>() {
          @Override
          public void onResult(Snapshots.OpenSnapshotResult res) {
            int status = res.getStatus().getStatusCode();
            byte[] data = new byte[0];
            if (status == GamesStatusCodes.STATUS_OK) {
              try {
                data = onStateConflictBehaviour(localSnapshot.getSnapshotContents().readFully(),
                    res.getSnapshot().getSnapshotContents().readFully());
              } catch (IOException e) {
                e.printStackTrace();
              }
              // Write data
              res.getSnapshot().getSnapshotContents().writeBytes(data);

              // Commit and close
              Games.Snapshots.commitAndClose(getApiClient(), res.getSnapshot(), SnapshotMetadataChange.EMPTY_CHANGE);
            }
          }
        }
    );
  }

  private void onStateLoaded(Snapshots.OpenSnapshotResult result) {
    if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
      try {
        onStateLoadedBehaviour(result.getSnapshot().getSnapshotContents().readFully());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  @Override
  public void onInvitationReceived(Invitation invitation) {
    if (!shouldShowInvitationDialog()) {
      Games.RealTimeMultiplayer.declineInvitation(getApiClient(), invitation.getInvitationId());
    } else {
      gserviceInvitationReceived(invitation.getInviter().getIconImageUri(), invitation.getInviter().getDisplayName(), invitation.getInvitationId());
    }
    gHelper.clearInvitation();
  }

  @Override
  public void onInvitationRemoved(String arg0) {
    if (invitationDialog != null)
      invitationDialog.dismiss();
    hideProgressDialog();
    onErrorBehaviour("Opponent canceled invitation");
  }

  @Override
  public void onJoinedRoom(int arg0, Room room) {
    if (room == null) {
      hideProgressDialog();
      onErrorBehaviour("Invalid invitation");
    } else {
      updateRoom(room);
      gConnecting = true;
    }
  }

  @Override
  public void onLeftRoom(int arg0, String arg1) {
    onLeftRoomBehaviour();
  }

  @Override
  public void onRoomConnected(int arg0, Room room) {
    hideProgressDialog();
    updateRoom(room);
    onRoomConnectedBehaviour();
    gConnecting = false;
    showProgressDialog(true);
  }

  @Override
  public void onRoomCreated(int statusCode, Room room) {
    if (statusCode != GServiceClient.STATUS_OK) {
      hideProgressDialog();
      onErrorBehaviour("Unknown error");
      return;
    }
    mRoomId = room.getRoomId();
    meSentInvitation = true;
    Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(getApiClient(), room, Integer.MAX_VALUE);
    startActivityForResult(i, RC_WAITING_ROOM);
  }


  @Override
  public void onConnectedToRoom(Room room) {
    if (gServiceGameCanceled) {
      gServiceGameCanceled = false;
      Games.RealTimeMultiplayer.leave(getApiClient(), this, room.getRoomId());
    }

    mParticipants = room.getParticipants();
    room.getParticipantId(Games.Players.getCurrentPlayerId(getApiClient()));
    mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(getApiClient()));
    updateRoom(room);
    String me, opponent, opponent_player_id;


    String sRdm = new BigInteger(130, rnd).toString(32);
    if (mParticipants.get(0).getParticipantId() == mMyId) {
      me = mParticipants.get(0).getDisplayName();
      opponent = mParticipants.get(1).getDisplayName();
      GnuBackgammon.Instance.nativeFunctions.loadIconImages(
          room.getParticipants().get(0).getIconImageUri(),
          room.getParticipants().get(1).getIconImageUri()
          );
      if (mParticipants.get(1).getPlayer() == null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(1).getPlayer().getPlayerId();
    } else {
      me = mParticipants.get(1).getDisplayName();
      opponent = mParticipants.get(0).getDisplayName();
      GnuBackgammon.Instance.nativeFunctions.loadIconImages(
          room.getParticipants().get(1).getIconImageUri(),
          room.getParticipants().get(0).getIconImageUri()
          );
      if (mParticipants.get(0).getPlayer() == null)
        opponent_player_id = sRdm;
      else
        opponent_player_id = mParticipants.get(0).getPlayer().getPlayerId();
    }

    GnuBackgammon.Instance.gameScreen.updatePInfo(opponent, me);
    if (meSentInvitation)
      AchievementsManager.getInstance().checkSocialAchievements(opponent_player_id);
  }
  @Override
  public void onDisconnectedFromRoom(Room room) {}

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
    if (gConnecting) {
      hideProgressDialog();
      _gserviceResetRoom();
      onErrorBehaviour("Error: peer left the room");
      updateRoom(room);
    }
  }

  @Override
  public void onPeersConnected(Room room, List<String> arg1) {
    updateRoom(room);
  }

  @Override
  public void onPeersDisconnected(Room room, List<String> arg1) {
    GServiceClient.getInstance().leaveRoom(0);
    onLeftRoomBehaviour();
    hideProgressDialog();
  }

  @Override
  public void onRoomAutoMatching(Room room) {
    updateRoom(room);
  }

  @Override
  public void onRoomConnecting(Room room) {
    updateRoom(room);
  }

  @Override
  public void onRealTimeMessageReceived(RealTimeMessage rtm) {
    byte[] buf = rtm.getMessageData();
    String s = new String(buf);
    onRTMessageReceivedBehaviour(s);
  }

  @Override
  public void onSignInFailed() {}

  @Override
  public void onSignInSucceeded() {
    //gserviceDeleteSnapshot();
    prefs.putBoolean("WANTS_GOOGLE_SIGNIN", true);
    prefs.flush();

    Games.Invitations.registerInvitationListener(getApiClient(), this);

    GnuBackgammon.out.println("===> LOADING SAVEDGAME");
    Games.Snapshots.open(getApiClient(), snapshotName, false).setResultCallback(
        new ResultCallback<Snapshots.OpenSnapshotResult>() {
          @Override
          public void onResult(Snapshots.OpenSnapshotResult result) {
            int status = result.getStatus().getStatusCode();
            switch (status) {
              case GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND:
                migrateFromAppState();
                break;
              case GamesStatusCodes.STATUS_OK:
                onStateLoaded(result);
                break;
              case GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT:
                onStateConflict(result);
                break;
            }
          }
        }
    );
    if (gHelper.hasInvitation()) {
      GnuBackgammon.Instance.invitationId = gHelper.getInvitationId();
      gHelper.clearInvitation();
      GnuBackgammon.Instance.setFSM("MENU_FSM");
    }
  }

  void gserviceInvitationReceived(final Uri imagesrc, final String username, final String invitationId) {
    GnuBackgammon.Instance.nativeFunctions.gserviceReset();
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();
    GnuBackgammon.fsm.state(MenuFSM.States.TWO_PLAYERS);

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_invitation, null);
        alert.setView(myView).setTitle("Invitation received").setCancelable(false).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Games.RealTimeMultiplayer.declineInvitation(getApiClient(), invitationId);
          }
        });
        alert.setPositiveButton("Accept", null);

        invitationDialog = alert.create();
        invitationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            TextView tv = (TextView)myView.findViewById(R.id.text);
            tv.setText(username + " wants to play with you...");
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.requestFocus();
            Button b = invitationDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                _gserviceAcceptInvitation(invitationId);
                invitationDialog.dismiss();
              }
            });
          }
        });

        ImageManager im = ImageManager.create(getApplicationContext());
        im.loadImage(new OnImageLoadedListener() {
          @Override
          public void onImageLoaded(Uri arg0, Drawable drawable, boolean arg2) {
            ImageView iv = ((ImageView)myView.findViewById(R.id.image));
            iv.setImageDrawable(drawable);
            if (android.os.Build.VERSION.SDK_INT >= 19) {
              invitationDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
              invitationDialog.show();
              invitationDialog.getWindow().getDecorView().setSystemUiVisibility(BaseGServiceApplication.this.getWindow().getDecorView().getSystemUiVisibility());
              invitationDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            } else {
              invitationDialog.show();
            }
          }
        }, imagesrc, R.drawable.gplayer);
      }
    });
  }


  public void _gserviceAcceptInvitation(String invitationId) {
    RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
    roomConfigBuilder.setInvitationIdToAccept(invitationId);
    roomConfigBuilder.setMessageReceivedListener(this);
    roomConfigBuilder.setRoomStatusUpdateListener(this);
    _gserviceResetRoom();
    Games.RealTimeMultiplayer.join(getApiClient(), roomConfigBuilder.build());
    showProgressDialog();
  }

  private void updateRoom(Room room) {
    try {
      mRoomId = room.getRoomId();
      mParticipants = room.getParticipants();
    } catch (Exception e) {
      GnuBackgammon.out.println("===> ECCEZIONE SU ROOM!");
    }
  }

  public void showProgressDialog() {
    showProgressDialog(false);
  }

  public void showProgressDialog(final boolean cancel_button) {
    runOnUiThread(new Runnable() {
      @SuppressWarnings("unused")
      @Override
      public void run() {
        if (mProgressDialog == null) {
          if (BaseGServiceApplication.this == null)
            return;
          mProgressDialog = new ProgressDialog(BaseGServiceApplication.this);
        }
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        if (cancel_button) {
          mProgressDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              gServiceGameCanceled = true;
              mProgressDialog.dismiss();
              onDismissProgressDialogBehaviour();
            }
          });
        }

        if (android.os.Build.VERSION.SDK_INT >= 19) {
          mProgressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
          mProgressDialog.show();
          mProgressDialog.getWindow().getDecorView().setSystemUiVisibility(BaseGServiceApplication.this.getWindow().getDecorView().getSystemUiVisibility());
          mProgressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
          mProgressDialog.show();
        }
      }
    });
  }

  public void hideProgressDialog() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (mProgressDialog != null) {
          mProgressDialog.dismiss();
          mProgressDialog = null;
        }
      }
    });
  }

  @Override
  public void onP2PConnected(String arg0) {}

  @Override
  public void onP2PDisconnected(String arg0) {}


  @Override
  protected void onCreate(Bundle b) {
    super.onCreate(b);
    prefs = Gdx.app.getPreferences("GameOptions");
    gHelper = new GServiceGameHelper(this, GServiceGameHelper.CLIENT_SNAPSHOT | GServiceGameHelper.CLIENT_APPSTATE | GServiceGameHelper.CLIENT_GAMES);
    gHelper.setup(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mRoomId != null) {
      onLeftRoomBehaviour();
    }
    gHelper.onStop();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    gHelper.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case RC_SELECT_PLAYERS:
        if (resultCode == RESULT_OK) {
          showProgressDialog();
          Bundle autoMatchCriteria = null;
          int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
          int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
          if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
          }
          final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

          // create the room
          RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
          rtmConfigBuilder.addPlayersToInvite(invitees);
          rtmConfigBuilder.setMessageReceivedListener(this);
          rtmConfigBuilder.setRoomStatusUpdateListener(this);
          if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
          }
          Games.RealTimeMultiplayer.create(getApiClient(), rtmConfigBuilder.build());
        }
        break;

      case RC_WAITING_ROOM:
        if (resultCode != RESULT_OK)
          _gserviceResetRoom();
        else {
          if (mRoomId != null) {
            showProgressDialog(true);
          } else {
            UIDialog.getFlashDialog(Events.NOOP, "Opponent abandoned game");
          }
        }
        break;

      case RC_RESOLVE: // RETURN FROM GMS LOGIN
        if (resultCode != RESULT_OK) {
          // PRIMO LOGIN RIFIUTATO
          prefs.putBoolean("WANTS_GOOGLE_SIGNIN", false);
          prefs.flush();
          onSignInFailed();
        }
        break;
    }

  }

  public void _gserviceResetRoom() {
    onResetRoomBehaviour();
    gConnecting = false;
    meSentInvitation = false;
    if (mRoomId != null) {
      Games.RealTimeMultiplayer.leave(getApiClient(), this, mRoomId);
      mRoomId = null;
    }
    gServiceGameCanceled = false;
  }

  protected void _gserviceSignIn() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        gHelper.beginUserInitiatedSignIn();
      }
    });
  }

  public void gserviceGetSigninDialog(final int from) {
    final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    final LayoutInflater inflater = this.getLayoutInflater();

    runOnUiThread(new Runnable() {
      @SuppressLint("NewApi")
      @Override
      public void run() {
        final View myView = inflater.inflate(R.layout.dialog_gplus, null);
        alert.setView(myView).setTitle("Signin").setCancelable(true);
        final AlertDialog d = alert.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
          @Override
          public void onShow(DialogInterface arg0) {
            String msg = "";
            TextView v = (TextView)d.findViewById(R.id.login_text);
            msg = "Please sign in on Google Play Games to enable this feature";
            v.setText(msg);
            com.google.android.gms.common.SignInButton b = (com.google.android.gms.common.SignInButton)d.findViewById(R.id.sign_in_button);
            b.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                d.dismiss();
                trySignIn(from);
              }
            });
          }
        });
        if (android.os.Build.VERSION.SDK_INT >= 19) {
          d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
          d.show();
          d.getWindow().getDecorView().setSystemUiVisibility(BaseGServiceApplication.this.getWindow().getDecorView().getSystemUiVisibility());
          d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        } else {
          d.show();
        }
      }
    });
  }


  protected void rcSelectPlayers() {
    showProgressDialog();
    Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1);
    startActivityForResult(intent, RC_SELECT_PLAYERS);
  }

  protected void trySignIn(final int from) {
    if ((from == FROM_ACHIEVEMENTS) || (from == FROM_SCOREBOARDS) || (from == FROM_NEWGAME)) {
      gHelper.setListener(new GServiceGameHelper.GameHelperListener() {
        @Override
        public void onSignInSucceeded() {
          prefs.putBoolean("WANTS_GOOGLE_SIGNIN", true);
          prefs.flush();

          gHelper.setListener(BaseGServiceApplication.this);
          if (from == FROM_ACHIEVEMENTS)
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_ACHIEVEMENTS);
          else if (from == FROM_SCOREBOARDS)
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_LEADERBOARD);
          else if (from == FROM_NEWGAME)
            rcSelectPlayers();
        }
        @Override
        public void onSignInFailed() {
          onErrorBehaviour("Login error");
        }
      });
    }
    _gserviceSignIn();
  }
  public GoogleApiClient getApiClient() {
    return gHelper.getApiClient();
  }

  public void gserviceDeleteSnapshot() {
    GnuBackgammon.out.println("GSERVICE: Going to delete savedgames");
    Games.Snapshots.open(getApiClient(), snapshotName, false).setResultCallback(
        new ResultCallback<Snapshots.OpenSnapshotResult>() {
          @Override
          public void onResult(Snapshots.OpenSnapshotResult res) {
            if (res.getStatus().getStatusCode() != GamesStatusCodes.STATUS_SNAPSHOT_NOT_FOUND) {
              Games.Snapshots.delete(getApiClient(), res.getSnapshot().getMetadata());
              GnuBackgammon.out.println("GSERVICE: SavedGames DELETED!");
            }
          }
        }
    );
  }
}
