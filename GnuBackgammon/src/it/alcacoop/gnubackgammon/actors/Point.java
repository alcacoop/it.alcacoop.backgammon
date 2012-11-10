package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.Board;
import it.alcacoop.gnubackgammon.logic.MatchState;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
  private Board board;
    

  public Point(Board _board, int _nPoint) {
    super();
    nPoint = _nPoint;
    board = _board;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        return true;
      }
      public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        board.highlightPoints(Point.this.nPoint(), 1);
      }
    });

    region = GnuBackgammon.atlas.findRegion("point");
    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    img = new Image(region);
    color = img.getColor().cpy();
    img.setColor(0, 0, 0, 0);
    
    img.setScaling(Scaling.none);
    img.setAlign(com.badlogic.gdx.scenes.scene2d.utils.Align.bottom+com.badlogic.gdx.scenes.scene2d.utils.Align.left);
    addActor(img);

  }
  
  
  public void highlight() {
      img.setColor(color);
  }
  
  
  public void reset() {
    img.setColor(0,0,0,0);
  }

  private int nPoint() {
    if (MatchState.fMove==0) return nPoint;
    else return 23-nPoint;
  }
  
}
