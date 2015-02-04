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
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.badlogic.gdx.Gdx;

public class AICalls {
  private static ExecutorService dispatchExecutor;

  static {
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  /* LOCKING IMPLEMENTATIONS */
  public static class Locking {
    public static int InitRNG(final int type) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.InitRNG(type);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int SetAILevel(final int l) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetAILevel(l);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int SetBoard(final int b1[], final int b2[]) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetBoard(b1, b2);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int UpdateMSCubeInfo(final int nCube, final int fCubeOwner) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.UpdateMSCubeInfo(nCube, fCubeOwner);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int SetMatchScore(final int AIScore, final int HumanScore) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetMatchScore(AIScore, HumanScore);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int SetGameTurn(final int fTurn, final int fMove) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetGameTurn(fTurn, fMove);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int[] RollDice() {
      Future<int[]> f = dispatchExecutor.submit(new Callable<int[]>() {
        @Override
        public int[] call() throws Exception {
          int[] ds = { 0, 0 };
          GnubgAPI.RollDice(ds);
          return ds;
        }
      });
      int d[] = { 0, 0 };
      try {
        d = f.get();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return d;
    }

    public static int[][] GenerateMoves(final int b1[], final int b2[], final int d1, final int d2) {
      Future<int[][]> f = dispatchExecutor.submit(new Callable<int[][]>() {
        @Override
        public int[][] call() throws Exception {
          int[][] mv = GnubgAPI.GenerateMoves(b1, b2, d1, d2);
          return mv;
        }
      });
      int mv[][] = { {}, {} };
      try {
        mv = f.get();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return mv;
    }

    public static int SetCubeUse(final int fCubeUse) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetCubeUse(fCubeUse);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

    public static int SetCrawford(final int fCrawford) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetCrawford(fCrawford);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }


    public static int SetAILevel(final AILevels l) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetAILevel(l.ordinal());
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }


    public static int InitializeEnvironment(final String s) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.InitializeEnvironment(s);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }


    public static int SetGameVariant(final int type) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetGameVariant(type);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }


    public static int SetMatchTo(final int nMatchTo) {
      Future<Integer> f = dispatchExecutor.submit(new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
          GnubgAPI.SetMatchTo(nMatchTo);
          return 1;
        }
      });
      try {
        return f.get();
      } catch (Exception e) {
        e.printStackTrace();
        return 0;
      }
    }

  }

  /* END LOCKING IMPLEMENTATIONS */


  /* MESSAGE SENDING IMPLEMENTATIONS */
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
        Gdx.app.postRunnable(new Runnable() {
          @Override
          public void run() {
            if (fsm == GnuBackgammon.fsm)
              GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_GAME_TURN, 1);
          }
        });
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
