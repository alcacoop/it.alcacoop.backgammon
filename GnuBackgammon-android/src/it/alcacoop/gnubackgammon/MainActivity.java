package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;

    initialize(new GnuBackgammon(), cfg);
    GnubgAPI.InitializeEnvironment("/sdcard/gnubg/");
  }
  
  //Load library
  static {
      System.loadLibrary("glib-2.0");
      System.loadLibrary("gthread-2.0");
      System.loadLibrary("gnubg");
  }   

}
