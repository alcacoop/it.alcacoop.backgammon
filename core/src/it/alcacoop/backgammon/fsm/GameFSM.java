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
import it.alcacoop.backgammon.stats.StatManager;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.AchievementsManager;
import it.alcacoop.backgammon.utils.ELORatingManager;


public class GameFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;
  public static boolean skip_stats = false;


  public enum States implements State {
    CPU_TURN {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case SET_GAME_TURN:
            AICalls.Locking.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            float p0 = ctx.board().getPIPS(0); // BACK PIPS
            float p1 = ctx.board().getPIPS(1); // WHITE PIPS
            p0 = p0 + p0 * 0.3f;
            if (p1 <= p0) {
              // NON CALCOLO RESIGN - PICCOLO VANTAGGIO PIPS
              GnuBackgammon.fsm.processEvent(GameFSM.Events.ASK_FOR_RESIGNATION, 0);
            } else {
              AICalls.AskForResignation();
            }
            break;

          case ASK_FOR_RESIGNATION:
            int resign = (Integer)params;
            if (resign > 0) {
              MatchState.resignValue = resign;
              String s = "Your opponent resigned a game";
              if (resign == 2)
                s = "Your opponent resigned a gammon game";
              if (resign == 3)
                s = "Your opponent resigned a backgammon game";
              ctx.state(DIALOG_HANDLER);
              UIDialog.getFlashDialog(Events.CPU_RESIGNED, s);
            } else { // ASKFORDOUBLING OR ROLL..
              if (MatchState.fCubeUse == 0) { // NO CUBE USE
                if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                  ctx.board().rollDices();
                } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                  UIDialog.getDicesDialog(false);
                }
              } else {
                if (
                ((MatchState.fCrawford == 0) || (!MatchState.fCrafwordGame)) && // NOCR OR NO CRGAME
                    ((MatchState.fCubeOwner == -1) || (MatchState.fCubeOwner == 1)) // AVAILABLE CUBE
                ) {
                  if (MatchState.nMatchTo - MatchState.anScore[1] > 1)
                    AICalls.AskForDoubling();
                  else {// DEAD CUBE!!
                    if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                      ctx.board().rollDices();
                    } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                      UIDialog.getDicesDialog(false);
                    }
                  }
                } else {
                  if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                    ctx.board().rollDices();
                  } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                    UIDialog.getDicesDialog(false);
                  }
                }
              }
            }
            break;

          case ASK_FOR_DOUBLING:
            if (Integer.parseInt(params.toString()) == 1) { // OPEN DOUBLING DIALOG
              GnuBackgammon.Instance.rec.addDoubleRequest(1);
              ctx.state(DIALOG_HANDLER);
              UIDialog.getYesNoDialog(Events.DOUBLING_RESPONSE, "CPU is asking for double. Accept?");
            } else {
              if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                ctx.board().rollDices();
              } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                UIDialog.getDicesDialog(false);
              }
            }
            break;

          case DICES_ROLLED:
            ctx.board().dices.animating = false;
            int dices[] = (int[])params;

            // STATS MANAGEMENT
            if (MatchState.matchType == 0) {
              StatManager.getInstance().addRoll(MatchState.fMove, dices, ctx.board()._board);
            }

            if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
              ctx.board().rollDices(dices[0], dices[1]);
            }
            GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], false);
            ctx.board().thinking(true);
            AICalls.EvaluateBestMove(dices);
            break;

          case EVALUATE_BEST_MOVE:
            ctx.board().thinking(false);
            int moves[] = (int[])params;
            int[] d = ctx.board().dices.get();
            GnuBackgammon.Instance.rec.addMove(1, d[0], d[1], moves);
            if (moves[0] == -1) {
              ctx.state(DIALOG_HANDLER);
              UIDialog.getFlashDialog(Events.NO_MORE_MOVES, "Your opponent has no legal moves", 0.8f);
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
            if (MatchState.fCubeUse == 0) {
              if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                ctx.board().rollDices();
              } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                UIDialog.getDicesDialog(false);
              }
            } else {
              if (
              ((MatchState.fCrawford == 0) || (!MatchState.fCrafwordGame)) && // NOCR OR NO CRGAME
                  ((MatchState.fCubeOwner == -1) || (MatchState.fCubeOwner == MatchState.fMove)) // AVAILABLE CUBE
              ) {
                ctx.board().addActor(ctx.board().rollBtn);
                ctx.board().addActor(ctx.board().doubleBtn);
              } else {
                if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                  ctx.board().rollDices();
                } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                  UIDialog.getDicesDialog(false);
                }
              }
            }
            break;

          case DICES_ROLLED:
            ctx.board().removeActor(ctx.board().rollBtn);
            if (MatchState.fCubeUse == 1)
              ctx.board().removeActor(ctx.board().doubleBtn);
            int dices[] = (int[])params;

            // STATS MANAGEMENT
            if (MatchState.matchType == 0) {
              StatManager.getInstance().addRoll(MatchState.fMove, dices, ctx.board()._board);
            }

            if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
              ctx.board().rollDices(dices[0], dices[1]);
            }
            GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], true);
            AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
            break;

          case GENERATE_MOVES:
            int moves[][] = (int[][])params;

            if (moves != null) { // PLAYER HAS MOVES
              ctx.board().availableMoves.setMoves(moves);

              if ((GnuBackgammon.fsm.greedyMoves = ctx.board().getGreedyBearoffMove(moves)) != null)
                ctx.state(GREEDY_BEAROFF); // START GREEDY BEAROFF

            } else { // PAYER HASN'T ANY MOVE
              ctx.state(DIALOG_HANDLER);
              int[] d = ctx.board().dices.get();
              int[] m = { -1, -1, -1, -1, -1, -1, -1, -1 };
              GnuBackgammon.Instance.rec.addMove(0, d[0], d[1], m);
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
                ctx.state(States.HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
              }
            } else {
              if (ctx.board().points.get((Integer)params).isTarget) { // MOVE CHECKER
                int origin = ctx.board().selected.boardX;
                int dest = (Integer)params;
                int m[] = { origin, dest, -1, -1, -1, -1, -1, -1 };

                int idx = GnuBackgammon.fsm.hnmove;
                GnuBackgammon.fsm.hmoves[idx * 2] = origin;
                GnuBackgammon.fsm.hmoves[idx * 2 + 1] = dest;
                GnuBackgammon.fsm.hnmove++;

                ctx.state(HUMAN_CHECKER_MOVING);
                ctx.board().humanMove(m);
                ctx.board().availableMoves.dropDice(origin - dest);
              } else { // SELECT NEW CHECKER
                if ((Integer)params != -1)
                  ctx.board().selectChecker((Integer)params);
              }
            }
            break;

          case DICE_CLICKED:
            int[] d = ctx.board().dices.get();
            GnuBackgammon.Instance.rec.addMove(0, d[0], d[1], GnuBackgammon.fsm.hmoves);
            ctx.board().dices.clear();
            ctx.state(CHECK_WIN);
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
            GnuBackgammon.fsm.state(HUMAN_TURN);
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
        for (int i = 0; i < 8; i++)
          GnuBackgammon.fsm.hmoves[i] = -1;
        GnuBackgammon.fsm.hnmove = 0;
        GnuBackgammon.Instance.rec.updateBoard();
        if (MatchState.matchType == 0)
          GnuBackgammon.Instance.rec.saveJson(GnuBackgammon.Instance.fname + "json");

        if (ctx.board().gameFinished()) {
          ctx.state(CHECK_END_MATCH);
        } else {
          if (MatchState.fMove == 1)
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

        if (MatchState.resignValue == 0) {
          game_score = MatchState.nCube * ctx.board().gameScore(MatchState.fMove == 1 ? 0 : 1);
          MatchState.win_type = ctx.board().gameScore(MatchState.fMove == 1 ? 0 : 1);
        } else {
          game_score = MatchState.resignValue * MatchState.nCube;
          MatchState.win_type = MatchState.resignValue;
        }
        // win_type in [1=SINGLE|2=GAMMON|3=BACKGAMMON]

        GnuBackgammon.Instance.rec.addResult(MatchState.fMove, game_score, (MatchState.resignValue > 0));
        if (MatchState.matchType == 0)
          GnuBackgammon.Instance.rec.saveJson(GnuBackgammon.Instance.fname + "json");

        if (MatchState.fMove == 0)
          MatchState.SetMatchScore(MatchState.anScore[1], MatchState.anScore[MatchState.fMove] + game_score);
        else
          MatchState.SetMatchScore(MatchState.anScore[MatchState.fMove] + game_score, MatchState.anScore[0]);

        String matchProgress = " (match to " + MatchState.nMatchTo + " in progress)";
        if (MatchState.anScore[MatchState.fMove] >= MatchState.nMatchTo) {
          matchProgress = " (match to " + MatchState.nMatchTo + " finished)";
        }

        String gameString = "Your opponent won " + game_score + " point";
        String score1 = "";
        String score2 = "";
        if (MatchState.matchType == 0) {
          if (MatchState.fMove == 1) {
            gameString = "CPU WON " + game_score + " POINT!";
            if (!skip_stats) StatManager.getInstance().addGame(1);
          } else {
            gameString = "YOU WON " + game_score + " POINT!";
            if (!skip_stats) StatManager.getInstance().addGame(0);
          }

          if (!skip_stats) GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();

          score1 = "CPU: " + MatchState.anScore[1];
          score2 = "YOU: " + MatchState.anScore[0];
        } else {
          score1 = "Player1: " + MatchState.anScore[1];
          score2 = "Player2: " + MatchState.anScore[0];
        }

        if (MatchState.anScore[MatchState.fMove] >= MatchState.nMatchTo) // MATCH FINISHED
          GnuBackgammon.Instance.nativeFunctions.showAds(false);
        if (!skip_stats) {
          if ((ctx.board().getPIPS(0) <= 0) || (((MatchState.resignValue == 1) ||
            (MatchState.resignValue == 2) || (MatchState.resignValue == 3)) && (MatchState.fMove == 0))) {
            AchievementsManager.getInstance().checkAchievements(true);
            if (MatchState.anScore[MatchState.fMove] >= MatchState.nMatchTo) {
              ELORatingManager.getInstance().updateRating(true);
            }
          } else {
            AchievementsManager.getInstance().checkAchievements(false);
          }
        }

        skip_stats = false;
        UIDialog.getEndGameDialog(Events.CONTINUE, matchProgress, gameString, score1, score2);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt == Events.CONTINUE) {
          if (MatchState.anScore[MatchState.fMove] >= MatchState.nMatchTo) { // MATCH FINISHED: GO TO MAIN MENU
            if (MatchState.matchType == 0)
              Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").delete();
            GnuBackgammon.Instance.rec.reset();
            GnuBackgammon.Instance.nativeFunctions.showInterstitial();
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
        MatchState.win_type = 0;
        ctx.board().initBoard();
        ctx.board().updatePInfo();

        GnuBackgammon.Instance.rec.addGame();

        if ((!GnuBackgammon.fsm.helpShown) && (GnuBackgammon.Instance.optionPrefs.getString("SHOWHELP", "Yes").equals("Yes"))) {
          UIDialog.getHelpDialog(true);
          GnuBackgammon.fsm.helpShown = true;
        } else {
          GnuBackgammon.fsm.processEvent(Events.NOOP, null);
        }
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {

          case NOOP:
            GnuBackgammon.Instance.optionPrefs.putString("SHOWHELP", "No");
            GnuBackgammon.Instance.optionPrefs.flush();
            ctx.board().rollDices();
            break;

          case DICES_ROLLED:
            int dices[] = { 0, 0 };
            if (dices[0] == dices[1]) {
              while (dices[0] == dices[1]) {
                dices = AICalls.Locking.RollDice();
              }
            }
            GnuBackgammon.fsm.processEvent(Events.ROLL_DICE, dices);
            break;

          case ROLL_DICE:
            dices = (int[])params;
            GnuBackgammon.Instance.rec.addDices(dices[0], dices[1], dices[0] > dices[1]);
            if (dices[0] > dices[1]) {// START HUMAN
              MatchState.SetGameTurn(0, 0);
              if (MatchState.matchType == 0)
                StatManager.getInstance().addRoll(0, dices, ctx.board()._board);
            } else if (dices[0] < dices[1]) {// START CPU
              MatchState.SetGameTurn(1, 1);
              if (MatchState.matchType == 0)
                StatManager.getInstance().addRoll(1, dices, ctx.board()._board);
            }
            ctx.board().showArrow();
            ctx.board().rollDices(dices[0], dices[1]);
            break;

          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.Locking.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else
              AICalls.Locking.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);

            if ((MatchState.fMove == 0) || (MatchState.matchType == 1)) {
              ctx.state(HUMAN_TURN);
              int d[] = ctx.board().dices.get();
              AICalls.GenerateMoves(ctx.board(), d[0], d[1]);
            } else {
              ctx.state(CPU_TURN);
              ctx.board().thinking(true);
              AICalls.EvaluateBestMove(ctx.board().dices.get());
            }
            ctx.board().dices.animating = false;
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

          case DOUBLING_RESPONSE: // RISPOSTA A CPU DOUBLING REQUEST
            if ((Boolean)params) { // DOUBLING ACCEPTED
              GnuBackgammon.Instance.rec.addDoubleTake(0);
              MatchState.UpdateMSCubeInfo(MatchState.nCube * 2, 0);
              GnuBackgammon.Instance.rec.setCube(MatchState.nCube, 0);
              ctx.board().doubleCube();
              ctx.state(CPU_TURN);
              if ((!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                ctx.board().rollDices();
              } else if ((GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) && (MatchState.matchType < 2)) {
                UIDialog.getDicesDialog(false);
              }
            } else { // DOUBLING NOT ACCEPTED
              GnuBackgammon.Instance.rec.addDoubleDrop(0);
              ctx.state(CHECK_END_MATCH);
            }
            break;

          case DOUBLE_REQUEST: // DOUBLE BUTTON CLICKED!
            if (MatchState.matchType == 0) { // CPU VS HUMAN
              GnuBackgammon.Instance.rec.addDoubleRequest(0);
              ctx.board().removeActor(ctx.board().doubleBtn);
              AICalls.Locking.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
              ctx.board().thinking(true);
              AICalls.AcceptDouble();
            } else { // SHOW DOUBLE DIALOG!
              UIDialog.getYesNoDialog(Events.HUMAN_DOUBLE_RESPONSE, "Accept double?");
            }
            break;

          case HUMAN_DOUBLE_RESPONSE: // HUMAN DOUBLE RESPONSE (TWO PLAYERS MODE)
            boolean res = (Boolean)params;
            if (res) { // HUMAN OPPONENT ACCEPTED DOUBLE
              MatchState.UpdateMSCubeInfo(MatchState.nCube * 2, MatchState.fMove == 0 ? 1 : 0);
              GnuBackgammon.Instance.rec.setCube(MatchState.nCube, MatchState.fMove == 0 ? 1 : 0);
              ctx.board().doubleCube();
              ctx.state(HUMAN_TURN);
            } else { // HUMAN OPPONENT DIDN'T ACCEPT IT
              ctx.state(CHECK_END_MATCH);
            }
            break;

          case ACCEPT_DOUBLE: // CPU DOUBLING RESPONSE
            ctx.board().thinking(false);
            if (((Integer)params == 1) || (MatchState.nMatchTo - MatchState.anScore[0] == 1)) { // CPU ACCEPTED MY DOUBLE || OPPONENT IS WINNING (DEAD CUBE!!)
              UIDialog.getFlashDialog(Events.CPU_DOUBLE_ACCEPTED, "Your opponent accepted double");
            } else { // CPU DIDN'T ACCEPT MY DOUBLE
              UIDialog.getFlashDialog(Events.CPU_DOUBLE_NOT_ACCEPTED, "Double not accepted");
            }
            break;

          case CPU_DOUBLE_ACCEPTED: // CPU ACCEPTED DOUBLE
            ctx.state(States.HUMAN_TURN);
            MatchState.UpdateMSCubeInfo(MatchState.nCube * 2, MatchState.fMove == 0 ? 1 : 0);
            GnuBackgammon.Instance.rec.setCube(MatchState.nCube, 1);
            GnuBackgammon.Instance.rec.addDoubleTake(1);
            ctx.board().doubleCube();
            break;

          case CPU_RESIGNED: // CPU RESIGN GAME
            ctx.board().switchTurn();
            ctx.state(CHECK_END_MATCH);
            break;

          case CPU_DOUBLE_NOT_ACCEPTED: // CPU DIDN'T ACCEPT DOUBLE
            GnuBackgammon.Instance.rec.addDoubleDrop(1);
            ctx.state(CHECK_END_MATCH);
            break;

          case NO_MORE_MOVES: // NO LEGAL MOVES AVAILABLE
            GnuBackgammon.fsm.state(States.CHECK_WIN);
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

          case HUMAN_RESIGNED: // HUMAN RESIGN GAME
            if ((Boolean)params) {
              ctx.board().switchTurn();
              GnuBackgammon.fsm.state(States.CHECK_END_MATCH);
            } else {
              MatchState.resignValue = 0;
              ctx.state(HUMAN_TURN);
            }
            break;

          case ABANDON_MATCH: // QUIT MATCH
            if (MatchState.matchType == 1) {
              if ((Boolean)params) { // ABANDON
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else { // CANCEL
                GnuBackgammon.fsm.back();
              }
            } else {
              String response = "YES";
              try {
                response = (String)params;
              } catch (Exception e) {}
              if (response.equals("YES")) {
                // SAVING AND ABANDONING
                StatManager.getInstance().addGame(1);
                GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
                GnuBackgammon.Instance.rec.saveJson(GnuBackgammon.Instance.fname + "json");
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else if (response.equals("NO")) {
                // ABANDONING
                StatManager.getInstance().addGame(1);
                GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
                Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").delete();
                GnuBackgammon.Instance.rec.reset();
                GnuBackgammon.Instance.setFSM("MENU_FSM");
              } else {
                // CANCEL!
                GnuBackgammon.fsm.back();
              }
            }
            break;

          default:
            return false;
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
      public void enterState(Context ctx) {}
    };

    // DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, Events evt, Object params) {
      return false;
    }
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}

  };


  public GameFSM(Board _board) {
    board = _board;
  }

  public void start() {
    super.start();
    GnuBackgammon.Instance.goToScreen(4);
    hnmove = 0;
  }

  public void stop() {
    state(States.STOPPED);
    GnuBackgammon.fsm.helpShown = false;
  }

  public Board board() {
    return board;
  }
}
