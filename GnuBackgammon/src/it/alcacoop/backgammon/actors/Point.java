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
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;



public class Point extends Group {

  private TextureRegion region;
  private int nPoint;
  private Color color;
  private Image img;
  public boolean isTarget;
    

  public Point(int _nPoint) {
    nPoint = _nPoint;
    isTarget = false;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        System.out.println(nPoint());
        GnuBackgammon.fsm.processEvent(Events.POINT_TOUCHED, nPoint());
        return true;
      }
    });

    region = GnuBackgammon.atlas.findRegion("point");
    img = new Image(region);
    color = img.getColor().cpy();
    img.setColor(0, 0, 0, 0);
    
    img.setScaling(Scaling.none);
    img.setAlign(com.badlogic.gdx.scenes.scene2d.utils.Align.bottom+com.badlogic.gdx.scenes.scene2d.utils.Align.left);
    addActor(img);
  }
  
  
  public void highlight() {
    if (GnuBackgammon.Instance.prefs.getString("LMOVES", "Yes").equals("Yes"))
      img.setColor(color);
    isTarget = true;
  }
  
  
  public void reset() {
    img.setColor(0,0,0,0);
    isTarget = false;
  }

  private int nPoint() {
    if ((nPoint == 24) || (nPoint==-1)) return nPoint; //BAR||BOFF
    
    if (MatchState.fMove==0) { //BLACK
      if (GnuBackgammon.Instance.appearencePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise")) {
        return nPoint;
      } else {
        if (nPoint>11) return 35-nPoint;
        else return 11-nPoint;
      }
    } else { //WHITE
      if (GnuBackgammon.Instance.appearencePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise")) {
      return 23-nPoint;
      } else {
        if (nPoint<=11) return 35-(23-nPoint);
        else return 11-(23-nPoint);
      }
    }
    
  }
  
  @Override
  public void setX(float x) {
    super.setX(x-img.getWidth()/2);
  }
  
  public float getHeight() {
    return img.getHeight();
  }
}
