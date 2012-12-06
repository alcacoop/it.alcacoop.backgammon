package it.alcacoop.gnubackgammon.layers;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.FixedButtonGroup;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class OptionsScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  private Table table;
  
  private FixedButtonGroup speed;
  private FixedButtonGroup sound;
  private FixedButtonGroup lmoves;
  
  
  public OptionsScreen(){
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    stage.addActor(bgImg);
    
    table = new Table();
    table.debug();
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    stage.addActor(table);

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
    
    float width = table.getWidth()/12;
    float height = table.getWidth()/15;
    
    table.add(new Label("GAME OPTIONS", GnuBackgammon.skin)).expand().colspan(5);
    
    table.row();
    table.add().fill().expand().colspan(5);

    table.row().padTop(6).padBottom(6).padLeft(2);
    table.add(new Label("Sounds:", GnuBackgammon.skin)).right().spaceRight(6);
    table.add(sn1).height(height).fill();
    table.add(sn2).fill();
    table.add().colspan(2);
    
    table.row().padTop(6).padBottom(6).padLeft(2);
    table.add(new Label("Animation Speed:", GnuBackgammon.skin)).right().spaceRight(6);
    table.add(sp1).fill().height(height);
    table.add(sp2).fill();
    table.add().colspan(2);
    
    table.row().padTop(6).padBottom(6).padLeft(2);
    table.add(new Label("Legal Moves:", GnuBackgammon.skin)).right().spaceRight(6);
    table.add(lm1).fill().height(height);
    table.add(lm2).fill();
    table.add().colspan(2);
    
    table.row();
    table.add().fill().expand().colspan(5);
    
    table.row();
    table.add(back).expand().fill().colspan(5).height(height).width(4*width);
    
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


  @Override
  public void render(float delta) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.gl.glClearColor(0, 0, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.input.setInputProcessor(stage);
    table.setColor(1,1,1,0);
    table.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }
}
