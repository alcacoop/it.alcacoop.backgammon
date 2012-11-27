package it.alcacoop.gnubackgammon.layers;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.FixedButtonGroup;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen implements Screen {

  private SpriteBatch sb;
  private TextureRegion bgRegion;
  private Stage stage;
  private Group g;
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
  
  
  public MatchOptionsScreen(){
    sb = new SpriteBatch();
    bgRegion = GnuBackgammon.atlas.findRegion("bg");
    
    prefs = Gdx.app.getPreferences("MatchOptions");
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    Label titleLabel = new Label("MATCH SETTINGS", GnuBackgammon.skin);
    Label difficultyLabel = new Label("Difficulty:", GnuBackgammon.skin);
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
    
    Table table = new Table();
    table.setFillParent(true);
    
    table.row().pad(2);
    table.add().colspan(9).fillY().expand();
    
    table.row().pad(2);
    table.add(titleLabel).colspan(9);
    
    table.row().pad(2);
    table.add().colspan(9).fillY().expand();
    
    table.row().pad(2);
    table.add(difficultyLabel).right();
    table.add(levelButtons[0]).expand().fill().colspan(2);
    table.add(levelButtons[1]).expand().fill().colspan(2);
    table.add(levelButtons[2]).expand().fill().colspan(2);
    table.add(levelButtons[3]).expand().fill().colspan(2);
    table.row().pad(2);
    table.add();
    table.add(levelButtons[4]).expand().fill().colspan(2);
    table.add(levelButtons[5]).expand().fill().colspan(2);
    table.add(levelButtons[6]).expand().fill().colspan(2);
    table.add(levelButtons[7]).expand().fill().colspan(2);
    
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    
    table.row().pad(2);
    table.add(playToLabel).right();
    table.add(matchToButtons[0]).expand().fill();
    table.add(matchToButtons[1]).expand().fill();
    table.add(matchToButtons[2]).expand().fill();
    table.add(matchToButtons[3]).expand().fill();
    table.add(matchToButtons[4]).expand().fill();
    table.add(matchToButtons[5]).expand().fill();
    table.add(matchToButtons[6]).expand().fill();
    table.add(matchToButtons[7]).expand().fill();
    
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    
    table.row().pad(2);
    table.add(doublingLabel).right();
    table.add(doublingButtons[0]).expand().fill();
    table.add(doublingButtons[1]).expand().fill();
    table.add().colspan(6);
    
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    
    table.row().pad(2);
    table.add(crawfordLabel).right();
    table.add(crawfordButtons[0]).expand().fill();
    table.add(crawfordButtons[1]).expand().fill();
    table.add().colspan(6);

    table.row().pad(2);
    table.add().colspan(9).fill().expand();

    table.row().pad(2);
    table.add(gameTypeLabel).right();
    table.add(gameTypeButtons[0]).expand().fill().colspan(2);
    table.add(gameTypeButtons[1]).expand().fill().colspan(2);
    table.add().colspan(4);
    
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    
    TextButton play = new TextButton("PLAY!", GnuBackgammon.skin);
    play.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.Instance.goToScreen(4);
      }
    });
    
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.Instance.goToScreen(2);
      }
    });
    
    table.row().pad(2);
    table.add().colspan(2);
    table.add(back).fill().expand().colspan(2);
    table.add();
    table.add(play).fill().expand().colspan(2);
    table.add().colspan(2);
    
    table.row().pad(2);
    table.add().colspan(9).fill().expand();
    
    g = new Group();
    g.setWidth(stage.getWidth()*0.9f);
    g.setHeight(stage.getHeight()*0.95f);
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    g.addActor(table);
    stage.addActor(g);
    
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
    MatchState.fCubeUse = sDoubleCube.equals("Yes")?1:0; //USING CUBE
    MatchState.nMatchTo = Integer.parseInt(sMatchTo);
    MatchState.fCrawford = sCrawford.equals("Yes")?1:0; //REGOLA DI CRAWFORD
    MatchState.bgv = sGameType.equals("Backgammon")?0:1; //GAME TYPE
    
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
    Gdx.input.setInputProcessor(stage);
    g.setColor(1,1,1,0);
    g.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
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
