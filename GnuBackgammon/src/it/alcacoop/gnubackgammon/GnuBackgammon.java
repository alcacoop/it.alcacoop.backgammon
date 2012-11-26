package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.layers.MatchOptionsScreen;
import it.alcacoop.gnubackgammon.layers.MenuScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class GnuBackgammon extends Game implements ApplicationListener {
  
  private static GameScreen gameScreen;
  private static MatchOptionsScreen matchOptionsScreen;
  private static MenuScreen menuScreen; 
  private int resolutions[][] = {
    {1280,740},
    {800,480},
    {480,320}
  };
  private static int ss;
  private static String[] resname = {"hdpi", "mdpi", "ldpi"};
  
  public static BitmapFont font;
  public static TextureAtlas atlas;
  public static Skin skin;
  public static int resolution[];
  public static GnuBackgammon Instance;
  
  
  @Override
  public void create() {		
    Instance = this;
    //CHECK SCREEN DIM AND SELECT CORRECT ATLAS
    int pWidth = Gdx.graphics.getWidth();
    if (pWidth<=480) ss = 2;
    else if (pWidth<=800) ss = 1;
    else ss = 0;
    resolution = resolutions[ss];

    skin = new Skin(Gdx.files.internal("data/"+resname[ss]+"/uiskin.json"));
    
    atlas = new TextureAtlas(Gdx.files.internal("data/"+resname[ss]+"/pack.atlas"));
    
    font = new BitmapFont(Gdx.files.internal("data/"+resname[ss]+"/checker.fnt"), false);
    TextureRegion r = font.getRegion();
    r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
      
    gameScreen = new GameScreen();
    matchOptionsScreen = new MatchOptionsScreen();
    menuScreen = new MenuScreen();
    
    setScreen(matchOptionsScreen);
  }

  
  public String getResName() {
    return resname[ss];
  }
  
  
  public void goToScreen(int s) {
    switch (s) {
      case 0:
        setScreen(menuScreen);
        break;
      
      case 1:
        setScreen(matchOptionsScreen);
        break;
        
      case 2:
        setScreen(gameScreen);
        break;
    }
  }
  
  
}