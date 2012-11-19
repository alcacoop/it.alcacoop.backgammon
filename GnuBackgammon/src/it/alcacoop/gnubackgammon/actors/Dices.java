package it.alcacoop.gnubackgammon.actors;

import java.util.ArrayList;
import java.util.Iterator;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.MatchState;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class Dices extends Group {
  
  private float x, y;
  private ArrayList<_dice> last;
  private Board b;
  
  
  class _dice extends Group {
    boolean disabled = false;
    int value = 0;
    Image i;
    
    _dice (int v) {
      value = v;
      TextureRegion r = GnuBackgammon.atlas.findRegion("d"+v);
      r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
      i = new Image(r);
      addActor(i);
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


  
  
  public Dices(Board _b, float _x, float _y) {
    last = new ArrayList<_dice>();
    x = _x;
    y = _y-28;
    b = _b;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        if (!b.availableMoves.hasMoves())
          GameScreen.fsm.processEvent(Events.DICE_CLICKED, null);
        return true;
      }
    });
  }

  
  public void clear() {
    Iterator<_dice> itr = last.iterator();
    while (itr.hasNext()) {
      removeActor(itr.next());
      itr.remove();
    }
    last.clear();
  }
  
  
  public void show(int d1, int d2) {
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
  }
  
  private void _show() {
    float x = 0;
    if (MatchState.fMove==0) x=this.x-60*(last.size()/2)+216;
    else x=this.x-60*(last.size()/2)-210;
    
    for (int i=0; i< last.size(); i++) {
      last.get(i).setX(x+60*i);
      last.get(i).setY(y);
      addActor(last.get(i));
    }
  }

  
  public void disable(int n) {
    boolean found = false;
    
    if (last.size() == 2) { //STANDARD ROLL
      for (int i=0;i<2; i++) {
        if ((last.get(i).value==n)&&(!last.get(i).disabled)) { 
          last.get(i).disable();
          b.playedMoves.lastElement().setDice(last.get(i).value);
          found = true;
        }
      }
      if (!found) { //BEAR OFF WITH BIGGER DICE
        if (last.get(0).disabled) {
          b.playedMoves.lastElement().setDice(last.get(1).value);
          last.get(1).disable();
        }
        if (last.get(1).disabled) {
          b.playedMoves.lastElement().setDice(last.get(0).value);
          last.get(0).disable();
        }
        
        if ((!last.get(0).disabled)&&(!last.get(1).disabled)) {
          if ((!last.get(0).disabled)&&(last.get(0).value>n)) {
            b.playedMoves.lastElement().setDice(last.get(0).value);
            last.get(0).disable();
          }
          else if ((!last.get(1).disabled)&&(last.get(1).value>n)) {
            b.playedMoves.lastElement().setDice(last.get(1).value);
            last.get(1).disable();
          }
            
        }
      }
    } else { //DOUBLE ROLL: WORK ON FIRST NOT DISABLED DICE
      for (int i=0;i<4; i++) {
        if (!last.get(i).disabled) {
          last.get(i).disable();
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
      for (int i=0;i<4; i++) {
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

}