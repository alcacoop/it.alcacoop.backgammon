package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class GnuBakgammon implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch; //BETTER STAGE!
	private Texture texture;
	private Sprite sBoard, sBlackChecker, sWhiteChecker;
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1, h/w);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/board.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion tBoard = new TextureRegion(texture, 0, 0, 760, 570);
		TextureRegion tWhiteChecker = new TextureRegion(texture, 0, 570, 40, 40);
		TextureRegion tBlackChecker = new TextureRegion(texture, 40, 570, 42, 40);
		
		sBoard = new Sprite(tBoard);
		sBoard.setSize(0.8f, 0.6f);// * sBoard.getHeight() / sBoard.getWidth());
		sBoard.setOrigin(sBoard.getWidth()/2, sBoard.getHeight()/2);
		sBoard.setPosition(-sBoard.getWidth()/2, -sBoard.getHeight()/2);
		
		sBlackChecker = new Sprite(tBlackChecker);
		sBlackChecker.setSize(0.05f, 0.05f);// * sBoard.getHeight() / sBoard.getWidth());
		sBlackChecker.setOrigin(sBlackChecker.getWidth()/2, sBlackChecker.getHeight()/2);
		sBlackChecker.setPosition(0.2f,0);
		
		
		sWhiteChecker = new Sprite(tWhiteChecker);
		sWhiteChecker.setSize(0.05f, 0.05f);
		sWhiteChecker.setOrigin(sWhiteChecker.getWidth()/2, sWhiteChecker.getHeight()/2);
		sWhiteChecker.setPosition(-0.2f,0);
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		sBoard.draw(batch);
		sWhiteChecker.draw(batch);
		sBlackChecker.draw(batch);
		
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
