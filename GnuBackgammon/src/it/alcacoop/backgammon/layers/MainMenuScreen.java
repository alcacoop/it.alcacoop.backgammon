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
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MainMenuScreen extends BaseScreen {

  private Group g;
  private IconButton onePlayer, twoPlayers, options, appearance, howtoplay, about, rate, getpro;
  private Image logo;
  
  public MainMenuScreen(){
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
          UIDialog.getQuitDialog(0.82f, stage);
        }
        return super.keyDown(event, keycode);
      }
    });
    
    String l = "logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    logo = new Image(r);
    
    
    TextButtonStyle tl = GnuBackgammon.skin.get("mainmenu", TextButtonStyle.class);
    
    onePlayer = new IconButton("Single Player", GnuBackgammon.atlas.findRegion("sp"), tl);
    onePlayer.addListener(cl);
    twoPlayers = new IconButton("Two Players", GnuBackgammon.atlas.findRegion("dp"), tl);
    twoPlayers.addListener(cl);
    options = new IconButton("Options", GnuBackgammon.atlas.findRegion("opt"), tl);
    options.addListener(cl);
    
    appearance = new IconButton("Appearance", GnuBackgammon.atlas.findRegion("app"), tl);
    appearance.addListener(cl);
    
    howtoplay = new IconButton("How To Play", GnuBackgammon.atlas.findRegion("how"), tl);
    howtoplay.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        UIDialog.getHelpDialog(0.82f, stage, false);
      }
    });
    about = new IconButton("About", GnuBackgammon.atlas.findRegion("abt"), tl);
    about.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        UIDialog.getAboutDialog(0.82f, stage, false);
      }
    });
    rate = new IconButton("Rate it!", GnuBackgammon.atlas.findRegion("str"), tl);
    rate.addListener(cl);

    getpro = new IconButton("Remove Ads", GnuBackgammon.atlas.findRegion("pro"), tl);
    getpro.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.inAppBilling();
        super.clicked(event, x, y);
      }
    });
    
    g = new Group();
    g.setColor(1,1,1,0);
  }
  
  
  public void redraw() {
    createMenu();
    g.addAction(MyActions.sequence(Actions.delay(0.2f),Actions.fadeIn(0.6f)));
  }
  
  
  private void createMenu() {
    g.setColor(1,1,1,0);
    Table table = new Table();
    table.setFillParent(true);
    
    table.add(logo).colspan(2);
    
    table.row().pad(1);
    table.add().colspan(2).fill().expand();
    table.row().pad(1);
    table.add().colspan(2).fill().expand();
    
    table.row().pad(1);
    table.add(onePlayer).expand().fill().colspan(2);
    table.row().pad(1);
    table.add(twoPlayers).expand().fill().colspan(2);
    
    table.row().pad(1);
    table.add().colspan(2).fill().expand();
    
    table.row().pad(1);
    table.add(options).expand().fill().width(table.getWidth()/2);
    table.add(appearance).expand().fill().width(table.getWidth()/2);
    
    table.row().pad(1);
    table.add().colspan(2).fill().expand();
    
    
    if (GnuBackgammon.Instance.nativeFunctions.isProVersion()) {
      table.row().pad(1);
      table.add(howtoplay).colspan(2).expand().fill();

      table.row().pad(1);
      table.add(rate).expand().fill().width(table.getWidth()/2);
      table.add(about).expand().fill().width(table.getWidth()/2);
      GnuBackgammon.Instance.nativeFunctions.removeAds();
    } else {
      table.row().pad(1);
      table.add(howtoplay).expand().fill().width(table.getWidth()/2);
      table.add(about).expand().fill().width(table.getWidth()/2);
      
      table.row().pad(1);
      table.add(rate).expand().fill().width(table.getWidth()/2);
      table.add(getpro).expand().fill().width(table.getWidth()/2);
    }
    
    g.clear();
    
    g.setWidth(stage.getWidth()*0.65f);
    g.setHeight(stage.getHeight()*0.9f);
    g.addActor(table);
    
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    
    stage.addActor(g);
  }
  

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  
  @Override
  public void show() {
    super.show();
    createMenu();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    g.addAction(MyActions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }
}
