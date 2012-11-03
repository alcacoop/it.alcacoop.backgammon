package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;


public class GnuBackgammon extends Game implements ApplicationListener {
	public static Texture texture;
	GameScreen gameScreen;
	
	@Override
	public void create() {		
		
		texture = new Texture(Gdx.files.internal("data/board.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		//TextureRegion tBoard = new TextureRegion(texture, 0, 0, 760, 570);
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}

}
