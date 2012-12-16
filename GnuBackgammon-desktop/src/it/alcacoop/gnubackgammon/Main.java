package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;



public class Main {
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "GnuBackgammon";
    cfg.width = 480;
    cfg.height = 320;
    
    new LwjglApplication(new GnuBackgammon(), cfg);
    
    new SharedLibraryLoader("libs/gnubg.jar").load("gnubg");
    String s = System.getProperty("user.dir");
    s+="/libs/";
    GnubgAPI.InitializeEnvironment(s);
  }
}
