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
import it.alcacoop.backgammon.actions.MySequenceAction;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;


public class Checker extends Group {

  private Board board;
  private Label label;
  private Image img, imgh;
  private MySequenceAction act;
  private MoveToAction mv;
  private BaseFSM fsm;
  private final TextureRegionDrawable dr;
  
  public int color = 0;
  public int boardX = -1; 
  public int boardY = -1;
  
  

  public Checker(Board _board, int _color) {
    super();
    
    boardX = boardY= 0;
    color = _color;
    board = _board;

    TextureRegion region;
    
    if (color==1) {//WHITE
      region = GnuBackgammon.atlas.findRegion("cw");
      LabelStyle styleBlack = new LabelStyle(GnuBackgammon.font, Color.BLACK);
      label = new Label("1", styleBlack);
      label.setColor(0,0,0,1);
    } else { 
      region = GnuBackgammon.atlas.findRegion("cb");
      LabelStyle styleWhite = new LabelStyle(GnuBackgammon.font, Color.WHITE);
      label = new Label("1", styleWhite);
      label.setColor(1,1,1,1);
    }

    dr = new TextureRegionDrawable(region);
    img = new Image(dr);
    
    region = GnuBackgammon.atlas.findRegion("ch");
    imgh = new Image(region);
    imgh.setScaling(Scaling.none);
    imgh.addAction(Actions.forever(Actions.sequence(Actions.fadeIn(0.4f), Actions.fadeOut(0.2f))));
    
    addActor(img);
    addActor(label);
    addActor(imgh);
    label.setText("");
    label.setWrap(false);
    label.setAlignment(Align.bottom|Align.center);
  }
  
  
  public void reset(int _boardX, int _boardY) {
    reset(_boardX, _boardY, 0);
  }
  public void reset(int _boardX, int _boardY, float t) {
    Vector2 _p = board.getBoardCoord(color, _boardX, _boardY);
    boardX = _boardX;
    boardY = _boardY;
    
    if (t==0) {
      setX(_p.x+this.getWidth()/2);
      setY(_p.y);
    }
    else {
      mv = Actions.moveTo(_p.x, _p.y, t);
      act = MyActions.sequence(mv, Actions.run(new Runnable() {
        @Override
        public void run() {
          GnuBackgammon.fsm.processEvent(Events.CHECKER_RESETTED, null);
        }
      }));
      addAction(act);
    }
    
    if ((boardY>4)&&(boardX!=-1)) { 
      label.setText(""+(boardY+1));
      label.setX(img.getImageWidth()/2-label.getWidth()/1.9f);
      label.setY(img.getImageHeight()/2-label.getHeight()/2.3f);
    } else 
      label.setText("");
    
    imgh.setVisible(false);
  }

  
  
  public void moveTo(int x){
    moveToDelayed(x, 0);
  }
  public void moveToDelayed(final int x, float delay){
    fsm = GnuBackgammon.fsm;
    board.removeActor(this);
    board.addActor(this);
    
    float tt = 0.2f*(GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast").equals("Fast")?1:2.5f);
    if (x==24) tt=0.25f;
    
    final int y;
    
    if (x>=0)
      y = board._board[color][x];
    else //BEARED OFF
      y = board.bearedOff[color];
    
    final Vector2 _p = board.getBoardCoord(color, x, y);
    final int d = boardX-x;
    setPosition(x);
    
    final Checker c = this;
    act = MyActions.sequence(
        Actions.delay(delay),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            highlight(false);
            board.selected = null;
            board.points.reset();
            if ((boardY<5)||(boardX==-1)) label.setText("");
          }
        }),
        Actions.moveTo(_p.x, _p.y, tt),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            GnuBackgammon.Instance.snd.playMoveStop();
            if ((boardY>4)&&(boardX!=-1)) {
              label.setText(""+(boardY+1));
              label.setX(img.getImageWidth()/2-label.getWidth()/1.9f);
              label.setY(getHeight()/2-label.getHeight()/2.3f);
            }
            if (fsm==GnuBackgammon.fsm) {
              if (!board.checkHit()) fsm.processEvent(Events.PERFORMED_MOVE, null);
              if ((x<24)&&(d>0)) board.dices.disable(d);
            }
          }
        })
        );
    c.addAction(act);
  }
  
  
  public void setPosition(int x) {
    board._board[color][boardX]--;
	  boardX = x;
	  
	  if (x>=0) //ON_TABLE!!
	    boardY = board._board[color][boardX]++;
	  else
	    boardY = board.bearedOff[color]++;
	     
  }

  
  public int getSpecularColor() {
    if (color==0) return 1;
    else return 0;
  }
  
  public int getSpecularPosition() {
    return 23-boardX;
  }

  public void highlight(boolean b) {
    if (b) GnuBackgammon.Instance.snd.playMoveStart();
    imgh.setVisible(b);
    Gdx.graphics.requestRendering();
    if (b) Gdx.graphics.setContinuousRendering(true);
    else Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }
  
  @Override
  public Actor hit(float x, float y, boolean touchable) {
    return null;
  }
  
  @Override
  public void setX(float x) {
    super.setX(x-img.getWidth()/2);
  }
  
  public float getWidth() {
    return img.getWidth();
  }
  
  public float getHeight() {
    return img.getHeight();
  }
  
  public void resetActions() {
    if (mv!=null) {
      this.removeAction(act);
      mv.setDuration(0);
      mv = null;
      act = null;
    }
  }
}
