package it.alcacoop.gnubackgammon.layers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


public class MenuScreen implements Screen {

  private Stage stage;
  
  public MenuScreen(){
    stage = new Stage(1005, 752, true);
    
    // Initialize skin
    Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
    
    Label titleLabel = new Label("GnuBackgammon", skin);
    TextButton onePlayer = new TextButton("Single Player", skin);
    TextButton twoPlayers = new TextButton("Two Player", skin);
    TextButton options = new TextButton("Options", skin);

    Table table = new Table();
    table.debug();
    table.add(titleLabel).expand().fillY();
    table.row();
    table.add(onePlayer).expand().width(180).height(80);
    table.row();
    table.add(twoPlayers).expand().width(180).height(80);
    table.row();
    table.add(options).expand().width(180).height(80);
    table.setFillParent(true);

    stage.addActor(table);
  }


  @Override
  public void render(float delta) {
    
    Gdx.gl.glClearColor(0, 0, 1, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    
    stage.act(delta);
    stage.draw();
    Table.drawDebug(stage);
  }


  @Override
  public void resize(int width, int height) {
  }

  
  @Override
  public void show() {
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
