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
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.GameMenuPopup;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import com.badlogic.gdx.Gdx;

public class GServiceFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private static boolean terminated = false;
  private static int d1, d2;
  private static int[] bufferedMoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  private static boolean isBufferedMoves = false;
  private static int moves[][];
  
  public enum States implements State {

    REMOTE_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          
        case GSERVICE_ROLL:
          int[] dices = (int[])params;
          int d1 = Math.max(dices[0], dices[1]);
          int d2 = Math.min(dices[0], dices[1]);
          GnuBackgammon.Instance.snd.playRoll();
          ctx.board().animateDices(d1, d2, true);
          break;
          
        case DICES_ROLLED:
          ctx.board().dices.animating = false;
          dices = (int[])params;
          int mv[][] = GnubgAPI.GenerateMoves(ctx.board()._board[0], ctx.board()._board[1], dices[0], dices[1]);
          if ((mv==null)||(mv.length==0)) {
            UIDialog.getFlashDialog(
                Events.NO_MORE_MOVES, 
                "Your opponent has no legal moves",
                0.82f,
                ctx.board().getStage());
          } else {
            ctx.board().availableMoves.setMoves(mv);
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_MOVES);
          }
          break;

        
        case GSERVICE_MOVES:
          int moves[] = (int[])params;
          ctx.board().setMoves(moves);
          break;

        case PERFORMED_MOVE:
          ctx.board().updatePInfo();
          ctx.board().performNextMove();
          break;

        case NO_MORE_MOVES: //END TURN
          GnuBackgammon.Instance.board.dices.clear();
          if (terminated)
            ctx.state(States.MATCH_OVER);
          else
            ctx.state(States.SWITCH_TURN);
          break;
          
        default:
          return false;
        }
        return true;
      }
    },



    LOCAL_TURN {
      @Override
      public boolean processEvent(Context ctx, GServiceFSM.Events evt, Object params) {
        
        switch (evt) {
        case GSERVICE_ROLL:
          GServiceFSM.d1 = 0;
          GServiceFSM.d2 = 0;
          int[] dices = (int[])params;
          GServiceFSM.d1 = Math.max(dices[0], dices[1]);
          GServiceFSM.d2 = Math.min(dices[0], dices[1]);
          GnuBackgammon.Instance.snd.playRoll();
          ctx.board().animateDices(GServiceFSM.d1, GServiceFSM.d2, true);
          break;
        
        case DICES_ROLLED:
          AICalls.GenerateMoves(ctx.board(), GServiceFSM.d1, GServiceFSM.d2);
          break;
          
        case GENERATE_MOVES:
          moves = (int[][])params;
          if ((moves!=null)&&(moves.length>0)) {
            ctx.board().availableMoves.setMoves(moves);  
          } else  {
            UIDialog.getFlashDialog(
              Events.NO_MORE_MOVES, 
              "No legal moves available",
              0.82f,
              ctx.board().getStage());
          }
          ctx.board().dices.animating = false;
          break;
        
        case POINT_TOUCHED:
          if (GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap").equals("Auto")) {
            int orig = (Integer)params;
            if ((orig==-1)||(ctx.board()._board[MatchState.fMove][orig]==0))
              break;
            int dest = ctx.board().getAutoDestination(orig);
            if (dest!=-2) {
              int m[] = {orig, dest, -1, -1, -1, -1, -1, -1};

              int idx = GnuBackgammon.fsm.hnmove;
              GnuBackgammon.fsm.hmoves[idx*2] = orig;
              GnuBackgammon.fsm.hmoves[idx*2+1] = dest;
              GnuBackgammon.fsm.hnmove++;
              
              ctx.board().availableMoves.dropDice(orig-dest);
              ctx.state(HUMAN_CHECKER_MOVING);
              ctx.board().humanMove(m);
            }
          } else { //TAP MODE
            if (ctx.board().points.get((Integer)params).isTarget) { //MOVE CHECKER
              int origin = ctx.board().selected.boardX;
              int dest = (Integer)params;
              int m[] = {origin, dest, -1, -1, -1, -1, -1, -1};

              int idx = GnuBackgammon.fsm.hnmove;
              GnuBackgammon.fsm.hmoves[idx*2] = origin;
              GnuBackgammon.fsm.hmoves[idx*2+1] = dest;
              GnuBackgammon.fsm.hnmove++;

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
          ctx.board().dices.clear();
          ctx.state(SWITCH_TURN);
          break;

        case DICE_CLICKED:
          ctx.board().dices.clear();
          String m = "6";
          for (int i=0; i<8; i++)
            m+=" "+GnuBackgammon.fsm.hmoves[i];
          
          GServiceClient.getInstance().sendMessage(m);
          ctx.state(SWITCH_TURN);
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
        for (int i=0;i<8;i++)
          GnuBackgammon.fsm.hmoves[i] = -1;
        GnuBackgammon.fsm.hnmove = 0;
        
        //SWITCH TURN
        ctx.board().switchTurn();
        if (MatchState.fTurn==0) {
          ctx.state(States.LOCAL_TURN);
          GServiceClient.getInstance().queue.pull(Events.GSERVICE_ROLL);
        } else {
          ctx.state(States.REMOTE_TURN);
          int dices[] = {0,0};
          GnubgAPI.RollDice(dices);
          GServiceClient.getInstance().queue.post(Events.GSERVICE_ROLL, dices);
          GServiceClient.getInstance().sendMessage("5 "+dices[0]+" "+dices[1]);
          GServiceClient.getInstance().queue.pull(Events.GSERVICE_ROLL);
        }
      }
    },


    
    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        ctx.board().rollBtn.remove();
        ctx.board().doubleBtn.remove();
        GnuBackgammon.fsm.hnmove = 0;
        MatchState.UpdateMSCubeInfo(1, -1);
        GnuBackgammon.Instance.optionPrefs.putString("SHOWHELP", "No");
        GnuBackgammon.Instance.optionPrefs.flush();
        GServiceClient.getInstance().queue.pull(Events.GSERVICE_FIRSTROLL); //WAITING FOR BOARD..
        terminated = false;
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
          case GSERVICE_FIRSTROLL:
            ctx.board().initBoard(0);
            MatchState.SetGameVariant(0);
            
            GameScreen gs = GnuBackgammon.Instance.gameScreen;
            gs.pInfo[1].setName("You"); //PRIMO NOME => NERO
            gs.pInfo[0].setName("Opponent");
            ctx.board().updatePInfo();
            GnubgAPI.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            
            int pars[] = (int[])params;
            int dices[] = {pars[1], pars[2]};
            int turn = pars[0]==1?0:1;
            
            MatchState.fMove = turn;
            MatchState.fTurn = turn;
            GnubgAPI.SetGameTurn(turn, turn);
            GameMenuPopup.setDisabledButtons();
            
            if (turn==0) {
              ctx.state(LOCAL_TURN);
            } else {
              ctx.state(REMOTE_TURN);
            }
            GnuBackgammon.fsm.processEvent(Events.GSERVICE_ROLL, dices);
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
        GnuBackgammon.Instance.FibsOpponent = "";
        GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
        if (MatchState.resignValue==4) {
          terminated = true;
          UIDialog.getFlashDialog(
              Events.STOPPED, 
              "Your opponent resigned the game",
              0.82f,
              ctx.board().getStage());
        } else {
          UIDialog.getFlashDialog(
            Events.STOPPED, 
            "Match terminated",
            0.82f,
            ctx.board().getStage());
        }
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.STOPPED) {
          MatchState.resignValue = 0;
          GnuBackgammon.Instance.nativeFunctions.showInterstitial();
          //ctx.state(FIBS_MENU);
          GnuBackgammon.Instance.goToScreen(8);
        }
        return super.processEvent(ctx, evt, params);
      }
    },


    DIALOG_HANDLER {
      @Override
      public void enterState(Context ctx) {
        GServiceFSM.isBufferedMoves = false;
        super.enterState(ctx);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case GSERVICE_MOVES:
          GServiceFSM.bufferedMoves = (int[])params;
          GServiceFSM.isBufferedMoves = true;
          break;
        
        case ABANDON_MATCH: //QUIT MATCH
          if ((Boolean)params) { //ABANDON
            GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
            if (ctx.board().getPIPS(0)>ctx.board().getPIPS(1)) {
              GnuBackgammon.Instance.commandDispatcher.send("resign b"); //FUCK YOU DROPPER!
            } else {
              GnuBackgammon.Instance.commandDispatcher.send("leave");
              GnuBackgammon.fsm.processEvent(Events.FIBS_MATCHOVER, null);
            }
          } else  { //CANCEL
            GnuBackgammon.fsm.back();
            if (GServiceFSM.isBufferedMoves) {
              GServiceFSM.isBufferedMoves = false;
              GnuBackgammon.fsm.processEvent(Events.GSERVICE_MOVES, GServiceFSM.bufferedMoves);
            }
          }
          break;
          
        case GET_RESIGN_VALUE:
          int ret = (Integer)params;
          String s = "Really resign the game?";
          if (ret == 2) s = "Really resign a gammon game?";
          if (ret == 3) s = "Really resign a backgammon game?";
          UIDialog.getYesNoDialog(Events.HUMAN_RESIGNED, s, 0.82f, ctx.board().getStage());
          break;
          
        case HUMAN_RESIGNED:
          if ((Boolean)params) {
            String t = "";
            if (MatchState.resignValue == 1) t="n";
            else if (MatchState.resignValue == 2) t="g";
            else t = "b";
            GnuBackgammon.Instance.commandDispatcher.send("resign "+t);
          } else {
            MatchState.resignValue = 0;
            GnuBackgammon.fsm.back();
          }
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


  public GServiceFSM(Board _board) {
    board = _board;
  }

  public void start() {
    super.start();
    GnuBackgammon.Instance.goToScreen(4);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }
  
  
  @Override
  public void processEvent(final Events evt, final Object params) {
    final GServiceFSM fsm = this;
    System.out.println("PROCESS "+evt+" ON "+state());
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        switch (evt) {
          case GSERVICE_CHATMSG:
            ((GameScreen)GnuBackgammon.Instance.currentScreen).chatBox.appendMessage("Opponent", (String)params, false);
            break;
            
          case GSERVICE_ERROR:
            GServiceClient.getInstance().disconnect();
            UIDialog.getFlashDialog(
                Events.GSERVICE_BYE, 
                "Network error: opponent disconnected!",
                0.82f,
                GnuBackgammon.Instance.currentScreen.getStage());  
            break;
            
          case GSERVICE_BYE:
            GnuBackgammon.Instance.setFSM("MENU_FSM");
            GnuBackgammon.fsm.state(MenuFSM.States.TWO_PLAYERS);
            break;
          /*
          
          case FIBS_PLAYER_LOGOUT:
            String s = (String)params;
            if (s.equals(GnuBackgammon.Instance.FibsOpponent)) {
              state(States.MATCH_OVER);
              UIDialog.getFlashDialog(
                Events.STOPPED, 
                "Your opponent dropped server connection..",
                0.82f, 
                fsm.board().getStage());
            } else {
              GnuBackgammon.Instance.fibsScreen.playerGone(s);
            }
            break;
            
          case FIBS_ABANDON_GAME:
            state(States.MATCH_OVER);
            UIDialog.getFlashDialog(
              Events.STOPPED, 
              "Your opponent abandoned the game..",
              0.82f,
              fsm.board().getStage());
            break;
            
          case FIBS_RESIGN_REQUEST:
            MatchState.resignValue = 4;
            terminated = true;
            GnuBackgammon.Instance.commandDispatcher.send("accept"); //FIBS WILL SEND MATCHOVER MSG
            break;
          */  
          default:
            state().processEvent(fsm, evt, params);
            break;
        }
      }
    });
  }
}
