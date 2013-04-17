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
import it.alcacoop.fibs.Player;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import com.badlogic.gdx.Gdx;

public class FIBSFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private int[] hmoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  public int hnmove = 0;
  private static boolean terminated = false;
  private static int d1, d2;
  private static int[] bufferedMoves = {-1,-1,-1,-1,-1,-1,-1,-1};
  private static boolean isBufferedMoves = false;
  
  
  public enum States implements State {

    REMOTE_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          
        case FIBS_ROLLS:
          int[] dices = (int[])params;
          int d1 = Math.max(dices[0], dices[1]);
          int d2 = Math.min(dices[0], dices[1]);
          GnuBackgammon.Instance.snd.playRoll();
          ctx.board().animateDices(d1, d2, true);
          break;
          
        case DICES_ROLLED:
          dices = (int[])params;
          int mv[][] = GnubgAPI.GenerateMoves(ctx.board()._board[0], ctx.board()._board[1], dices[0], dices[1]);
          ctx.board().availableMoves.setMoves(mv);
          GnuBackgammon.Instance.fibs.pull(Events.FIBS_MOVES);          
          break;
          
        case FIBS_MOVES:
          int moves[] = (int[])params;
          if (moves[0]!=-1) 
            ctx.board().setMoves(moves);
          else
            UIDialog.getFlashDialog(
              Events.NO_MORE_MOVES, 
              "Your opponent has no legal moves",
              0.82f,
              ctx.board().getStage());
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
      public boolean processEvent(Context ctx, FIBSFSM.Events evt, Object params) {

        switch (evt) {
        case FIBS_ROLLS:
          int[] dices = (int[])params;
          FIBSFSM.d1 = Math.max(dices[0], dices[1]);
          FIBSFSM.d2 = Math.min(dices[0], dices[1]);
          GnuBackgammon.Instance.snd.playRoll();
          ctx.board().animateDices(d1, d2, true);
          break;

        case DICES_ROLLED:
          dices = (int[])params;
          AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
          break;


        case GENERATE_MOVES:
          int moves[][] = (int[][])params;
          if ((moves!=null)&&(moves.length>0)) {
            ctx.board().availableMoves.setMoves(moves);
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
          if (GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap").equals("Auto")) {
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
          } else { //TAP MODE
            if (ctx.board().points.get((Integer)params).isTarget) { //MOVE CHECKER
              int origin = ctx.board().selected.boardX;
              int dest = (Integer)params;
              int m[] = {origin, dest, -1, -1, -1, -1, -1, -1};

              int idx = ((FIBSFSM)GnuBackgammon.fsm).hnmove;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx*2] = origin;
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[idx*2+1] = dest;
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
          ctx.board().dices.clear();
          ctx.state(SWITCH_TURN);
          break;

        case DICE_CLICKED:
          ctx.board().dices.clear();
          String m = "";
          for (int i=0; i<4; i++) {
            if (((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i]==-1) break;
            m+=" ";
            if (MatchState.FibsDirection==-1) {
              m+=(((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i]+1)+" ";
              m+=(((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i+1]+1);
            } else {
              m+=(24-((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i])+" ";
              m+=(24-((FIBSFSM)GnuBackgammon.fsm).hmoves[2*i+1]);
            }
          }
          GnuBackgammon.Instance.commandDispatcher.dispatch(Command.SEND_MOVE, m);
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
        GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.FIBS_BOARD) {
          FibsBoard b = (FibsBoard)params;
          
          if (b.dices[0]==0||b.dices[1]==0) {
            GnuBackgammon.Instance.fibs.releaseBoard(b);
            GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD);
          } else {
            if (b.turn == MatchState.FibsColor) {
              ctx.state(States.LOCAL_TURN);
            } else {
              ctx.state(States.REMOTE_TURN);          
            }
            ctx.board().switchTurn();
            for (int i=0;i<8;i++)
              ((FIBSFSM)GnuBackgammon.fsm).hmoves[i] = -1;
            ((FIBSFSM)GnuBackgammon.fsm).hnmove = 0;

            boolean differ = false;
            for (int i=0;i<2;i++)
              for (int j=0;j<25;j++)
                if (ctx.board()._board[i][j]!=b.board[i][j]) {
                  differ = true;
                  break;
                }
            if (differ) {
              ctx.board().initBoard(b.board[0], b.board[1]);//RESYNC!
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            }
            int ds[] = {b.dices[0], b.dices[1]};
            GnuBackgammon.fsm.processEvent(Events.FIBS_ROLLS, ds);
            GnuBackgammon.Instance.fibs.releaseBoard(b);
          }
        }
        return true;
      }
    },


    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        ctx.board().rollBtn.remove();
        ctx.board().doubleBtn.remove();
        ((FIBSFSM)GnuBackgammon.fsm).hnmove = 0;
        MatchState.UpdateMSCubeInfo(1, -1);
        GnuBackgammon.Instance.optionPrefs.putString("SHOWHELP", "No");
        GnuBackgammon.Instance.optionPrefs.flush();
        GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD); //WAITING FOR BOARD..
        terminated = false;
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
          case FIBS_BOARD:
            FibsBoard b = (FibsBoard)params;
            if (b.dices[0]==0||b.dices[1]==0) {
              GnuBackgammon.Instance.fibs.releaseBoard(b);
              GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD);
              break;
            }
            
            for (int i=0;i<25;i++) {
              MatchState.board[4][i] = b.board[0][i];
              MatchState.board[5][i] = b.board[1][i];
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            }
            ctx.board().initBoard(2);
            MatchState.SetGameVariant(0);
            
            GameScreen gs = (GameScreen)GnuBackgammon.Instance.currentScreen;
            gs.pInfo[1].setName(GnuBackgammon.Instance.fibsScreen.username); //PRIMO NOME => NERO
            gs.pInfo[0].setName(b.p2);
            ctx.board().updatePInfo();
            AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            MatchState.FibsDirection = b.direction;
            MatchState.FibsColor = b.color;
            
            if (b.turn == b.color) {
              MatchState.SetGameTurn(0, 0);
              ctx.state(LOCAL_TURN);
            } else {
              MatchState.SetGameTurn(1, 1);
              ctx.state(REMOTE_TURN);
              AICalls.GenerateMoves(ctx.board(), b.dices[0], b.dices[1]);
            }
            
            GnuBackgammon.fsm.processEvent(Events.FIBS_ROLLS, b.dices);
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
        ((GameScreen)GnuBackgammon.Instance.currentScreen).chatBox.hardHide();
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
          ctx.state(FIBS_MENU);
          GnuBackgammon.Instance.goToScreen(8);
        }
        return super.processEvent(ctx, evt, params);
      }
    },


    DIALOG_HANDLER {
      @Override
      public void enterState(Context ctx) {
        FIBSFSM.isBufferedMoves = false;
        super.enterState(ctx);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        
        case FIBS_MOVES:
          FIBSFSM.bufferedMoves = (int[])params;
          FIBSFSM.isBufferedMoves = true;
          break;
        
        case ABANDON_MATCH: //QUIT MATCH
          if ((Boolean)params) { //ABANDON
            ((GameScreen)GnuBackgammon.Instance.currentScreen).chatBox.hardHide();
            GnuBackgammon.Instance.commandDispatcher.send("leave");
            ctx.state(FIBS_MENU);
            GnuBackgammon.Instance.goToScreen(8);
          } else  { //CANCEL
            GnuBackgammon.fsm.back();
            if (FIBSFSM.isBufferedMoves) {
              FIBSFSM.isBufferedMoves = false;
              GnuBackgammon.fsm.processEvent(Events.FIBS_MOVES, FIBSFSM.bufferedMoves);
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
    
    
    FIBS_MENU {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.fibs.reset();
        GnuBackgammon.Instance.FibsOpponent = "";
        super.enterState(ctx);
      }
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            if (params.toString().equals("BACK")) {
              GnuBackgammon.Instance.commandDispatcher.send("BYE");
              GnuBackgammon.Instance.setFSM("MENU_FSM");
              GnuBackgammon.Instance.fibsScreen.fibsInvitations.clear();
              GnuBackgammon.Instance.fibsScreen.fibsPlayers.clear();
            }
            break;
            
          case FIBS_WHO_END:
            if (GnuBackgammon.Instance.fibsScreen.showWho) {
              GnuBackgammon.Instance.fibsScreen.showWho = false;
              GnuBackgammon.Instance.fibsScreen.refreshPlayerList();
              GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
            }
            break;

          case FIBS_PLAYER_LOGIN:
            String s = (String)params;
            GnuBackgammon.Instance.fibsScreen.playerLogged(s);
            break;
            
          case FIBS_INVITE_RECEIVED:
            s = (String)params;
            GnuBackgammon.Instance.fibsScreen.fibsInvitations.put(s.trim(), 1); //1=INVITE IN
            GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
            GnuBackgammon.Instance.snd.playInvite();
            break;
            
          case FIBS_INVITE_SENDED:
            if ((Boolean)params) {
              String u = GnuBackgammon.Instance.fibsScreen.lastInvite;
              GnuBackgammon.Instance.fibsScreen.fibsInvitations.put(u.trim(), -1);
              GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
              GnuBackgammon.Instance.commandDispatcher.dispatch(Command.INVITE, u, "1");
            }
            break;
         
          case FIBS_INVITE_DECLINED:
            String u = (String)params;
            if (GnuBackgammon.Instance.fibsScreen.fibsInvitations.containsKey(u)) {
              GnuBackgammon.Instance.fibsScreen.fibsInvitations.remove(u);
              UIDialog.getFlashDialog(Events.NOOP, "User \""+u+"\" declined your invitation", 0.9f, GnuBackgammon.Instance.fibsScreen.getStage());
              GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
            }
            break;
            
          case FIBS_START_GAME:
            String opponent = (String)params;
            GnuBackgammon.Instance.FibsOpponent = opponent;
            GnuBackgammon.Instance.fibsScreen.clearSendedInvitations();
            GnuBackgammon.Instance.fibsScreen.initGame();
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
    GnuBackgammon.Instance.goToScreen(8);
    state(States.FIBS_MENU);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }
  
  
  @Override
  public void processEvent(final Events evt, final Object params) {
    final FIBSFSM fsm = this;
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        switch (evt) {
          case FIBS_PLAYER_CHANGED:
            Player p = (Player)params;
            GnuBackgammon.Instance.fibsScreen.playerChanged(p);
            break;
          
          case FIBS_MATCHOVER:
            terminated = true;
            state(States.MATCH_OVER);
            break;
            
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
            
          default:
            state().processEvent(fsm, evt, params);
            break;
        }
      }
    });
  }
}