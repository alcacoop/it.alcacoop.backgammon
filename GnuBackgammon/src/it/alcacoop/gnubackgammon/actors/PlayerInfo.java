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
      region = GnuBackgammon.atlas.findRegion("cw-small");
    else 
      region = GnuBackgammon.atlas.findRegion("cb-small");
    Image i =new Image(region);
    
    setWidth(width);
    
    float w = width*0.6f;
    row().height(0);
    add().minWidth(w*0.7f).height(0);
    add().width(w*0.1f).height(0);
    add().width(w*0.3f).height(0);
    add(i).width(i.getWidth()).height(i.getHeight()).padRight(2+(2-GnuBackgammon.ss)).height(0);
    add().height(0);
    row();
    add(name).right();
    add();
    add(score).left();
    add(i).center().height(i.getHeight()).padRight(2+(2-GnuBackgammon.ss));
    add(pips);
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
