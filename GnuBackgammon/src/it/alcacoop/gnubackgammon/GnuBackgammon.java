package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class GnuBackgammon extends Game implements ApplicationListener {
  public static TextureAtlas atlas;
  private BitmapFont font;
  public static LabelStyle styleBlack, styleWhite;
  public static Skin skin;
  private GameScreen gameScreen;
  
  private int resolutions[][] = {
    {1280,740},
    {800,480},
    {480,320}
  };
  
  private static int ss;
  private static String[] resname = {"hdpi", "mdpi", "ldpi"};
  public static int resolution[];
  
  @Override
  public void create() {		
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
      
    styleWhite = new LabelStyle(font, Color.WHITE);
    styleBlack = new LabelStyle(font, Color.BLACK);

    gameScreen = new GameScreen();
    setScreen(gameScreen);
//    MenuScreen menuScreen = new MenuScreen();
//    setScreen(menuScreen);
//    MatchOptionsScreen matchOptionsScreen = new MatchOptionsScreen();
//    setScreen(matchOptionsScreen);
    
    
//  OptionsScreen optionsScreen = new OptionsScreen();
//  setScreen(optionsScreen);
  }

  public static String getResName() {
    return resname[ss];
  }
}
