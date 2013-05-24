/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon.layers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.ui.IconButton;
import it.alcacoop.backgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen implements Screen {

  private Stage stage;
  private Group g;
  private Image bgImg;
  private Label connecting;
  
  public MainMenuScreen(){
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.Instance.resolution[0], GnuBackgammon.Instance.resolution[1], false);
    
    stage.addActor(bgImg);
    
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((IconButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened()) return false;
          UIDialog.getQuitDialog(stage);
        }
        return super.keyDown(event, keycode);
      }
    });
    
    String l = GnuBackgammon.Instance.isGNU?"logo-gnu":"logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    Image i = new Image(r);
    
    
    TextButtonStyle tl = GnuBackgammon.skin.get("mainmenu", TextButtonStyle.class);
    
    IconButton onePlayer = new IconButton("Single Player", GnuBackgammon.atlas.findRegion("sp"), tl);
    onePlayer.addListener(cl);
    IconButton twoPlayers = new IconButton("Two Players", GnuBackgammon.atlas.findRegion("dp"), tl);
    twoPlayers.addListener(cl);
    IconButton fibs  = new IconButton("FIBS", GnuBackgammon.atlas.findRegion("mpl"), tl);
    fibs.addListener(cl);
    IconButton options = new IconButton("Options", GnuBackgammon.atlas.findRegion("opt"), tl);
    options.addListener(cl);
    
    IconButton appearance = new IconButton("Appearance", GnuBackgammon.atlas.findRegion("app"), tl);
    appearance.addListener(cl);
    
    IconButton howtoplay = new IconButton("How To Play", GnuBackgammon.atlas.findRegion("how"), tl);
    howtoplay.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        UIDialog.getHelpDialog(stage, false);
      }
    });
    IconButton about = new IconButton("About", GnuBackgammon.atlas.findRegion("abt"), tl);
    about.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        UIDialog.getAboutDialog(stage, false);
      }
    });
    IconButton rate = new IconButton("Rate it!", GnuBackgammon.atlas.findRegion("str"), tl);
    rate.addListener(cl);

    Table table = new Table();
    table.setFillParent(true);
    
    table.add(i).colspan(2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    
    table.row().pad(2);
    table.add(onePlayer).expand().fill().colspan(2);
    table.row().pad(2);
    table.add(twoPlayers).expand().fill().width(table.getWidth()/2);;
    table.add(fibs).expand().fill().width(table.getWidth()/2);;
    
    table.row().pad(2);
    table.add(options).expand().fill().width(table.getWidth()/2);
    table.add(appearance).expand().fill().width(table.getWidth()/2);
    
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add(howtoplay).colspan(2).expand().fill();

    table.row().pad(2);
    table.add(rate).expand().fill().width(table.getWidth()/2);
    table.add(about).expand().fill().width(table.getWidth()/2);

    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    table.row().pad(2);
    table.add().colspan(2).fill().expand();
    
    g = new Group();
    g.setWidth(stage.getWidth()*0.6f);
    g.setHeight(stage.getHeight()*0.95f);
    g.addActor(table);
    
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    
    stage.addActor(g);
    connecting = new Label("Connecting to server...", GnuBackgammon.skin);
    connecting.setVisible(false);
    connecting.setX((stage.getWidth()-connecting.getWidth())/2);
    connecting.setY(connecting.getHeight());
    connecting.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.5f, 0.4f), Actions.alpha(1,  0.4f))));
    stage.addActor(connecting);
  }
  
  public Stage getStage() {
    return stage;
  }


  public void setConnecting(boolean b) {
    connecting.setVisible(b);
    if (b) Gdx.graphics.setContinuousRendering(true);
    else Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }
  
  @Override
  public void render(float delta) {
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
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    g.setColor(1,1,1,0);
    g.addAction(MyActions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
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
