package it.alcacoop.gnubackgammon.ui;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.fsm.GameFSM.States;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public final class GameMenuPopup extends Table {

  private Table t1;
  private Drawable background;
  private static TextButton undo;
  private static TextButton resign;
  private static TextButton abandon;
  private TextButton options;
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
    a.addListener(new InputListener(){
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        hide(noop);
        return true;
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
          if (undo.isDisabled()) return;
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
            if (resign.isDisabled()) return;
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
        if (abandon.isDisabled()) return;
        GnuBackgammon.fsm.state(States.DIALOG_HANDLER);
        UIDialog.getYesNoDialog(
            Events.ABANDON_MATCH, 
            "Really exit this match?", 
            GnuBackgammon.Instance.board.getStage());
      }});
    }});
    
    options = new TextButton("Options", GnuBackgammon.skin);
    options.addListener(new ClickListener(){@Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
        @Override
        public void run() {
          UIDialog.getOptionsDialog(GnuBackgammon.Instance.board.getStage());
        }});
      }});
    
    float pad = getHeight()/8;
    float w = getWidth()/4 - pad;
    
    add(undo).fill().expand().pad(pad).width(w);
    add(resign).fill().expand().pad(pad).width(w);
    add(abandon).fill().expand().pad(pad).width(w);
    add(options).fill().expand().pad(pad).width(w);
    
    visible = false;
    addActor(t1);
  }

  public static void setDisabledButtons() {
    if ((MatchState.matchType==0) && (MatchState.fMove==1)) { //CPU IS PLAYING
      System.out.println("DISABILITALI!");
      undo.setDisabled(true);
      resign.setDisabled(true);
      abandon.setDisabled(true);
      undo.setColor(1,1,1,0.4f);
      resign.setColor(1,1,1,0.4f);
      abandon.setColor(1,1,1,0.4f);
    } else {
      System.out.println("RIABILITALI!");
      undo.setDisabled(false);
      resign.setDisabled(false);
      abandon.setDisabled(false);
      undo.setColor(1,1,1,1);
      resign.setColor(1,1,1,1);
      abandon.setColor(1,1,1,1);
    }
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
