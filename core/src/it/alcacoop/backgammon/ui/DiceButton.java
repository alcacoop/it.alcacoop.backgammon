package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class DiceButton extends Group {

  private Image dice1, dice2;
  private String name;
  private Table t;
  
  public DiceButton(int d1, int d2) {
    
    t = new Table();
    t.setFillParent(true);
    addActor(t);
    Drawable background = GnuBackgammon.skin.getDrawable("slider");
    t.setBackground(background);
    
    name = d1+"x"+d2;
    TextureRegion r1 = new TextureRegion(GnuBackgammon.atlas.findRegion("d"+d1));
    TextureRegion r2 = new TextureRegion(GnuBackgammon.atlas.findRegion("d"+d2));
    dice1 = new Image(r1);
    dice2 = new Image(r2);
    
    t.add(dice1).fill();
    t.add(dice2).fill();
  }
  
  public String getName() {
    return name;
  }

  @Override
  public Actor hit(float x, float y, boolean touchable) {
    if (t.hit(x, y, touchable)!=null) {
      return this;
    } else {
      return null;
    }
  }
  
}
