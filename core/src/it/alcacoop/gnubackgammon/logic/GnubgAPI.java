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

package it.alcacoop.gnubackgammon.logic;

public class GnubgAPI {
  public static native void InitializeEnvironment(String str);
  public static native void InitRNG(int type);
  public static native void SetAILevel(int level);
  public static native void RollDice(int d[]);
  public static native void SetBoard(int b1[], int b2[]);
  public static native int AcceptResign(int r);
  public static native int AcceptDouble();
  public static native void UpdateMSCubeInfo(int nCube, int fCubeOwner);
  public static native int AskForResignation();
  public static native int AskForDoubling();
  public static native void EvaluateBestMove(int dices[], int move[]);
  public static native void SetMatchScore(int AIScore, int HumanScore);
  public static native void SetGameTurn(int fTurn, int fMove);
  public static native void SetMatchTo(int nMatchTo);
  public static native int[][] GenerateMoves(int b1[], int b2[], int d1, int d2);
  public static native void SetGameVariant(int type);
  public static native void SetCubeUse(int fCubeUse);
  public static native void SetCrawford(int fCrawford);
  public static native void SetPostCrawford(int fPostCrawford);
}
