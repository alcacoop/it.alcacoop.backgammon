package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChatBox extends Table {
  private Stage stage;
  public boolean visible = false;
  private boolean animating = false;
  private ScrollPane scroll;
  private Table tchat;
  private float position;
  float height, cheight;
  String lastSender = "----";
  private Runnable r1, r2;

  private Table cont;

  public ChatBox(Stage _stage) {
    super();
    stage = _stage;

    setWidth(stage.getWidth() * 0.7f);
    setHeight(stage.getHeight() * 0.8f);

    cont = new Table();

    height = stage.getHeight() * 0.6f;
    cheight = GnuBackgammon.chatHeight;
    position = (height) * 0.31f;

    r1 = new Runnable() {
      @Override
      public void run() {
        if (visible)
          GnuBackgammon.Instance.nativeFunctions.hideChatBox();
        else
          GnuBackgammon.Instance.nativeFunctions.showChatBox();
        visible = !visible;
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

    cont.setHeight(height);
    cont.setWidth(stage.getWidth() * 0.7f);
    cont.setColor(0.85f, 0.85f, 0.85f, 0.85f);


    cont.setBackground(GnuBackgammon.skin.getDrawable("chatbox"));
    scroll = new ScrollPane(tchat, GnuBackgammon.skin);
    scroll.setColor(1, 1, 1, 1);
    scroll.setOverscroll(false, false);
    cont.add().expandX().fillX().height(cheight);
    cont.row();
    cont.add(scroll).expand().fill().height((height - cheight));

    add(cont).width(stage.getWidth() * 0.7f).fill();
    row();
    add().expand().fill();
    setX((stage.getWidth() - getWidth()) / 2);
    setY(stage.getHeight() - position);

    setTouchable(Touchable.enabled);

    addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        toggle();
      }
    });
  }

  public void toggle() {
    if (animating)
      return;
    if (!visible)
      show();
    else
      hide();
  }


  public void show() {
    animating = true;
    if (!visible)
      addAction(MyActions.sequence(Actions.moveTo(getX(), stage.getHeight() - getHeight(), 0.18f), Actions.run(r1), Actions.run(r2)));
  }

  public void hide() {
    animating = true;
    if (visible)
      addAction(MyActions.sequence(Actions.run(r1), Actions.moveTo(getX(), stage.getHeight() - position, 0.18f), Actions.run(r2)));
  }

  public void hardHide() {
    GnuBackgammon.Instance.nativeFunctions.hideChatBox();
    visible = false;
    animating = false;
    setY(stage.getHeight() - position);
  }


  public void appendMessage(final String user, final String msg, final boolean direction) {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
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
          tchat.add(new Label(user + ":", ls)).colspan(2).fillX();
          lastSender = user;
        }

        tchat.row();
        tchat.add().width(stage.getWidth() * 0.05f).right().top().fillX();
        Label m = new Label(msg, ls);
        m.setWrap(true);
        tchat.add(m).left().expandX().fill().bottom();
        scroll.setWidget(tchat);
        scroll.addAction(MyActions.sequence(Actions.run(new Runnable() {
          @Override
          public void run() {
            scroll.setScrollPercentY(1);
          }
        }), Actions.delay(1.2f)));

        Gdx.graphics.requestRendering();
      }
    });
  }


  public void reset() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        tchat = new Table();
        lastSender = "----";
        tchat.bottom();
        setY(stage.getHeight() - position);
        scroll.setWidget(tchat);
        setVisible(true);
      }
    });
  }

  public boolean isScrolling() {
    return (scroll.getVelocityY() != 0);
  }

}
