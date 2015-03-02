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

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import it.alcacoop.backgammon.GServiceInterface;
import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.utils.AppDataManager;

public abstract class GServiceApplication extends BaseGServiceApplication implements GServiceInterface, RealTimeMultiplayer.ReliableMessageSentCallback {

  private ExecutorService senderExecutor;
  private Semaphore senderSemaphore;

  private class SendRunnable implements Runnable {
    String msg;

    public SendRunnable(String s) {
      msg = s;
    }
    @Override
    public void run() {
      try {
        senderSemaphore.acquire();
        _gserviceSendReliableRealTimeMessage(msg);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  public GServiceApplication() {
    senderSemaphore = new Semaphore(1, false);
    senderExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void gserviceReset() {
    GServiceClient.getInstance().reset();
    if (senderSemaphore.availablePermits() == 0)
      senderSemaphore.release();
    senderExecutor.shutdownNow();
    senderExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void onRealTimeMessageSent(int statusCode, int token, String recipientParticipantId) {
    if (statusCode != GServiceClient.STATUS_OK) {
      onLeaveRoomBehaviour(GServiceClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
      GServiceClient.getInstance().leaveRoom(GServiceClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    } else {
      GnuBackgammon.out.println("===> SENT!!");
    }
    if (senderSemaphore.availablePermits() == 0)
      senderSemaphore.release();
  }

  @Override
  public boolean gserviceIsSignedIn() {
    return gHelper.isSignedIn();
  }

  @Override
  public void gserviceSignIn() {
    _gserviceSignIn();
  }

  @Override
  public void gserviceStartRoom() {
    if (gHelper.isSignedIn()) {
      rcSelectPlayers();
    } else {
      gserviceGetSigninDialog(FROM_NEWGAME);
    }
  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
    _gserviceAcceptInvitation(invitationId);
  }


  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
    senderExecutor.execute(new SendRunnable(msg));
  }

  public void _gserviceSendReliableRealTimeMessage(String msg) {
    GnuBackgammon.out.print("===> SENDING.. " + msg);
    if ((mRoomId == null) || (mRoomId == "")) {
      GnuBackgammon.out.println("KO!");
      GServiceClient.getInstance().leaveRoom(GServiceClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    } else {
      GnuBackgammon.out.println(" OK!");
      for (Participant p : mParticipants) {
        if (p.getParticipantId().equals(mMyId))
          continue;
        if (p.getStatus() != Participant.STATUS_JOINED) {
          continue;
        }
        Games.RealTimeMultiplayer.sendReliableMessage(getApiClient(), this, msg.getBytes(), mRoomId, p.getParticipantId());
      }
    }
  }

  @Override
  public void gserviceResetRoom() {
    _gserviceResetRoom();
    GnuBackgammon.out.println("===> SENDING QUEUE RESETTED! " + senderSemaphore.availablePermits());
    if (senderSemaphore.availablePermits() == 0)
      senderSemaphore.release();

  }

  @Override
  public void gserviceOpenLeaderboards() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_LEADERBOARD);
    } else {
      gserviceGetSigninDialog(FROM_SCOREBOARDS);
    }
  }

  @Override
  public void gserviceOpenAchievements() {
    if (gserviceIsSignedIn()) {
      startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_ACHIEVEMENTS);
    } else {
      gserviceGetSigninDialog(FROM_ACHIEVEMENTS);
    }
  }

  @Override
  public void gserviceSubmitRating(long score, final String board_id) {
    if (!gHelper.isSignedIn())
      return;
    Games.Leaderboards.submitScoreImmediate(getApiClient(), board_id, score).setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
      @Override
      public void onResult(Leaderboards.SubmitScoreResult arg0) {
        onScoreSubmittedBehaviour(board_id, arg0);
      }
    });
  }

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id == "")
      return;
    if (!gHelper.isSignedIn())
      return;

    Games.Achievements.incrementImmediate(getApiClient(), achievement_id, increment);
  }


  @Override
  public void gserviceUnlockAchievement(String achievement_id) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id == "")
      return;
    if (!gHelper.isSignedIn())
      return;

    Games.Achievements.unlockImmediate(getApiClient(), achievement_id);
  }

  @Override
  public void gserviceUpdateState() {
    if (gHelper.isSignedIn()) {
      GnuBackgammon.out.println("===> SAVEDGAME UPDATE");

      Games.Snapshots.open(getApiClient(), snapshotName, true).setResultCallback(
          new ResultCallback<Snapshots.OpenSnapshotResult>() {
            @Override
            public void onResult(Snapshots.OpenSnapshotResult result) {
              if (result.getStatus().isSuccess()) {
                // Write data
                result.getSnapshot().getSnapshotContents().writeBytes(AppDataManager.getInstance().getBytes());
                // Commit and close
                Games.Snapshots.commitAndClose(getApiClient(), result.getSnapshot(), SnapshotMetadataChange.EMPTY_CHANGE);
              }
            }
          }
      );
    }
  }

  /*
  private void gserviceDeleteAppState() {
    if (gHelper.isSignedIn()) {
      GnuBackgammon.out.println(" ===> APPSTATE DELETION!");
      AppStateManager.delete(getApiClient(), APP_DATA_KEY).setResultCallback(new ResultCallback<AppStateManager.StateDeletedResult>() {
        @Override
        public void onResult(StateDeletedResult arg0) {
          GnuBackgammon.out.println("GSERVICE STATE DELETED");
        }
      });
    }
  }
  */


}
