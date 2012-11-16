package it.alcacoop.gnubackgammon.actors;

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
      if (i<12) points[i].setY(b.board.getY()+380);
      else points[i].setY(b.board.getY()+50);
      addActor(points[i]);
    }
    
    bar[0] = new Point(24);
    bar[0].setX(b.board.getX()+475);
    bar[0].setY(b.board.getY()+50);
    addActor(bar[0]);
    
    bar[1] = new Point(24);
    bar[1].setX(b.board.getX()+475);
    bar[1].setY(b.board.getY()+378);
    addActor(bar[1]);
    
    boff[0] = new Point(-1);
    boff[0].setX(b.board.getX()+918);
    boff[0].setY(b.board.getY()+50);
    addActor(boff[0]);
    
    boff[1] = new Point(-1);
    boff[1].setX(b.board.getX()+918);
    boff[1].setY(b.board.getY()+378);
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
    if (MatchState.fMove==1) return i;
    return 23-i;
  }
}
