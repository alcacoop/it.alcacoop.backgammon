package it.alcacoop.gnubackgammon.ui;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
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
  private boolean quitWindow = false;
  private boolean optionsWindow = false;
  
  private GameOptionsTable opts;
  
  
  static {
    instance = new UIDialog();
  }
  
  private UIDialog() {
    super("", GnuBackgammon.skin);
    setModal(true);
    setMovable(false);
    
    opts = new GameOptionsTable(false);
    
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
            instance.remove();
            boolean ret = s.equals("YES")||s.equals("OK");
            
            if ((instance.quitWindow)&&(ret)) {
              Gdx.app.exit();
            } else {
              GnuBackgammon.fsm.processEvent(instance.evt, ret);
              if (instance.optionsWindow) opts.savePrefs();
            }
          }
        });
      };
    };
    
    label = new Label("", GnuBackgammon.skin);
    
    TextButtonStyle tl = GnuBackgammon.skin2.get("button", TextButtonStyle.class);
    
    bYes = new TextButton("Yes", tl);
    bYes.addListener(cl);
    bNo = new TextButton("No", tl);
    bNo.addListener(cl);
    bContinue = new TextButton("Ok", tl);
    bContinue.addListener(cl);

    background = GnuBackgammon.skin2.getDrawable("default-window");
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
        Actions.run(r)
    ));
  }
  
  public static void getYesNoDialog(BaseFSM.Events evt, String text, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.6f;
    
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
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.4f;
    float width = stage.getWidth()*0.6f;
    
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
 
  public static void getEndGameDialog(BaseFSM.Events evt, String text, String text1, String score1, String score2, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.evt = evt;
    instance.remove();
    
    float height = stage.getHeight()*0.6f;
    float width = stage.getWidth()*0.6f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    instance.row().padTop(width/25);
    instance.add().expand();
    instance.add(text1).colspan(2).expand().align(Align.center);
    instance.add().expand();
    

    instance.row();
    instance.add().expand();
    instance.add("Overall Score" + text).colspan(2).expand().align(Align.center);
    instance.add().expand();
    instance.row();
    instance.add().expand();
    instance.add(score1).expand().align(Align.center);
    instance.add(score2).expand().align(Align.center);
    instance.add().expand();
    
    instance.row();
    instance.add();
    instance.add(instance.bContinue).colspan(2).fill().expand().height(height*0.15f).width(width/4);
    instance.add();
    
    stage.addActor(instance);
    instance.addAction(Actions.fadeIn(0.3f));
  }
  
  public static void getFlashDialog(BaseFSM.Events evt, String text, Stage stage) {
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);
    
    float height = stage.getHeight()*0.3f;
    float width = stage.getWidth()*0.6f;
    
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
            instance.remove();
            GnuBackgammon.fsm.processEvent(instance.evt, true);
          }
        })
    ));
  }
  
  
  public static void getQuitDialog(Stage stage) {
    instance.quitWindow = true;
    instance.optionsWindow = false;
    instance.remove();
    instance.setText("Really quit the game?");
    
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

  
  public static void getHelpDialog(Stage stage, Boolean cb) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.remove();
    Label l = new Label(
        "GAME TYPE\n" +
        "You can choose two game type, and several options:\n" +
        "Backgammon - usual starting position\n" +
        "Nackgammon - Nack's starting position, attenuates lucky starting roll\n" +
        "Doubling Cube: use or not the doubling cube, with or without Crawford rule\n\n" +
        "START TURN\n" +
        "If cube isn't available, dices are rolled automatically,\n" +
        "else you must click on 'Double' or 'Roll' button\n\n" +
        "MOVING MECHANIC\n" +
        "You can choose two moves mechanic (Options->Move Logic):\n" +
        "TAP - once you rolled dices, select the piece you would move.\n" +
        "If legal moves for that piece are available, they will be shown.\n" +
        "Click an available point and the piece will move there.\n" +
        "AUTO - click on a piece and it moves automatically to destination point.\n" +
        "Bigger dice is played first. You can change dice order clicking on dices\n\n" +
        "You can cancel your moves in current hand just clicking the UNDO button\n" +
        "in the game options menu popup.\n\n" +
        "END TURN\n" +
        "When you finish your turn, click again the dices to take back them and change turn.\n"
    , GnuBackgammon.skin);
    l.setWrap(true);
    
    ScrollPane sc = new ScrollPane(l,GnuBackgammon.skin2);
    sc.setFadeScrollBars(true);
    
    float height = stage.getHeight()*0.75f;
    float width = stage.getWidth()*0.9f;
    
    instance.clear();
    instance.row().padTop(width/25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width/25).padLeft(width/35).padRight(width/35);
    
    instance.row().pad(width/25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height*0.15f).width(width/4);
    instance.add();
    
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);
    
    stage.addActor(instance);
    instance.addAction(Actions.fadeIn(0.3f));
  }
  
  
  
  public static void getOptionsDialog(Stage stage) {
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.optionsWindow = true;
    instance.remove();
    
    instance.opts.initFromPrefs();
    
    float width = stage.getWidth()*0.75f;
    float height = stage.getHeight()*0.85f;
    
    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth()-width)/2);
    instance.setY((stage.getHeight()-height)/2);

    instance.add().fill().expand();
    instance.row();
    instance.add(instance.opts).fill();
    instance.row();
    instance.add().fill().expand();
    instance.row();
    instance.add(instance.bContinue).padBottom(height/13).width(width*0.3f).height(height*0.13f);
    
    stage.addActor(instance);
    instance.addAction(Actions.fadeIn(0.3f));
  }
}
