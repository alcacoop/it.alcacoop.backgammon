package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.layers.GameScreen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;


public class GnuBackgammon extends Game implements ApplicationListener {
  public static TextureAtlas atlas;
  private BitmapFont font;
  public static LabelStyle styleBlack, styleWhite;
  public static int width;
  public static int height;
  private GameScreen gameScreen;
  
  
  public GnuBackgammon(int w, int h) {
   width = w;
   height = h;
  }
  
  @Override
  public void create() {		
    atlas = new TextureAtlas(Gdx.files.internal("data/pack"));
    
    //render Font
    font = new BitmapFont(Gdx.files.internal("data/checker.fnt"), false);
    font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    styleWhite = new LabelStyle(font, Color.WHITE);
    styleBlack = new LabelStyle(font, Color.BLACK);

    gameScreen = new GameScreen(this);
    setScreen(gameScreen);
  }

}
