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
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.logic.MatchState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class Dices extends Group {
  
  private float x0, x1, y;
  private ArrayList<_dice> last;
  private Board b;
  private animation ans[];
  private float animDuration = 0.45f;
  private BaseFSM fsm;
  
  int nr = 0;
  public boolean animating = true;
  boolean firstBigger = true;
  
  private float leftX, rightX;
  
  
  
  class animation extends Group {
    TextureRegion bg[];
    Image img;
    TextureRegionDrawable d;
    int num;
    Random generator = new Random();
    int nr = 0;
    Action act1, act2, act3;
    
    animation(int n) {
      num = n;
      
      bg = new TextureRegion[6];
      for (int i=0;i<6;i++)
        bg[i] = new TextureRegion(GnuBackgammon.atlas.findRegion("d"+(i+1)));

      img = new Image(bg[0]);
      addActor(img);
      setOrigin(img.getWidth()/2, img.getHeight()/2);
      d = (TextureRegionDrawable) img.getDrawable();
      
      act3 = Actions.delay(0.15f);
      act1 = 
          Actions.sequence(
          act3,
          Actions.run(new Runnable() {
            @Override
            public void run() {
              int n = 0;
              while ((n = generator.nextInt(6))==nr);
              nr = n;
              d.getRegion().setRegion(bg[nr]);        
            }
          })
        );
      act2 = Actions.rotateBy(360*2, animDuration);
      addAction(Actions.parallel(Actions.forever(act1), Actions.forever(act2)));
    }

    
    public void show() {
      ((TemporalAction)act2).setDuration(animDuration*(GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast").equals("Fast")?1:2));
      ((DelayAction)act3).setDuration(1.5f*(GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast").equals("Fast")?1:2));
      float w = img.getWidth()+10;
      float x = 0;
      if (MatchState.fMove==0) 
        x=x0-w;
      else 
        x=x1-w;
      
      setX(x+w*num + 11);
      setY(y-img.getHeight()/2);
      
      setVisible(true);
    }
    
    public void hide() {
      setVisible(false);
    }
    
  }
  
  class _dice extends Group {
    boolean disabled = false;
    int value = 0;
    Image i;
    float h = 0;
    
    _dice (int v) {
      value = v;
      TextureRegion r = GnuBackgammon.atlas.findRegion("d"+v);
      i = new Image(r);
      addActor(i);
      h=i.getHeight();
    }
    
    public void disable() {
      disabled=true;
      setColor(0.7f,0.7f,0.7f,0.4f);
    }
    
    public void enable() {
      disabled=false;
      setColor(1, 1, 1, 1);
    }
  }

  
  public Dices(Board _b) {
    last = new ArrayList<_dice>();
    b = _b;
    x0 = GnuBackgammon.Instance.jp.asFloat("dice0", 0);
    x1 = GnuBackgammon.Instance.jp.asFloat("dice1", 0);
    y = b.getHeight()/2;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float _x, float _y, int pointer, int button) {
        if ((!b.availableMoves.hasMoves())&&(!animating))
          GnuBackgammon.fsm.processEvent(Events.DICE_CLICKED, null);
        else if (!animating) { //SWAP DICES
          firstBigger = !firstBigger;
          if (last.size()==2) {
            if (last.get(0).getX()==leftX) {
              last.get(1).addAction(MyActions.moveTo(leftX, last.get(1).getY(), 0.2f));  //setX(leftX);
              last.get(0).addAction(MyActions.moveTo(rightX, last.get(1).getY(), 0.2f)); //setX(rightX);
            } else {
              last.get(1).addAction(MyActions.moveTo(rightX, last.get(1).getY(), 0.2f));  //setX(leftX);
              last.get(0).addAction(MyActions.moveTo(leftX, last.get(1).getY(), 0.2f)); //setX(rightX)
            }
          }
        }
        return true;
      }
    });
    
    ans = new animation[2];
    for (int i=0;i<2;i++) {
      ans[i] = new animation(i);
      ans[i].setVisible(false);
      addActor(ans[i]);
    }
  }

  
  public void clear() {
    firstBigger = true;
    Iterator<_dice> itr = last.iterator();
    while (itr.hasNext()) {
      removeActor(itr.next());
      itr.remove();
    }
    last.clear();
    ans[0].hide();
    ans[1].hide();
  }
  
  public void animate(final int d1, final int d2) {
    animate(d1, d2, true);
  }
  public void animate(final int d1, final int d2, final boolean evt) {
    fsm = GnuBackgammon.fsm;
    animating = true;
    ans[0].show();
    ans[1].show();
    
    Gdx.graphics.setContinuousRendering(true);
    addAction(MyActions.sequence(
        Actions.delay(animDuration*(GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast").equals("Fast")?1:2)),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            ans[0].hide();
            ans[1].hide();
            if (fsm == GnuBackgammon.fsm)
              show(d1, d2, evt);
            Gdx.graphics.setContinuousRendering(false);
            Gdx.graphics.requestRendering();
          }
        })
    ));
  }

  public void show(final int d1, final int d2) {
    show(d1, d2, true);
  }
  public void show(final int d1, final int d2, boolean event) {
    clear();
    if (d1!=d2) {
      last.add(new _dice(d1));
      last.add(new _dice(d2));
    } else {
      for (int i=0; i<4; i++) {
        last.add(new _dice(d1));
      }
    }
    _show();
    //animating = false;
    if ((event)&&(fsm == GnuBackgammon.fsm))
      GnuBackgammon.fsm.processEvent(GameFSM.Events.DICES_ROLLED, get());
  }
  
  
  private void _show() {
    float w = last.get(0).i.getWidth()+10;
    float x = 0;
    if (MatchState.fMove==0) 
      x=x0-w*(last.size()/2);
    else 
      x=x1-w*(last.size()/2);
    
    for (int i=0; i< last.size(); i++) {
      last.get(i).setX(x+w*i + 11);
      last.get(i).setY(y-last.get(i).i.getHeight()/2);
      addActor(last.get(i));
    }
    
    if (last.size()==2) {
      leftX=last.get(0).getX();
      rightX=last.get(1).getX();
    }
  }

  
  public void disable(int n) {
    if (last.size()==0) return;
    boolean found = false;
    
    if (last.size() == 2) { //STANDARD ROLL
      for (int i=0;i<2; i++) {
        if ((last.get(i).value==n)&&(!last.get(i).disabled)) {  //FOUND!
          last.get(i).disable();
          if (b.playedMoves.size()>0)
            b.playedMoves.lastElement().setDice(last.get(i).value); 
          found = true;
        }
      }
      
      if (!found) { //BEAR OFF WITH BIGGER DICE
        if (last.get(0).disabled) {
          if (b.playedMoves.size()>0)
            b.playedMoves.lastElement().setDice(last.get(1).value);
          last.get(1).disable();
        }
        else if (last.get(1).disabled) {
          if (b.playedMoves.size()>0)
            b.playedMoves.lastElement().setDice(last.get(0).value);
          last.get(0).disable();
        }
        
        else if ((!last.get(0).disabled)&&(!last.get(1).disabled)) {
          if (last.get(0).value>n) {
            if (b.playedMoves.size()>0)
              b.playedMoves.lastElement().setDice(last.get(0).value);
            last.get(0).disable();
          }
          else if (last.get(1).value>n) {
            if (b.playedMoves.size()>0)
              b.playedMoves.lastElement().setDice(last.get(1).value);
            last.get(1).disable();
          }
            
        }
      }
    } else { //DOUBLE ROLL: WORK ON FIRST NOT DISABLED DICE
      for (int i=0;i<4; i++) {
        if (!last.get(i).disabled) {
          last.get(i).disable();
          if (b.playedMoves.size()>0)
            b.playedMoves.lastElement().setDice(last.get(i).value);
          return;
        }
      }
    }
  }
 
  
  public void enable(int n) {
    if (last.size() == 2) { //STANDARD ROLL
      for (int i=0;i<2; i++) {
        if ((last.get(i).value==n)&&(last.get(i).disabled)) { 
          last.get(i).enable();
        }
      }
    } else { //DOUBLE ROLL: WORK ON FIRST NOT DISABLED DICE
      for (int i=3;i>=0; i--) {
        if (last.get(i).disabled) {
          last.get(i).enable();
          return;
        }
      }
    }
  }
  
  
  public int[] get() {
    int ret[] = new int[last.size()];
    for (int i=0;i<last.size();i++)
      ret[i] = last.get(i).value;
    return ret;
  }
  
  public boolean getDiceOrder() {
    return firstBigger; 
  }

}