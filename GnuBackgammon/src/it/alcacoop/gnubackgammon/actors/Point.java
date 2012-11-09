package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;



public class Point extends Group {

  private TextureRegion region;
  public int nPoint;

  public Point(int _nPoint){
    super();
    nPoint = _nPoint;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        return true;
      }
      public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        System.out.println("Click on: "+Point.this.nPoint);
      }
    });

    region = GnuBackgammon.atlas.findRegion("point");
    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

    Image img = new Image(region);
    
    img.setScaling(Scaling.none);
    img.setAlign(com.badlogic.gdx.scenes.scene2d.utils.Align.bottom+com.badlogic.gdx.scenes.scene2d.utils.Align.left);
    addActor(img);

  }
}
