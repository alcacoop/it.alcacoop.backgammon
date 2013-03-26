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
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DoublingCube extends Group {
  
  private TextureRegion region;
  private Image i;
  int value = 64;
  float up, down, center, x;
  private Board b;
  
  public DoublingCube(Board _b) {
    b = _b;
    region = GnuBackgammon.atlas.findRegion("c"+value);
    i = new Image(region);
    i.setAlign(Align.left);
    addActor(i);
    
    evaluateX();
    
    center = (b.getHeight()-i.getHeight())/2 - GnuBackgammon.Instance.jp.asFloat("pos", 0)/5.9f;
    down = GnuBackgammon.Instance.jp.asFloat("pos", 0)*2;
    up = b.getHeight() - i.getHeight() - GnuBackgammon.Instance.jp.asFloat("pos", 0)*2;
    
    setX(x);
    setY(center);
    setVisible(true);
  }

  public void setValue(int v) {
    evaluateX();
    setX(x);
    value = v;
    if (v>64) value = 64;
    region = GnuBackgammon.atlas.findRegion("c"+value);
    TextureRegionDrawable d = new TextureRegionDrawable(region);
    i.setDrawable(d);
    if (MatchState.fCubeOwner == -1) setY(center);
    else if (MatchState.fCubeOwner==1) addAction(MyActions.moveTo(getX(), up, 0.3f));
    else addAction(MyActions.moveTo(getX(), down, 0.3f));
  }
  
  public void setValue (int v, int o) {
    evaluateX();
    setX(x);
    value = v;
    if (v==1) value = 64;
    region = GnuBackgammon.atlas.findRegion("c"+value);
    TextureRegionDrawable d = new TextureRegionDrawable(region);
    i.setDrawable(d);
    if (o == -1) setY(center);
    else if (o==1) setY(up);
    else setY(down);
  }
  
  public void reset() {
    setValue(64);
  }
  
  
  private void evaluateX(){
    if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION","AntiClockwise").equals("AntiClockwise"))
      x = b.getWidth()-GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) -
        i.getWidth()/2 -
        GnuBackgammon.Instance.jp.asFloat("pos", 0)/3.7f;
    else
      x = GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) -
        i.getWidth()/2 +
        GnuBackgammon.Instance.jp.asFloat("pos", 0)/2f;
  }
}
