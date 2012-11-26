package it.alcacoop.gnubackgammon.layers;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen implements Screen {

  private Stage stage;
  private Group g;
  
  private final ButtonGroup level;
  private final ButtonGroup matchTo;
  private final ButtonGroup doubleCube;
  private final ButtonGroup crawford;
  
  private String _levels[] = {"Beginner","Casual","Intermediate","Advanced","Expert","Worldclass","Supremo","Grandmaster"};
  private TextButton levelButtons[];
  
  private String _matchTo[] = {"1","3","5","7","9","11","15"};
  private TextButton matchToButtons[];
  
  private String _yesNo[] = {"Yes","No"};
  private TextButton doublingButtons[];
  private TextButton crawfordButtons[];
  
  
  public MatchOptionsScreen(){
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    Label titleLabel = new Label("MATCH SETTINGS", GnuBackgammon.skin);
    Label difficultyLabel = new Label("Difficulty:", GnuBackgammon.skin);
    Label playToLabel = new Label("Play to:", GnuBackgammon.skin);
    Label doublingLabel = new Label("Doubling Cube:", GnuBackgammon.skin);
    Label crawfordLabel = new Label("Crawford rule:", GnuBackgammon.skin);
    
    ClickListener cl = new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Button b = (Button) event.getListenerActor();
        String group = "";
        if (b == matchTo.getChecked()) group="MATCHTO";
        else if (b == level.getChecked()) group="LEVEL";
        else if (b == crawford.getChecked()) group="CRAWFORD";
        else if (b == doubleCube.getChecked()) group="DOUBLE_CUBE";
        System.out.println("CLICKED: "+group+" "+((TextButton) b).getText());
      }
    };
    
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    levelButtons = new TextButton[_levels.length];
    level = new ButtonGroup();
    for (int i=0; i<_levels.length; i++) {
      levelButtons[i] = new TextButton(_levels[i], ts);
      level.add(levelButtons[i]);
      levelButtons[i].addListener(cl);
    }
    
    matchToButtons = new TextButton[_matchTo.length];
    matchTo = new ButtonGroup();
    for (int i=0; i<_matchTo.length; i++) {
      matchToButtons[i] = new TextButton(_matchTo[i], ts);
      matchTo.add(matchToButtons[i]);
      matchToButtons[i].addListener(cl);
    }
    
    doublingButtons = new TextButton[_yesNo.length];
    doubleCube = new ButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      doublingButtons[i] = new TextButton(_yesNo[i], ts);
      doubleCube.add(doublingButtons[i]);
      doublingButtons[i].addListener(cl);
    }
    
    crawfordButtons = new TextButton[_yesNo.length];
    crawford = new ButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      crawfordButtons[i] = new TextButton(_yesNo[i], ts);
      crawford.add(crawfordButtons[i]);
      crawfordButtons[i].addListener(cl);
    }
    
    
    
    Table table = new Table();
    table.setFillParent(true);
    
    table.row().pad(2);
    table.add().colspan(5).fillY().expand();
    
    table.row().pad(2);
    table.add(titleLabel).colspan(9);
    
    table.row().pad(2);
    table.add().colspan(5).fillY().expand();
    
    table.row().pad(2);
    table.add(difficultyLabel).right();
    table.add(levelButtons[0]).expand().fill();
    table.add(levelButtons[1]).expand().fill();
    table.add(levelButtons[2]).expand().fill();
    table.add(levelButtons[3]).expand().fill();
    table.row().pad(2);
    table.add();
    table.add(levelButtons[4]).expand().fill();
    table.add(levelButtons[5]).expand().fill();
    table.add(levelButtons[6]).expand().fill();
    table.add(levelButtons[7]).expand().fill();
    
    table.row().pad(2);
    table.add().colspan(5).fill().expand();
    
    table.row().pad(2);
    table.add(playToLabel).right();
    table.add(matchToButtons[0]).expand().fill();
    table.add(matchToButtons[1]).expand().fill();
    table.add(matchToButtons[2]).expand().fill();
    table.add(matchToButtons[3]).expand().fill();
    table.row().pad(2);
    table.add();
    table.add(matchToButtons[4]).expand().fill();
    table.add(matchToButtons[5]).expand().fill();
    table.add(matchToButtons[6]).expand().fill();
    
    table.row().pad(2);
    table.add().colspan(5).fill().expand();
    
    table.row().pad(2);
    table.add(doublingLabel).right();
    table.add(doublingButtons[0]).expand().fill();
    table.add(doublingButtons[1]).expand().fill();
    
    
    table.row().pad(2);
    table.add().colspan(5).fill().expand();
    
    table.row().pad(2);
    table.add(crawfordLabel).right();
    table.add(crawfordButtons[0]).expand().fill();
    table.add(crawfordButtons[1]).expand().fill();
    
    table.row().pad(2);
    table.add().colspan(5).fill().expand();
    
    TextButton play = new TextButton("PLAY!", GnuBackgammon.skin);
    play.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.goToScreen(2);
      }
    });
    table.row().pad(2);
    table.add(play).fillY().expand().colspan(5).width(240);
    
    table.row().pad(2);
    table.add().colspan(5).fill().expand();
    
    g = new Group();
    g.setWidth(stage.getWidth()*0.9f);
    g.setHeight(stage.getHeight()*0.95f);
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    g.addActor(table);
    stage.addActor(g);
    
    level.setChecked("Casual");
    TextButton b = (TextButton)level.getAllChecked().get(0);
    System.out.println(b.getText());
    
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
