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
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;


public class GameFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private boolean helpShown = false;
  private int[] hmoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  public int hnmove = 0;

  public enum States implements State {

    CPU_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case SET_GAME_TURN:
          AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
          break;
          
        case SET_BOARD:
          AICalls.AskForResignation();
          break;
          
        case ASK_FOR_RESIGNATION:
          if((Integer)params > 0) {
            MatchState.resignValue = (Integer)params;
            String s = "Your opponent resigned a game";
            if ((Integer)params==2) s = "Your opponent resigned a gammon game";
            if ((Integer)params==3) s = "Your opponent resigned a backgammon game";
            ctx.state(DIALOG_HANDLER);
            UIDialog.getFlashDialog(Events.CPU_RESIGNED, s, 0.82f, ctx.board().getStage());
          } else { //ASKFORDOUBLING OR ROLL..
            if(MatchState.fCubeUse == 0) { //NO CUBE USE
              ctx.board().rollDices();
            } else {
              if (
                  ((MatchState.fCrawford==0)||(!MatchState.fCrafwordGame)) && //NOCR OR NO CRGAME
                  ((MatchState.fCubeOwner==-1)||(MatchState.fCubeOwner==1)) //AVAILABLE CUBE
                 ) {
                if (MatchState.nMatchTo-MatchState.anScore[1]>1)
                  AICalls.AskForDoubling();
                else //DEAD CUBE!!
                  ctx.board().rollDices();
              } else {
                ctx.board().rollDices();
              }
            }
          }
          break;
          
        case ASK_FOR_DOUBLING:
          if(Integer.parseInt(params.toString())==1) { // OPEN DOUBLING DIALOG
            GnuBackgammon.Instance.rec.addDoubleRequest(1);
            ctx.state(DIALOG_HANDLER);
            UIDialog.getYesNoDialog(
              Events.DOUBLING_RESPONSE, 
              "CPU is asking for double. Accept?", 0.82f, 
              ctx.board().getStage());
          } else {
            ctx.board().rollDices();
          }
          break;
          
        case DICES_ROLLED:
          int dices[] = (int[])params;
          GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], false);
          ctx.board().thinking(true);
          AICalls.EvaluateBestMove(dices);
          break;
          
        case EVALUATE_BEST_MOVE:
          ctx.board().thinking(false);
          int moves[] = (int[])params;
          int[] d = ctx.board().dices.get();
          GnuBackgammon.Instance.rec.addMove(1, d[0], d[1], moves);
          if(moves[0] == -1) {
            ctx.state(DIALOG_HANDLER);
            UIDialog.getFlashDialog(
              Events.NO_MORE_MOVES, 
              "Your opponent has no legal moves",
              0.82f,
              ctx.board().getStage());
          } else {
            moves = (int[])params;
            ctx.board().setMoves(moves);
          }
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


    HUMAN_TURN {
      @Override
      public boolean processEvent(Context ctx, GameFSM.Events evt, Object params) {
        switch (evt) {
        
        case SET_GAME_TURN:
          ctx.board().dices.clear();
          if (MatchState.fCubeUse==0) {
            ctx.board().rollDices();
          } else {
            if (
                ((MatchState.fCrawford==0)||(!MatchState.fCrafwordGame)) && //NOCR OR NO CRGAME
                ((MatchState.fCubeOwner==-1)||(MatchState.fCubeOwner==MatchState.fMove)) //AVAILABLE CUBE
               ) {
              ctx.board().addActor(ctx.board().rollBtn);
              ctx.board().addActor(ctx.board().doubleBtn);
            } else {
              ctx.board().rollDices();
            }
          }
          break;
          
        case DICES_ROLLED:
          ctx.board().removeActor(ctx.board().rollBtn);
          if(MatchState.fCubeUse == 1)
            ctx.board().removeActor(ctx.board().doubleBtn);
          int dices[] = (int[])params;
          GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], true);
          AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
          break;
          
        case GENERATE_MOVES:
          int moves[][] = (int[][])params;
          
          if(moves != null) {
            ctx.board().availableMoves.setMoves((int[][])params);
          } else { //player (human) has no more moves
            ctx.state(DIALOG_HANDLER);
            
            int[] d = ctx.board().dices.get();
            int[] m = {-1,-1,-1,-1,-1,-1,-1,-1};
            GnuBackgammon.Instance.rec.addMove(0, d[0], d[1], m);
            
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
              
              int idx = ((GameFSM)GnuBackgammon.fsm).hnmove;
              ((GameFSM)GnuBackgammon.fsm).hmoves[idx*2] = orig;
              ((GameFSM)GnuBackgammon.fsm).hmoves[idx*2+1] = dest;
              ((GameFSM)GnuBackgammon.fsm).hnmove++;
              
              ctx.board().availableMoves.dropDice(orig-dest);
              ctx.state(HUMAN_CHECKER_MOVING);
              ctx.board().humanMove(m);
            }
          } else {
            if (ctx.board().points.get((Integer)params).isTarget) { //MOVE CHECKER
              int origin = ctx.board().selected.boardX;
              int dest = (Integer)params;
              int m[] = {origin, dest, -1, -1, -1, -1, -1, -1};
              
              int idx = ((GameFSM)GnuBackgammon.fsm).hnmove;
              ((GameFSM)GnuBackgammon.fsm).hmoves[idx*2] = origin;
              ((GameFSM)GnuBackgammon.fsm).hmoves[idx*2+1] = dest;
              ((GameFSM)GnuBackgammon.fsm).hnmove++;
              
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
          int[] d = ctx.board().dices.get();
          GnuBackgammon.Instance.rec.addMove(0, d[0], d[1], ((GameFSM)GnuBackgammon.fsm).hmoves);
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
            ctx.state(HUMAN_TURN);
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
          ((GameFSM)GnuBackgammon.fsm).hmoves[i] = -1;
        ((GameFSM)GnuBackgammon.fsm).hnmove = 0;
        GnuBackgammon.Instance.rec.updateBoard();
        
        if (ctx.board().gameFinished()) {
          ctx.state(CHECK_END_MATCH);
        } else {
          if (MatchState.fMove==1)
            ctx.state(States.HUMAN_TURN);
          else {
            if (MatchState.matchType == 0)
              ctx.state(States.CPU_TURN);
            else
              ctx.state(States.HUMAN_TURN);
          }
            
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
        
        GnuBackgammon.Instance.rec.addResult(MatchState.fMove, game_score, (MatchState.resignValue>0));
        if (MatchState.matchType==0)
          GnuBackgammon.Instance.rec.saveJson(GnuBackgammon.fname+"json");
        
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
            if (MatchState.matchType==0)
              Gdx.files.absolute(GnuBackgammon.fname+"json").delete();
            GnuBackgammon.Instance.rec.reset();
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
        ctx.board().initBoard();
        ctx.board().updatePInfo();
        
        GnuBackgammon.Instance.rec.addGame();
        
        if ((!((GameFSM)GnuBackgammon.fsm).helpShown)&&(GnuBackgammon.Instance.prefs.getString("SHOWHELP", "Yes").equals("Yes"))) {
          UIDialog.getHelpDialog(0.82f, ctx.board().getStage(), true);
          ((GameFSM)GnuBackgammon.fsm).helpShown = true;
        } else {
          processEvent(ctx, Events.NOOP, null);
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case NOOP: 
          GnuBackgammon.Instance.prefs.putString("SHOWHELP", "No");
          GnuBackgammon.Instance.prefs.flush();
          ctx.board().rollDices();
          break;
        
        case DICES_ROLLED:
          int dices[] ={0,0};
          if (dices[0]==dices[1]) {
            while (dices[0]==dices[1]) {
              GnubgAPI.RollDice(dices);
            }
          }
          processEvent(ctx, Events.ROLL_DICE, dices);
          break;
          
        case ROLL_DICE:
          dices = (int[])params;
          GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], dices[0]>dices[1]);
          if (dices[0]>dices[1]) {//START HUMAN
            MatchState.SetGameTurn(0, 0);
          } else if (dices[0]<dices[1]) {//START CPU
            MatchState.SetGameTurn(1, 1);
          }
          ctx.board().showArrow();
          ctx.board().rollDices(dices[0], dices[1]);
          break;
          
        case SET_GAME_TURN:
          if (MatchState.fMove == 0)
            AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
          else 
            AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
          break;
          
        case SET_BOARD:
          if ((MatchState.fMove == 0)||(MatchState.matchType==1)) {
            ctx.state(HUMAN_TURN);
            int d[] = ctx.board().dices.get();
            AICalls.GenerateMoves(ctx.board(), d[0], d[1]);
          } else {
            ctx.state(CPU_TURN);
            ctx.board().thinking(true);
            AICalls.EvaluateBestMove(ctx.board().dices.get());
          }
          break;
          
        default:
          return false;
        }
        return false;
      }
    },

    
    
    DIALOG_HANDLER {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
          case DOUBLING_RESPONSE: //RISPOSTA A CPU DOUBLING REQUEST
            if((Boolean)params) { //DOUBLING ACCEPTED
              GnuBackgammon.Instance.rec.addDoubleTake(0);
              MatchState.UpdateMSCubeInfo(MatchState.nCube*2, 0);
              GnuBackgammon.Instance.rec.setCube(MatchState.nCube, 0);
              ctx.board().doubleCube();
              ctx.state(CPU_TURN);
              ctx.board().rollDices();
            } else { //DOUBLING NOT ACCEPTED
              GnuBackgammon.Instance.rec.addDoubleDrop(0);
              ctx.state(CHECK_END_MATCH);
            }
            break;
          
          case DOUBLE_REQUEST: //DOUBLE BUTTON CLICKED!
            if(MatchState.matchType == 0) { //CPU VS HUMAN
              GnuBackgammon.Instance.rec.addDoubleRequest(0);
              ctx.board().removeActor(ctx.board().doubleBtn);
              GnubgAPI.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
              ctx.board().thinking(true);
              AICalls.AcceptDouble();
            } else { //SHOW DOUBLE DIALOG!
              UIDialog.getYesNoDialog(Events.HUMAN_DOUBLE_RESPONSE, "Accept double?", 0.82f, ctx.board().getStage());
            }
            break;
            
          case HUMAN_DOUBLE_RESPONSE: //HUMAN DOUBLE RESPONSE (TWO PLAYERS MODE)
            boolean res = (Boolean)params;
            if (res) { //HUMAN OPPONENT ACCEPTED DOUBLE
              MatchState.UpdateMSCubeInfo(MatchState.nCube*2, MatchState.fMove==0?1:0);
              GnuBackgammon.Instance.rec.setCube(MatchState.nCube, MatchState.fMove==0?1:0);
              ctx.board().doubleCube();
              ctx.state(HUMAN_TURN);
            } else { //HUMAN OPPONENT DIDN'T ACCEPT IT
              ctx.state(CHECK_END_MATCH);
            }
            break;
            
          case ACCEPT_DOUBLE: //CPU DOUBLING RESPONSE
            ctx.board().thinking(false);
            if(((Integer)params == 1)||(MatchState.nMatchTo-MatchState.anScore[0]==1)) { //CPU ACCEPTED MY DOUBLE || OPPONENT IS WINNING (DEAD CUBE!!) 
              UIDialog.getFlashDialog(Events.CPU_DOUBLE_ACCEPTED, "Your opponent accepted double", 0.82f, ctx.board().getStage());
            } else { //CPU DIDN'T ACCEPT MY DOUBLE
              UIDialog.getFlashDialog(Events.CPU_DOUBLE_NOT_ACCEPTED, "Double not accepted", 0.82f, ctx.board().getStage());
            }
            break;
            
          case CPU_DOUBLE_ACCEPTED: //CPU ACCEPTED DOUBLE
            ctx.state(States.HUMAN_TURN);
            MatchState.UpdateMSCubeInfo(MatchState.nCube*2, MatchState.fMove==0?1:0);
            GnuBackgammon.Instance.rec.setCube(MatchState.nCube, 1);
            GnuBackgammon.Instance.rec.addDoubleTake(1);
            ctx.board().doubleCube();
            break;
          
          case CPU_RESIGNED: //CPU RESIGN GAME
            ctx.board().switchTurn();
            ctx.state(CHECK_END_MATCH);
            break;
            
          case CPU_DOUBLE_NOT_ACCEPTED: //CPU DIDN'T ACCEPT DOUBLE
            GnuBackgammon.Instance.rec.addDoubleDrop(1);
            ctx.state(CHECK_END_MATCH);
            break;
          
          case NO_MORE_MOVES: //NO LEGAL MOVES AVAILABLE
            GnuBackgammon.fsm.state(States.CHECK_WIN);
            break;
            
          case ACCEPT_RESIGN: //ASK TO HUMAN IF ACCEPT CALCULATED RESIGN
            int ret = (Integer)params;
            if (ret == 0) {
              if (MatchState.fMove == 1)
                GnubgAPI.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
              else
                GnubgAPI.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
              
              MatchState.resignValue++;
              AICalls.AcceptResign(MatchState.resignValue);
            } else {
              String s = "Really resign the game?";
              if (ret == 2) s = "Really resign a gammon game?";
              if (ret == 3) s = "Really resign a backgammon game?";
              UIDialog.getYesNoDialog(Events.HUMAN_RESIGNED, s, 0.82f, ctx.board().getStage());
            }
            break;
            
          case HUMAN_RESIGNED: //HUMAN RESIGN GAME
            if ((Boolean)params) {
              ctx.board().switchTurn();
              GnuBackgammon.fsm.state(States.CHECK_END_MATCH);
            } else {
              MatchState.resignValue = 0;
              ctx.state(HUMAN_TURN);
            }
            break;
            
          case ABANDON_MATCH: //QUIT MATCH
            if (MatchState.matchType==1) {
              if ((Boolean)params) { //ABANDON
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else  { //CANCEL
                GnuBackgammon.fsm.back();
              }
            } else {
              if (((String)params).equals("YES")) {
                //SAVING AND ABANDONING
                GnuBackgammon.Instance.rec.saveJson(GnuBackgammon.fname+"json");
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else if (((String)params).equals("NO")) {
                //ABANDONING
                Gdx.files.absolute(GnuBackgammon.fname+"json").delete();
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else {
                //CANCEL!
                GnuBackgammon.fsm.back();
              }
            }
            break;
            
          default: return false;
        }
        
        
        return true;
      }
      
      @Override
      public void exitState(Context ctx) {
        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();
        super.exitState(ctx);
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


  public GameFSM(Board _board) {
    board = _board;
  }

  public void start() {
    GnuBackgammon.Instance.goToScreen(4);
    hnmove = 0;
  }

  public void stop() {
    state(States.STOPPED);
    helpShown = false;
  }

  public Board board() {
    return board;
  }
}