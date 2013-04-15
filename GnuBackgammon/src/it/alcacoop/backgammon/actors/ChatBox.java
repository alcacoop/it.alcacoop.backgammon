package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ChatBox extends Table {
  private Stage stage; 
  private boolean visible = false;
  private boolean animating = false;
  private ScrollPane scroll;
  private Table tchat;
  private float position;
  float height, cheight;
  String lastSender = "----";
  private Runnable r1, r2;
  

  public ChatBox(Stage _stage) {
    super();
    stage = _stage;
    height = stage.getHeight()*0.68f;
    cheight = GnuBackgammon.chatHeight;
    position = (height)*0.07f;
    
    r1 = new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
        visible=!visible;
      }
    };
    
    r2 = new Runnable() {
      @Override
      public void run() {
        animating = false;
      }
    };
    
    tchat = new Table();
    tchat.bottom();
    
    setHeight(height);
    setWidth(stage.getWidth()*0.7f);
    setX((stage.getWidth()-getWidth())/2);
    setY(stage.getHeight()-position);
    setColor(0.85f, 0.85f, 0.85f, 0.85f);

    
    setBackground(GnuBackgammon.skin.getDrawable("chatbox"));
    scroll = new ScrollPane(tchat, GnuBackgammon.skin);
    scroll.setColor(1, 1, 1, 1);
    scroll.setOverscroll(false, false);
    add().expandX().fillX().height(cheight);
    row();
    add(scroll).expand().fill().height((height-cheight));
  }
  
  public void toggle() {
    if (!visible)
      show();
    else
      hide();
  }

  
  public void show() {
    if (animating) return;
    animating = true;
    if (!visible)
      addAction(MyActions.sequence(Actions.moveTo(getX(), stage.getHeight()-getHeight(), 0.18f), Actions.run(r1), Actions.run(r2)));
    else
      animating = false;
  }
  
  public void hide() {
    if (animating) return;
    animating = true;
    if (visible)
      addAction(MyActions.sequence(Actions.run(r1), Actions.moveTo(getX(), stage.getHeight()-position, 0.18f), Actions.run(r2)));
    else
      animating = false;
  }
  
  
  public void appendMessage(final String user, final String msg, final boolean direction) {

    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        // TODO Auto-generated method stub
        LabelStyle ls;
        if (direction)
          ls = GnuBackgammon.skin.get("gray", LabelStyle.class);
        else
          ls = GnuBackgammon.skin.get("black", LabelStyle.class);
        
        if (!user.equals(lastSender)) {
          if (!lastSender.equals("----")) {
            tchat.row();
            tchat.add(new Image(GnuBackgammon.skin.getDrawable("separator"))).colspan(2).fillX().height(6).expandX();
          }
          tchat.row();
          tchat.add(new Label(user+" says: ", ls)).colspan(2).fillX();
          lastSender = user;
        } 
        
        tchat.row();    
        tchat.add().width(stage.getWidth()*0.05f).right().top().fillX();
        Label m = new Label(msg, ls);
        m.setWrap(true);
        tchat.add(m).left().expandX().fill().bottom();
        scroll.setWidget(tchat);
        scroll.addAction(MyActions.sequence(Actions.run(new Runnable() {
          @Override
          public void run() {
            scroll.setScrollPercentY(1);
          }
        }), Actions.delay(1f)));
        
        Gdx.graphics.requestRendering();        
      }
    });
  }
  
  public void hardHide() {
    if (visible) {
      visible = !visible;
      GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
      setY(stage.getHeight()-position);
    }
  }
  
  public void reset() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        tchat = new Table();
        lastSender = "----";
        tchat.bottom();
        setY(stage.getHeight()-position);
        scroll.setWidget(tchat);
      }
    });
  }
  
  public boolean chatHit(float x, float y) {
    return ((x > getX() && x< getX()+getWidth())&&(y < getHeight()));
  }
}
