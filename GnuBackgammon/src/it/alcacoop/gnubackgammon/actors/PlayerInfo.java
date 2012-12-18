package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlayerInfo extends Table {

  private Label name;
  private Label score;
  private Label pips;
  private int color;
  
  public PlayerInfo(String title, int color, float width) {
    this.color = color;
    name = new Label(title , GnuBackgammon.skin);
    score = new Label("0", GnuBackgammon.skin);
    pips = new Label("200", GnuBackgammon.skin);
    
    TextureRegion region;
    if (color==1)
      region = GnuBackgammon.atlas.findRegion("cw");
    else 
      region = GnuBackgammon.atlas.findRegion("cb");
    Image i =new Image(region);
    
    float w = width*0.6f;
    add(name).width(w*0.7f);
    add(score).width(w*0.3f);
    add(i).center().width(i.getWidth()*0.5f).height(i.getHeight()*0.5f).padRight(3+3*(2-GnuBackgammon.ss));
    add(pips).left();
  }
  

  public void setName(String name) {
    this.name.setText(name);
  }
  
  public void setScore() {
    this.score.setText(String.valueOf(MatchState.anScore[color]));
  }

  public void setPIPS() {
    this.pips.setText(String.valueOf(GnuBackgammon.Instance.board.getPIPS(color)));
  } 
  
  public void update() {
    setScore();
    setPIPS();
  }
}
