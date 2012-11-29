package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen implements Screen {

  private Stage stage;
  private Group g;
  private SpriteBatch sb;
  private TextureRegion bgRegion;
  
  public MainMenuScreen(){
    sb = new SpriteBatch();
    bgRegion = GnuBackgammon.atlas.findRegion("bg");
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    
    
    Label titleLabel = new Label("Gnu Backgammon", GnuBackgammon.skin);
    
    TextButton onePlayer = new TextButton("Single Player", GnuBackgammon.skin);
    onePlayer.addListener(cl);
    TextButton twoPlayers = new TextButton("Two Players", GnuBackgammon.skin);
    twoPlayers.addListener(cl);
    TextButton stats = new TextButton("Statistics", GnuBackgammon.skin);
    stats.addListener(cl);
    TextButton options = new TextButton("Options", GnuBackgammon.skin);
    options.addListener(cl);

    Table table = new Table();
    table.debug();
    table.setFillParent(true);
    
    table.add(titleLabel).colspan(2).pad(20);
    
    table.row().pad(2);
    table.row().pad(2);
    table.add(onePlayer).expand().fill().colspan(2);
    
    table.row().pad(2);
    table.add(twoPlayers).expand().fill().colspan(2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add(stats).expand().fill();
    table.add(options).expand().fill();
    
    g = new Group();
    g.setWidth(stage.getWidth()*0.7f);
    g.setHeight(stage.getHeight()*0.65f);
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    g.addActor(table);
    stage.addActor(g);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    
    sb.begin();
    sb.draw(bgRegion, 0,0 , Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    sb.end();
    
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
  }

  
  @Override
  public void show() {
    Gdx.input.setInputProcessor(stage);
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
