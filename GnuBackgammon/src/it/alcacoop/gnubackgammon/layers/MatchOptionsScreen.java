package it.alcacoop.gnubackgammon.layers;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.FixedButtonGroup;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen implements Screen {

  private SpriteBatch sb;
  private TextureRegion bgRegion;
  private Stage stage;
  private Preferences prefs;
  
  private final FixedButtonGroup level;
  private final FixedButtonGroup matchTo;
  private final FixedButtonGroup doubleCube;
  private final FixedButtonGroup crawford;
  private final FixedButtonGroup gametype;
  
  private String _levels[] = {"Beginner","Casual","Intermediate","Advanced","Expert","Worldclass","Supremo","Grandmaster"};
  private TextButton levelButtons[];
  
  private String _matchTo[] = {"1","3","5","7","9","11","13","15"};
  private TextButton matchToButtons[];
  
  private String _yesNo[] = {"Yes","No"};
  private TextButton doublingButtons[];
  private TextButton crawfordButtons[];
  
  private String _gametype[] = {"Backgammon","Nackgammon"};
  private TextButton gameTypeButtons[];
  
  private Label difficultyLabel;
  private Table t1, t2, t3, t4, container; 
  
  public MatchOptionsScreen(){
    sb = new SpriteBatch();
    bgRegion = GnuBackgammon.atlas.findRegion("bg");
    
    prefs = Gdx.app.getPreferences("MatchOptions");
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    Label titleLabel = new Label("MATCH SETTINGS", GnuBackgammon.skin);
    difficultyLabel = new Label("Difficulty:", GnuBackgammon.skin);
    Label playToLabel = new Label("Match to:", GnuBackgammon.skin);
    Label doublingLabel = new Label("Doubling Cube:", GnuBackgammon.skin);
    Label crawfordLabel = new Label("Crawford rule:", GnuBackgammon.skin);
    Label gameTypeLabel = new Label("Game variant:", GnuBackgammon.skin);
    
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    levelButtons = new TextButton[_levels.length];
    level = new FixedButtonGroup();
    for (int i=0; i<_levels.length; i++) {
      levelButtons[i] = new TextButton(_levels[i], ts);
      level.add(levelButtons[i]);
    }
    
    matchToButtons = new TextButton[_matchTo.length];
    matchTo = new FixedButtonGroup();
    for (int i=0; i<_matchTo.length; i++) {
      matchToButtons[i] = new TextButton(_matchTo[i], ts);
      matchTo.add(matchToButtons[i]);
    }
    
    doublingButtons = new TextButton[_yesNo.length];
    doubleCube = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      doublingButtons[i] = new TextButton(_yesNo[i], ts);
      doubleCube.add(doublingButtons[i]);
    }
    
    crawfordButtons = new TextButton[_yesNo.length];
    crawford = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      crawfordButtons[i] = new TextButton(_yesNo[i], ts);
      crawford.add(crawfordButtons[i]);
    }
    
    gameTypeButtons = new TextButton[_gametype.length];
    gametype = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      gameTypeButtons[i] = new TextButton(_gametype[i], ts);
      gametype.add(gameTypeButtons[i]);
    }
    
    t1 = new Table();
    t1.add(titleLabel).colspan(9);
    
    float width = stage.getWidth()/11;
    float height = stage.getWidth()/16;
    
    t2 = new Table();
    t2.add(difficultyLabel).width(2*width).height(height);
    t2.add(levelButtons[0]).expand().fill().width(2*width).height(height).pad(1);
    t2.add(levelButtons[1]).expand().fill().width(2*width).height(height).pad(1);
    t2.add(levelButtons[2]).expand().fill().width(2*width).height(height).pad(1);
    t2.add(levelButtons[3]).expand().fill().width(2*width).height(height).pad(1);
    t2.row().pad(1);
    t2.add();
    t2.add(levelButtons[4]).expand().fill().width(2*width).height(height);
    t2.add(levelButtons[5]).expand().fill().width(2*width).height(height);
    t2.add(levelButtons[6]).expand().fill().width(2*width).height(height);
    t2.add(levelButtons[7]).expand().fill().width(2*width).height(height);
    
    
    t3 = new Table();
    
    t3.row();
    t3.add(playToLabel).width(2*width);
    t3.add(matchToButtons[0]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[1]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[2]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[3]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[4]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[5]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[6]).expand().fill().width(width).height(height).pad(1);
    t3.add(matchToButtons[7]).expand().fill().width(width).height(height).pad(1);
    
    t3.row();
    t3.add().colspan(9).fill().expand().height(10);
    t3.row();
    t3.add(doublingLabel).width(2*width);
    t3.add(doublingButtons[0]).expand().fill().width(width).height(height);
    t3.add(doublingButtons[1]).expand().fill().width(width).height(height);
    t3.add().colspan(6).fill();
    
    t3.row();
    t3.add().colspan(9).fill().expand().height(10);
    t3.row();
    t3.add(crawfordLabel).width(2*width);
    t3.add(crawfordButtons[0]).expand().fill().width(width).height(height);
    t3.add(crawfordButtons[1]).expand().fill().width(width).height(height);
    t3.add().colspan(6);

    t3.row();
    t3.add().colspan(9).fill().expand().height(10);
    t3.row();
    t3.add(gameTypeLabel).width(2*width);
    t3.add(gameTypeButtons[0]).expand().fill().colspan(2).width(2*width).height(height);
    t3.add(gameTypeButtons[1]).expand().fill().colspan(2).width(2*width).height(height);
    t3.add().colspan(4);

    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    TextButton play = new TextButton("PLAY", GnuBackgammon.skin);
    play.addListener(cl);
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    
    t4 = new Table();
    t4.add();
    t4.add(back).fill().expand().width(3*width).height(height);
    t4.add().width(2*width);
    t4.add(play).fill().expand().width(3*width).height(height);
    t4.add();

    container = new Table();
    stage.addActor(container);
    container.setFillParent(true);
    
    initFromPrefs();
  }

  
  public void initFromPrefs() {
    String sLevel = prefs.getString("LEVEL", "Beginner");
    level.setChecked(sLevel);
    String sMatchTo= prefs.getString("MATCHTO", "1");
    matchTo.setChecked(sMatchTo);
    String sDoubleCube= prefs.getString("DOUBLE_CUBE", "yes");
    doubleCube.setChecked(sDoubleCube);
    String sCrawford= prefs.getString("CRAWFORD", "yes");
    crawford.setChecked(sCrawford);
    String sVariant= prefs.getString("VARIANT", "Backgammon");
    gametype.setChecked(sVariant);
  }

  
  public void savePrefs() {
    String sLevel = ((TextButton)level.getChecked()).getText().toString(); 
    prefs.putString("LEVEL", sLevel);
    String sMatchTo = ((TextButton)matchTo.getChecked()).getText().toString();
    prefs.putString("MATCHTO", sMatchTo);
    String sDoubleCube = ((TextButton)doubleCube.getChecked()).getText().toString();
    prefs.putString("DOUBLE_CUBE", sDoubleCube);
    String sCrawford = ((TextButton)crawford.getChecked()).getText().toString();
    prefs.putString("CRAWFORD", sCrawford);
    String sGameType = ((TextButton)gametype.getChecked()).getText().toString();
    prefs.putString("VARIANT", sGameType);
    
    prefs.flush();
    
    AICalls.SetAILevel(AILevels.getAILevelFromString(sLevel));
    //TODO: Implement functions
    MatchState.fCubeUse = sDoubleCube.equals("Yes")?1:0; //USING CUBE
    MatchState.SetMatchTo(sMatchTo);
    MatchState.fCrawford = sCrawford.equals("Yes")?1:0; //REGOLA DI CRAWFORD
    MatchState.bgv = sGameType.equals("Backgammon")?0:1; //GAME TYPE
    MatchState.SetMatchScore(0, 0);
  }
  
  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    sb.begin();
    sb.draw(bgRegion, 0,0 , Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    sb.end();
    
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
  }

  
  @Override
  public void show() {
    t1.remove();
    t2.remove();
    t3.remove();
    t4.remove();
    container = new Table();
    container.setFillParent(true);
    stage.addActor(container);
    
    if (MatchState.matchType==1) {
      container.add(t1).expand();
      container.row().pad(5);
      container.add(t3).expand();
      container.row().pad(5);
      container.add(t4).expand();
    } else {
      container.add(t1);
      container.row().pad(5);
      container.add(t2);
      container.row().pad(5);
      container.add(t3);
      container.row().pad(5);
      container.add(t4);
    }
    
    Gdx.input.setInputProcessor(stage);
    container.setColor(1,1,1,0);
    container.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }
}
