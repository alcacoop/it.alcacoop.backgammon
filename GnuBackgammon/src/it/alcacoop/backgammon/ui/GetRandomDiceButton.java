package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class GetRandomDiceButton extends Group {

  private Table t;

  public GetRandomDiceButton() {

    t = new Table();
    t.setFillParent(true);
    addActor(t);
    Drawable background = GnuBackgammon.skin.getDrawable("slider");
    t.setBackground(background);

    t.add(new Label("Get Random Dices!", GnuBackgammon.skin)).fill();
  }


  @Override
  public Actor hit(float x, float y, boolean touchable) {
    if (t.hit(x, y, touchable) != null) {
      return this;
    } else {
      return null;
    }
  }

}
