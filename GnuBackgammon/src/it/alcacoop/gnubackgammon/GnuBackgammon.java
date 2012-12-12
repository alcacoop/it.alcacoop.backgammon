package it.alcacoop.gnubackgammon;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.fsm.MenuFSM;
import it.alcacoop.gnubackgammon.fsm.SimulationFSM;
import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.layers.MatchOptionsScreen;
import it.alcacoop.gnubackgammon.layers.MainMenuScreen;
import it.alcacoop.gnubackgammon.layers.OptionsScreen;
import it.alcacoop.gnubackgammon.layers.WelcomeScreen;
import it.alcacoop.gnubackgammon.utils.JSONProperties;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class GnuBackgammon extends Game implements ApplicationListener {
  
  private static GameScreen gameScreen;
  private static MatchOptionsScreen matchOptionsScreen;
  private static MainMenuScreen menuScreen;
  private static OptionsScreen optionsScreen;
  private static WelcomeScreen welcomeScreen;
  private int resolutions[][] = {
    {1280,740},
    {800,480},
    {480,320}
  };
  public static int ss;
  private static String[] resname = {"hdpi", "mdpi", "ldpi"};
  
  private GameFSM gameFSM;
  private SimulationFSM simulationFSM;
  private MenuFSM menuFSM;
  
  public static BitmapFont font;
  public static TextureAtlas atlas;
  public static Skin skin, skin2;
  public static int resolution[];
  public static GnuBackgammon Instance;
  public static BaseFSM fsm;
  public Board board;
  public Screen currentScreen;
  public JSONProperties jp;
  public Preferences prefs;
  public SoundManager snd;
  
  
  @Override
  public void create() {
    Instance = this;
    prefs = Gdx.app.getPreferences("GameOptions");
    snd = new SoundManager();
    
    //CHECK SCREEN DIM AND SELECT CORRECT ATLAS
    int pWidth = Gdx.graphics.getWidth();
    if (pWidth<=480) ss = 2;
    else if (pWidth<=800) ss = 1;
    else ss = 0;
    resolution = resolutions[ss];

    GnuBackgammon.Instance.jp = new JSONProperties(Gdx.files.internal("data/"+GnuBackgammon.Instance.getResName()+"/pos.json"));
    skin = new Skin(Gdx.files.internal("data/"+resname[ss]+"/uiskin.json"));
    skin2 = new Skin(Gdx.files.internal("data/"+resname[ss]+"/myskin.json"));
    atlas = new TextureAtlas(Gdx.files.internal("data/"+resname[ss]+"/pack.atlas"));
    font = new BitmapFont(Gdx.files.internal("data/"+resname[ss]+"/checker.fnt"), false);
    TextureRegion r = font.getRegion();
    r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    BitmapFont f  = skin.getFont("default-font");
    f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    f  = skin2.getFont("default-font");
    f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    board = new Board();
    
    gameFSM = new GameFSM(board);
    simulationFSM = new SimulationFSM(board);
    menuFSM = new MenuFSM(board);
    
    fsm = simulationFSM;
    
    
    gameScreen = new GameScreen();
    matchOptionsScreen = new MatchOptionsScreen();
    menuScreen = new MainMenuScreen();
    optionsScreen = new OptionsScreen();
    welcomeScreen = new WelcomeScreen();
    
    setScreen(welcomeScreen);
  }

  
  public String getResName() {
    return resname[ss];
  }
  
  
  public void goToScreen(int s) {
    switch (s) {
      case 0:
        currentScreen = welcomeScreen;
        setScreen(welcomeScreen);
        break;
        
      case 1:
        currentScreen = optionsScreen;
        setScreen(optionsScreen);
        break;
      
      case 2:
        currentScreen = menuScreen;
        setScreen(menuScreen);
        break;
      
      case 3:
        currentScreen = matchOptionsScreen;
        setScreen(matchOptionsScreen);
        break;
        
      case 4:
        currentScreen = gameScreen;
        setScreen(gameScreen);
        break;
    }
  }


  public void setFSM(String type) {
    if (fsm!=null) fsm.stop();
    
    if (type == "SIMULATED_FSM")
      fsm = simulationFSM;
    else if (type == "MENU_FSM")
      fsm = menuFSM;
    else if (type == "GAME_FSM")
      fsm = gameFSM;
    
    fsm.start();
  }
  
}
