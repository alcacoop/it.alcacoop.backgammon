package it.alcacoop.backgammon.layers;

import it.alcacoop.backgammon.GnuBackgammon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BaseScreen implements Screen{

  protected Stage stage;
  protected Image bgImg;
  
  public BaseScreen() {
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.Instance.resolution[0], GnuBackgammon.Instance.resolution[1], false);
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    stage.addActor(bgImg);
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
  }
  
  public Stage getStage() {
    return stage;
  }

  @Override
  public void render(float delta) {}
  @Override
  public void hide() {}
  @Override
  public void pause() {}
  @Override
  public void resume() {}
  @Override
  public void dispose() {}
}
