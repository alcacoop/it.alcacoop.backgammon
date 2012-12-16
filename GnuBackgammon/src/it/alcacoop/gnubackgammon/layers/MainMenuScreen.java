package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen implements Screen {

  private Stage stage;
  private Group g;
  private Image bgImg;
  
  public MainMenuScreen(){
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    stage.addActor(bgImg);
    
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          UIDialog.getQuitDialog(stage);
        }
        return super.keyDown(event, keycode);
      }
    });
    
    //Label titleLabel = new Label("GNU BACKGAMMON MOBILE", GnuBackgammon.skin);
    TextureRegion r = GnuBackgammon.atlas.findRegion("logo");
    Image i = new Image(r);
    
    TextButtonStyle tl = GnuBackgammon.skin2.get("button", TextButtonStyle.class);
    
    TextButton onePlayer = new TextButton("Single Player", tl);
    onePlayer.addListener(cl);
    TextButton twoPlayers = new TextButton("Two Players", tl);
    twoPlayers.addListener(cl);
    TextButton stats = new TextButton("Statistics", tl);
    stats.addListener(cl);
    TextButton options = new TextButton("Options", tl);
    options.addListener(cl);
    TextButton howtoplay = new TextButton("How To Play", tl);
    howtoplay.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        UIDialog.getHelpDialog(stage, false);
      }
    });
    TextButton about = new TextButton("About", tl);
    about.addListener(cl);

    Table table = new Table();
    table.setFillParent(true);
    
    table.add(i).colspan(2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    
    table.row().pad(2);
    table.add(onePlayer).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(twoPlayers).expand().fill().colspan(2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    
    table.row().pad(2);
    table.add(options).expand().fill().colspan(2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add(stats).expand().fill();
    table.add(howtoplay).expand().fill();

    table.row().pad(2);
    table.add(about).expand().fill().colspan(2);

    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    
    g = new Group();
    g.setWidth(stage.getWidth()*0.6f);
    g.setHeight(stage.getHeight()*0.85f);
    g.addActor(table);
    
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    
    stage.addActor(g);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
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
    Gdx.input.setCatchBackKey(true);
    g.setColor(1,1,1,0);
    g.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
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
