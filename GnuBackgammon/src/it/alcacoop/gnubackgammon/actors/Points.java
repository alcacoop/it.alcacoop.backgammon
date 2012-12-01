package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.MatchState;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Points extends Group {

  Point points[];
  Point bar[];
  Point boff[];
  
  public Points(Board b) {
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
  
  
  private int rotate(int i) {
    if (MatchState.fMove==0) return i;
    return 23-i;
  }
}
