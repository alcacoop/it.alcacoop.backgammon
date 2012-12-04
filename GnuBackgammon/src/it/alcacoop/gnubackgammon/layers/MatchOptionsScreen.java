package it.alcacoop.gnubackgammon.layers;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.FixedButtonGroup;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen implements Screen {

  private Image bgImg;
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
  private Table table;
  private Label doublingLabel;
  private Label crawfordLabel;
  private Label gameTypeLabel;
  private TextButton back;
  private TextButton play;
  private Label playToLabel;
  private Label titleLabel; 
  
  private Group g;
  
  public MatchOptionsScreen(){
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    prefs = Gdx.app.getPreferences("MatchOptions");
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    stage.addActor(bgImg);
    
    titleLabel = new Label("MATCH SETTINGS", GnuBackgammon.skin);
    difficultyLabel = new Label("Difficulty:", GnuBackgammon.skin);
    playToLabel = new Label("Match to:", GnuBackgammon.skin);
    doublingLabel = new Label("Dbl. Cube:", GnuBackgammon.skin);
    crawfordLabel = new Label("Crawford R.:", GnuBackgammon.skin);
    gameTypeLabel = new Label("Variant:", GnuBackgammon.skin);
    
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
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    play = new TextButton("PLAY", GnuBackgammon.skin);
    play.addListener(cl);
    back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    initFromPrefs();
    table = new Table();
    g = new Group();
    g.setWidth(Gdx.graphics.getWidth()*0.9f);
    g.setHeight(Gdx.graphics.getHeight());
    g.setX((Gdx.graphics.getWidth()-g.getWidth())/2);
    stage.addActor(g);
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
    
    MatchState.SetAILevel(AILevels.getAILevelFromString(sLevel));
    int fCubeUse = sDoubleCube.equals("Yes")?1:0; //USING CUBE
    MatchState.SetCubeUse(fCubeUse);
    MatchState.SetMatchTo(sMatchTo);
    int fCrawford = sCrawford.equals("Yes")?1:0; //REGOLA DI CRAWFORD
    MatchState.SetCrawford(fCrawford);
    int bgv = sGameType.equals("Backgammon")?0:1; //GAME TYPE
    MatchState.setGameVariant(bgv);
    MatchState.SetMatchScore(0, 0);
  }
  
  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(width);
    bgImg.setHeight(height);
  }

  
  @Override
  public void show() {
    initTable();
    g.addActor(table);
    
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
  
  public void initTable() {
    float width = Gdx.graphics.getWidth()/10; 
    table.remove();
    float height = stage.getWidth()/15;
    table = new Table();
    table.setFillParent(true);
    table.add(titleLabel).colspan(9);
    
    
    if (MatchState.matchType==0) {
      table.row();
      table.add().expand().fill();
      table.row();
      table.add().expand().fill();
      table.row();
      table.add(difficultyLabel).right().spaceRight(6);
      table.add(levelButtons[0]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[1]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[2]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[3]).expand().fill().height(height).colspan(2);
      table.row();
      table.add();
      table.add(levelButtons[4]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[5]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[6]).expand().fill().height(height).colspan(2);
      table.add(levelButtons[7]).expand().fill().height(height).colspan(2);
    }
    
    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add(playToLabel).right().spaceRight(6);
    table.add(matchToButtons[0]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[1]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[2]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[3]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[4]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[5]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[6]).expand().fill().height(height).pad(1).width(width);
    table.add(matchToButtons[7]).expand().fill().height(height).pad(1).width(width);
    
    table.row();
    table.add(doublingLabel).right().spaceRight(6);
    table.add(doublingButtons[0]).expand().fill().height(height);
    table.add(doublingButtons[1]).expand().fill().height(height);
    table.add();
    table.add(crawfordLabel).right().colspan(2).spaceRight(6);
    table.add(crawfordButtons[0]).expand().fill().height(height);
    table.add(crawfordButtons[1]).expand().fill().height(height);
    table.add();
    
    table.row();
    table.add(gameTypeLabel).right().spaceRight(6);
    table.add(gameTypeButtons[0]).expand().fill().colspan(2).height(height);
    table.add(gameTypeButtons[1]).expand().fill().colspan(2).height(height);
    table.add().colspan(6);
    table.add();
    
    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add().colspan(2);
    table.add(back).fill().expand().height(height).colspan(2);
    table.add();
    table.add(play).fill().expand().height(height).colspan(2);
    table.add().colspan(2);
    
    table.row();
    table.add().expand().fill();
  }
}
