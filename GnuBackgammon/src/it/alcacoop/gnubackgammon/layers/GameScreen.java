package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameScreen implements Screen {

  private Stage stage;
  public Board board;
  private SpriteBatch sb;
  private TextureRegion bgRegion;
  private Table table;
  
  private Label pl1, pl2;
  private TextButton abandon;
  private TextButton resign;
  private TextButton undo;
  
  public GameScreen(){
    sb = new SpriteBatch();
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    bgRegion = GnuBackgammon.atlas.findRegion("bg");
    board = GnuBackgammon.Instance.board;
    
    
    pl1 = new Label("PLAYER 1:", GnuBackgammon.skin);
    pl2 = new Label("CPU ("+MatchState.currentLevel.toString()+"):", GnuBackgammon.skin);
    
    abandon = new TextButton("ABANDON", GnuBackgammon.skin);
    abandon.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.goToScreen(3);
      }
    });
    
    resign = new TextButton("RESIGN", GnuBackgammon.skin);
    resign.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
      }
    });
    
    undo = new TextButton("UNDO", GnuBackgammon.skin);
    undo.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        board.undoMove();
      }
    });
    
    table = new Table();
    stage.addActor(table);
  }

  
  private void initTable() {
    table.clear();
    table.pad(5).setFillParent(true);
    
    table.add(pl2).expand().pad(2).left();
    table.add(pl1).expand().pad(2).left();
    table.add(undo).fill().pad(2);
    table.add(resign).fill().pad(2);
    table.add(abandon).fill().pad(2);
    
    table.row();
    table.add(board).colspan(5).expand().fill();
  }

  
  @Override
  public void render(float delta) {
    
    Gdx.gl.glClearColor(1, 1, 1, 1);
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
    initTable();
    GnuBackgammon.Instance.setFSM("GAME_FSM");
    pl2.setText("CPU ("+MatchState.currentLevel.toString()+"):");
    board.initBoard(0);
    
    Gdx.input.setInputProcessor(stage);
    
    table.setY(stage.getHeight());
    table.addAction(Actions.sequence(
      Actions.delay(0.1f),
      Actions.moveTo(0, 0, 0.3f), 
      Actions.run(new Runnable(){
        @Override
        public void run() {
          MatchState.fTurn = 0;
          MatchState.fMove = 0;
          GnuBackgammon.fsm.processEvent(GameFSM.Events.START_GAME, null);
          board.switchTurn();
        }
      }))
    );
  }

  
  @Override
  public void hide() {
    GnuBackgammon.fsm.stop();
    board.abandon();
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
