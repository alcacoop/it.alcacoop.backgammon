package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;



public class Checker extends Group {

  private Board board;
  private Label label;
  private Image img, imgh;
  
  public int color = 0;
  public int boardX = -1; 
  public int boardY = -1;
  

  public Checker(Board _board, int _color) {
    super();
    
    boardX = boardY= 0;
    color = _color;
    board = _board;

    TextureRegion region;
    
    if (color==1) {//WHITE
      region = GnuBackgammon.atlas.findRegion("cw");
      label = new Label("1", GnuBackgammon.styleBlack);
    } else { 
      region = GnuBackgammon.atlas.findRegion("cb");
      label = new Label("1", GnuBackgammon.styleWhite);
    }

    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    img = new Image(region);
    
    label.setAlignment(com.badlogic.gdx.scenes.scene2d.utils.Align.center);

    region = GnuBackgammon.atlas.findRegion("ch");
    region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    imgh = new Image(region);
    imgh.setScaling(Scaling.none);
    
    imgh.addAction(Actions.forever(Actions.sequence(Actions.fadeIn(0.4f), Actions.fadeOut(0.2f))));
    
    label.setX(img.getWidth()/2-label.getWidth()/2);
    label.setY(img.getHeight()/2-label.getHeight()/2);
    addActor(img);
    addActor(label);
    addActor(imgh);
    label.setText("");
    
    
  }
  
  
  public void reset(int _boardX, int _boardY) {
    reset(_boardX, _boardY, 0);
  }
  public void reset(int _boardX, int _boardY, float t) {
    Vector2 _p = board.getBoardCoord(color, _boardX, _boardY);
    boardX = _boardX;
    boardY = _boardY;
    addAction(Actions.moveTo(_p.x, _p.y, t));
    if ((boardY>4)&&(boardX!=-1)) 
      label.setText(""+(boardY+1));
    else 
      label.setText("");
    label.setX(img.getWidth()/2-label.getWidth()/2);
    label.setY(img.getHeight()/2-label.getHeight()/2);
    imgh.setVisible(false);
  }

  
  public void moveTo(int x){
    moveToDelayed(x, 0);
  }
  
  public void moveToDelayed(final int x, float delay){
    board.removeActor(this);
    board.addActor(this);
    
    float tt = 0.3f;
    if (x==24) tt=0.25f;
    
    final int y;
    
    if (x>=0)
      y = board._board[color][x];
    else //BEARED OFF
      y = board.bearedOff[color];
    
    Vector2 _p = board.getBoardCoord(color, x, y);
    
    final int d = boardX-x;
    
    setPosition(x);
    
    this.addAction(Actions.sequence(
        Actions.delay(delay),
        new Action(){
          @Override
          public boolean act(float delta) {
            highlight(false);
            board.selected = null;
            board.points.reset();
            if ((x<24)&&(d>0))
              board.dices.disable(d);
            if ((boardY<5)||(boardX==-1)) label.setText("");
            return true;
          }},
        Actions.moveTo(_p.x, _p.y, tt),
        new Action(){
            @Override
            public boolean act(float delta) {
              if ((boardY>4)&&(boardX!=-1)) label.setText(""+(boardY+1));
              if (!board.checkHit())
                GameScreen.fsm.processEvent(Events.PERFORMED_MOVE, null);
              label.setX(img.getWidth()/2-label.getWidth()/2);
              label.setY(img.getHeight()/2-label.getHeight()/2);
              return true;
            }}
        ));
    
  }
  
  
  public void setPosition(int x) {
    board._board[color][boardX]--;
	  boardX = x;
	  
	  if (x>=0) //ON_TABLE!!
	    boardY = board._board[color][boardX]++;
	  else
	    boardY = board.bearedOff[color]++;
	     
  }

  
  public int getSpecularColor() {
    if (color==0) return 1;
    else return 0;
  }
  
  public int getSpecularPosition() {
    return 23-boardX;
  }

  public void highlight(boolean b) {
    imgh.setVisible(b);
  }
  
  @Override
  public Actor hit(float x, float y, boolean touchable) {
    return null;
  }
  
  @Override
  public void setX(float x) {
    super.setX(x-img.getWidth()/2);
  }
  
  public float getWidth() {
    return img.getWidth();
  }
}
