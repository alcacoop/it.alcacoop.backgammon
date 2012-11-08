package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.Board;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Group {

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
    
    if (color==1) {//WHITE
      region = GnuBackgammon.atlas.findRegion("cw");
      label = new Label("1", GnuBackgammon.styleBlack);
    } else { 
      region = GnuBackgammon.atlas.findRegion("cb");
      label = new Label("1", GnuBackgammon.styleWhite);
    }

    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    img = new Image(region);
    img.setScaling(Scaling.none);
    label.setAlignment(com.badlogic.gdx.scenes.scene2d.utils.Align.center);

    label.setX(15);
    label.setY(10);
    addActor(img);
    addActor(label);
    label.setText("");
  }

  
  public void moveTo(int x){
    moveToDelayed(x, 0);
  }
  
  public void moveToDelayed(final int x, float delay){
    board.removeActor(this);
    board.addActor(this);
    
    float tt = 0.4f;
    if (x==24) tt=0.3f;
    
    final int y;
    if (x>=0) {
      y = board._board[color][x];
    } else { //BEARED OFF
      board.bearedOff[color]++; 
      y = board.bearedOff[color];
    }
    Vector2 _p = board.getBoardCoord(color, x, y);
    
    
    this.addAction(Actions.sequence(
        Actions.delay(delay),
        new Action(){
          @Override
          public boolean act(float delta) {
            actionCompleted(0);
            return true;
          }},
        Actions.moveTo(_p.x, _p.y, tt),
        new Action(){
            @Override
            public boolean act(float delta) {
              actionCompleted(1);
              
              return true;
            }}
        ));
    setPosition(x, y);
  }
  
  
  public void setPosition(int x, int y) {
    board._board[color][boardX]--;
	  boardX = x;
	  if (x>=0) //ON_TABLE!! 
	    boardY = board._board[color][boardX]++;
  }

  
  public int getSpecularColor() {
    if (color==0) return 1;
    else return 0;
  }
  
  public int getSpecularPosition() {
    return 23-boardX;
  }

  
  
  public void actionCompleted(int mode) {
    if (mode==0) { //PRE_MOVEMENT
      if ((boardY<5)||(boardX==-1)) label.setText("");
    } else if (mode==1) { //POST_MOVEMENT
      if (boardY>4) label.setText(""+(boardY+1));
      board.performNextMove();
//      if (Checker.this.boardX!=-1) Checker.this.setZIndex(_zindex);
    }
  }
}
