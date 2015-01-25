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

package it.alcacoop.backgammon.fsm;


import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.gservice.GServiceMessages;
import it.alcacoop.backgammon.layers.DiceStatsScreen;
import it.alcacoop.backgammon.layers.GeneralStatsScreen;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.AILevels;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.stats.StatManager;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.ELORatingManager;
import it.alcacoop.fibs.CommandDispatcher.Command;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;


// MENU FSM
public class MenuFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private static boolean accountCreated = false;
  private static Timer timer;
  private static long waitTime;

  public enum States implements State {

    MAIN_MENU {
      @Override
      public void enterState(Context ctx) {
        if (GnuBackgammon.Instance.currentScreen != GnuBackgammon.Instance.menuScreen)
          GnuBackgammon.Instance.goToScreen(2);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.BUTTON_CLICKED) {
          GnuBackgammon.Instance.snd.playMoveStart();
          if (params.toString().equals("SINGLE PLAYER")) {
            MatchState.matchType = 0;
            if (!Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").exists()) { // NO SAVED MATCH
              Gdx.files.absolute(GnuBackgammon.Instance.fname + "sgf").delete();
              ctx.state(States.MATCH_OPTIONS);
            } else { // SAVED MATCH PRESENT!
              UIDialog.getYesNoDialog(Events.RESTORE_ANSWER, "Restore previous match?");
            }
          }
          if (params.toString().equals("TWO PLAYERS")) {
            ctx.state(States.TWO_PLAYERS);
          }
          if (params.toString().equals("OPTIONS")) {
            ctx.state(States.GAME_OPTIONS);
          }
          if (params.toString().equals("RATE IT!")) {
            GnuBackgammon.Instance.nativeFunctions.openURL("https://play.google.com/store/apps/details?id=it.alcacoop.backgammon");
          }
          if (params.toString().equals("LOOK")) {
            ctx.state(States.APPEARANCE);
          }
          if (params.toString().equals("STATS")) {
            ctx.state(States.GENERAL_STATISTICS);
          }
          return true;

        } else if (evt == Events.RESTORE_ANSWER) {
          if ((Boolean)params) {
            GnuBackgammon.Instance.setFSM("GAME_FSM");
          } else {
            Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").delete();
            Gdx.files.absolute(GnuBackgammon.Instance.fname + "sgf").delete();
            ctx.state(States.MATCH_OPTIONS);
          }
          return true;
        }
        return false;
      }
    },


    GAME_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(1);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.BUTTON_CLICKED) {
          GnuBackgammon.Instance.snd.playMoveStart();
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        }
        return false;
      }
    },


    FIBS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.twoplayersScreen.showConnecting("Connecting to server...");
        timer = new Timer();
        TimerTask task = new TimerTask() {
          @Override
          public void run() {
            GnuBackgammon.fsm.processEvent(Events.FIBS_ERROR, null);
          }
        };
        timer.schedule(task, 5000);
        GnuBackgammon.Instance.commandDispatcher.dispatch(Command.CONNECT_TO_SERVER);
        super.enterState(ctx);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case FIBS_CONNECTED:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            timer.cancel();
            timer.purge();
            if (MenuFSM.accountCreated) {
              GnuBackgammon.Instance.commandDispatcher.sendLogin(GnuBackgammon.Instance.FibsUsername, GnuBackgammon.Instance.FibsPassword);
              MenuFSM.accountCreated = false;
            } else {
              GnuBackgammon.Instance.nativeFunctions.fibsSignin();
            }
            break;

          case FIBS_ERROR:
            if (timer != null) {
              timer.cancel();
              timer.purge();
            }
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            UIDialog.getFlashDialog(Events.NOOP, "Connection error..\nPlease retry later");
            ctx.state(States.TWO_PLAYERS);
            break;

          case FIBS_CANCEL:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.TWO_PLAYERS);
            break;

          case FIBS_LOGIN_ERROR:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            if (GnuBackgammon.Instance.server.equals("fibs.com")) {
              GnuBackgammon.Instance.fibsPrefs.putString("fusername", "");
              GnuBackgammon.Instance.fibsPrefs.putString("fpassword", "");
            } else {
              GnuBackgammon.Instance.fibsPrefs.putString("tusername", "");
              GnuBackgammon.Instance.fibsPrefs.putString("tpassword", "");
            }
            GnuBackgammon.Instance.fibsPrefs.flush();
            GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.TWO_PLAYERS);
            UIDialog.getFlashDialog(Events.NOOP, "User not known or wrong password");
            break;

          case FIBS_LOGIN_OK:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            if (GnuBackgammon.Instance.server.equals("fibs.com")) {
              GnuBackgammon.Instance.fibsPrefs.putString("fusername", GnuBackgammon.Instance.FibsUsername);
              GnuBackgammon.Instance.fibsPrefs.putString("fpassword", GnuBackgammon.Instance.FibsPassword);
            } else {
              GnuBackgammon.Instance.fibsPrefs.putString("tusername", GnuBackgammon.Instance.FibsUsername);
              GnuBackgammon.Instance.fibsPrefs.putString("tpassword", GnuBackgammon.Instance.FibsPassword);
            }
            GnuBackgammon.Instance.fibsPrefs.flush();
            GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
            GnuBackgammon.Instance.setFSM("FIBS_FSM");
            break;

          case FIBS_NETWORK_ERROR:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            UIDialog.getFlashDialog(Events.NOOP, "Sorry.. a network error occurred");
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.TWO_PLAYERS);
            break;

          case FIBS_ACCOUNT_PRESENT:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            UIDialog.getFlashDialog(Events.NOOP, "Please use another name:\n'" + GnuBackgammon.Instance.FibsUsername + "' is already used by someone else");
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.TWO_PLAYERS);
            break;

          case FIBS_ACCOUNT_SPAM:
            GnuBackgammon.Instance.twoplayersScreen.hideConnecting();
            UIDialog.getFlashDialog(Events.NOOP, "Too much account created from your IP..\nAre you a spammer?");
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.TWO_PLAYERS);
            break;

          case FIBS_ACCOUNT_CREATED:
            GnuBackgammon.Instance.twoplayersScreen.showConnecting("Connecting to server...");
            MenuFSM.accountCreated = true;
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.CONNECT_TO_SERVER);
            break;

          default:
            return false;
        }
        return true;
      }

    },


    TWO_PLAYERS {
      @Override
      public void enterState(Context ctx) {
        if (GnuBackgammon.Instance.currentScreen != GnuBackgammon.Instance.twoplayersScreen)
          GnuBackgammon.Instance.goToScreen(9);
        if (GnuBackgammon.Instance.invitationId != "") {
          MatchState.matchType = 3;
          GnuBackgammon.Instance.nativeFunctions.gserviceAcceptInvitation(GnuBackgammon.Instance.invitationId);
          GnuBackgammon.Instance.invitationId = "";
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {

        if ((evt == Events.GSERVICE_LOGIN) && ((Boolean)params)) {
          GnuBackgammon.Instance.nativeFunctions.gserviceSignIn();
        }

        if (evt == Events.BUTTON_CLICKED) {
          GnuBackgammon.Instance.snd.playMoveStart();
          if (params.toString().equals("PLAY0")) {
            MatchState.matchType = 1;
            ctx.state(States.MATCH_OPTIONS);
          }
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          if ((params.toString().equals("PLAY1")) || (params.toString().equals("PLAY2"))) {
            if (GnuBackgammon.Instance.nativeFunctions.isNetworkUp()) {
              MatchState.matchType = 2;
              ctx.state(States.FIBS);
            } else {
              UIDialog.getFlashDialog(Events.NOOP, "Network is down - Multiplayer not available");
            }
          }
          if (params.toString().equals("PLAY3")) {
            if (GnuBackgammon.Instance.nativeFunctions.isNetworkUp()) {
              MatchState.matchType = 3;
              GnuBackgammon.Instance.nativeFunctions.gserviceReset();
              GnuBackgammon.Instance.nativeFunctions.gserviceStartRoom();
            } else {
              UIDialog.getFlashDialog(Events.NOOP, "Network is down - Multiplayer not available");
            }
          }
          return true;
        }
        return false;
      }

    },


    GSERVICE {
      @Override
      public void enterState(Context ctx) {
        MatchState.anScore[0] = 0;
        MatchState.anScore[1] = 0;
        MatchState.nMatchTo = 1;
        MatchState.SetCubeUse(1);
        MatchState.UpdateMSCubeInfo(1, -1);

        GServiceClient.getInstance().queue.pull(Events.GSERVICE_READY);
        GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_READY + "");
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {


          case GSERVICE_READY:
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_INIT_RATING);
            GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_READY + "");
            GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_INIT_RATING + " " + GnuBackgammon.Instance.optionPrefs.getString("multiboard", "0"));
            break;

          case GSERVICE_INIT_RATING:
            double opponentRating = (Double)params;
            ELORatingManager.getInstance().setRatings(opponentRating);

            Random gen = new Random();
            waitTime = gen.nextLong();
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_HANDSHAKE);
            GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_HANDSHAKE + " " + waitTime + " " + GnuBackgammon.Instance.nativeFunctions.getAppVersionCode());
            break;

          case GSERVICE_HANDSHAKE:
            long _params[] = (long[])params;
            long remoteWaitTime = _params[0];
            long remoteVersion = _params[1];

            if (remoteVersion < 270)
              GnuBackgammon.Instance.setFSM("OLD_GSERVICE_FSM");
            else
              GnuBackgammon.Instance.setFSM("GSERVICE_FSM");

            if (waitTime > remoteWaitTime) {
              int dices[] = { 0, 0 };
              while (dices[0] == dices[1])
                dices = AICalls.Locking.RollDice();
              int[] p = { (dices[0] > dices[1] ? 1 : 0), dices[0], dices[1] };

              GServiceClient.getInstance().queue.post(Events.GSERVICE_FIRSTROLL, p);
              GServiceClient.getInstance().sendMessage("4 " + (dices[0] > dices[1] ? 0 : 1) + " " + dices[0] + " " + dices[1]);
            }
            break;

          case GSERVICE_BYE:
            ctx.state(TWO_PLAYERS);
            break;

          default:
            return false;
        }
        return true;
      }
    },


    MATCH_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(3);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.BUTTON_CLICKED) {
          GnuBackgammon.Instance.snd.playMoveStart();
          if (params.toString().equals("PLAY")) {
            GnuBackgammon.Instance.setFSM("GAME_FSM");
            ELORatingManager.getInstance().setRatings(AILevels.getAILevelELO(MatchState.currentLevel));
          }
          if (params.toString().equals("BACK")) {
            if (MatchState.matchType == 0)
              ctx.state(States.MAIN_MENU);
            else
              ctx.state(States.TWO_PLAYERS);
          }
          return true;
        }
        return false;
      }
    },


    GENERAL_STATISTICS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(10);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            GnuBackgammon.Instance.snd.playMoveStart();
            if (params.toString().equals("BACK")) {
              ctx.state(States.MAIN_MENU);
            } else if (params.toString().equals("RESET")) {
              UIDialog.getYesNoDialog(Events.RESET_STATS, "Really reset game results statistics?");
            } else if (params.toString().equals("DICE STATS")) {
              ctx.state(DICE_STATISTICS);
            }
            break;

          case RESET_STATS:
            boolean resp = (Boolean)params;
            if (resp) {
              StatManager.getInstance().resetGameStats();
              ((GeneralStatsScreen)(GnuBackgammon.Instance.currentScreen)).initTable();
            }
            break;
          default:
            return false;
        }

        return true;
      }
    },

    DICE_STATISTICS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(11);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            GnuBackgammon.Instance.snd.playMoveStart();
            if (params.toString().equals("BACK")) {
              ctx.state(States.GENERAL_STATISTICS);
            } else if (params.toString().equals("RESET")) {
              UIDialog.getYesNoDialog(Events.RESET_STATS, "Really reset dice statistics?");
            }
            break;

          case RESET_STATS:
            boolean resp = (Boolean)params;
            if (resp) {
              StatManager.getInstance().resetRollStats();
              ((DiceStatsScreen)(GnuBackgammon.Instance.currentScreen)).initTable();
            }
            Gdx.graphics.setContinuousRendering(true);
            break;
          default:
            return false;
        }

        return true;
      }
    },

    APPEARANCE {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(7);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.BUTTON_CLICKED) {
          GnuBackgammon.Instance.snd.playMoveStart();
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        }
        return false;
      }
    },

    STOPPED {
      @Override
      public void enterState(Context ctx) {}
    };

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {
      return false;
    }

    public void enterState(Context ctx) {}

    public void exitState(Context ctx) {}

  };


  public MenuFSM(Board _board) {
    board = _board;
  }

  public void start() {
    if ((GnuBackgammon.Instance.currentScreen == GnuBackgammon.Instance.fibsScreen) || (GnuBackgammon.Instance.invitationId != "") || (MatchState.matchType == 1))
      state(States.TWO_PLAYERS);
    else
      state(States.MAIN_MENU);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }

}
