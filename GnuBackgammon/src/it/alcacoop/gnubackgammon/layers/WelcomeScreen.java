package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class WelcomeScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  private Table table;

  
  public WelcomeScreen(){
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    stage.addActor(bgImg);
    
    //Label titleLabel = new Label("GNU BACKGAMMON MOBILE", GnuBackgammon.skin);
    TextureRegion r = GnuBackgammon.atlas.findRegion("logo");
    Image i = new Image(r);
    
    TextButtonStyle tl = GnuBackgammon.skin2.get("button", TextButtonStyle.class);
    TextButton play = new TextButton("PLAY!", tl);
    play.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.setFSM("MENU_FSM");
      }
    });
    
    GnuBackgammon.Instance.setFSM("SIMULATED_FSM");
    
    table = new Table();
    table.setFillParent(true);
    
    table.row();
    table.add().fill().expand().colspan(5);
    table.row();
    table.add(i).colspan(5);
    
    table.row().height(stage.getHeight()*0.75f);
    table.add().width(stage.getWidth()*0.1f);
    table.add(GnuBackgammon.Instance.board).width(stage.getWidth()*0.7f).colspan(3).height(stage.getHeight()*0.65f);
    table.add().width(stage.getWidth()*0.1f);
    
    table.row().height(stage.getHeight()*0.1f);
    table.add().fill().expand();
    table.add().width(stage.getWidth()*0.2f);
    table.add(play).fill().expand();
    table.add().width(stage.getWidth()*0.2f);
    table.add().fill().expand();
    
    table.row();
    table.add().fill().expand().colspan(5);
    
    stage.addActor(table);
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
    table.setColor(1,1,1,0);
    table.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }

  @Override
  public void hide() {
    GnuBackgammon.Instance.board.initBoard();
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
