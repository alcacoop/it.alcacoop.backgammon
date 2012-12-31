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
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.actors.PlayerInfo;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GameFSM.States;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.GameMenuPopup;
import it.alcacoop.backgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class GameScreen implements Screen {

  private Stage stage;
  private Image bgImg;
  public Board board;
  private Table table;
  
  private PlayerInfo pInfo[];
  private GameMenuPopup menuPopup;
  private ImageButton menu;
  private TextureRegionDrawable wheel;

  
  public GameScreen(){
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.resolution[0], GnuBackgammon.resolution[1], false);
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    stage.addActor(bgImg);
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if ((MatchState.matchType==1) || (MatchState.fMove==0)) { //CPU IS PLAYING
            GnuBackgammon.fsm.state(States.DIALOG_HANDLER);
            UIDialog.getYesNoDialog(
              Events.ABANDON_MATCH, 
              "Really exit this match?",
              0.82f,
              GnuBackgammon.Instance.board.getStage());
          }
        }
        if(Gdx.input.isKeyPressed(Keys.MENU)||Gdx.input.isKeyPressed(Keys.M)) {
          menuPopup.toggle();
        }
        return super.keyDown(event, keycode);
      }
    });
    
    board = GnuBackgammon.Instance.board;
    
    pInfo = new PlayerInfo[2];
    pInfo[0] = new PlayerInfo("AI():", 1, stage.getWidth()/4.2f);
    pInfo[1] = new PlayerInfo("PL1:", 0, stage.getWidth()/4.2f);      
    
    table = new Table();
    stage.addActor(table);
    
    menuPopup = new GameMenuPopup(stage);
    wheel = new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("wheel")); 
    
    ImageButtonStyle ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("button"),
        GnuBackgammon.skin.getDrawable("button-down"),
        null,
        wheel,
        wheel,
        null
    );
    
    menu = new ImageButton(ibs);
    menu.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        menuPopup.toggle();
      }
    });
    
    stage.addActor(menuPopup);
  }

  
  private void initTable() {
    table.clear();
    table.setFillParent(true);
    
    float width = stage.getWidth()/4.2f;
    
    String l = GnuBackgammon.Instance.isGNU?"logo-gnu":"logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    Image i = new Image(r);
    i.setScale(0.8f);
    
    table.row().minHeight(44); //BANNER ON ldpi
    table.add(i).expandX().left().padLeft(6+6*(2-GnuBackgammon.ss));
    
    Table t = new Table();
    //t.debug();
    t.add(pInfo[0]).left();
    t.row();
    t.add(pInfo[1]).left();
    
    table.add(t).width(width).padTop(3+3*(2-GnuBackgammon.ss)).right().padRight(2+3*(2-GnuBackgammon.ss));
    
    table.add(menu).fillY().width(width/2.5f).padRight(6+6*(2-GnuBackgammon.ss)).padTop(3+3*(2-GnuBackgammon.ss));
    
    table.row();
    table.add(board).colspan(4).expand().fill();
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
    Table.drawDebug(stage);
  }

  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    loadTextures();
    initTable();
    board.initBoard();
       
    if(MatchState.matchType == 0){ //single player
      pInfo[0].setName("AI("+(MatchState.currentLevel.ordinal()+1)+"):");
      pInfo[1].setName("PL1:");
    } else {
      pInfo[0].setName("PL1:");
      pInfo[1].setName("PL2:");
    }
    
    pInfo[0].update();
    pInfo[1].update();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    
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
    UIDialog.setButtonsStyle("B1");
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

  private void loadTextures() {
    String sBoard = GnuBackgammon.Instance.appearancePrefs.getString("BOARD", "B1");
    GnuBackgammon.atlas.findRegion("board").setRegion(GnuBackgammon.atlas.findRegion(sBoard));
    GnuBackgammon.atlas.findRegion("boardbg").setRegion(GnuBackgammon.atlas.findRegion(sBoard+"-BG"));
    board.rollBtn.setStyle(GnuBackgammon.skin.get("button-"+sBoard, TextButtonStyle.class));
    board.doubleBtn.setStyle(GnuBackgammon.skin.get("button-"+sBoard, TextButtonStyle.class));
    menuPopup.setButtonsStyle(sBoard);
    UIDialog.setButtonsStyle(sBoard);
    String sCs = GnuBackgammon.Instance.appearancePrefs.getString("CHECKERS", "CS1");
    GnuBackgammon.atlas.findRegion("cb").setRegion(GnuBackgammon.atlas.findRegion(sCs+"-B"));
    GnuBackgammon.atlas.findRegion("cw").setRegion(GnuBackgammon.atlas.findRegion(sCs+"-W"));
    GnuBackgammon.atlas.findRegion("ch").setRegion(GnuBackgammon.atlas.findRegion(sCs+"-H"));
    ImageButtonStyle ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("button"+sBoard.charAt(1)), GnuBackgammon.skin.getDrawable("button"+sBoard.charAt(1)+"-down"), null,
        wheel, wheel, null
    );
    menu.setStyle(ibs);
  }

}
