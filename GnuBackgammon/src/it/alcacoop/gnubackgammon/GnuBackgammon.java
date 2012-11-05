package it.alcacoop.gnubackgammon;

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
  public static LabelStyle style;
  GameScreen gameScreen;

  @Override
  public void create() {		
    atlas = new TextureAtlas(Gdx.files.internal("data/pack"));
    //render Font
    font = new BitmapFont(Gdx.files.internal("data/white_bold.fnt"), false);
    style = new LabelStyle(font, Color.WHITE);
    //font.scale(0.005f);
    font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    gameScreen = new GameScreen(this);
    setScreen(gameScreen);
  }


}
