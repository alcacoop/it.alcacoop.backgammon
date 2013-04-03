/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon;

import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.FIBSFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.fsm.MenuFSM;
import it.alcacoop.backgammon.fsm.SimulationFSM;
import it.alcacoop.backgammon.layers.AppearanceScreen;
import it.alcacoop.backgammon.layers.FibsScreen;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.layers.MainMenuScreen;
import it.alcacoop.backgammon.layers.MatchOptionsScreen;
import it.alcacoop.backgammon.layers.OptionsScreen;
import it.alcacoop.backgammon.layers.SplashScreen;
import it.alcacoop.backgammon.layers.WelcomeScreen;
import it.alcacoop.backgammon.logic.FibsNetHandler;
import it.alcacoop.backgammon.utils.JSONProperties;
import it.alcacoop.backgammon.utils.MatchRecorder;
import it.alcacoop.fibs.CommandDispatcherImpl;
import it.alcacoop.fibs.Player;

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
import com.badlogic.gdx.utils.Pool;



public class GnuBackgammon extends Game implements ApplicationListener {
  
  private GameScreen gameScreen;
  private MatchOptionsScreen matchOptionsScreen;
  public  MainMenuScreen menuScreen;
  private OptionsScreen optionsScreen;
  private WelcomeScreen welcomeScreen;
  private AppearanceScreen appearanceScreen;
  public FibsScreen fibsScreen;
  public int chatWidth;
  public int chatHeight;
  
  private int resolutions[][] = {
    {1280,740},
    {800,480},
    {480,320}
  };
  public int ss;
  private String[] resname = {"hdpi", "mdpi", "ldpi"};
  public int resolution[];
  
  private GameFSM gameFSM;
  private SimulationFSM simulationFSM;
  private MenuFSM menuFSM;
  private FIBSFSM fibsFSM;
  
  public static BitmapFont font;
  public static TextureAtlas atlas;
  public static Skin skin;
  public static GnuBackgammon Instance;
  public static BaseFSM fsm;
  
  public Board board;
  public Screen currentScreen;
  public JSONProperties jp;
  public Preferences optionPrefs, appearancePrefs;
  
  public SoundManager snd;
  public NativeFunctions nativeFunctions;
  public boolean isGNU = false;
  
  public Preferences fibsPrefs;
  
  public MatchRecorder rec;
  public String fname;
  
  public CommandDispatcherImpl commandDispatcher;
  public FibsNetHandler fibs;
  public String FibsUsername; //ONLY FOR ACCOUNT CREATION
  public String FibsPassword; //ONLY FOR ACCOUNT CREATION
  public Pool<Player> fibsPlayersPool;
  
  
  
  public GnuBackgammon(NativeFunctions n) {
    nativeFunctions = n;
  }
  
  public GnuBackgammon(NativeFunctions n, boolean _isGNU) {
    nativeFunctions = n;
    this.isGNU = _isGNU;
  }

  public void isCR() {
    System.out.println("CR: "+Gdx.graphics.isContinuousRendering());
  }
  
  @Override
  public void create() {
    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();

    Instance = this;
    
    fibsPlayersPool = new Pool<Player>(50){
      @Override
      protected Player newObject() {
        return new Player();
      }
    };
    
    optionPrefs = Gdx.app.getPreferences("GameOptions");
    appearancePrefs = Gdx.app.getPreferences("Appearance");

    snd = new SoundManager();
    rec = new MatchRecorder();
    
    fibsPrefs = Gdx.app.getPreferences("FibsPreferences");
    
    fibs = new FibsNetHandler();
    commandDispatcher = new CommandDispatcherImpl();
    nativeFunctions.injectBGInstance();
    
    //CHECK SCREEN DIM AND SELECT CORRECT ATLAS
    int pWidth = Gdx.graphics.getWidth();
    chatWidth = pWidth;
    if (pWidth<=480) ss = 2;
    else if (pWidth<=800) ss = 1;
    else ss = 0;
    resolution = resolutions[ss];
    
    fname = nativeFunctions.getDataDir()+"/data/match.";

    GnuBackgammon.Instance.jp = new JSONProperties(Gdx.files.internal("data/"+GnuBackgammon.Instance.getResName()+"/pos.json"));
    skin = new Skin(Gdx.files.internal("data/"+resname[ss]+"/myskin.json"));
    atlas = new TextureAtlas(Gdx.files.internal("data/"+resname[ss]+"/pack.atlas"));
    font = new BitmapFont(Gdx.files.internal("data/"+resname[ss]+"/checker.fnt"), false);
    TextureRegion r = font.getRegion();
    r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    BitmapFont f  = skin.getFont("default-font");
    f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    f  = skin.getFont("default-font");
    f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    GnuBackgammon.atlas.addRegion("board", atlas.findRegion("B1"));
    GnuBackgammon.atlas.addRegion("boardbg", atlas.findRegion("B1-BG"));
    GnuBackgammon.atlas.addRegion("cb", atlas.findRegion("CS1-B"));
    GnuBackgammon.atlas.addRegion("cw", atlas.findRegion("CS1-W"));
    GnuBackgammon.atlas.addRegion("ch", atlas.findRegion("CS1-H"));
    
    board = new Board();
    
    gameFSM = new GameFSM(board);
    simulationFSM = new SimulationFSM(board);
    menuFSM = new MenuFSM(board);
    fibsFSM = new FIBSFSM(board);
    
    fsm = simulationFSM;
    
    gameScreen = new GameScreen();
    matchOptionsScreen = new MatchOptionsScreen();
    menuScreen = new MainMenuScreen();
    optionsScreen = new OptionsScreen();
    welcomeScreen = new WelcomeScreen();
    appearanceScreen = new AppearanceScreen();
    fibsScreen = new FibsScreen();
    
    //setScreen(new SplashScreen());
    setFSM("MENU_FSM");
  }

  public String getResName() {
    return resname[ss];
  }
  
  public void goToScreen(int s) {
    switch (s) {
      case 0:
        nativeFunctions.showAds(false);
        setScreen(welcomeScreen);
        currentScreen = welcomeScreen;
        break;
        
      case 1:
        nativeFunctions.showAds(false);
        setScreen(optionsScreen);
        currentScreen = optionsScreen;
        break;
      
      case 2:
        nativeFunctions.showAds(false);
        setScreen(menuScreen);
        currentScreen = menuScreen;
        break;
      
      case 3:
        nativeFunctions.showAds(false);
        setScreen(matchOptionsScreen);
        currentScreen = matchOptionsScreen;
        break;
        
      case 4:
        nativeFunctions.showAds(true);
        setScreen(gameScreen);
        currentScreen = gameScreen;
        break;
        
      case 6:
        setScreen(welcomeScreen);
        currentScreen = welcomeScreen;
        break;
        
      case 7:
        setScreen(appearanceScreen);
        currentScreen = appearanceScreen;
        break;
        
      case 8:
        setScreen(fibsScreen);
        currentScreen = fibsScreen;
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
    else if (type == "FIBS_FSM")
      fsm = fibsFSM;
    fsm.start();
  }

}
