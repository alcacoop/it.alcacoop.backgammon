package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AIThread;
import it.alcacoop.gnubackgammon.logic.FSM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

  private Stage stage;
  private Board board;
  
  public static FSM fsm;


  public GameScreen(GnuBackgammon bg){
    fsm = new FSM();
    fsm.start();
    
    stage = new Stage(1280, 740, true);
    board = new Board();
    stage.addActor(board);
    
    AIThread thread = new AIThread();
    
    int d[] = {0,0};
    thread.post(AICalls.RollDice(d));
    
    board.initBoard();
    board.simulate();
    //board.animate();
    
    //int[]  moves = {5,-1,5,-1,-1,-1,-1,-1};
    //board.setMoves(moves);
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
