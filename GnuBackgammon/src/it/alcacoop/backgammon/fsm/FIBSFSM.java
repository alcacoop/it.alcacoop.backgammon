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
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.FibsBoard;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;


public class FIBSFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private int[] hmoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  public int hnmove = 0;

  public enum States implements State {

    WATCH_TURN {
      @Override
      public void enterState(Context ctx) {
        ctx.board().showArrow();
        super.enterState(ctx);
      }
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          
          case FIBS_BOARD:
            FibsBoard b = (FibsBoard)params;
            if (b.dices[0]!=0)
              ctx.board().animateDices(b.dices[0], b.dices[1]);
              //AICalls.GenerateMoves(ctx.board(), b.dices[0], b.dices[1]);
            break;
          
          case EVALUATE_BEST_MOVE:
            System.out.println("PERFORMING MOVES...");
            int moves[] = (int[])params;
            if(moves[0] == -1) {
              //ctx.state(DIALOG_HANDLER);
              UIDialog.getFlashDialog(
                Events.NO_MORE_MOVES, 
                "Your opponent has no legal moves",
                0.82f,
                ctx.board().getStage());
            } else {
              ctx.board().setMoves(moves);
            }
            break;
            
          case PERFORMED_MOVE:
            ctx.board().updatePInfo();
            ctx.board().performNextMove();
            break;
            
          case NO_MORE_MOVES:
            ctx.state(States.CHECK_WIN);
            ctx.board().dices.clear();
            break;          

          default:
            return false;
        }
        return true;
      }
    },
    
        
    CHECK_WIN {
      public void enterState(Context ctx) {
        for (int i=0;i<8;i++)
          ((FIBSFSM)GnuBackgammon.fsm).hmoves[i] = -1;
        ((FIBSFSM)GnuBackgammon.fsm).hnmove = 0;
        
        if (ctx.board().gameFinished()) {
          ctx.state(CHECK_END_MATCH);
        } else {
          ctx.state(States.WATCH_TURN);
          ctx.board().switchTurn();
        }
      }
    },
    
    CHECK_END_MATCH {
      @Override
      public void enterState(Context ctx) {
        int game_score = 0;
        if(MatchState.resignValue == 0) {
          game_score = MatchState.nCube*ctx.board().gameScore(MatchState.fMove==1?0:1);
        } else {
          game_score = MatchState.resignValue * MatchState.nCube;
        }
        
        if(MatchState.fMove == 0) 
          MatchState.SetMatchScore(MatchState.anScore[1], MatchState.anScore[MatchState.fMove]+game_score);
        else 
          MatchState.SetMatchScore(MatchState.anScore[MatchState.fMove]+game_score, MatchState.anScore[0]);
        
        String matchProgress = " (match to "+MatchState.nMatchTo+" in progress)";
        if (MatchState.anScore[MatchState.fMove]>=MatchState.nMatchTo) {
          matchProgress = " (match to "+MatchState.nMatchTo+" finished)";
        }
        
        String gameString = "Your opponent won "+game_score+" point";
        String score1 = "";
        String score2 = "";
        if (MatchState.matchType == 0) {
          if(MatchState.fMove==1) {
            gameString = "CPU WON "+game_score+" POINT!";
          } else {
            gameString = "YOU WON "+game_score+" POINT!";
          }
          score1 = "CPU: " + MatchState.anScore[1];
          score2 = "YOU: " + MatchState.anScore[0];
        } else {
          score1 = "Player1: " + MatchState.anScore[1];
          score2 = "Player2: " + MatchState.anScore[0];
        }
        UIDialog.getEndGameDialog(Events.CONTINUE, matchProgress, gameString, score1, score2, 0.82f, ctx.board().getStage());
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.CONTINUE) {
          if (MatchState.anScore[MatchState.fMove]>=MatchState.nMatchTo) { //MATCH FINISHED: GO TO MAIN MENU
            GnuBackgammon.Instance.setFSM("MENU_FSM");
          } else {
            ctx.state(OPENING_ROLL);
          }
        }
        return false;
      }
    },

    
    
    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        MatchState.UpdateMSCubeInfo(1, -1);
        processEvent(ctx, Events.NOOP, null);
        GnuBackgammon.Instance.prefs.putString("SHOWHELP", "No");
        GnuBackgammon.Instance.prefs.flush();
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case NOOP: 
          break;
        
        case FIBS_BOARD:
          FibsBoard b = (FibsBoard)params;
          
          for (int i=0;i<25;i++) {
            MatchState.board[4][i] = b.board[0][i];
            MatchState.board[5][i] = b.board[1][i];
          }
          ctx.board().initBoard(2);
          GameScreen gs = (GameScreen)GnuBackgammon.Instance.currentScreen;
          gs.pInfo[1].setName(b.p1); //PRIMO NOME => NERO
          gs.pInfo[0].setName(b.p2);
          ctx.board().updatePInfo();

          AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
          
          if (b.turn == b.color) {
            MatchState.SetGameTurn(0, 0);
          } else {
            MatchState.SetGameTurn(1, 1);
          }
          
          if (b.dices[0]!=0)
            ctx.board().animateDices(b.dices[0], b.dices[1]);
          ctx.state(WATCH_TURN);
          break;
          
        default:
          return false;
        }
        return true;
      }
    },

    
    
    
    
    STOPPED {
      @Override
      public void enterState(Context ctx) {
      }
    };

    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}

  };


  public FIBSFSM(Board _board) {
    board = _board;
  }

  public void start() {
    GnuBackgammon.Instance.board.initBoard(2);
    GnuBackgammon.Instance.board.dices.clear();
    GnuBackgammon.Instance.goToScreen(4);
    hnmove = 0;
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }
}