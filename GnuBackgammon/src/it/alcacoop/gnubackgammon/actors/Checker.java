package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.Board;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.MoveTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Group implements OnActionCompleted {

  private TextureRegion region;
  private Board board;
  private Label label;
  private Image img;
  
  public int color = 0;
  public int boardX = -1; 
  public int boardY = -1;
  

  public Checker(Board _board, int _color) {
    super();

    boardX = boardY= 0;
    color = _color;
    board = _board;
    
    if (color==0) {//WHITE
      region = GnuBackgammon.atlas.findRegion("cw");
      label = new Label("1", GnuBackgammon.styleBlack, "10");
    } else { 
      region = GnuBackgammon.atlas.findRegion("cb");
      label = new Label("1", GnuBackgammon.styleWhite, "10");
    }

    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    img = new Image(region);
    img.setScaling(Scaling.none);
    label.setAlignment(Align.CENTER);
    label.x =15;
    label.y = 10;
    addActor(img);
    addActor(label);
    label.setText("");
  }

  public void moveTo(int x){
    moveToDelayed(x, 0);
  }

  public void moveToDelayed(int x, float delay){
    board.removeActor(this);
    board.addActor(this);
    int y = board._board[color][x];
    
    Vector2 _p = board.getBoardCoord(color, x, y);
    Delay dl = Delay.$(delay);
    dl.setCompletionListener(this);
    MoveTo mt = MoveTo.$(_p.x, _p.y, 0.5f);
    mt.setCompletionListener(this);
    action(Sequence.$(dl,  mt));
    setPosition(x, y);
  }
  
  
  public void setPosition(int x, int y) {
    //Gdx.app.log("LOG", "BOARD[boardX]: "+board._board[boardX]);
    Gdx.app.log("LOG", "FBOARDX: "+boardX+" FBOARDY: "+boardY);
    board._board[color][boardX]--;
	  boardX = x;
	  boardY = board._board[color][boardX]++;
	  Gdx.app.log("LOG", "TBOARDX: "+boardX+" TBOARDY: "+boardY);
	  
	  
  }

  
  public void setLabel() {
    if (boardY>4)
      label.setText(""+(boardY+1));
    else
      label.setText("");
  }

  @Override
  public void completed(Action action) {
    if (action instanceof Delay) {
      if (boardY<5) label.setText("");
    } else {
      setLabel();
      board.printBoard();
      board.performNextMove();
    }
  }
}
