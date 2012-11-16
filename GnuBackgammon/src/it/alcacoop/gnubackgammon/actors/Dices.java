package it.alcacoop.gnubackgammon.actors;


import java.util.ArrayList;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.MatchState;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Dices extends Group {

  private Image d[][];
  private float x, y;
  private ArrayList<Integer> last;
  private Board b;
  
  public Dices(Board _b, float _x, float _y) {
    last = new ArrayList<Integer>();
    x = _x;
    y = _y-28;
    b = _b;
    
    d = new Image[4][6];
    for (int i=0; i<4; i++) {
    for (int j=0; j<6; j++) {
      TextureRegion r = GnuBackgammon.atlas.findRegion("d"+(j+1));
      r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
      d[i][j] = new Image(r);
      addActor(d[i][j]);
      d[i][j].setVisible(false);
    }
    }
  }
  
  
  public void hide() {
    for (int i=0; i<4; i++) 
      for (int j=0; j<6; j++) {
        d[i][j].setVisible(false);
        d[i][j].setColor(1,1,1,1f);
      }
  }

  
  public void show(int d1, int d2) {
    last.clear();
    if (d1!=d2) {
      last.add(d1);
      last.add(d2);
    } else {
      for (int i=0; i<4; i++)
        last.add(d1);
    }
    _show(last);
  }
  
  private void _show(ArrayList<Integer> _d) {
    hide();
    float x = 0;
    if (MatchState.fMove==0) x=this.x-60*(_d.size()/2)+216;
    else x=this.x-60*(_d.size()/2)-210;
    
    for (int i=0; i< _d.size(); i++) {
      d[i][_d.get(i)-1].setX(x+60*i);
      d[i][_d.get(i)-1].setY(y);
      d[i][_d.get(i)-1].setVisible(true);
    }
  }

  
  public void disable(int nn) {
    int n = nn;
    if(b.bearingOff()>=0) {
      int max_dice = 0;
      int is_present = 0;
      for(int i=0;i<last.size();i++) {
        if(last.get(i)>=max_dice)
          max_dice = last.get(i);
        if(last.get(i)==nn)
          is_present++;
      }
      
      if(is_present==0)
        n = max_dice;
    }
    
    if (last.size() == 2) {
      for (int i=0;i<last.size(); i++) {
        if (last.get(i)==n) { 
          d[i][n-1].setColor(0.7f,0.7f,0.7f,0.4f);
          return;
        }
      }
    } else { //DOUBLE
      for (int i=0;i<4; i++) {
        float c = d[i][n-1].getColor().a;
        if (c!=0.4f) { 
          d[i][n-1].setColor(0.7f,0.7f,0.7f,0.4f);
          return;
        }
      }
    }
  }
  
  
  public int[] get() {
    int ret[] = new int[last.size()];
    for (int i=0;i<last.size();i++)
      ret[i] = last.get(i);
    return ret;
  }

  //REMOVE A NON PLAYABLE DIXC
  public void remove(int n) {
    disable(n);
    last.remove(last.indexOf(n));
  }
}