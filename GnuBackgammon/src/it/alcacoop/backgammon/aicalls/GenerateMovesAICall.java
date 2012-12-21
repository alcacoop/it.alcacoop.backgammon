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

package it.alcacoop.backgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class GenerateMovesAICall implements Runnable {

  int d1, d2; 
  Board b;
  BaseFSM fsm;
  
  public GenerateMovesAICall(Board _b, int _d1, int _d2) {
    d1 = _d1;
    d2 = _d2;
    b = _b;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    final int mv[][];
    
    if (MatchState.fMove==1)
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
  
}