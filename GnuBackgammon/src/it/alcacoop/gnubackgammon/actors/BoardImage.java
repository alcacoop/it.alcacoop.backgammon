package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;



public class BoardImage extends Image {

  private TextureRegion region;

  public BoardImage(int x0, int y0){
    region = GnuBackgammon.atlas.findRegion("board");
    TextureRegionDrawable d = new TextureRegionDrawable(region);
    this.setDrawable(d);
    
    setScaling(Scaling.none);
    this.setAlign(com.badlogic.gdx.scenes.scene2d.utils.Align.bottom+com.badlogic.gdx.scenes.scene2d.utils.Align.left);
    setX(x0);
    setY(y0);
  }
  
  @Override
  public Actor hit(float x, float y, boolean touchable) {
    return null;
  }

}
