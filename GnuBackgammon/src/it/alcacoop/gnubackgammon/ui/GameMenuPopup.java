package it.alcacoop.gnubackgammon.ui;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.fsm.GameFSM.States;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public final class GameMenuPopup extends Table {

  private Table t1;
  private Drawable background;
  private TextButton undo, resign, abandon, options;
  private Actor a;
  private Runnable noop;
  
  private boolean visible;
  
  
  
  public GameMenuPopup(Stage stage) {
    noop = new Runnable(){
      @Override
      public void run() {
      }
    };
        
    setWidth(stage.getWidth());
    setHeight(stage.getHeight()/8);
    setX(0);
    setY(-getHeight());
    
    background = GnuBackgammon.skin.getDrawable("default-rect");
    setBackground(background);
    
    a = new Actor();
    a.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide(noop);
      }
    });
    
    t1 = new Table();
    t1.setFillParent(true);
    
    undo = new TextButton("Undo Move", GnuBackgammon.skin);
    undo.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
        @Override
        public void run() {
          GnuBackgammon.Instance.board.undoMove();
        }});
      }
    });
    
    resign = new TextButton("Resign Game", GnuBackgammon.skin);
    resign.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
          @Override
          public void run() {
          GnuBackgammon.fsm.state(States.DIALOG_HANDLER);
          GnuBackgammon.fsm.processEvent(Events.ACCEPT_RESIGN, 0);
        }});
        
      }
    });
    
    abandon = new TextButton("Abandon Match", GnuBackgammon.skin);
    abandon.addListener(new ClickListener(){@Override
    public void clicked(InputEvent event, float x, float y) {
      hide(new Runnable(){
      @Override
      public void run() {
        GnuBackgammon.Instance.setFSM("MENU_FSM");
      }});
    }});
    
    options = new TextButton("Options", GnuBackgammon.skin);
    
    float pad = getHeight()/8;
    float w = getWidth()/4 - pad;
    
    add(undo).fill().expand().pad(pad).width(w);
    add(resign).fill().expand().pad(pad).width(w);
    add(abandon).fill().expand().pad(pad).width(w);
    add(options).fill().expand().pad(pad).width(w);
    
    visible = false;
    addActor(t1);
  }

  private void show() {
    visible = true;
    addAction(Actions.moveTo(0, 0, 0.1f));
  }
  
  private void hide(Runnable r) {
    visible = false;
    addAction(Actions.sequence(
      Actions.moveTo(0, -getHeight(), 0.1f),
      Actions.run(r)
    ));
  }
  
  public void toggle() {
    if (visible) hide(noop);
    else show();
  }
  
  
  public Actor hit (float x, float y, boolean touchable) {
    Actor hit = super.hit(x, y, touchable);
    
    if (visible) {
      if (hit != null) return hit;
      else {
        return a;
      }
      
    } else {
      return hit;  
    }
    
  }

  
}
