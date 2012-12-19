package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;



public class Main implements NativeFunctions {
  private static Main instance;
  
  
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "GnuBackgammon";
    cfg.width = 1280;
    cfg.height = 740;
    instance = new Main();
    
    new LwjglApplication(new GnuBackgammon(instance), cfg);
    
    new SharedLibraryLoader("libs/gnubg.jar").load("gnubg");
    String s = System.getProperty("user.dir");
    s+="/libs/";
    GnubgAPI.InitializeEnvironment(s);
  }

  @Override
  public void showAds(boolean show) {
  }
  
  protected Object handler = new Object() {
  };
}
