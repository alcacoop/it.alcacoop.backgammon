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

import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

// SIMULATED GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

interface Context {
  Board board();
  int getMoves();
  void setMoves(int n);
  State state();
  void state(State state);
}

interface State {
  boolean processEvent(Context ctx, Events evt, Object params);
  void enterState(Context ctx);
  void exitState(Context ctx);
}


// MAIN FSM
public class BaseFSM implements Context {

  private int nMoves; 

  public enum Events {
    NOOP,
    
    FIBS_ERROR,
    
    FIBS_ACCOUNT_OK,
    FIBS_ACCOUNT_PRESENT,
    FIBS_ACCOUNT_SPAM,
    
    FIBS_CANCEL,
    FIBS_CONNECTED,
    FIBS_DISCONNECT,
    FIBS_LOGIN_OK,
    FIBS_LOGIN_ERROR,
    FIBS_NETWORK_ERROR,
    FIBS_PLAYER_CHANGED,
    FIBS_PLAYER_LOGOUT,
    
    FIBS_BOARD,
    FIBS_MOVES,
    FIBS_NOMOVES,
    FIBS_ROLLS,
    FIBS_MATCHOVER,
    
    ACCEPT_DOUBLE,
    ACCEPT_RESIGN,
    GET_RESIGN_VALUE,
    ASK_FOR_DOUBLING,
    ASK_FOR_RESIGNATION,
    EVALUATE_BEST_MOVE,
    INITIALIZE_ENVIRONMENT,
    ROLL_DICE,
    DICES_ROLLED,
    SET_BOARD,
    SET_GAME_TURN,
    SET_MATCH_SCORE,
    SET_MATCH_TO,
    UPDATE_MS_CUBEINFO,
    PERFORMED_MOVE,
    NO_MORE_MOVES,
    POINT_TOUCHED,
    GENERATE_MOVES,
    DICE_CLICKED,
    STARTING_SIMULATION,
    START_GAME,
    SIMULATED_TURN,
    BUTTON_CLICKED,
    CHECKER_RESETTED,
    DOUBLING_RESPONSE,
    CONTINUE,
    STOPPED,
    DOUBLE_REQUEST,
    HUMAN_DOUBLE_RESPONSE,
    CPU_DOUBLE_ACCEPTED,
    CPU_DOUBLE_NOT_ACCEPTED,
    SHOW_DOUBLE_DIALOG,
    CPU_RESIGNED,
    HUMAN_RESIGNED, 
    ABANDON_MATCH, 
    LEVEL_ALERT, 
    RESTORE_ANSWER
  }

  public enum States implements State {
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

  public State currentState;
  public State previousState;


  public void start() {
  }

  public void stop() {
    state(States.STOPPED);
  }

  public void restart() {
    stop();
    start();
  }

  public boolean processEvent(Events evt, Object params) {
    //System.out.println("EVENT: "+evt+" ON: "+currentState);
    boolean res = state().processEvent(this, evt, params);
    return res;
  }

  public State state() {
    return currentState;
  }
  
  public void back() {
    if(previousState != null)
      state(previousState);
  }

  public void state(State state) {
    //System.out.println("CHANGING STATE FROM: "+currentState+" TO: "+state);
    if(currentState != null)
      currentState.exitState(this);
    previousState = currentState;
    currentState = state;
    if(currentState != null)
      currentState.enterState(this);        
  }

  public boolean isStopped() {
    return currentState == States.STOPPED;
  }

  public Board board() {
    return null;
  }

  @Override
  public int getMoves() {
    return nMoves;
  }
  @Override
  public void setMoves(int n) {
    nMoves = n;
  }

}