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
import it.alcacoop.backgammon.layers.MainMenuScreen;
import it.alcacoop.backgammon.layers.MatchOptionsScreen;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.fibs.CommandDispatcher.Command;

import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;


// MENU FSM
public class MenuFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private static boolean accountCreated = false;
  private static Timer timer;

  public enum States implements State {

    MAIN_MENU {
      @Override
      public void enterState(Context ctx) {
        if (GnuBackgammon.Instance.currentScreen!=GnuBackgammon.Instance.menuScreen)
          GnuBackgammon.Instance.goToScreen(2);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          if (params.toString().equals("SINGLE PLAYER")) {
            MatchState.matchType = 0;
            if (!Gdx.files.absolute(GnuBackgammon.Instance.fname+"json").exists()) { //NO SAVED MATCH
              Gdx.files.absolute(GnuBackgammon.Instance.fname+"sgf").delete();
              ctx.state(States.MATCH_OPTIONS);
            } else { //SAVED MATCH PRESENT!
              UIDialog.getYesNoDialog(
                  Events.RESTORE_ANSWER, 
                  "Restore previous match?", 
                  ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            }
          }
          if (params.toString().equals("TWO PLAYERS")) {
            MatchState.matchType = 1;
            ctx.state(States.MATCH_OPTIONS);
          }
          if (params.toString().equals("FIBS")) {
            if (GnuBackgammon.Instance.nativeFunctions.isNetworkUp()) {
              MatchState.matchType = 2;
              ctx.state(States.FIBS);
            } else {
              UIDialog.getFlashDialog(
                Events.NOOP, 
                "Network is down - Multiplayer not available",
                0.9f,
                ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage()
              );
            }
          }
          if (params.toString().equals("STATISTICS")) {
          }
          if (params.toString().equals("OPTIONS")) {
            ctx.state(States.GAME_OPTIONS);
          }
          if (params.toString().equals("RATE IT!")) {
            if (!GnuBackgammon.Instance.isGNU)
              GnuBackgammon.Instance.nativeFunctions.openURL("https://play.google.com/store/apps/details?id=it.alcacoop.backgammon");
            else
              GnuBackgammon.Instance.nativeFunctions.openURL("https://play.google.com/store/apps/details?id=it.alcacoop.gnubackgammonmobile");
          }
          if (params.toString().equals("APPEARANCE")) {
            ctx.state(States.APPEARANCE);
          }
          return true;
          
        } else if (evt==Events.RESTORE_ANSWER) {
          if ((Boolean)params) {
            GnuBackgammon.Instance.setFSM("GAME_FSM");
          } else {
            Gdx.files.absolute(GnuBackgammon.Instance.fname+"json").delete();
            Gdx.files.absolute(GnuBackgammon.Instance.fname+"sgf").delete();
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
        if (evt==Events.BUTTON_CLICKED) {
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
        ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(true);
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
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            timer.cancel();
            if (MenuFSM.accountCreated) {
              GnuBackgammon.Instance.commandDispatcher.sendLogin(GnuBackgammon.Instance.FibsUsername, GnuBackgammon.Instance.FibsPassword);
              MenuFSM.accountCreated = false;
            } else {
              GnuBackgammon.Instance.nativeFunctions.fibsSignin();
            }
            break;
          
          case FIBS_ERROR:
            ctx.state(MAIN_MENU);
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            UIDialog.getFlashDialog(Events.NOOP, "Connection error..\nPlease retry later", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            break;
            
          case FIBS_CANCEL:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
            
          case FIBS_LOGIN_ERROR:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            GnuBackgammon.Instance.fibsPrefs.putString("username", "");
            GnuBackgammon.Instance.fibsPrefs.putString("password", "");
            GnuBackgammon.Instance.fibsPrefs.flush();
            UIDialog.getFlashDialog(Events.NOOP, "Authentication error...\nUser not known or wrong password", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
            
          case FIBS_LOGIN_OK:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            GnuBackgammon.Instance.fibsPrefs.putString("username", GnuBackgammon.Instance.FibsUsername);
            GnuBackgammon.Instance.fibsPrefs.putString("password", GnuBackgammon.Instance.FibsPassword);
            GnuBackgammon.Instance.fibsPrefs.flush();
            GnuBackgammon.Instance.setFSM("FIBS_FSM");
            break;
          
          case FIBS_NETWORK_ERROR:  
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            UIDialog.getFlashDialog(Events.NOOP, "Sorry.. a network error occurred", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
          
          case FIBS_ACCOUNT_PRESENT:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            UIDialog.getFlashDialog(Events.NOOP, "Please use another name:\n'"+GnuBackgammon.Instance.FibsUsername+"' is already used by someone else", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
            
          case FIBS_ACCOUNT_SPAM:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
            UIDialog.getFlashDialog(Events.NOOP, "Too much account created from your IP..\nAre you a spammer?", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;  
            
          case FIBS_ACCOUNT_OK:
            ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).setConnecting(false);
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
    
    
    MATCH_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(3);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          if (params.toString().equals("PLAY")) {
            if((MatchState.currentLevel.ordinal() >= 5)&&(MatchState.matchType==0)) {
              UIDialog.getYesNoDialog(
                  Events.LEVEL_ALERT, 
                  "AI Level choosed is very CPU intensive. \nAre you sure to proceed?", 
                  ((MatchOptionsScreen)GnuBackgammon.Instance.currentScreen).stage);
            } else
              GnuBackgammon.Instance.setFSM("GAME_FSM");
          }
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        } else if (evt == Events.LEVEL_ALERT) {
          if ((Boolean)params)
            GnuBackgammon.Instance.setFSM("GAME_FSM");
        }
        return false;
      }
    },

    APPEARANCE {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(7);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
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
      public void enterState(Context ctx) {
      }
    }; 

    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}

  };


  public MenuFSM(Board _board) {
    board = _board;
  }

  public void start() {
    state(States.MAIN_MENU);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }

}