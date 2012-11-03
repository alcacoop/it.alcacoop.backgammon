package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.actors.Checker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameScreen implements Screen {

	private Stage stage;
	private Board board;
	
	
	public GameScreen(GnuBackgammon bg){
		stage = new Stage(1280, 800, true);
		
		board = new Board();
		stage.addActor(board);
		
		Checker c1 = new Checker(0);
		Checker c2 = new Checker(1);
		c1.x = 522;
		c1.y = 280;
		c2.x = 0f;
		c2.y = 0f;
		
		stage.addActor(c1);
		stage.addActor(c2);
	}

	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
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
