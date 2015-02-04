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
import it.alcacoop.backgammon.actors.Checker;

import java.util.ArrayList;

public class Move {
  
  public int from, to;
  public boolean hitted = false;
  int dice = 0;
  Board b;
  int color;
  public ArrayList<int[]> removedMoves;

  public Move(Board _b, int _from, int _to) {
    removedMoves = new ArrayList<int[]>();
    b = _b;
    from = _from;
    to = _to;
    color = MatchState.fMove;
  }

  public void setDice(int d) {
    dice = d;
  }
  
  public void setRemovedMoves(ArrayList<int[]> rm) {
    removedMoves = rm;
  }
  
  public void undo() {
    b.availableMoves.restoreMoves(removedMoves);
    
    b.points.reset();
    if (b.selected!=null) {
      b.selected.highlight(false);
      b.selected = null;
    }
      
    Checker c = b.getChecker(color, to);
    
    if (hitted) {
      int _c = c.getSpecularColor();
      int _x = c.getSpecularPosition();
      Checker ch = b.getChecker(_c, 24); //PICK HITTED FROM THE BAR
      b._board[_c][24]--;
      int y = b._board[_c][_x]++;
      ch.reset(_x, y, 0.2f);
    }
    
    if (to!=-1) b._board[color][to]--;
    else b.bearedOff[color]--;
    int y = b._board[color][from]++;
    
    c.reset(from, y, 0.2f);
    b.dices.enable(dice);
    b.availableMoves.dices.add(dice);
  }
}
