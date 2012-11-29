package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Scaling;


public class Checker extends Group {

  private Board board;
  private Label label;
  private Image img, imgh;
  private ParallelAction act;
  private BaseFSM fsm;
  
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
      LabelStyle styleBlack = new LabelStyle(GnuBackgammon.font, Color.BLACK);
      label = new Label("1", styleBlack);
      label.setColor(0,0,0,1);
    } else { 
      region = GnuBackgammon.atlas.findRegion("cb");
      LabelStyle styleWhite = new LabelStyle(GnuBackgammon.font, Color.WHITE);
      label = new Label("1", styleWhite);
      label.setColor(1,1,1,1);
    }

    img = new Image(region);
    label.setAlignment(com.badlogic.gdx.scenes.scene2d.utils.Align.center);
    region = GnuBackgammon.atlas.findRegion("ch");
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
    
    if (t==0) {
      setX(_p.x);
      setY(_p.y);
    }
    else {
      act = Actions.sequence(Actions.moveTo(_p.x, _p.y, t),Actions.run(new Runnable() {
        @Override
        public void run() {
          GnuBackgammon.fsm.processEvent(Events.CHECKER_RESETTED, null);
        }
      }));
      addAction(act);
    }
    
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
    fsm = GnuBackgammon.fsm;
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
    
    final Checker c = this;
    act = Actions.sequence(
        Actions.delay(delay),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            highlight(false);
            board.selected = null;
            board.points.reset();
            if ((boardY<5)||(boardX==-1)) label.setText("");
          }
        }),
        Actions.moveTo(_p.x, _p.y, tt),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            if ((boardY>4)&&(boardX!=-1)) label.setText(""+(boardY+1));
            if (!board.checkHit())
              if (fsm==GnuBackgammon.fsm) fsm.processEvent(Events.PERFORMED_MOVE, null);
            label.setX(img.getWidth()/2-label.getWidth()/2);
            label.setY(img.getHeight()/2-label.getHeight()/2);
            if ((x<24)&&(d>0))
              board.dices.disable(d);
          }
        })
        );
    c.addAction(act);
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
  
  public void resetActions() {
    if (act!=null) //act.setActor(null);
      act.reset();
  }
  
}
