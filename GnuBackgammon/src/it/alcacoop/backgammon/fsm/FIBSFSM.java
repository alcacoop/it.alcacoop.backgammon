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
import it.alcacoop.fibs.CommandDispatcher.Command;


public class FIBSFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private int[] hmoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  public int hnmove = 0;
  public int direction = 0;
  
  
  public enum States implements State {

    REMOTE_TURN {
      @Override
      public void exitState(Context ctx) {
        GnuBackgammon.Instance.board.dices.clear();
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        case FIBS_BOARD:
          FibsBoard b = (FibsBoard)params;
          if (b.dices[0]==0) { // NOY YET ROLLED.. 
            GnuBackgammon.Instance.fibs.pull();
          } else {
            AICalls.GenerateMoves(ctx.board(), b.dices[0], b.dices[1]);
            ctx.board().animateDices(b.dices[0], b.dices[1], true);
          }
          break;
        
        case DICES_ROLLED:
          GnuBackgammon.Instance.fibs.pull(); //I'M WAITING FIBS_MOVES OR FIBS_NOMOVES
          break;
          
        case FIBS_MOVES:
          int moves[] = (int[])params;
          System.out.println("\n\n***** ARRIVATE MOSSE: ");
          for (int i=0;i<4;i++)
            System.out.println(moves[2*i]+"/"+moves[2*i+1]);
          System.out.println("**** FINE MOSSE\n\n");
          if (moves[0]!=-1) ctx.board().setMoves(moves);
          break;

        case PERFORMED_MOVE:
          ctx.board().updatePInfo();
          ctx.board().performNextMove();
          break;

        case FIBS_NOMOVES:
          //ctx.state(DIALOG_HANDLER);
          UIDialog.getFlashDialog(
              Events.NO_MORE_MOVES, 
              "Your opponent has no legal moves",
              0.82f,
              ctx.board().getStage());
          break;
          
        case NO_MORE_MOVES: //END TURN
          ctx.state(States.SWITCH_TURN);
          break;
        
        case FIBS_MATCHOVER:
          ctx.state(MATCH_OVER);
          break;
          
        default:
          return false;
        }
        System.out.println("\n********** EVENTO: "+evt+" ***********\n");
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
        switch (evt) {
        
        case FIBS_BOARD:
          FibsBoard b = (FibsBoard)params;
          ((FIBSFSM)GnuBackgammon.fsm).direction = b.direction;
          System.out.println("\n\n**** VALORE DEI DADI:"+b.dices[0]+":"+b.dices[1]+"\n\n");
          if (b.dices[0]==0) {
            ctx.board().addActor(ctx.board().rollBtn);
          } else {
            ctx.board().rollBtn.remove();
            ctx.board().doubleBtn.remove();
            ctx.board().animateDices(b.dices[0], b.dices[1], true);
          }
          break;
          
        case ROLL_DICE: //RICHIESTA REMOTA ROLL
          GnuBackgammon.Instance.fibs.pull();
          GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SEND_ROLL);
          
          break;
        
        case DICES_ROLLED:
          System.out.println("FSM - DICESROLLED");
          ctx.board().rollBtn.remove();
          ctx.board().doubleBtn.remove();
          
          int dices[] = (int[])params;
          AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
          
          break;
          
        
        case GENERATE_MOVES:
          int moves[][] = (int[][])params;
          
          if ((moves!=null)&&(moves.length>0)) {
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

        case NO_MORE_MOVES:
        case DICE_CLICKED:
          ctx.board().dices.clear();
          ctx.state(SWITCH_TURN);
          break;
          
        case FIBS_MATCHOVER:
          ctx.state(MATCH_OVER);
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



    SWITCH_TURN {
      public void enterState(Context ctx) {
        if (MatchState.fTurn==1) {
          ctx.state(States.LOCAL_TURN);
          //ctx.board().addActor(ctx.board().rollBtn);
        } else {
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
          ctx.state(States.REMOTE_TURN);
          GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SEND_MOVE, m);
        }
        
        ctx.board().switchTurn();
        for (int i=0;i<8;i++)
          ((FIBSFSM)GnuBackgammon.fsm).hmoves[i] = -1;
        ((FIBSFSM)GnuBackgammon.fsm).hnmove = 0;
        GnuBackgammon.Instance.fibs.pull();
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
          MatchState.FibsDirection = b.direction;

          if (b.turn == b.color) {
            MatchState.SetGameTurn(0, 0);
          } else {
            MatchState.SetGameTurn(1, 1);
          }
          
          if (b.color==b.turn)
            ctx.state(LOCAL_TURN);
          else
            ctx.state(REMOTE_TURN);
          
          GnuBackgammon.Instance.fibs.post(Events.FIBS_BOARD, b);
          GnuBackgammon.Instance.fibs.pull();
          
          break;

        default:
          return false;
        }
        return true;
      }
    },


    MATCH_OVER {
      @Override
      public void enterState(Context ctx) {
        UIDialog.getFlashDialog(
            Events.STOPPED, 
            "Match terminated",
            0.82f,
            ctx.board().getStage());
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.STOPPED) {
          GnuBackgammon.Instance.setFSM("MENU_FSM");
        }
        return super.processEvent(ctx, evt, params);
      }
    },



    STOPPED {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.fibs.reset();
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
    GnuBackgammon.Instance.fibs.pull();
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }

}