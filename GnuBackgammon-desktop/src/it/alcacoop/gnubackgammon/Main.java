package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "GnuBackgammon";
		cfg.useGL20 = false;
		cfg.width = 640;
		cfg.height = 370;
		
		new LwjglApplication(new GnuBackgammon(), cfg);
	}
}
