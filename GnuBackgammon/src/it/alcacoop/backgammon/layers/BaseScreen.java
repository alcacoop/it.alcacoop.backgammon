package it.alcacoop.backgammon.layers;

import it.alcacoop.backgammon.GnuBackgammon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BaseScreen implements Screen{

  protected Stage stage;
  protected Image bgImg;
  public float animationTime = 0.2f;
  protected static float lastBGX;
  private float width; 
  
  public BaseScreen() {
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.Instance.resolution[0], GnuBackgammon.Instance.resolution[1], false);
    width = stage.getWidth()*1.2f;
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    bgImg.setWidth(width);
    bgImg.setHeight(stage.getHeight());
    lastBGX = (stage.getWidth()-width)/2;
    bgImg.setPosition(lastBGX, 0);


    NinePatch patch = null;
    TextureRegion r = GnuBackgammon.atlas.findRegion("alca");
    int[] splits = ((AtlasRegion)r).splits;
    patch = new NinePatch(r, splits[0], splits[1], splits[2], splits[3]);

    Image i = new Image(patch);
    
    i.setWidth(stage.getWidth());
    i.setPosition(0, 0);
    
    stage.addActor(bgImg);
    stage.addActor(i);
  }

  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth()*1.2f);
    bgImg.setHeight(stage.getHeight());
  }

  @Override
  public void show() {
    if (lastBGX>0) lastBGX = 0;
    if (lastBGX<(stage.getWidth()-width)) lastBGX=stage.getWidth()-width;
    bgImg.setX(lastBGX);
    GnuBackgammon.Instance.nativeFunctions.showAds(false);
  }
  
  public void initialize() {}
  
  public void moveBG(float x) {
    float _x = lastBGX;
    float newx = _x+x*2;
    if (newx>0) newx = 0;
    if (newx<(stage.getWidth()-width)) newx=stage.getWidth()-width;
    if (lastBGX!=newx) {
      bgImg.setX(newx);
      Gdx.graphics.requestRendering();
    }
    lastBGX = newx;
  }

  
  public void fixBGImg() {
    bgImg.setX(lastBGX);
  }
  
  public Stage getStage() {
    return stage;
  }

  public void fadeOut() {}
  
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
