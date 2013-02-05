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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class WelcomeScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  private Table table;

  
  public WelcomeScreen(){
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    stage.addActor(bgImg);
    
    //Label titleLabel = new Label("GNU BACKGAMMON MOBILE", GnuBackgammon.skin);
    String l = GnuBackgammon.Instance.isGNU?"logo-gnu":"logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    Image i = new Image(r);
    
    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    TextButton play = new TextButton("PLAY!", tl);
    play.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.setFSM("MENU_FSM");
      }
    });
    
    GnuBackgammon.Instance.setFSM("SIMULATED_FSM");
    
    table = new Table();
    
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.85f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    
    table.add(i).colspan(5);
    
    table.row();
    table.add().expand().fill();
    table.add(GnuBackgammon.Instance.board).expand().width(stage.getWidth()*0.65f).height(stage.getHeight()*0.6f).colspan(3);
    table.add().expand().fill();
    
    table.row();
    table.add().colspan(5).fill().expand();
    
    table.row();
    table.add().fill().expand().colspan(2);
    table.add(play).fill().expand().width(stage.getWidth()*0.35f);
    table.add().fill().expand().colspan(2);
    
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
