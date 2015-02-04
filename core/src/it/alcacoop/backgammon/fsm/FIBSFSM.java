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
import it.alcacoop.backgammon.utils.ELORatingManager;
import it.alcacoop.backgammon.utils.FibsNetHandler;
import it.alcacoop.fibs.CommandDispatcher.Command;
import it.alcacoop.fibs.Player;

import com.badlogic.gdx.Gdx;

public class FIBSFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  private static boolean terminated = false;
  private static int d1, d2;
  private static int[] bufferedMoves = { -1, -1, -1, -1, -1, -1, -1, -1 };
  private static boolean isBufferedMoves = false;
  private static int moves[][];

  private static double rating;

  public enum States implements State {

    REMOTE_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case FIBS_OPPONENT_ROLLS:
            int[] dices = (int[])params;
            int d1 = Math.max(dices[0], dices[1]);
            int d2 = Math.min(dices[0], dices[1]);
            GnuBackgammon.Instance.snd.playRoll();
            ctx.board().animateDices(d1, d2, true);
            break;

          case DICES_ROLLED:
            ctx.board().dices.animating = false;
            dices = (int[])params;
            int mv[][] = AICalls.Locking.GenerateMoves(ctx.board()._board[0], ctx.board()._board[1], dices[0], dices[1]);
            if ((mv == null) || (mv.length == 0)) {
              UIDialog.getFlashDialog(Events.NO_MORE_MOVES, "Your opponent has no legal moves", 0.8f);
            } else {
              GnuBackgammon.Instance.fibs.pull(Events.FIBS_MOVES);
            }
            break;

          case FIBS_MOVES:
            int moves[] = (int[])params;
            ctx.board().setMoves(moves);
            break;

          case PERFORMED_MOVE:
            ctx.board().updatePInfo();
            ctx.board().performNextMove();
            break;

          case NO_MORE_MOVES: // END TURN
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
          case FIBS_YOU_ROLL:
            FIBSFSM.d1 = 0;
            FIBSFSM.d2 = 0;
            int[] dices = (int[])params;
            FIBSFSM.d1 = Math.max(dices[0], dices[1]);
            FIBSFSM.d2 = Math.min(dices[0], dices[1]);
            int mv[][] = AICalls.Locking.GenerateMoves(ctx.board()._board[1], ctx.board()._board[0], dices[0], dices[1]);
            GnuBackgammon.fsm.processEvent(Events.GENERATE_MOVES, mv);
            break;

          case GENERATE_MOVES:
            moves = (int[][])params;
            if ((moves != null) && (moves.length > 0)) {
              GnuBackgammon.fsm.state(BOARD_SYNC);
            } else {
              // NO WAY TO SYNC
              GnuBackgammon.fsm.processEvent(Events.FIBS_BOARD_SYNCED, null);
            }
            break;

          case FIBS_BOARD_SYNCED:
            GnuBackgammon.Instance.snd.playRoll();
            ctx.board().animateDices(FIBSFSM.d1, FIBSFSM.d2, true);
            break;

          case DICES_ROLLED:
            if ((moves != null) && (moves.length > 0)) {
              ctx.board().availableMoves.setMoves(moves);

              if ((GnuBackgammon.fsm.greedyMoves = ctx.board().getGreedyBearoffMove(moves)) != null)
                ctx.state(GREEDY_BEAROFF); // START GREEDY BEAROFF

            } else {
              UIDialog.getFlashDialog(Events.NO_MORE_MOVES, "No legal moves available", 0.8f);
            }
            ctx.board().dices.animating = false;
            break;


          case POINT_TOUCHED:
            if (!ctx.board().availableMoves.hasMoves())
              break;
            if (GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap").equals("Auto")) {
              int orig = (Integer)params;
              if ((orig == -1) || (ctx.board()._board[MatchState.fMove][orig] == 0))
                break;
              int dest = ctx.board().getAutoDestination(orig);
              if (dest != -2) {
                int m[] = { orig, dest, -1, -1, -1, -1, -1, -1 };

                int idx = GnuBackgammon.fsm.hnmove;
                GnuBackgammon.fsm.hmoves[idx * 2] = orig;
                GnuBackgammon.fsm.hmoves[idx * 2 + 1] = dest;
                GnuBackgammon.fsm.hnmove++;

                ctx.board().availableMoves.dropDice(orig - dest);
                ctx.state(HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
              }
            } else { // TAP MODE
              int point = (Integer)params;
              if (ctx.board().points.get(point).isTarget) { // MOVE CHECKER
                int origin = ctx.board().selected.boardX;

                int m[] = { origin, point, -1, -1, -1, -1, -1, -1 };

                int idx = GnuBackgammon.fsm.hnmove;
                GnuBackgammon.fsm.hmoves[idx * 2] = origin;
                GnuBackgammon.fsm.hmoves[idx * 2 + 1] = point;
                GnuBackgammon.fsm.hnmove++;

                ctx.state(HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
                ctx.board().availableMoves.dropDice(origin - point);
              } else { // SELECT NEW CHECKER
                if (point != -1)
                  ctx.board().selectChecker(point);
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
            for (int i = 0; i < 4; i++) {
              if (GnuBackgammon.fsm.hmoves[2 * i] == -1)
                break;
              m += " ";
              if (MatchState.FibsDirection == -1) {
                m += (GnuBackgammon.fsm.hmoves[2 * i] + 1) + " ";
                m += (GnuBackgammon.fsm.hmoves[2 * i + 1] + 1);
                m = m.replaceAll(" 25", " bar");
                m = m.replaceAll(" 0", " off");
              } else {
                m += (24 - GnuBackgammon.fsm.hmoves[2 * i]) + " ";
                m += (24 - GnuBackgammon.fsm.hmoves[2 * i + 1]);
                m = m.replaceAll(" 0", " bar");
                m = m.replaceAll(" 25", " off");
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


    GREEDY_BEAROFF {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GREEDY_MOVE:
            int idx = GnuBackgammon.fsm.hnmove;
            int gm[] = (int[])params;
            int m[] = { gm[0], gm[1], -1, -1, -1, -1, -1, -1 };
            GnuBackgammon.fsm.hmoves[idx * 2] = gm[0];
            GnuBackgammon.fsm.hmoves[idx * 2 + 1] = gm[1];
            GnuBackgammon.fsm.hnmove++;
            ctx.board().availableMoves.dropDice(gm[0] - gm[1]);
            ctx.board().humanMove(m);
            break;

          case PERFORMED_MOVE:
            ctx.board().updatePInfo();
            int mv[] = { -1, -1 };
            for (int i = 0; i < 4; i++) {
              if (GnuBackgammon.fsm.greedyMoves[i * 2] != -1) {
                mv[0] = GnuBackgammon.fsm.greedyMoves[i * 2];
                mv[1] = GnuBackgammon.fsm.greedyMoves[(i * 2) + 1];
                GnuBackgammon.fsm.greedyMoves[i * 2] = -1;
                GnuBackgammon.fsm.greedyMoves[(i * 2) + 1] = -1;
                break;
              }
            }
            if (mv[0] != -1)
              GnuBackgammon.fsm.processEvent(Events.GREEDY_MOVE, mv);
            else
              GnuBackgammon.fsm.processEvent(Events.NO_MORE_MOVES, null);
            break;

          case NO_MORE_MOVES:
            GnuBackgammon.fsm.state(LOCAL_TURN);
            GnuBackgammon.fsm.processEvent(Events.DICE_CLICKED, null);
            break;

          default:
            return false;
        }
        return true;
      }

      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.fsm.processEvent(Events.PERFORMED_MOVE, null);
      }
    },

    HUMAN_CHECKER_MOVING { // HERE ALL TOUCH EVENTS ARE IGNORED!
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


    BOARD_SYNC {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.fibs.boardReset();
        GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD);
        GnuBackgammon.Instance.commandDispatcher.send("board");
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.FIBS_BOARD) {
          FibsBoard b = (FibsBoard)params;
          // SYNC...
          boolean differ = false;
          for (int i = 0; i < 2; i++)
            for (int j = 0; j < 25; j++)
              if (ctx.board()._board[i][j] != b.board[i][j]) {
                differ = true;
                break;
              }

          if (differ) {
            ctx.board().initBoard(b.board[0], b.board[1]);// RESYNC!
            AICalls.Locking.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
          }

          GnuBackgammon.fsm.back();
          GnuBackgammon.fsm.processEvent(Events.FIBS_BOARD_SYNCED, null);
          return true;
        }
        return false;
      }
    },


    SWITCH_TURN {
      public void enterState(Context ctx) {
        for (int i = 0; i < 8; i++)
          GnuBackgammon.fsm.hmoves[i] = -1;
        GnuBackgammon.fsm.hnmove = 0;

        // SWITCH TURN
        ctx.board().switchTurn();
        if (MatchState.fTurn == 0) {
          ctx.state(States.LOCAL_TURN);
          GnuBackgammon.Instance.fibs.pull(Events.FIBS_YOU_ROLL);
        } else {
          ctx.state(States.REMOTE_TURN);
          GnuBackgammon.Instance.fibs.pull(Events.FIBS_OPPONENT_ROLLS);
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
        GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD); // WAITING FOR BOARD..
        terminated = false;
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case FIBS_BOARD:
            FibsBoard b = (FibsBoard)params;
            if (b.dices[0] == 0 || b.dices[1] == 0) {
              GnuBackgammon.Instance.commandDispatcher.send("board");
              GnuBackgammon.Instance.fibs.pull(Events.FIBS_BOARD);
              break;
            }

            for (int i = 0; i < 25; i++) {
              MatchState.board[4][i] = b.board[0][i];
              MatchState.board[5][i] = b.board[1][i];
              AICalls.Locking.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            }
            ctx.board().initBoard(2);
            MatchState.SetGameVariant(0);

            GameScreen gs = GnuBackgammon.Instance.gameScreen;
            gs.pInfo[1].setName(GnuBackgammon.Instance.fibsScreen.username); // PRIMO NOME => NERO
            gs.pInfo[0].setName(b.p2);
            ctx.board().updatePInfo();
            AICalls.Locking.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            MatchState.FibsDirection = b.direction;
            MatchState.FibsColor = b.color;

            if (b.turn == b.color) {
              MatchState.SetGameTurn(0, 0);
              ctx.state(LOCAL_TURN);
              GnuBackgammon.fsm.processEvent(Events.FIBS_YOU_ROLL, b.dices);
            } else {
              MatchState.SetGameTurn(1, 1);
              ctx.state(REMOTE_TURN);
              GnuBackgammon.fsm.processEvent(Events.FIBS_OPPONENT_ROLLS, b.dices);
            }
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
        if (MatchState.resignValue == 4) {
          terminated = true;
          UIDialog.getFlashDialog(Events.STOPPED, "Your opponent resigned the game");
        } else {
          UIDialog.getFlashDialog(Events.STOPPED, "Match terminated");
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.STOPPED) {
          MatchState.resignValue = 0;
          GnuBackgammon.Instance.nativeFunctions.showInterstitial();
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

          case ABANDON_MATCH: // QUIT MATCH
            if ((Boolean)params) { // ABANDON
              GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
              if (ctx.board().getPIPS(0) > ctx.board().getPIPS(1)) {
                GnuBackgammon.Instance.commandDispatcher.send("resign b"); // FUCK YOU DROPPER!
              } else {
                GnuBackgammon.Instance.commandDispatcher.send("leave");
                GnuBackgammon.fsm.processEvent(Events.FIBS_MATCHOVER, null);
              }
            } else { // CANCEL
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
            if (ret == 2)
              s = "Really resign a gammon game?";
            if (ret == 3)
              s = "Really resign a backgammon game?";
            UIDialog.getYesNoDialog(Events.HUMAN_RESIGNED, s);
            break;

          case HUMAN_RESIGNED:
            if ((Boolean)params) {
              String t = "";
              if (MatchState.resignValue == 1)
                t = "n";
              else if (MatchState.resignValue == 2)
                t = "g";
              else
                t = "b";
              GnuBackgammon.Instance.commandDispatcher.send("resign " + t);
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
        GnuBackgammon.Instance.fibs = new FibsNetHandler();

        GnuBackgammon.Instance.FibsOpponent = "";
        GnuBackgammon.Instance.fibsScreen.showWho = true;
        GnuBackgammon.Instance.commandDispatcher.send("who");
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case BUTTON_CLICKED:
            GnuBackgammon.Instance.snd.playMoveStart();
            if (params.toString().equals("BACK")) {
              GnuBackgammon.Instance.commandDispatcher.send("BYE");
              GnuBackgammon.Instance.fibsScreen.fibsInvitations.clear();
              GnuBackgammon.Instance.fibsScreen.fibsPlayers.clear();
              GnuBackgammon.Instance.setFSM("MENU_FSM");
            }
            break;

          case FIBS_WHO_END:
            if (GnuBackgammon.Instance.fibsScreen.showWho) {
              GnuBackgammon.Instance.fibsScreen.showWho = false;
              GnuBackgammon.Instance.fibsScreen.refreshPlayerList();
              GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
              GnuBackgammon.Instance.commandDispatcher.send("who " + GnuBackgammon.Instance.FibsUsername);
            } else {
              double new_rating = GnuBackgammon.Instance.fibsScreen.me.getRating();
              double delta = new_rating - rating;
              if (rating > 0 && delta > 0) {
                ELORatingManager.getInstance().updateRating(GnuBackgammon.Instance.server.equals("fibs.com") ? 1 : 0, delta);
                rating = 0;
              }
            }
            break;

          case FIBS_PLAYER_LOGIN:
            String s = (String)params;
            GnuBackgammon.Instance.fibsScreen.playerLogged(s);
            break;

          case FIBS_INVITE_RECEIVED:
            s = (String)params;
            GnuBackgammon.Instance.fibsScreen.fibsInvitations.put(s.trim(), 1); // 1=INVITE IN
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
              UIDialog.getFlashDialog(Events.NOOP, "User \"" + u + "\" declined your invitation");
              GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
            }
            break;

          case FIBS_START_GAME:
            String opponent = (String)params;
            GnuBackgammon.Instance.FibsOpponent = opponent;
            GnuBackgammon.Instance.fibsScreen.clearSendedInvitations();
            GnuBackgammon.Instance.fibsScreen.initGame();
            MatchState.anScore[0] = 0;
            MatchState.anScore[1] = 0;
            MatchState.nMatchTo = 1;
            GnuBackgammon.Instance.gameScreen.pInfo[0].setScore();
            GnuBackgammon.Instance.gameScreen.pInfo[1].setScore();
            rating = GnuBackgammon.Instance.fibsScreen.me.getRating();
            break;

          default:
            return false;
        }
        return true;
      }
    },


    STOPPED {
      @Override
      public void enterState(Context ctx) {}
    };

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, Events evt, Object params) {
      return false;
    }

    public void enterState(Context ctx) {}

    public void exitState(Context ctx) {}

  };


  public FIBSFSM(Board _board) {
    board = _board;
  }

  public void start() {
    super.start();
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
              UIDialog.getFlashDialog(Events.STOPPED, "Your opponent dropped server connection..");
            } else {
              GnuBackgammon.Instance.fibsScreen.playerGone(s);
            }
            break;

          case FIBS_ABANDON_GAME:
            state(States.MATCH_OVER);
            UIDialog.getFlashDialog(Events.STOPPED, "Your opponent abandoned the game..");
            break;

          case FIBS_RESIGN_REQUEST:
            MatchState.resignValue = 4;
            terminated = true;
            GnuBackgammon.Instance.commandDispatcher.send("accept"); // FIBS WILL SEND MATCHOVER MSG
            break;

          default:
            state().processEvent(FIBSFSM.this, evt, params);
            break;
        }
      }
    });
  }
}
