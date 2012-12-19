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

package it.alcacoop.gnubackgammon.fsm;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.MatchState;


// MAIN FSM
public class SimulationFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;

  public enum States implements State {
    STARTING_SIMULATION {
      int resetted;
      
      public void enterState(Context ctx) {
        resetted = 0;
        ctx.setMoves(0);
        ctx.board().initBoard(2);
        MatchState.SetGameVariant(0);
        MatchState.SetGameTurn(0, 0);
        ctx.board().addAction(Actions.sequence(
            Actions.delay(0.8f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                GnuBackgammon.Instance.board.animate(0.6f);
              }
            }) 
        ));
      };
     
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.CHECKER_RESETTED) {
          resetted++;
          if (resetted==30) ctx.state(SIMULATED_TURN);
          return true;
        }
        return false;
      }
    },
    
    SIMULATED_TURN {
      @Override
      public void enterState(Context ctx) {
        ctx.board().switchTurn();
      }
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            ctx.setMoves(ctx.getMoves()+1);
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            ctx.board().rollDices();
            break;
          case DICES_ROLLED:
            int dices[] = (int[])params;
            AICalls.EvaluateBestMove(dices);
            break;
          case EVALUATE_BEST_MOVE:
            int moves[] = (int[])params;
            ctx.board().setMoves(moves);
            break;
          case PERFORMED_MOVE:
            ctx.board().performNextMove();
            break;
          case NO_MORE_MOVES:
            ctx.state(States.CHECK_WIN);
            break;
          default:
            return false;
        }
        return true;
      }
    },

    
    CHECK_WIN {
      public void enterState(Context ctx) {
        if (ctx.board().gameFinished()||ctx.getMoves()==10) {
          ctx.state(States.STARTING_SIMULATION);
        } else {
          ctx.state(States.SIMULATED_TURN);
        }
      }
    },
    
    STOPPED {
      @Override
      public void enterState(Context ctx) {
        ctx.board().initBoard();
      }
    };
    
    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}
    
  };


  public SimulationFSM(Board _board) {
    board = _board;
  }

  public void start() {
    MatchState.SetAILevel(AILevels.BEGINNER);
    state(States.STARTING_SIMULATION);
  }

  public void stop() {
    state(States.STOPPED);
  }
  
  public Board board() {
    return board;
  }
  
}