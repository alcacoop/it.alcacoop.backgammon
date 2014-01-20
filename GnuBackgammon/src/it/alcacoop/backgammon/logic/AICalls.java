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

package it.alcacoop.backgammon.logic;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.GServiceFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.Gdx;

public class AICalls {
  private static ExecutorService dispatchExecutor;

  static {
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public static void SetAILevel(final AILevels l) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.SetAILevel(l.ordinal());
      }
    });
  }


  public static void SetBoard(final int b1[], final int b2[]) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.SetBoard(b1, b2);
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_BOARD, 1);
          }
        });
      }
    });
  }


  public static void GetResignValue(final int b1[], final int b2[]) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.SetBoard(b1, b2);
        int resign = 1;
        while (GnubgAPI.AcceptResign(resign) != resign)
          resign++;

        final int r = resign;

        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.GET_RESIGN_VALUE, r);
          }
        });
      }
    });
  }


  public static void AcceptResign(final int r) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        final int ret = GnubgAPI.AcceptResign(r);
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.ACCEPT_RESIGN, ret);
          }
        });
      }
    });
  }


  public static void AcceptDouble() {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        final int ret = GnubgAPI.AcceptDouble();
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.ACCEPT_DOUBLE, ret);
          }
        });
      }
    });
  }


  public static void UpdateMSCubeInfo(final int nCube, final int fCubeOwner) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.UpdateMSCubeInfo(nCube, fCubeOwner);
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.UPDATE_MS_CUBEINFO, 1);
          }
        });
      }
    });
  }


  public static void AskForResignation() {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        final int ret = GnubgAPI.AskForResignation();
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.ASK_FOR_RESIGNATION, ret);
          }
        });
      }
    });
  }


  public static void AskForDoubling() {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        final int ret = GnubgAPI.AskForDoubling();
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.ASK_FOR_DOUBLING, ret);
          }
        });
      }
    });
  }


  public static void EvaluateBestMove(final int dices[]) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;
      int moves[] = { 0, 0, 0, 0, 0, 0, 0, 0 };

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.EvaluateBestMove(dices, moves);
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.EVALUATE_BEST_MOVE, moves);
          }
        });
      }
    });
  }


  public static void SetMatchScore(final int AIScore, final int HumanScore) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.SetMatchScore(AIScore, HumanScore);
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_MATCH_SCORE, 1);
          }
        });
      }
    });
  }


  public static void SetGameTurn(final int fTurn, final int fMove) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        GnubgAPI.SetGameTurn(fTurn, fMove);
        if (!(GnuBackgammon.fsm instanceof GServiceFSM)) {
          Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
              if (fsm == GnuBackgammon.fsm)
                GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_GAME_TURN, 1);
            }
          });
        }
      }
    });
  }

  public static void GenerateMoves(final Board b, final int d1, final int d2) {
    dispatchExecutor.submit(new Runnable() {
      BaseFSM fsm = GnuBackgammon.fsm;

      @Override
      public void run() {
        if (fsm != GnuBackgammon.fsm)
          return;
        final int mv[][];

        if (MatchState.fMove == 1)
          mv = GnubgAPI.GenerateMoves(b._board[0], b._board[1], d1, d2);
        else
          mv = GnubgAPI.GenerateMoves(b._board[1], b._board[0], d1, d2);

        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.GENERATE_MOVES, mv);
          }
        });
      }
    });
  }
}
