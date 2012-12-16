package it.alcacoop.gnubackgammon.ui;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.FixedButtonGroup;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;




public class GameOptionsTable extends Table {
  
  private FixedButtonGroup speed;
  private FixedButtonGroup sound;
  private FixedButtonGroup lmoves;

  public GameOptionsTable(boolean decoration) {
    setFillParent(true);
    
    TextButtonStyle ts = GnuBackgammon.skin2.get("toggle", TextButtonStyle.class);
    
    speed = new FixedButtonGroup();
    TextButton sp1 = new TextButton("Fast", ts);
    TextButton sp2 = new TextButton("Slow", ts);
    speed.add(sp1);
    speed.add(sp2);
    
    sound = new FixedButtonGroup();
    TextButton sn1 = new TextButton("Yes", ts);
    TextButton sn2 = new TextButton("No", ts);
    sound.add(sn1);
    sound.add(sn2);
    
    lmoves = new FixedButtonGroup();
    TextButton lm1 = new TextButton("Yes", ts);
    TextButton lm2 = new TextButton("No", ts);
    lmoves.add(lm1);
    lmoves.add(lm2);
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    TextButton back = new TextButton("BACK", GnuBackgammon.skin2);
    back.addListener(cl);
    
    Label l = new Label("Animation Speed:", GnuBackgammon.skin);
    
    float width = l.getWidth()*0.8f;
    float height = l.getHeight()*1.8f;
    
    
    if (decoration) {
      add(new Label("GAME OPTIONS", GnuBackgammon.skin)).expand().colspan(5);
    }
    
    row();
    add().fill().expand().colspan(5);

    row().height(height*1.4f);
    add().fill().height(height).expandX();
    add(new Label("Sounds:", GnuBackgammon.skin)).right().spaceRight(6).height(height);
    add(sn1).width(width).fillY().height(height).spaceRight(6);
    add(sn2).width(width).fillY().height(height);
    add().fill().height(height).expandX();
    
    
    row().height(height*1.4f);
    add().fill().height(height).expandX();
    add(l).right().spaceRight(6);
    add(sp1).height(height).width(width).spaceRight(6);
    add(sp2).height(height).width(width);
    add().fill().height(height).expandX();
    
    row();
    add().fill().height(height).expandX();
    add(new Label("Legal Moves:", GnuBackgammon.skin)).right().spaceRight(6);
    add(lm1).height(height).width(width).spaceRight(6);
    add(lm2).height(height).width(width);
    add().fill().height(height).expandX();

    
    if (decoration) {
      row();
      add().fill().expand().colspan(5);
    
      row();
      add(back).expand().fill().colspan(5).height(height).width(1.5f*width);
    }
    
    initFromPrefs();
  }
  
  
  public void initFromPrefs() {
    String sound = GnuBackgammon.Instance.prefs.getString("SOUND", "Yes");
    this.sound.setChecked(sound);
    String speed = GnuBackgammon.Instance.prefs.getString("SPEED", "Fast");
    this.speed.setChecked(speed);
    String lmoves = GnuBackgammon.Instance.prefs.getString("LMOVES", "Yes");
    this.lmoves.setChecked(lmoves);
  }
  
  public void savePrefs() {
    String sound = ((TextButton)this.sound.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("SOUND", sound);
    String speed = ((TextButton)this.speed.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("SPEED", speed);
    String lmoves = ((TextButton)this.lmoves.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("LMOVES", lmoves);
    
    GnuBackgammon.Instance.prefs.flush();
  }


}
