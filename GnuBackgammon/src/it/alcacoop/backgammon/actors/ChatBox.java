package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChatBox extends ScrollPane {
  private Stage stage; 
  private boolean visible = false;

  public ChatBox(Actor widget) {
    super(widget);
  }

  public ChatBox(Actor widget, Skin skin) {
    super(widget, skin);
  }

  public ChatBox(Actor widget, ScrollPaneStyle style) {
    super(widget, style);
  }

  public ChatBox(Actor widget, Skin skin, String styleName) {
    super(widget, skin, styleName);
  }
  
  public ChatBox(Stage _stage) {
    super(new Table(), GnuBackgammon.skin);
    stage = _stage;
    ScrollPaneStyle sps = GnuBackgammon.skin.get("chat", ScrollPaneStyle.class);
    setStyle(sps);
    setWidth(stage.getWidth()*0.7f);
    setHeight(stage.getHeight()*0.55f);
    setX((stage.getWidth()-getWidth())/2);
    setY(stage.getHeight()-getHeight()*0.25f);
    //setColor(0.13f, 0.35f, 0.1f, 0.8f);
    //setColor(0.85f, 0.95f, 0.85f, 0.85f);
    setColor(0.85f, 0.85f, 0.85f, 0.85f);
    addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        super.clicked(event, x, y);
        Runnable r = new Runnable() {
          @Override
          public void run() {
            visible = !visible;
            GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
          }
        };
        
        if (!visible)
          addAction(MyActions.sequence(Actions.moveTo(getX(), stage.getHeight()-getHeight(), 0.18f), Actions.run(r)));
        else
          addAction(MyActions.sequence(Actions.run(r), Actions.moveTo(getX(), stage.getHeight()-getHeight()*0.25f, 0.18f)));
      }
    });
  }

  
  public void hide() {
    if (visible) {
      visible = !visible;
      GnuBackgammon.Instance.nativeFunctions.toggleChatBox();
      setY(stage.getHeight()-getHeight()*0.25f);
    }
  }
}
