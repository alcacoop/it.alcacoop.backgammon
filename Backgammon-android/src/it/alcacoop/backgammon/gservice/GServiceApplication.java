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
 #  FOUR IN A LINE MOBILE is distributed in the hope that it      #
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

import it.alcacoop.backgammon.GServiceInterface;
import it.alcacoop.backgammon.utils.AppDataManager;
import android.content.Intent;

import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.Participant;

public abstract class GServiceApplication extends BaseGServiceApplication implements GServiceInterface {

  @Override
  public String gservicePendingNotificationAreaInvitation() {
    String tmp = invitationId;
    invitationId = "";
    return tmp;
  };

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
      showProgressDialog();
      Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(getApiClient(), 1, 1);
      startActivityForResult(intent, RC_SELECT_PLAYERS);
    } else {
      gserviceGetSigninDialog(-1);
    }
  }

  @Override
  public void gserviceAcceptInvitation(String invitationId) {
    _gserviceAcceptInvitation(invitationId);
  }

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {
    System.out.print("==> SENDING.. " + msg);
    if ((mRoomId == null) || (mRoomId == "")) {
      System.out.println("KO!");
      GServiceClient.getInstance().leaveRoom(GServiceClient.STATUS_NETWORK_ERROR_OPERATION_FAILED);
    } else {
      System.out.println("OK!");
      for (Participant p : mParticipants) {
        System.out.println("==> PROVO A INVIARE A: " + p.getParticipantId());
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
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false))
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
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn()))
      return;

    Games.Achievements.incrementImmediate(getApiClient(), achievement_id, increment);
  }


  @Override
  public void gserviceUnlockAchievement(String achievement_id) {
    if (achievement_id == null || achievement_id.equals("") || achievement_id == "")
      return;
    if (!prefs.getBoolean("ALREADY_SIGNEDIN", false) || (!gHelper.isSignedIn()))
      return;

    Games.Achievements.unlockImmediate(getApiClient(), achievement_id);
  }

  @Override
  public void gserviceUpdateState() {
    if (gHelper.isSignedIn()) {
      AppStateManager.update(getApiClient(), APP_DATA_KEY, AppDataManager.getInstance().getBytes());
    }
  }

  /*
  private void gserviceDeleteAppState() {
    if (gHelper.isSignedIn()) {
      AppStateManager.delete(getApiClient(), APP_DATA_KEY).setResultCallback(new ResultCallback<AppStateManager.StateDeletedResult>() {
        @Override
        public void onResult(StateDeletedResult arg0) {
          System.out.println("GSERVICE STATE DELETED");
        }
      });
    }
  }
  */


}
