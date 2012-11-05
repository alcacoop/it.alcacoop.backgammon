package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;


public class GnuBackgammon extends Game implements ApplicationListener {
	public static Texture texture;
	GameScreen gameScreen;
	
	@Override
	public void create() {		
		
		gameScreen = new GameScreen(this);
		setScreen(gameScreen);
	}

}
