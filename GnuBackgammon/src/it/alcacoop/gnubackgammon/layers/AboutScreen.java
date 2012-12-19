package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM.Events;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
  private Image bgImg;
  private Table table;
  
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
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });
    
    Label titleLabel = new Label("ABOUT", GnuBackgammon.skin);
    Label label1 = new Label("GnuBackgammon Mobile is based on GNU Backgammon (gnubg).", GnuBackgammon.skin);
    label1.setWrap(true);
    Label label1Link = new Label("http://www.gnubg.org", GnuBackgammon.skin);
    label1Link.setWrap(true);
    Label label2 = new Label("Its source code is released under a GPLv3 License", GnuBackgammon.skin);
    label2.setWrap(true);
    Label label2Link = new Label("http://www.gnu.org/licenses/gpl.html", GnuBackgammon.skin);
    label2Link.setWrap(true);
    Label label3 = new Label("and is available on GitHub at", GnuBackgammon.skin);
    label3.setWrap(true);
    Label label3Link = new Label("https://github.com/alcacoop/it.alcacoop.gnubackgammon", GnuBackgammon.skin);
    label3Link.setWrap(true);
    
    table = new Table();
    table.setWidth(stage.getWidth()*0.8f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);
    
    table.row().pad(2);
    table.add(titleLabel).colspan(2);
    
    table.row().pad(2);
    table.add(label1).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label1Link).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label2).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label2Link).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label3).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(label3Link).expand().fill().colspan(2);

    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    TextButton back = new TextButton("BACK", GnuBackgammon.skin2);
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
    table.setColor(1,1,1,0);
    table.addAction(Actions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
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
