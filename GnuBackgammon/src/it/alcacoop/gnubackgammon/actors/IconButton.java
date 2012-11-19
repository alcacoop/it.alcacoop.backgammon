package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class IconButton extends Group {

  private Image i;
  
  public IconButton(String ti, InputListener il) {
    TextureRegion r = GnuBackgammon.atlas.findRegion(ti);
    r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    i = new Image(r);
    addActor(i);
    addListener(il);
  }

}
