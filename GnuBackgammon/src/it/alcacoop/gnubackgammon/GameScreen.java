package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.Board;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

  private Stage stage;
  private Board board;


  public GameScreen(GnuBackgammon bg){
    stage = new Stage(1280, 740, true);
    board = new Board();
    stage.addActor(board);
    GnubgAPI.SetAILevel(7);
    
    //board.initBoard();
    //board.simulate();
    
    board.animate();
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
