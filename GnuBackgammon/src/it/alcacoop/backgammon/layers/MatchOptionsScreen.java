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
import it.alcacoop.backgammon.actors.FixedButtonGroup;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.logic.AILevels;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class MatchOptionsScreen implements Screen {

  private Image bgImg;
  public Stage stage;
  private Preferences prefs;
  
  private final FixedButtonGroup level;
  private final FixedButtonGroup matchTo;
  private final FixedButtonGroup doubleCube;
  private final FixedButtonGroup crawford;
  private final FixedButtonGroup gametype;
  
  private String _levels[] = {"Beginner","Casual","Intermediate","Advanced","Expert","Worldclass","Supremo","Grandmaster"};
  private TextButton levelButtons[];
  
  private String _matchTo[] = {"1","3","5","7","9","11","13","15"};
  private TextButton matchToButtons[];
  
  private String _yesNo[] = {"Yes","No"};
  private TextButton doublingButtons[];
  private TextButton crawfordButtons[];
  
  private String _gametype[] = {"Backgammon","Nackgammon"};
  private TextButton gameTypeButtons[];
  
  private Label difficultyLabel;
  private Table table;
  private Label doublingLabel;
  private Label crawfordLabel;
  private Label gameTypeLabel;
  private TextButton back;
  private TextButton play;
  private Label playToLabel;
  private Label titleLabel; 
  
  //private Group g;
  
  public MatchOptionsScreen(){
    
    TextureRegion bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    prefs = Gdx.app.getPreferences("MatchOptions");
    //STAGE DIM = SCREEN RES
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
    stage.setViewport(GnuBackgammon.Instance.resolution[0], GnuBackgammon.Instance.resolution[1], false);
    
    stage.addActor(bgImg);
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened()) return false;
          savePrefs();
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });
    
    titleLabel = new Label("MATCH SETTINGS", GnuBackgammon.skin);
    difficultyLabel = new Label("Difficulty:", GnuBackgammon.skin);
    playToLabel = new Label("Match to:", GnuBackgammon.skin);
    doublingLabel = new Label("Dbl. Cube:", GnuBackgammon.skin);
    crawfordLabel = new Label("Crawford R.:", GnuBackgammon.skin);
    gameTypeLabel = new Label("Variant:", GnuBackgammon.skin);
    
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    levelButtons = new TextButton[_levels.length];
    level = new FixedButtonGroup();
    for (int i=0; i<_levels.length; i++) {
      levelButtons[i] = new TextButton(_levels[i], ts);
      level.add(levelButtons[i]);
    }
    
    matchToButtons = new TextButton[_matchTo.length];
    matchTo = new FixedButtonGroup();
    for (int i=0; i<_matchTo.length; i++) {
      matchToButtons[i] = new TextButton(_matchTo[i], ts);
      matchTo.add(matchToButtons[i]);
    }
    
    doublingButtons = new TextButton[_yesNo.length];
    doubleCube = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      doublingButtons[i] = new TextButton(_yesNo[i], ts);
      doubleCube.add(doublingButtons[i]);
    }
    
    crawfordButtons = new TextButton[_yesNo.length];
    crawford = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      crawfordButtons[i] = new TextButton(_yesNo[i], ts);
      crawford.add(crawfordButtons[i]);
    }
    
    gameTypeButtons = new TextButton[_gametype.length];
    gametype = new FixedButtonGroup();
    for (int i=0; i<_yesNo.length; i++) {
      gameTypeButtons[i] = new TextButton(_gametype[i], ts);
      gametype.add(gameTypeButtons[i]);
    }
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    play = new TextButton("PLAY", GnuBackgammon.skin);
    play.addListener(cl);
    back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    initFromPrefs();
    table = new Table();
    //table.setFillParent(true);
    stage.addActor(table);
  }

  
  public void initFromPrefs() {
    String sLevel = prefs.getString("LEVEL", "Beginner");
    level.setChecked(sLevel);
    String sMatchTo= prefs.getString("MATCHTO", "1");
    matchTo.setChecked(sMatchTo);
    String sDoubleCube= prefs.getString("DOUBLE_CUBE", "yes");
    doubleCube.setChecked(sDoubleCube);
    String sCrawford= prefs.getString("CRAWFORD", "yes");
    crawford.setChecked(sCrawford);
    String sVariant= prefs.getString("VARIANT", "Backgammon");
    gametype.setChecked(sVariant);
  }

  
  public void savePrefs() {
    String sLevel = ((TextButton)level.getChecked()).getText().toString(); 
    prefs.putString("LEVEL", sLevel);
    String sMatchTo = ((TextButton)matchTo.getChecked()).getText().toString();
    prefs.putString("MATCHTO", sMatchTo);
    String sDoubleCube = ((TextButton)doubleCube.getChecked()).getText().toString();
    prefs.putString("DOUBLE_CUBE", sDoubleCube);
    String sCrawford = ((TextButton)crawford.getChecked()).getText().toString();
    prefs.putString("CRAWFORD", sCrawford);
    String sGameType = ((TextButton)gametype.getChecked()).getText().toString();
    prefs.putString("VARIANT", sGameType);
    
    prefs.flush();
    
    MatchState.SetAILevel(AILevels.getAILevelFromString(sLevel));
    int fCubeUse = sDoubleCube.equals("Yes")?1:0; //USING CUBE
    MatchState.SetCubeUse(fCubeUse);
    MatchState.SetMatchTo(sMatchTo);
    int fCrawford = sCrawford.equals("Yes")?1:0; //REGOLA DI CRAWFORD
    MatchState.SetCrawford(fCrawford);
    int bgv = sGameType.equals("Backgammon")?0:1; //GAME TYPE
    MatchState.SetGameVariant(bgv);
    MatchState.SetMatchScore(0, 0);
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
    initTable();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.setColor(1,1,1,0);
    table.addAction(MyActions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f)));
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }

  @Override
  public void dispose() {
  }
  
  public void initTable() {
    //float height = stage.getWidth()/15;
    table.clear();
    
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    
    float width = table.getWidth()/9f;

    table.add(titleLabel).colspan(9);
    
    if (MatchState.matchType==0) {
      table.row();
      table.add().expand().fill();
      table.row();
      table.add().expand().fill();
      table.row();
      table.add(difficultyLabel).right().spaceRight(6);
      table.add(levelButtons[0]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[1]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[2]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[3]).expand().fill().colspan(2).width(2*width);
      table.row();
      table.add();
      table.add(levelButtons[4]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[5]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[6]).expand().fill().colspan(2).width(2*width);
      table.add(levelButtons[7]).expand().fill().colspan(2).width(2*width);
    }
    
    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add(playToLabel).right().spaceRight(6);
    table.add(matchToButtons[0]).expand().fill().width(width);
    table.add(matchToButtons[1]).expand().fill().width(width);
    table.add(matchToButtons[2]).expand().fill().width(width);
    table.add(matchToButtons[3]).expand().fill().width(width);
    table.add(matchToButtons[4]).expand().fill().width(width);
    table.add(matchToButtons[5]).expand().fill().width(width);
    table.add(matchToButtons[6]).expand().fill().width(width);
    table.add(matchToButtons[7]).expand().fill().width(width);
    
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add(doublingLabel).right().spaceRight(6);
    table.add(doublingButtons[0]).expand().fill().width(width);
    table.add(doublingButtons[1]).expand().fill().width(width);
    table.add();
    table.add(crawfordLabel).right().colspan(2).spaceRight(6);
    table.add(crawfordButtons[0]).expand().fill().width(width);
    table.add(crawfordButtons[1]).expand().fill().width(width);
    table.add();
    
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add(gameTypeLabel).right().spaceRight(6);
    table.add(gameTypeButtons[0]).expand().fill().colspan(2).width(2*width);
    table.add(gameTypeButtons[1]).expand().fill().colspan(2).width(2*width);
    table.add().colspan(6);
    table.add();
    
    table.row();
    table.add().expand().fill();
    table.row();
    table.add().expand().fill();
    
    table.row();
    table.add().colspan(2);
    table.add(back).fill().expand().colspan(2);
    table.add();
    table.add(play).fill().expand().colspan(2);
    table.add().colspan(2);
    
    table.row();
    table.add().expand().fill();
  }
}
