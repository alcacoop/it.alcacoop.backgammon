package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
    
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.85f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    
    table.add(i).colspan(5);
    
    table.row();
    table.add().expand().fill();
    table.add(GnuBackgammon.Instance.board).expand().width(stage.getWidth()*0.65f).height(stage.getHeight()*0.6f).colspan(3);
    table.add().expand().fill();
    
    table.row();
    table.add().colspan(5).fill().expand();
    
    table.row();
    table.add().fill().expand().colspan(2);
    table.add(play).fill().expand().width(stage.getWidth()*0.35f);
    table.add().fill().expand().colspan(2);
    
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
