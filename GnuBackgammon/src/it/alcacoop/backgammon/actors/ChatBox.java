package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChatBox extends Group {
  private Stage stage; 
  private boolean visible = false;
  private ScrollPane scroll;
  private Table tchat;
  private float position;
  float height, cheight;
  String lastSender = "----";
  

  public ChatBox(Stage _stage) {
    super();
    stage = _stage;
    height = stage.getHeight()*0.72f;
    cheight = GnuBackgammon.chatHeight;
    position = (height-cheight)*0.20f + 10;
    
    tchat = new Table();
    tchat.bottom();
    
    setHeight(height);
    setWidth(stage.getWidth()*0.7f);
    setX((stage.getWidth()-getWidth())/2);
    setY(stage.getHeight()-position);
    setColor(0.85f, 0.85f, 0.85f, 0.85f);

    
    Table t1 = new Table();
    t1.setBackground(GnuBackgammon.skin.getDrawable("chatbox"));
    scroll = new ScrollPane(tchat, GnuBackgammon.skin);
    scroll.setColor(1, 1, 1, 1);
    scroll.setOverscroll(false, false);
    t1.add().expandX().fillX().height(cheight);
    t1.row();
    t1.add(scroll).expand().fill().height((height-cheight)*0.80f);

    Table t = new Table();
    t.setFillParent(true);
    
    t.add(t1).expand().width(stage.getWidth()*0.7f);
    t.row();
    t.add().expand().width(stage.getWidth()*0.7f).height((height-cheight)*0.20f);
    
    addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Runnable r = new Runnable() {
          @Override
          public void run() {
            visible = !visible;
            GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
          }
        };
        
        if (!visible) {
          addAction(MyActions.sequence(Actions.moveTo(getX(), stage.getHeight()-getHeight(), 0.18f), Actions.run(r)));
        } else {
          addAction(MyActions.sequence(Actions.run(r), Actions.moveTo(getX(), stage.getHeight()-position, 0.18f)));
        }
      }
    });
    addActor(t);
  }

  public void appendMessage(String user, String msg, boolean direction) {
    
    LabelStyle ls;
    if (direction)
      ls = GnuBackgammon.skin.get("gray", LabelStyle.class);
    else
      ls = GnuBackgammon.skin.get("black", LabelStyle.class);
    scroll.setWidget(null);
    
    
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
  
  public void hide() {
    if (visible) {
      visible = !visible;
      GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
      setY(stage.getHeight()-position);
    }
  }
  
  public void softHide() {
    if (visible) {
      Runnable r = new Runnable() {
        @Override
        public void run() {
          visible = !visible;
          GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
        }
      };
      addAction(MyActions.sequence(Actions.run(r), Actions.moveTo(getX(), stage.getHeight()-position, 0.18f)));
    }
  }
  
  public void reset() {
    tchat = new Table();
    lastSender = "----";
    tchat.bottom();
    setY(stage.getHeight()-position);
    scroll.setWidget(tchat);
  }
}
