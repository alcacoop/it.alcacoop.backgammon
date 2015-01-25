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

import com.badlogic.gdx.Gdx;

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
  public int[] greedyMoves = { -1, -1, -1, -1, -1, -1, -1, -1 };
  public int[] hmoves = { -1, -1, -1, -1, -1, -1, -1, -1 };
  public int hnmove = 0;
  public boolean helpShown = false;

  public enum Events {
    NOOP,

    GSERVICE_LOGIN,
    GSERVICE_CONNECTED,
    GSERVICE_READY,
    GSERVICE_INIT_RATING, // params={rating}
    GSERVICE_HANDSHAKE,
    GSERVICE_FIRSTROLL, // params={turn, dice0, dice1}
    GSERVICE_ROLL, // params={dice0, dice1}
    GSERVICE_DOUBLE,
    GSERVICE_ACCEPT,
    GSERVICE_MOVES, // params={-1,-1,-1,-1,-1,-1,-1,-1}
    GSERVICE_BOARD,
    GSERVICE_BOARD_SYNCED,
    GSERVICE_CHATMSG,
    GSERVICE_ERROR,
    GSERVICE_ABANDON, // params: 0=abandon,1=resign_single,2=resign_gammon,3=resign_bg
    GSERVICE_BYE,
    GSERVICE_RETURN_GAME,
    GSERVICE_PLAY_AGAIN,

    FIBS_ERROR,
    FIBS_ACCOUNT_CREATED,
    FIBS_ACCOUNT_PRESENT,
    FIBS_ACCOUNT_SPAM,
    FIBS_WHO_END,
    FIBS_CANCEL,
    FIBS_CONNECTED,
    FIBS_DISCONNECT,
    FIBS_LOGIN_OK,
    FIBS_LOGIN_ERROR,
    FIBS_NETWORK_ERROR,
    FIBS_PLAYER_CHANGED,
    FIBS_PLAYER_LOGOUT,
    FIBS_PLAYER_LOGIN,
    FIBS_INVITE_RECEIVED,
    FIBS_INVITE_SENDED,
    FIBS_INVITE_DECLINED,
    FIBS_START_GAME,
    FIBS_FIRSTROLL,
    FIBS_RESUMEGAME,
    FIBS_BOARD,
    FIBS_BOARD_SYNCED,
    FIBS_MOVES,
    FIBS_OPPONENT_ROLLS,
    FIBS_YOU_ROLL,
    FIBS_ABANDON_GAME,
    FIBS_RESIGN_REQUEST,
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
    GREEDY_MOVE,
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
    RESTORE_ANSWER,
    RESET_STATS
  }

  public enum States implements State {
    STOPPED {};

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {
      return false;
    }

    public void enterState(Context ctx) {}

    public void exitState(Context ctx) {}
  };

  public State currentState;
  public State previousState;


  public void start() {
    for (int i = 0; i < 8; i++)
      hmoves[i] = -1;
    hnmove = 0;
  }

  public void stop() {
    state(States.STOPPED);
  }

  public void restart() {
    stop();
    start();
  }

  public void processEvent(final Events evt, final Object params) {
    final BaseFSM ctx = this;
    // GnuBackgammon.out.println("PROCESS " + evt + " ON " + state());
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        state().processEvent(ctx, evt, params);
      }
    });
  }

  public State state() {
    return currentState;
  }

  public void back() {
    if (previousState != null)
      state(previousState);
  }

  public void state(final State state) {
    // GnuBackgammon.out.println("---> +++ ENQUEUE ST " + state + ": " + Thread.currentThread().getName());
    final Context _ctx = this;
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        // GnuBackgammon.out.println("---> +++ EXECUTE ST " + state + ": " + Thread.currentThread().getName());
        // GnuBackgammon.out.println(" FSM ---> MOVE FROM " + currentState + " TO " + state);
        if (currentState != null)
          currentState.exitState(_ctx);
        previousState = currentState;
        currentState = state;
        if (currentState != null)
          currentState.enterState(_ctx);
      }
    });

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
