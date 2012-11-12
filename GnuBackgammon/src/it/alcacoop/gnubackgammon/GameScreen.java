package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

  private Stage stage;
  private Board board;
  public static FSM fsm;


  public GameScreen(GnuBackgammon bg){
    
    //stage = new Stage(1280, 740, true);
    stage = new Stage(1005, 692, true);
    
    /*
    TextureRegion region = GnuBackgammon.atlas.findRegion("bg");
    Image img = new Image(region);
    img.setWidth(1005);
    img.setHeight(692);
    stage.addActor(img);
    */
    
    board = new Board();
    stage.addActor(board);
    fsm = new FSM(board);
    fsm.start();
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
    board.initBoard();
    Gdx.input.setInputProcessor(stage);
    AICalls.SetAILevel(AILevels.SUPREMO);
    fsm.processEvent(Events.START, null);
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
