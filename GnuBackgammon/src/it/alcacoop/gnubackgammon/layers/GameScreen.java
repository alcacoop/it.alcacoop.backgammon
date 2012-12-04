package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.actors.PlayerInfo;
import it.alcacoop.gnubackgammon.fsm.GameFSM.States;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  public Board board;
  private Table table;
  
  private PlayerInfo pInfo[];
  private TextButton abandon;
  private TextButton resign;
  private TextButton undo;
  
  public GameScreen(){
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    stage.addActor(bgImg);
    
    board = GnuBackgammon.Instance.board;
    
    pInfo = new PlayerInfo[2];
    pInfo[0] = new PlayerInfo("AI():", 1);
    pInfo[1] = new PlayerInfo("PL1:", 0);
    
    abandon = new TextButton("ABANDON", GnuBackgammon.skin2);
    abandon.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.setFSM("MENU_FSM");
      }
    });
    
    resign = new TextButton("RESIGN", GnuBackgammon.skin2);
    resign.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
      }
    });
    
    undo = new TextButton("UNDO", GnuBackgammon.skin2);
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
    table.setFillParent(true);
    
    table.add(pInfo[0]).minHeight(50).padTop(5);
    table.add(pInfo[1]).height(50);
    table.add(undo).fill().pad(2).height(50);
    table.add(resign).fill().pad(2).height(50);
    table.add(abandon).fill().pad(2).height(50);
    
    table.row();
    //TODO: PROPORTIONAL FILL COULD BE BETTER
    table.add(board).colspan(5).expand().fill();
  }

  
  public void updatePInfo() {
    pInfo[0].update();
    pInfo[1].update();
  }
  
  @Override
  public void render(float delta) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());    
    Gdx.gl.glClearColor(1, 1, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    initTable();
    board.initBoard();
    
    pInfo[0].setName("AI("+(MatchState.currentLevel.ordinal()+1)+"):");
    pInfo[0].update();
    pInfo[1].update();
    Gdx.input.setInputProcessor(stage);
    
    table.setY(stage.getHeight());
    table.addAction(Actions.sequence(
      Actions.delay(0.1f),
      Actions.moveTo(0, 0, 0.3f),
      Actions.run(new Runnable() {
        @Override
        public void run() {
          GnuBackgammon.fsm.state(States.OPENING_ROLL);
        }
      })
    ));
  }

  
  @Override
  public void hide() {
    board.initBoard();
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
