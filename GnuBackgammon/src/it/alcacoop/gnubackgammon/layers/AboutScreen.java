package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import it.alcacoop.gnubackgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class AboutScreen implements Screen {

  private Stage stage;
  private Group g;
  private Image bgImg;
  //private Dialog exitDialog;
  
  public AboutScreen(){
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    stage.addActor(bgImg);   
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          UIDialog.getQuitDialog(stage);
        }
        return super.keyDown(event, keycode);
      }
    });
    
    Label titleLabel = new Label("GNU BACKGAMMON: ABOUT", GnuBackgammon.skin);
    Label label1 = new Label("GnuBackgammon for Android is based on GNU Backgammon (gnubg).", GnuBackgammon.skin);
    label1.setWrap(true);
    Label label2 = new Label("Its source code is released with a GPLv3 License and is available on GitHub at https://github.com/alcacoop/it.alcacoop.gnubackgammon.", GnuBackgammon.skin);
    label2.setWrap(true);
    
    Table table = new Table();
    table.setWidth(stage.getWidth()*0.8f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);
    
    table.row().pad(2);
    table.add(titleLabel).colspan(2);
    
    table.row().pad(2);
    table.add(label1).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label2).expand().fill().colspan(2);

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    
    float width = table.getWidth()/12;
    float height = table.getWidth()/15;
    table.row();
    table.add(back).expand().fill().colspan(5).height(height).width(4*width);

    stage.addActor(table);
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
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
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
