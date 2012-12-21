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

import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.aicalls.*;
import it.alcacoop.backgammon.logic.AILevels;

public class AICalls {
  
  static AIThread thread = new AIThread();
  
  static {
    thread = new AIThread();
  }

  public static void SetAILevel(AILevels l) {
    thread.post(new SetAILevelAICall(l));
  }
  
  public static void SetBoard(int b1[], int b2[]) {
    thread.post(new SetBoardAICall(b1, b2));
  }
  
  public static void AcceptResign(int r) {
    thread.post(new AcceptResignAICall(r));
  }

  public static void AcceptDouble() {
    thread.post(new AcceptDoubleAICall());
  }
  
  public static void UpdateMSCubeInfo(int c, int f) {
    thread.post(new UpdateMSCubeInfoAICall(c, f));
  }
  
  public static void AskForResignation() {
    thread.post(new AskForResignationAICall());
  }

  public static void AskForDoubling() {
    thread.post(new AskForDoublingAICall());
  }

  public static void EvaluateBestMove(int dices[]) {
    thread.post(new EvaluateBestMoveAICall(dices));
  }
  
  public static void SetMatchScore(int AIScore, int HumanScore) {
    thread.post(new SetMatchScoreAICall(AIScore, HumanScore));
  }
  
  public static void SetGameTurn(int fTurn, int fMove) {
    thread.post(new SetGameTurnAICall(fTurn, fMove));
  }
  
  public static void GenerateMoves(Board b, int d1, int d2) {
    thread.post(new GenerateMovesAICall(b, d1, d2));
  }
}