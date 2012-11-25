package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GameScreen implements Screen {

  private Stage stage;
  private final Board board;
  public static FSM fsm;
  private SpriteBatch sb;
  private TextureRegion bgRegion;
  
  public GameScreen(){
    
    sb = new SpriteBatch();
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    bgRegion = GnuBackgammon.atlas.findRegion("bg");
    board = new Board();
    
    Label pl1 = new Label("PL1:", GnuBackgammon.skin);
    Label pl2 = new Label("CPU:", GnuBackgammon.skin);
    TextButton resign = new TextButton("RESIGN", GnuBackgammon.skin);
    
    TextButton undo = new TextButton("UNDO", GnuBackgammon.skin);
    
    undo.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        board.undoMove();
      }
    });
    
    Table table = new Table();
    table.pad(5).setFillParent(true);
    
    table.add(pl1).expand().pad(10).left();
    table.add(pl2).expand().pad(10).left();
    table.add(resign).fill().pad(10);
    table.add(undo).fill().pad(10);
    table.row();
    table.add(board).colspan(4).expand().fill();
    
    stage.addActor(table);
    fsm = new FSM(board);
    fsm.start();
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
    System.out.println("DIMS: "+Gdx.graphics.getWidth()+"x"+Gdx.graphics.getHeight());
  }

  
  @Override
  public void show() {
    board.initBoard();
    Gdx.input.setInputProcessor(stage);
    AICalls.SetAILevel(AILevels.GRANDMASTER);
    fsm.processEvent(Events.START, null);
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
