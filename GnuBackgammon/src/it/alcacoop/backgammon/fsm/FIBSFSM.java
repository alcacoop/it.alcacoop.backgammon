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

import com.buckosoft.fibs.BuckoFIBS.CommandDispatcher.Command;

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
  public int direction = 0;

  public enum States implements State {

    REMOTE_TURN {

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case FIBS_BOARD:
          FibsBoard b = (FibsBoard)params;
          if (b.dices[0]!=0) {
            ctx.board().rollDices(b.dices[0], b.dices[1]);
            System.out.println("NOW DICE... :)");
          } else {
            System.out.println("NO DICE... :(");
          }
          break;

        case FIBS_NOMOVES:
          //ctx.state(DIALOG_HANDLER);
          UIDialog.getFlashDialog(
              Events.NO_MORE_MOVES, 
              "Your opponent has no legal moves",
              0.82f,
              ctx.board().getStage());
          break;
          
        /* XXX
         * RACE CONDITION:
         * IL SERVER LANCIA I DADI, E SE NON HA MOSSE MANDA FIBS_MOVES (RECEIVE_MOVES) 
         * PRIMA CHE L'ANIMAZIONE DEI DADI SIA FINITA...
         * SOLUZIONE: ACCODARE (SYNC) I MESSAGGI IN ENTRATA 
         */
        case FIBS_MOVES: //QUI RICEVO LE MOSSE DAL SERVER...
          System.out.println("PERFORMING MOVES...");
          int moves[] = (int[])params;

          System.out.println("BOARD DICES: "+GnuBackgammon.Instance.board.dices.get().length);
          int _m[][] = new int[4][8];
          for (int i=0;i<4;i++)
            for (int j=0;j<8;j++)
              _m[i][j] = moves[j];
          ctx.board().availableMoves.setMoves(_m);
          
          ctx.board().setMoves(moves);
          break;

        case PERFORMED_MOVE:
          ctx.board().updatePInfo();
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



    LOCAL_TURN {
      @Override
      public void enterState(Context ctx) {
        Float left1 = GnuBackgammon.Instance.board.rollBtn.getX();
        Float left2 = GnuBackgammon.Instance.board.doubleBtn.getX();
        GnuBackgammon.Instance.board.rollBtn.setX(Math.max(left1, left2));
        GnuBackgammon.Instance.board.doubleBtn.setX(Math.min(left1, left2));
      }
      @Override
      public boolean processEvent(Context ctx, FIBSFSM.Events evt, Object params) {
        System.out.println("LOCAL EVENT: "+evt);
        switch (evt) {

        
        case FIBS_BOARD:
          FibsBoard b = (FibsBoard)params;
          ((FIBSFSM)GnuBackgammon.fsm).direction = b.direction;
          
          //if (b.dices[0]==0) break;
          //System.out.println("FSM - FIBSBOARD");
          
          if (b.dices[0]!=0) {
            System.out.println("HERE WE ARE!!");
            ctx.board().rollBtn.remove();
            ctx.board().doubleBtn.remove();
            ctx.board().animateDices(b.dices[0], b.dices[1],true);
          } else {
            ctx.board().addActor(ctx.board().rollBtn);
          }
          break;
        
        case DICES_ROLLED:
          System.out.println("FSM - DICESROLLED");
          ctx.board().rollBtn.remove();
          ctx.board().doubleBtn.remove();
          
          int dices[] = (int[])params;
          //ctx.board().dices.show(dices[0], dices[1], false);
          AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
          
          break;
          
        
        case GENERATE_MOVES:
          System.out.println("FSM - GENERATEMOVES");
          int moves[][] = (int[][])params;
          
          System.out.println("MOVES LENGTH: "+moves.length);

          if(moves.length>0) {
            ctx.board().availableMoves.setMoves((int[][])params);
          } else  {
            //ctx.state(DIALOG_HANDLER);
            UIDialog.getFlashDialog(
                Events.NO_MORE_MOVES, 
                "No legal moves available",
                0.82f,
                ctx.board().getStage());
          }
          break;

        case POINT_TOUCHED:
          if (GnuBackgammon.Instance.prefs.getString("AMOVES", "Tap").equals("Auto")) {
            int orig = (Integer)params;
            if ((orig==-1)||(ctx.board()._board[MatchState.fMove][orig]==0))
              break;
            int dest = ctx.board().getAutoDestination(orig);
            if (dest!=-2) {
              int m[] = {orig, dest, -1, -1, -1, -1, -1, -1};

              int idx = ((FIBSFSM)GnuBackgammon.fsm).hnmove;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx*2] = orig;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx*2+1] = dest;
              ((FIBSFSM)GnuBackgammon.fsm).hnmove++;

              ctx.board().availableMoves.dropDice(orig-dest);
              ctx.state(HUMAN_CHECKER_MOVING);
              ctx.board().humanMove(m);
            }
          } else {
            if (ctx.board().points.get((Integer)params).isTarget) { //MOVE CHECKER
              int origin = ctx.board().selected.boardX;
              int dest = (Integer)params;
              int m[] = {origin, dest, -1, -1, -1, -1, -1, -1};

              int idx = ((FIBSFSM)GnuBackgammon.fsm).hnmove;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx] = origin;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx+1] = dest;
              ((FIBSFSM)GnuBackgammon.fsm).hnmove++;

              ctx.state(HUMAN_CHECKER_MOVING);
              ctx.board().humanMove(m);
              ctx.board().availableMoves.dropDice(origin-dest);
            } else { //SELECT NEW CHECKER
              if ((Integer)params!=-1)
                ctx.board().selectChecker((Integer)params);
            }
          }
          break;

        case DICE_CLICKED:
          ctx.board().dices.clear();
          ctx.state(CHECK_WIN);
          break;

        default:
          return false;
        }
        
        return true;
      }
    },

    HUMAN_CHECKER_MOVING { //HERE ALL TOUCH EVENTS ARE IGNORED!
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

        case PERFORMED_MOVE:
          ctx.board().updatePInfo();
          ctx.state(LOCAL_TURN);
          break;

        default:
          return false;
        }
        return true;
      }
    },





    CHECK_WIN {
      public void enterState(Context ctx) {
        System.out.println("CHECKWIN!!");

        if (ctx.board().gameFinished()) {
          ctx.state(CHECK_END_MATCH);
        } else {
          if (MatchState.fTurn==1) {
            ctx.state(States.LOCAL_TURN);
            ctx.board().addActor(ctx.board().rollBtn);
          } else {
            ctx.state(States.REMOTE_TURN);
            String m = "";
            for (int i=0; i<4; i++) {
              if (((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i]==-1) break;
              m+=" ";
              if (((FIBSFSM)GnuBackgammon.fsm).direction==-1) {
                m+=(((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i]+1)+" ";
                m+=(((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i+1]+1);
              } else {
                m+=(24-((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i])+" ";
                m+=(24-((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i+1]);
              }
            }
            GnuBackgammon.commandDispatcher.dispatch(Command.SEND_MOVE, m);
          }
          ctx.board().switchTurn();
          for (int i=0;i<8;i++)
            ((FIBSFSM)GnuBackgammon.fsm).hmoves[i] = -1;
          ((FIBSFSM)GnuBackgammon.fsm).hnmove = 0;
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
            //ctx.state(OPENING_ROLL);
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
        System.out.println("CURRENT STATE: "+ctx.state());
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
          
          if (b.color==b.turn)
            ctx.state(LOCAL_TURN);
          else
            ctx.state(REMOTE_TURN);
          
          GnuBackgammon.fsm.processEvent(Events.FIBS_BOARD, b); //XXX
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
    GnuBackgammon.Instance.board.dices.clear();
    GnuBackgammon.Instance.board.initBoard(2);
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