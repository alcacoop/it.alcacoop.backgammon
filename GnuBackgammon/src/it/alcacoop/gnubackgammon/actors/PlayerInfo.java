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
  
  public PlayerInfo(String title, int color) {
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
    add(i).width(i.getWidth()).spaceRight(6);
    
    
    Table t = new Table();
    t.add(name).left();
    t.add(score).left().padLeft(5+10*(2-GnuBackgammon.ss));
    t.row();
    t.add(new Label("PIPS:", GnuBackgammon.skin)).left();
    t.add(pips).left().padLeft(5+10*(2-GnuBackgammon.ss));
    add(t);
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
