package it.alcacoop.gnubackgammon.actors;

import it.alcacoop.gnubackgammon.GnuBackgammon;
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

  public GameOptionsTable(float w, float h, boolean decoration) {
    setWidth(w);
    setHeight(h);
    debug();
    
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    
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
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    
    
    float width = w/3;
    float height = h/7;
    if (!decoration) {
      width = w;
      height = h/2.4f;
    }
    
    if (decoration) {
      add(new Label("GAME OPTIONS", GnuBackgammon.skin)).expand().colspan(5);
      row();
      add().fill().expand().colspan(5);
    }

    row().padTop(6).padBottom(6);
    add(new Label("Sounds:", GnuBackgammon.skin)).right().spaceRight(6);
    add(sn1).fill().height(height).width(width);
    add(sn2).fill().height(height).width(width);
    add().colspan(2);
    
    row().padTop(6).padBottom(6);
    add(new Label("Animation Speed:", GnuBackgammon.skin)).right().spaceRight(6);
    add(sp1).fill().height(height).width(width);
    add(sp2).fill().height(height).width(width);
    add().colspan(2);
    
    row().padTop(6).padBottom(6);
    add(new Label("Legal Moves:", GnuBackgammon.skin)).right().spaceRight(6);
    add(lm1).fill().height(height).width(width);
    add(lm2).fill().height(height).width(width);
    add().colspan(2);

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
