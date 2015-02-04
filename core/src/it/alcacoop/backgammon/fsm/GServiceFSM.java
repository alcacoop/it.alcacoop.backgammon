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
import it.alcacoop.backgammon.gservice.GServiceMessages;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.EndGameLayer;
import it.alcacoop.backgammon.ui.GameMenuPopup;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.AchievementsManager;
import it.alcacoop.backgammon.utils.ELORatingManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;


public class GServiceFSM extends BaseFSM implements Context, GServiceMessages {

  private Board board;
  public State currentState;
  public static boolean noEndGameLayer = false;
  private static long waitTime;
  private static int d1, d2;
  private static int[] bufferedMoves = { -1, -1, -1, -1, -1, -1, -1, -1 };
  private static boolean isBufferedMoves = false;
  private static int moves[][];
  private static int obPlayAgain;
  static Timer pingTimer;


  public enum States implements State {

    REMOTE_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GSERVICE_ACCEPT:
            GnuBackgammon.fsm.board().waiting(true);
            boolean resp = (Boolean)params;
            if (resp) {
              MatchState.UpdateMSCubeInfo(MatchState.nCube * 2, 0);
              ctx.board().doubleCube();
              GServiceClient.getInstance().queue.pull(Events.GSERVICE_ROLL);
            } else {
              MatchState.resignValue = -1;
              GnuBackgammon.fsm.state(States.MATCH_OVER);
            }
            GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_ACCEPT + " " + (resp ? 1 : 0));
            break;

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
            int mv[][] = AICalls.Locking.GenerateMoves(ctx.board()._board[0], ctx.board()._board[1], dices[0], dices[1]);
            if ((mv == null) || (mv.length == 0)) {
              UIDialog.getFlashDialog(Events.NO_MORE_MOVES, "Your opponent has no legal moves", 0.8f);
            } else {
              GServiceClient.getInstance().queue.pull(Events.GSERVICE_MOVES);
            }
            break;


          case GSERVICE_MOVES:
            ctx.board().waiting(false);
            int moves[] = (int[])params;
            ctx.board().setMoves(moves);
            break;

          case PERFORMED_MOVE:
            ctx.board().updatePInfo();
            ctx.board().performNextMove();
            break;

          case NO_MORE_MOVES: // END TURN
            ctx.board().waiting(false);
            GnuBackgammon.Instance.board.dices.clear();
            GnuBackgammon.fsm.state(States.SWITCH_TURN);
            break;

          default:
            return false;
        }
        return true;
      }
    },

    CUBE_REQUEST {
      @Override
      public void enterState(Context ctx) {
        ctx.board().removeActor(ctx.board().rollBtn);
        ctx.board().waiting(true);
      }
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case GSERVICE_ACCEPT:
            int resp = (Integer)params;
            ctx.board().waiting(false);
            if (resp == 1) {
              MatchState.UpdateMSCubeInfo(MatchState.nCube * 2, 1);
              ctx.board().doubleCube();

              GnuBackgammon.fsm.state(States.LOCAL_TURN);
              UIDialog.getFlashDialog(Events.ROLL_DICE, "Your opponent accepted double");
            } else {
              UIDialog.getFlashDialog(Events.CPU_DOUBLE_NOT_ACCEPTED, "Double not accepted");
            }
            break;
          case CPU_DOUBLE_NOT_ACCEPTED:
            MatchState.resignValue = 1;
            GnuBackgammon.fsm.state(States.MATCH_OVER);
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
          case ROLL_DICE:
            int dices[] = AICalls.Locking.RollDice();
            GServiceClient.getInstance().sendMessage(GSERVICE_ROLL + " " + dices[0] + " " + dices[1]);
            /*
            String s = "";
            for (int i = 1; i >= 0; i--)
              for (int j = 0; j < 25; j++) {
                s += " " + ctx.board()._board[i][j];
              }
            GServiceClient.getInstance().sendMessage(GSERVICE_BOARD + s);
            */
            GnuBackgammon.fsm.processEvent(Events.GSERVICE_ROLL, dices);
            break;

          case GSERVICE_ROLL:
            GServiceFSM.d1 = 0;
            GServiceFSM.d2 = 0;
            dices = (int[])params;
            GServiceFSM.d1 = Math.max(dices[0], dices[1]);
            GServiceFSM.d2 = Math.min(dices[0], dices[1]);
            AICalls.GenerateMoves(ctx.board(), GServiceFSM.d1, GServiceFSM.d2);
            break;


          case GENERATE_MOVES:
            /*
            moves = (int[][])params;
            if ((moves != null) && (moves.length > 0) && (ctx.board().getPIPS(0) != 167)) {
              GnuBackgammon.fsm.state(BOARD_SYNC);
            } else {
              // NO WAY TO SYNC
              GnuBackgammon.fsm.processEvent(Events.GSERVICE_BOARD_SYNCED, null);
            }
            */
            moves = (int[][])params;
            GnuBackgammon.fsm.processEvent(Events.GSERVICE_BOARD_SYNCED, null);
            break;


          case GSERVICE_BOARD_SYNCED:
            GnuBackgammon.Instance.snd.playRoll();
            ctx.board().animateDices(GServiceFSM.d1, GServiceFSM.d2, true);
            break;


          case DICES_ROLLED:
            if ((moves != null) && (moves.length > 0)) {
              ctx.board().availableMoves.setMoves(moves);

              if ((GnuBackgammon.fsm.greedyMoves = ctx.board().getGreedyBearoffMove(moves)) != null)
                GnuBackgammon.fsm.state(States.GREEDY_BEAROFF); // START GREEDY BEAROFF

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
                GnuBackgammon.fsm.state(States.HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
              }
            } else { // TAP MODE
              if (ctx.board().points.get((Integer)params).isTarget) { // MOVE CHECKER
                int origin = ctx.board().selected.boardX;
                int dest = (Integer)params;
                int m[] = { origin, dest, -1, -1, -1, -1, -1, -1 };

                int idx = GnuBackgammon.fsm.hnmove;
                GnuBackgammon.fsm.hmoves[idx * 2] = origin;
                GnuBackgammon.fsm.hmoves[idx * 2 + 1] = dest;
                GnuBackgammon.fsm.hnmove++;

                GnuBackgammon.fsm.state(States.HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
                ctx.board().availableMoves.dropDice(origin - dest);
              } else { // SELECT NEW CHECKER
                if ((Integer)params != -1)
                  ctx.board().selectChecker((Integer)params);
              }
            }
            break;

          case NO_MORE_MOVES:
            ctx.board().dices.clear();
            GnuBackgammon.fsm.state(States.SWITCH_TURN);
            break;

          case DICE_CLICKED:
            ctx.board().dices.clear();
            String m = "" + GSERVICE_MOVE;
            for (int i = 0; i < 8; i++)
              m += " " + GnuBackgammon.fsm.hmoves[i];

            GServiceClient.getInstance().sendMessage(m);
            GnuBackgammon.fsm.state(States.SWITCH_TURN);
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
            GnuBackgammon.fsm.state(States.LOCAL_TURN);
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
            GnuBackgammon.fsm.state(States.LOCAL_TURN);
            break;

          default:
            return false;
        }
        return true;
      }
    },


    /*
    BOARD_SYNC {
      @Override
      public void enterState(Context ctx) {
        GServiceClient.getInstance().queue.pull(Events.GSERVICE_BOARD);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.GSERVICE_BOARD) {
          int b[][] = (int[][])params;
          // SYNC...
          boolean differ = false;
          for (int i = 0; i < 2; i++)
            for (int j = 0; j < 25; j++)
              if (ctx.board()._board[i][j] != b[i][j]) {
                differ = true;
                break;
              }
          if (differ) {
            ctx.board().initBoard(b[0], b[1]);// RESYNC!
            AICalls.Locking.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
          }
          GnuBackgammon.fsm.back();
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_BOARD_SYNCED, null);
          return true;
        }
        return false;
      }
    },
    */

    SWITCH_TURN {
      public void enterState(Context ctx) {
        // GServiceClient.getInstance().debug();
        for (int i = 0; i < 8; i++)
          GnuBackgammon.fsm.hmoves[i] = -1;
        GnuBackgammon.fsm.hnmove = 0;

        if (ctx.board().gameFinished()) {
          GnuBackgammon.fsm.state(States.MATCH_OVER);
        } else {
          // SWITCH TURN
          ctx.board().switchTurn();
          if (MatchState.fTurn == 0) {
            GnuBackgammon.fsm.state(States.LOCAL_TURN);

            if ((MatchState.fCubeOwner == -1) || (MatchState.fCubeOwner == 0)) {// AVAILABLE CUBE
              ctx.board().addActor(ctx.board().rollBtn);
              ctx.board().addActor(ctx.board().doubleBtn);
            } else {
              GnuBackgammon.fsm.processEvent(Events.ROLL_DICE, null);
            }
          } else {
            GnuBackgammon.fsm.state(States.REMOTE_TURN);
            ctx.board().waiting(true);
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_DOUBLE, Events.GSERVICE_ROLL);
          }
        }
      }
    },


    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        GServiceFSM.obPlayAgain = -1;
        ctx.board().waiting(false);
        GnuBackgammon.Instance.nativeFunctions.hideProgressDialog();
        ctx.board().rollBtn.remove();
        ctx.board().doubleBtn.remove();
        GnuBackgammon.fsm.hnmove = 0;
        MatchState.UpdateMSCubeInfo(1, -1);
        GnuBackgammon.Instance.optionPrefs.putString("SHOWHELP", "No");
        GnuBackgammon.Instance.optionPrefs.flush();
        GServiceClient.getInstance().queue.pull(Events.GSERVICE_FIRSTROLL);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case GSERVICE_FIRSTROLL:
            MatchState.SetCubeUse(1);
            MatchState.UpdateMSCubeInfo(1, -1);
            ctx.board().initBoard(0);
            MatchState.SetGameVariant(0);
            AICalls.Locking.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);

            int pars[] = (int[])params;
            int dices[] = { pars[1], pars[2] };
            int turn = pars[0] == 1 ? 0 : 1;

            MatchState.fMove = turn;
            MatchState.fTurn = turn;
            MatchState.nMatchTo = 1;
            AICalls.Locking.SetGameTurn(turn, turn);
            GameMenuPopup.setDisabledButtons();

            if (turn == 0) {
              GnuBackgammon.fsm.state(States.LOCAL_TURN);
            } else {
              GnuBackgammon.fsm.state(States.REMOTE_TURN);
              ctx.board().waiting(true);
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
        if (GnuBackgammon.fsm.previousState == MATCH_OVER) // TODO: WORKAROUND.. PROBABLY NEED FIX
          return;

        ctx.board().waiting(false);
        GnuBackgammon.Instance.FibsOpponent = "";

        if (MatchState.resignValue > 10)
          MatchState.resignValue -= 10; // NORMALIZE RESIGN VALUE!

        GServiceClient.instance.reset();
        GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
        if ((ctx.board().getPIPS(0) <= 0) || (MatchState.resignValue > 0)) { // YOU WIN!

          int nPoints = MatchState.nCube * MatchState.resignValue;
          if (MatchState.resignValue == 0)
            nPoints = MatchState.nCube * ctx.board().gameScore(1);

          MatchState.anScore[0] += nPoints;
          GnuBackgammon.Instance.gameScreen.pInfo[1].setScore();


          AchievementsManager.getInstance().checkAchievements(true);
          ELORatingManager.getInstance().updateRating(true);
          GnuBackgammon.Instance.gameScreen.endLayer.show(0, nPoints);
        } else if ((ctx.board().getPIPS(0) > 0) && (MatchState.resignValue <= 0)) {

          int nPoints = MatchState.nCube * -MatchState.resignValue;
          if (MatchState.resignValue == 0)
            nPoints = MatchState.nCube * ctx.board().gameScore(0);

          MatchState.anScore[1] += nPoints;
          GnuBackgammon.Instance.gameScreen.pInfo[0].setScore();

          AchievementsManager.getInstance().checkAchievements(false);
          GnuBackgammon.Instance.gameScreen.endLayer.show(1, nPoints);
        }

        MatchState.resignValue = 0;
        if (GServiceFSM.obPlayAgain != -1) {
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_PLAY_AGAIN, GServiceFSM.obPlayAgain);
          GServiceFSM.obPlayAgain = -1;
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        EndGameLayer endLayer = ((GameScreen)GnuBackgammon.Instance.currentScreen).endLayer;
        switch (evt) {

          case GSERVICE_PLAY_AGAIN:
            ((GameScreen)GnuBackgammon.Instance.currentScreen).initNewMatch();
            int response = (Integer)params;
            if (response == 0) {
              endLayer.opponentAbandoned();
            } else {
              if (endLayer.isWaiting()) {
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_RETURN_GAME, null);
                endLayer.hide();
              } else {
                endLayer.opponentAvailable();
              }
            }
            break;

          case GSERVICE_RETURN_GAME:
            ctx.board().waiting(true);
            GServiceClient.getInstance().sendMessage(GSERVICE_INIT_RATING + " " + GnuBackgammon.Instance.optionPrefs.getString("multiboard", "0"));
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_INIT_RATING);
            break;

          case GSERVICE_INIT_RATING:
            double opponentRating = (Double)params;
            ELORatingManager.getInstance().setRatings(opponentRating);

            Random gen = new Random();
            waitTime = gen.nextLong();
            GServiceClient.getInstance().sendMessage(GSERVICE_HANDSHAKE + " " + waitTime + " " + GnuBackgammon.Instance.nativeFunctions.getAppVersionCode());
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_HANDSHAKE);
            break;

          case GSERVICE_HANDSHAKE:
            long _params[] = (long[])params;
            long remoteWaitTime = _params[0];
            GnuBackgammon.fsm.state(States.OPENING_ROLL);
            if (waitTime > remoteWaitTime) {
              int dices[] = { 0, 0 };
              while (dices[0] == dices[1])
                dices = AICalls.Locking.RollDice();
              int[] p = { (dices[0] > dices[1] ? 1 : 0), dices[0], dices[1] };

              GServiceClient.getInstance().queue.post(Events.GSERVICE_FIRSTROLL, p);
              GServiceClient.getInstance().sendMessage(GSERVICE_OPENING_ROLL + " " + (dices[0] > dices[1] ? 0 : 1) + " " + dices[0] + " " + dices[1]);
            }
            break;

          default:
            return false;

        }
        return true;
      }
    },


    ABANDON_MATCH {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.HUMAN_RESIGNED) {
          GnuBackgammon.fsm.state(States.MATCH_OVER);
        }
        return true;
      }
    },


    DIALOG_HANDLER {
      @Override
      public void enterState(Context ctx) {
        GServiceFSM.isBufferedMoves = false;
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case GSERVICE_MOVES:
            GServiceFSM.bufferedMoves = (int[])params;
            GServiceFSM.isBufferedMoves = true;
            break;

          case ABANDON_MATCH: // QUIT MATCH
            if ((Boolean)params) { // ABANDON
              GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
              if (ctx.board().getPIPS(0) > ctx.board().getPIPS(1)) {
                GServiceClient.getInstance().sendMessage(GSERVICE_ABANDON + " 13");// FUCK YOU DROPPER!
              } else {
                GServiceClient.getInstance().sendMessage(GSERVICE_ABANDON + " 11");
              }
              GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
            } else { // CANCEL
              GnuBackgammon.fsm.back();
              if (GServiceFSM.isBufferedMoves) {
                GServiceFSM.isBufferedMoves = false;
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_MOVES, GServiceFSM.bufferedMoves);
              }
            }
            break;

          case GET_RESIGN_VALUE:
            int ret = (Integer)params;
            MatchState.resignValue = ret;
            String s = "Really resign the game?";
            if (ret == 2)
              s = "Really resign a gammon game?";
            if (ret == 3)
              s = "Really resign a backgammon game?";
            UIDialog.getYesNoDialog(Events.HUMAN_RESIGNED, s);
            break;

          case HUMAN_RESIGNED:
            if ((Boolean)params) {
              GServiceClient.getInstance().sendMessage(GSERVICE_ABANDON + " " + MatchState.resignValue);
              GnuBackgammon.fsm.state(States.MATCH_OVER);
            } else {
              GnuBackgammon.fsm.back();
            }
            MatchState.resignValue = -MatchState.resignValue;
            break;

          case DOUBLE_REQUEST:
            GnuBackgammon.fsm.state(States.CUBE_REQUEST);
            GServiceClient.getInstance().sendMessage(GServiceMessages.GSERVICE_DOUBLE + " ");
            GServiceClient.getInstance().queue.pull(Events.GSERVICE_ACCEPT);
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
        super.enterState(ctx);
        ctx.board().waiting(false);
      }
    };

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, Events evt, Object params) {
      return false;
    }
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}
  };


  public GServiceFSM(Board _board) {
    board = _board;
  }


  public void start() {
    super.start();
    MatchState.SetCubeUse(1);
    MatchState.UpdateMSCubeInfo(1, -1);
    GnuBackgammon.Instance.goToScreen(4);
    pingTimer = new Timer();

    pingTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        GServiceClient.getInstance().sendMessage("PING");
      }
    }, 0, 2500);
  }


  public void stop() {
    pingTimer.cancel();
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
          case GSERVICE_PLAY_AGAIN:
            if (GnuBackgammon.fsm.currentState == States.MATCH_OVER) {
              GServiceFSM.obPlayAgain = -1;
              state().processEvent(GServiceFSM.this, evt, params);
            } else {
              GServiceFSM.obPlayAgain = (Integer)params;
            }
            break;

          case GSERVICE_DOUBLE:
            UIDialog.getYesNoDialog(Events.GSERVICE_ACCEPT, "Opponent is asking for double. Accept?");
            GnuBackgammon.fsm.board().waiting(false);
            break;

          case GSERVICE_CHATMSG:
            GnuBackgammon.Instance.snd.playMessage();
            ((GameScreen)GnuBackgammon.Instance.currentScreen).chatBox.appendMessage(
                GnuBackgammon.Instance.gameScreen.pInfo[0].getPName(), (String)params, false);
            break;

          case GSERVICE_ERROR:
            EndGameLayer endLayer = ((GameScreen)GnuBackgammon.Instance.currentScreen).endLayer;
            int errorCode = (Integer)params;
            String message = "";
            switch (errorCode) {
              case 0:
                message = "Network error: opponent disconnected!";
                break;
              case 1:
                message = "Network Error: you disconnected!";
                break;
              case 2:
                message = "Match stopped. You have to reinvite!";
                break;
            }
            if (errorCode != 0) { // LOCAL ERROR
              if (endLayer.isVisible()) {
                processEvent(Events.GSERVICE_BYE, null);
              } else {
                UIDialog.getFlashDialog(Events.GSERVICE_BYE, message);
              }
            } else { // REMOTE ERROR
              if (endLayer.isVisible())
                endLayer.opponentAbandoned();
              else
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_ABANDON, 11);
            }
            break;

          case GSERVICE_ABANDON:
            int status = (Integer)params;
            MatchState.resignValue = status;

            String msg = "";
            if (MatchState.resignValue > 0) {
              switch (MatchState.resignValue) {
                case 1:
                  msg = "Opponent resigned the game";
                  state(States.ABANDON_MATCH);
                  UIDialog.getFlashDialog(Events.HUMAN_RESIGNED, msg);
                  break;
                case 2:
                  msg = "Opponent resigned a gammon game";
                  state(States.ABANDON_MATCH);
                  UIDialog.getFlashDialog(Events.HUMAN_RESIGNED, msg);
                  break;
                case 3:
                  msg = "Opponent resigned a backgammon game";
                  state(States.ABANDON_MATCH);
                  UIDialog.getFlashDialog(Events.HUMAN_RESIGNED, msg);
                  break;

                case 11:
                case 13:
                  ((GameScreen)GnuBackgammon.Instance.currentScreen).endLayer.opponentAbandoned();
                  GnuBackgammon.fsm.state(States.MATCH_OVER);
                  break;
              }
            }
            break;

          case GSERVICE_BYE:
            MatchState.resignValue = 0;
            GnuBackgammon.Instance.nativeFunctions.gserviceResetRoom();
            GServiceClient.instance.dispose();
            GnuBackgammon.Instance.invitationId = "";
            GnuBackgammon.Instance.gameScreen.chatBox.hardHide();
            GnuBackgammon.Instance.nativeFunctions.showAds(false);
            GnuBackgammon.Instance.nativeFunctions.showInterstitial();
            GnuBackgammon.Instance.setFSM("MENU_FSM");
            GnuBackgammon.fsm.state(MenuFSM.States.TWO_PLAYERS);
            break;

          default:
            state().processEvent(GServiceFSM.this, evt, params);
            break;
        }
      }
    });
  }


}
