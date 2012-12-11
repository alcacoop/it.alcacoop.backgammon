package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DoublingCube extends Group {
  
  private TextureRegion region;
  private Image i;
  int value = 64;
  float up, down, center, x;
  
  public DoublingCube(Board b) {
    region = GnuBackgammon.atlas.findRegion("c"+value);
    i = new Image(region);
    i.setAlign(Align.left);
    addActor(i);
    
    x = b.getWidth()-GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) -
        i.getWidth()/2 -
        GnuBackgammon.Instance.jp.asFloat("pos", 0)/3.7f;
    
    center = (b.getHeight()-i.getHeight())/2 - GnuBackgammon.Instance.jp.asFloat("pos", 0)/5.9f;
    down = GnuBackgammon.Instance.jp.asFloat("pos", 0)*2;
    up = b.getHeight() - i.getHeight() - GnuBackgammon.Instance.jp.asFloat("pos", 0)*2;
    
    setX(x);
    setY(center);
    setVisible(true);
  }

  public void setValue(int v) {
    value = v;
    if (v>64) value = 64;
    region = GnuBackgammon.atlas.findRegion("c"+value);
    TextureRegionDrawable d = new TextureRegionDrawable(region);
    i.setDrawable(d);
    if (MatchState.fCubeOwner == -1) setY(center);
    else if (MatchState.fMove==0) addAction(Actions.moveTo(getX(), up, 0.3f));
    else addAction(Actions.moveTo(getX(), down, 0.3f));
  }
  
  public void reset() {
    setValue(64);
  }
}
