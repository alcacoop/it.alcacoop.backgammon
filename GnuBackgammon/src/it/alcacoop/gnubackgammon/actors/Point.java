package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
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
  public boolean isTarget = false;
    

  public Point(int _nPoint) {
    super();
    nPoint = _nPoint;
    
    addListener(new InputListener() {
      public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        return true;
      }
      public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
        GameScreen.fsm.processEvent(Events.POINT_TOUCHED, nPoint());
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
      isTarget = true;
  }
  
  
  public void reset() {
    img.setColor(0,0,0,0);
    isTarget = false;
  }

  private int nPoint() {
    if (MatchState.fMove==0) return nPoint;
    else return 23-nPoint;
  }
  
}
