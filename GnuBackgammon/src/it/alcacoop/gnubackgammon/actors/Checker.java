package it.alcacoop.gnubackgammon.actors;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.Board;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.MoveTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Image {

  private TextureRegion region;
  private int color;
  private Board board;

  public Checker(Board _board, int _color) {
    super();

    color = _color;
    board = _board;

    if (color==0) //WHITE
      region = GnuBackgammon.atlas.findRegion("cw");
    else 
      region = GnuBackgammon.atlas.findRegion("cb");

    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    setRegion(region);
    setScaling(Scaling.none);
    setAlign(Align.LEFT+Align.BOTTOM);
  }

  public void setPosition(int x, int y){
    setPositionDelayed(x, y, 0);
  }

  public void setPositionDelayed(int x, int y, float delay){
    Vector2 _p = board.getBoardCoord(color, x, y);
    action(Sequence.$(Delay.$(delay), MoveTo.$(_p.x, _p.y, 0.5f)));
  }
}
