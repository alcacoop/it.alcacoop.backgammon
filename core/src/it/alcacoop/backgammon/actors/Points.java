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

package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Points extends Group {

  private Point points[];
  private Point bar[];
  private Point boff[];
  private Board b;
  
  public Points(Board _b) {
    b = _b;
    bar = new Point[2];
    boff = new Point[2];
    points = new Point[24];
    for (int i = 0; i<24; i++) {
      points[i] = new Point(i);
      Vector2 p = b.pos[i];
      points[i].setX(p.x);
      if (i>11) 
        points[i].setY(p.y-points[i].getHeight()+3);
      else
        points[i].setY(p.y-3);
      addActor(points[i]);
    }
    
    bar[0] = new Point(24);
    bar[0].setX(b.pos[24].x - GnuBackgammon.Instance.jp.asFloat("pos", 0)/2);
    bar[0].setY(b.getY() + GnuBackgammon.Instance.jp.asFloat("up", 0)-bar[0].getHeight()-b.checkers[0][0].getWidth()/2);
    addActor(bar[0]);

    bar[1] = new Point(24);
    bar[1].setX(b.pos[24].x - GnuBackgammon.Instance.jp.asFloat("pos", 0)/2);
    bar[1].setY(b.getY() + GnuBackgammon.Instance.jp.asFloat("down", 0)+b.checkers[0][0].getWidth()/2);
    addActor(bar[1]);
    
    boff[0] = new Point(-1);
    boff[0].setX(b.getX() + GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0)/2);
    boff[0].setY(b.getY() + GnuBackgammon.Instance.jp.asFloat("down", 0)-3);
    addActor(boff[0]);
    
    boff[1] = new Point(-1);
    boff[1].setX(b.getX() + GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0)/2);
    boff[1].setY(b.getY() + GnuBackgammon.Instance.jp.asFloat("up", 0)-boff[1].getHeight()+4);
    addActor(boff[1]);
  }
  
  
  public Point get(int i) {
    if (i==24) return bar[MatchState.fMove];
    if (i==-1) return boff[MatchState.fMove];
    return points[rotate(i)];
  }

  
  public void reset() {
    for (int i=0; i<points.length; i++)
      points[i].reset();
    for (int i=0; i<2; i++) {
      bar[i].reset();
      boff[i].reset();
    }
  }
  
  
  private int rotate(int nPoint) {
    if (MatchState.fMove==0) { //BLACK
      if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise")) {
        return nPoint;
      } else {
        if (nPoint>11) return 35-nPoint;
        else return 11-nPoint;
      }
    } else { //WHITE
      if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise")) {
      return 23-nPoint;
      } else {
        if (nPoint<=11) return 35-(23-nPoint);
        else return 11-(23-nPoint);
      }
    }
  }
  
  public void resetBoff() {
    float x = 0;
    if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise")) {
      x = b.getX() + GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0)/2;
    } else {
      x = b.getX() + b.getWidth()-GnuBackgammon.Instance.jp.asFloat("pos_bo", 0);
    }
    boff[0].setX(x);
    boff[1].setX(x);
  }
}
