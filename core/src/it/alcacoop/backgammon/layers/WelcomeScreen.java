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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;


public class WelcomeScreen extends BaseScreen {

  private Table table;
  
  public WelcomeScreen(){
    super();

    String l = "logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    Image i = new Image(r);
    
    Label tap = new Label("Tap to continue...", GnuBackgammon.skin);
    tap.addAction(Actions.forever(MyActions.sequence(Actions.fadeOut(0.3f), Actions.fadeIn(0.6f))));
    
    GnuBackgammon.Instance.setFSM("SIMULATED_FSM");
    
    table = new Table();
    
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.85f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    
    table.add(i).colspan(5);
    
    table.row();
    table.add().expand().fill();
    GnuBackgammon.Instance.board.setWidth(stage.getWidth() * 0.65f);
    GnuBackgammon.Instance.board.setHeight(stage.getHeight() * 0.6f);
    table.add(GnuBackgammon.Instance.board).expand().width(stage.getWidth() * 0.65f).height(stage.getHeight() * 0.6f).colspan(3);
    table.add().expand().fill();

    table.row();
    table.add().colspan(5).fill().expand();
    
    table.row();
    table.add().fill().expand().colspan(2);
    table.add(tap).center();
    table.add().fill().expand().colspan(2);
    
    stage.addActor(table);
    stage.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.setFSM("MENU_FSM");
      }
    });
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void hide() {
    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }

  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }
}
