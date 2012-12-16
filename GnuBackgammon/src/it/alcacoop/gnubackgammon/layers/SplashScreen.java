package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


public class SplashScreen implements Screen {

  private Stage stage;
  private final Image alca;
  private final Image gnu;
  
  public SplashScreen(){
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    TextureRegion r = GnuBackgammon.atlas.findRegion("alca");
    
    alca = new Image(r);
    alca.setX((stage.getWidth()-alca.getWidth())/2);
    alca.setY((stage.getHeight()-alca.getHeight())/2);
    alca.setColor(1,1,1,0);
    
    r = GnuBackgammon.atlas.findRegion("gnu");
    gnu = new Image(r);
    gnu.setX((stage.getWidth()-gnu.getWidth())/2);
    gnu.setY((stage.getHeight()-gnu.getHeight())/2);
    gnu.setColor(1,1,1,0);
    
    stage.addActor(alca);
    stage.addActor(gnu);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
  }

  
  @Override
  public void show() {
    alca.addAction(Actions.sequence(
        Actions.delay(0.1f),
        Actions.fadeIn(0.8f),
        Actions.delay(1.5f),
        Actions.fadeOut(0.8f),
        Actions.delay(0.6f),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            gnu.addAction(Actions.sequence(
                Actions.fadeIn(0.8f),
                Actions.delay(1.5f),
                Actions.fadeOut(0.8f),
                Actions.delay(0.6f),
                Actions.run(new Runnable() {
                  @Override
                  public void run() {
                    GnuBackgammon.Instance.goToScreen(6);
                  }
                })
            ));
          }
        })
      ));
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
