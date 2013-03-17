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


import com.badlogic.gdx.Gdx;
import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.layers.MainMenuScreen;
import it.alcacoop.backgammon.layers.MatchOptionsScreen;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.fibs.CommandDispatcher.Command;


// MENU FSM
public class MenuFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;

  public enum States implements State {

    MAIN_MENU {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(2);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          if (params.toString().equals("SINGLE PLAYER")) {
            MatchState.matchType = 0;
            if (!Gdx.files.absolute(GnuBackgammon.fname+"json").exists()) { //NO SAVED MATCHE
              Gdx.files.absolute(GnuBackgammon.fname+"sgf").delete();
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
              //GnuBackgammon.Instance.setFSM("FIBS_FSM");
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
            Gdx.files.absolute(GnuBackgammon.fname+"json").delete();
            Gdx.files.absolute(GnuBackgammon.fname+"sgf").delete();
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
        //GnuBackgammon.Instance.goToScreen(8);
        GnuBackgammon.Instance.commandDispatcher.dispatch(Command.CONNECT_TO_SERVER);
        super.enterState(ctx);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            if (params.toString().equals("BACK")) {
              GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
              ctx.state(States.MAIN_MENU);
            }
            break;
          
          case FIBS_CONNECTED:
            GnuBackgammon.Instance.nativeFunctions.fibsSignin();
            break;
          
          case FIBS_CANCEL:
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
            
          case FIBS_LOGIN_ERROR:
            UIDialog.getFlashDialog(Events.NOOP, "Authentication error: wrong username or password", 0.90f, ((MainMenuScreen)GnuBackgammon.Instance.currentScreen).getStage());
            GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SHUTTING_DOWN);
            ctx.state(States.MAIN_MENU);
            break;
            
          case FIBS_LOGIN_OK:
            GnuBackgammon.Instance.goToScreen(8);
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