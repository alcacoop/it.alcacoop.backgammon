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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class AvailableMoves {

  public ArrayList<Integer> dices;
  private ArrayList<int[]> moves;
  private Board b;


  public AvailableMoves(Board _b) {
    dices = new ArrayList<Integer>();
    moves = new ArrayList<int[]>();
    b = _b;
  }

  public void setMoves(int _moves[][]) {
    int _dices[] = b.dices.get();
    moves.clear();
    dices.clear();

    if (_moves != null) {
        Collections.addAll(moves, _moves);
      evaluatePlayableDices(_dices);
    }
  }


  private void evaluatePlayableDices(int d[]) {
    boolean all_presents = false;
    int max_moves = 0;
    for (int i = 0; i < moves.size(); i++) {
      for (int j = 0; j < 4; j++) {
        if (moves.get(i)[j * 2] != -1)
          max_moves = (j + 1);
      }
      if (((d.length == 4) && (max_moves == 4)) || ((d.length == 2) && (max_moves == 2))) {
        all_presents = true;
        break;
      }
    }

    if (all_presents) {
      for (int i = 0; i < d.length; i++)
        dices.add(d[i]);
    } else { // NOT ALL DICES ARE PLAYABLE
      if (d.length == 4) { // DOUBLING
        for (int i = 0; i < max_moves; i++)
          dices.add(d[0]);
        for (int i = 0; i < 4 - max_moves; i++)
          b.dices.disable(d[0]);
      } else { // NON DOUBLING ROLL
        int dice = moves.get(0)[0] - moves.get(0)[1];
        if ((dice != d[0]) && (dice != d[1])) {// BEAR OFF
          if (d[0] >= dice) {
            dices.add(d[0]);
            b.dices.disable(d[1]);
          } else {
            dices.add(d[1]);
            b.dices.disable(d[0]);
          }
        } else {
          if (d[0] == dice) {
            dices.add(d[0]);
            b.dices.disable(d[1]);
          } else {
            dices.add(d[1]);
            b.dices.disable(d[0]);
          }
        }
      }
    }
  }


  public int[] getPoints(int nPoint) {

    ArrayList<Integer> ret = new ArrayList<Integer>();

    if (moves.size() == 0)
      return null; // NO MOVES AVAILABLE
    if ((b._board[MatchState.fMove][24] > 0) && (nPoint != 24)) // CHECKERS ON BAR
      return null;

    for (int k = 0; k < dices.size(); k++) {
      for (int i = 0; i < moves.size(); i++) {
        for (int j = 0; j < 4; j++) {

          if (moves.get(i)[2 * j] == nPoint) {
            int max_point = b.bearingOff();
            if (max_point == -1) { // STANDARD MOVE...
              if ((moves.get(i)[2 * j] - moves.get(i)[2 * j + 1] == dices.get(k)) && (moves.get(i)[2 * j + 1] != -1)) {
                ret.add(moves.get(i)[2 * j + 1]);
              }
            } else { // BOFF
              if (moves.get(i)[2 * j] - moves.get(i)[2 * j + 1] == dices.get(k)) {
                // STANDARD BEAROFF
                ret.add(moves.get(i)[2 * j + 1]);
              }
              else if ((moves.get(i)[2 * j] - moves.get(i)[2 * j + 1] <= dices.get(k)) && (nPoint == max_point) && (moves.get(i)[2 * j + 1] < 0)) {
                // BEARFOFF WITH BIGGER DICE
                ret.add(moves.get(i)[2 * j + 1]);
              }

            }
          }
        }
      }
    }

    List<Integer> unique = new ArrayList<Integer>(new HashSet<Integer>(ret));
    // RETURN unique AS STANDARD ARRAY
    int[] r = new int[unique.size()];
    for (int i = 0; i < unique.size(); i++) {
      r[i] = unique.get(i);
    }
    return r;
  }


  public void dropDice(int d) {
    int idx = dices.indexOf(d);
    if (idx == -1) {// BEARING OFF WITH GREATER DICE
      Iterator<Integer> itr = dices.iterator();
      while (itr.hasNext()) {
        if (itr.next() > d) {
          itr.remove();
          break;
        }
      }
    } else
      // REMOVE PLAYED DICE
      dices.remove(idx);
  }


  public boolean hasMoves() {
    return (!dices.isEmpty() && (!b.gameFinished()));
  }


  public ArrayList<int[]> removeMoves(int orig, int dest) {
    ArrayList<int[]> removed = new ArrayList<int[]>();
    Iterator<int[]> itr = moves.iterator();
    while (itr.hasNext()) {
      boolean matched = false;
      int mv[] = itr.next();
      for (int i = 0; i < 4; i++) {
        if ((mv[2 * i] == orig) && (mv[2 * i + 1] == dest)) {
          matched = true;
          break;
        }
      }
      if (!matched) {
        itr.remove();
        removed.add(mv);
      }
    }
    return removed;
  }


  public void restoreMoves(ArrayList<int[]> rm) {
    for (int i = 0; i < rm.size(); i++)
      moves.add(rm.get(i));
  }

  public int getSize() {
    return moves.size();
  }
}
