package it.alcacoop.gnubackgammon.ui;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public final class UIDialog extends Window {

  private Table t1, t2, t3;
  private TextButton bContinue;
  private TextButton bYes;
  private TextButton bNo;
  
  private Label label;
  private Drawable background;
  private ClickListener cl;
  
  private static UIDialog instance;
  
  private BaseFSM.Events evt;
  
  
  static {
    instance = new UIDialog();
  }
  
  private UIDialog() {
    super("", GnuBackgammon.skin);
    setModal(true);
    setMovable(false);
    
    cl = new ClickListener(){
      public void clicked(InputEvent event, float x, float y) {
        final String s;
        if (event.getTarget() instanceof Label) {
          s = ((Label)event.getTarget()).getText().toString().toUpperCase();
        } else { 
          s = ((TextButton)event.getTarget()).getText().toString().toUpperCase();
        }
        hide(new Runnable(){
          @Override
          public void run() {
            boolean ret = s.equals("YES")||s.equals("OK");
            GnuBackgammon.fsm.processEvent(instance.evt, ret);
          }
        });
      };
    };
    
    label = new Label("...", GnuBackgammon.skin);
    bYes = new TextButton("Yes", GnuBackgammon.skin);
    bYes.addListener(cl);
    bNo = new TextButton("No", GnuBackgammon.skin);
    bNo.addListener(cl);
    bContinue = new TextButton("Ok", GnuBackgammon.skin);
    bContinue.addListener(cl);

    background = GnuBackgammon.skin.getDrawable("default-window");
    setBackground(background);
    
    t1 = new Table();
    t1.setFillParent(true);
    t1.add(label).fill().expand().center();
    
    t2 = new Table();
    t2.setFillParent(true);
    t2.add().colspan(2).expand();
    t2.add(bContinue).fill().expand();
    t2.add().colspan(2).expand();
    
    t3 = new Table();
    t3.setFillParent(true);
    t3.add().expand();
    t3.add(bNo).fill().expand();
    t3.add().expand();
    t3.add(bYes).fill().expand();
    t3.add().expand();
    
    setColor(1,1,1,0);
  }
  
  private void setText(String t) {
    label.setText(t);
  }
  
  private void hide(Runnable r) {
    addAction(Actions.sequence(
        Actions.fadeOut(0.3f),
        Actions.run(r),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            remove();
          }
        })
    ));
  }
  
  public static void getYesNoDialog(BaseFSM.Events evt, String text, Stage stage) {
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.5f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(Actions.fadeIn(0.3f));
  }
  
  
  public static void getContinueDialog(BaseFSM.Events evt, String text, Stage stage) {
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.5f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add(instance.label).colspan(3).expand().align(Align.center);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height*0.25f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(Actions.fadeIn(0.3f));
  }
  
  
  public static void getFlashDialog(BaseFSM.Events evt, String text, Stage stage) {
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.3f;
    float width = stage.getWidth()*0.5f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.add(instance.label).expand().align(Align.center);
    
    stage.addActor(instance);
    instance.addAction(Actions.sequence(
        Actions.fadeIn(0.3f),
        Actions.delay(1.5f),
        Actions.fadeOut(0.3f),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            GnuBackgammon.fsm.processEvent(instance.evt, true);
            instance.remove();
          }
        })
    ));
  }
  
}
