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
import it.alcacoop.backgammon.ui.GameMenuPopup;


public class MatchState {

  public static int[][] board = {
      { 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0 },// BLACK (HUMAN) BGV
      { 0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0 },// WHITE (PC)
      { 0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0 },// BLACK (HUMAN) NGV
      { 0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0 },// WHITE (PC) NGV

      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },// ALL BEARED OFF
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },// ALL BEARED OFF
  };

  public static final int RNG_MERSENNE = 0;
  public static final int RNG_ISAAC = 1;

  /* NOTE: ON NATIVE API HUMAN=1 AND PC=0 */
  public static AILevels currentLevel = AILevels.EXPERT;
  public static int fMove = 1; // CHI HA I DADI IN MANO (1=PC 0=HUMAN)
  public static int fTurn = 1; // CHI DEVE DECIDERE (1=PC 0=HUMAN)
  public static int fCubeOwner = -1; // (-1=BAR 1=PC 0=HUMAN)
  public static int nCube = 1; // VALORE ATTUALE DEL CUBO
  public static int fCrawford = 0; // REGOLA DI CRAWFORD
  public static int nMatchTo = 0;
  public static int[] anScore = { 0, 0 }; // MATCH SCORES
  public static int bgv = 0; // 0=BACKGAMMON 1=NACKGAMMON 2=RESTORED_GAME
  public static int fCubeUse = 0; // USING CUBE
  public static int matchType = 0; // 0=SINGLE PLAYER, 1=TWO PLAYERS, 2=FIBS, 3=GSERVICE
  public static boolean fPostCrawford = false; // POST CRAWFORD RULE
  public static boolean fCrafwordGame = false;
  public static int resignValue = 0;
  public static int FibsDirection = 0;
  public static int FibsColor = 0;

  public static int win_type = 0; // 1=SINGLE, 2=GAMMON, 3=BACKGAMMON

  public static String pl0;
  public static String pl1;


  public static void SwitchTurn() {
    SwitchTurn(true);
  }
  public static void SwitchTurn(boolean event) {
    Float left1 = GnuBackgammon.Instance.board.rollBtn.getX();
    Float left2 = GnuBackgammon.Instance.board.doubleBtn.getX();
    if (fMove == 0) {
      if (event)
        SetGameTurn(1, 1);
      GnuBackgammon.Instance.board.rollBtn.setX(Math.min(left1, left2));
      GnuBackgammon.Instance.board.doubleBtn.setX(Math.max(left1, left2));
    } else {
      if (event)
        SetGameTurn(0, 0);
      GnuBackgammon.Instance.board.rollBtn.setX(Math.max(left1, left2));
      GnuBackgammon.Instance.board.doubleBtn.setX(Math.min(left1, left2));
    }
    GnuBackgammon.Instance.board.showArrow();
  }

  public static void SetGameVariant(int type) {
    MatchState.bgv = type;
    AICalls.Locking.SetGameVariant(type);
  }

  public static void UpdateMSCubeInfo(int cubeValue, int owner) {
    MatchState.fCubeOwner = owner;
    MatchState.nCube = cubeValue;
    AICalls.Locking.UpdateMSCubeInfo(MatchState.nCube, MatchState.fCubeOwner);
  }

  public static void SetMatchTo(String sMatchTo) {
    MatchState.nMatchTo = Integer.parseInt(sMatchTo);
    AICalls.Locking.SetMatchTo(MatchState.nMatchTo);
  }

  public static void SetMatchScore(int AIScore, int HumanScore) {
    MatchState.anScore[0] = HumanScore;
    MatchState.anScore[1] = AIScore;
    if ((AIScore == 0) && (HumanScore == 0)) { // RESET CRAWFORD LOGIC
      fPostCrawford = false;
      fCrafwordGame = false;
      resignValue = 0;
      nCube = 1;
    }
    if (((nMatchTo - AIScore <= 1) || (nMatchTo - HumanScore <= 1)) && (!fCrafwordGame) && (!fPostCrawford)) {
      // CRAWFORD GAME!
      fCrafwordGame = true;
      fPostCrawford = true;
    }
    else if (((nMatchTo - AIScore <= 1) || (nMatchTo - HumanScore <= 1)) && (fCrafwordGame) && (fPostCrawford)) {
      // POST CRAWFORD GAMES!
      fCrafwordGame = false;
    }
    AICalls.Locking.SetMatchScore(MatchState.anScore[1], MatchState.anScore[0]);
  }

  public static void SetAILevel(AILevels level) {
    MatchState.currentLevel = level;
    AICalls.Locking.SetAILevel(level.ordinal());
  }

  public static void SetGameTurn(int fTurn, int fMove) {
    MatchState.fMove = fMove;
    MatchState.fTurn = fTurn;
    AICalls.SetGameTurn(MatchState.fTurn, MatchState.fMove);
    GameMenuPopup.setDisabledButtons();
  }

  public static void SetCubeUse(int fCubeUse) {
    MatchState.fCubeUse = fCubeUse;
    AICalls.Locking.SetCubeUse(fCubeUse);
  }

  public static void SetCrawford(int fCrawford) {
    MatchState.fCrawford = fCrawford;
    AICalls.Locking.SetCrawford(fCrawford);
  }

  public static void setBoardFromString(String sbb, String sbw) {
    String ssw[] = sbw.split(":");
    String ssb[] = sbb.split(":");
    for (int i = 0; i < 25; i++) {
      MatchState.board[4][i] = Integer.parseInt(ssb[i]);
      MatchState.board[5][i] = Integer.parseInt(ssw[i]);
    }
  }
}
