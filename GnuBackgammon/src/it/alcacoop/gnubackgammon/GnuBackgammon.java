package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


public class GnuBackgammon extends Game implements ApplicationListener {
	public static TextureAtlas atlas;
	GameScreen gameScreen;
	
	@Override
	public void create() {		
		atlas = new TextureAtlas(Gdx.files.internal("data/pack"));
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}
	

}
