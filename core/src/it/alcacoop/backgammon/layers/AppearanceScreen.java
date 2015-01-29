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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;


public class AppearanceScreen extends BaseScreen {

  private Preferences prefs;
  
  private final FixedButtonGroup board;
  private final FixedButtonGroup checkers;
  private final FixedButtonGroup direction;
  private final FixedButtonGroup npoints;
  private Table table;
  private TextButton back, d1, d2, np1, np2;
  private ImageButton b1, b2, b3, cs1, cs2, cs3;
  
  
  public AppearanceScreen(){
    prefs = GnuBackgammon.Instance.appearancePrefs;
    
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          savePrefs();
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });
    
    ClickListener cls = new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        super.clicked(event, x, y);
      }
    };
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };
    ClickListener cl2 = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        savePrefs();
        GnuBackgammon.Instance.board.showArrow();
      };
    };

    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    
    ImageButtonStyle ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b1")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b1")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b1"))
    );
    b1 = new ImageButton(ibs);
    b1.setName("B1");
    ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b2")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b2")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b2"))
    );
    b2 = new ImageButton(ibs);
    b2.setName("B2");
    ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b3")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b3")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("b3"))
    );
    b3 = new ImageButton(ibs);
    b3.setName("B3");
    board = new FixedButtonGroup();
    board.add(b1);
    board.add(b2);
    board.add(b3);
    b1.addListener(cls);
    b2.addListener(cls);
    b3.addListener(cls);
    
    ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs1")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs1")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs1"))
    );
    cs1 = new ImageButton(ibs);
    cs1.setName("CS1");
    ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs2")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs2")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs2"))
    );
    cs2 = new ImageButton(ibs);
    cs2.setName("CS2");
    ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("check"),
        GnuBackgammon.skin.getDrawable("check-down"),
        GnuBackgammon.skin.getDrawable("check-selected"),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs3")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs3")),
        new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("cs3"))
    );
    cs3 = new ImageButton(ibs);
    cs3.setName("CS3");
    checkers = new FixedButtonGroup();
    checkers.add(cs1);
    checkers.add(cs2);
    checkers.add(cs3);
    cs1.addListener(cls);
    cs2.addListener(cls);
    cs3.addListener(cls);
    
    d1 = new TextButton("AntiClockwise", ts);
    d2 = new TextButton("Clockwise", ts);
    direction = new FixedButtonGroup();
    direction.add(d1);
    direction.add(d2);
    d1.addListener(cls);
    d2.addListener(cls);
    
    npoints = new FixedButtonGroup();
    np1 = new TextButton("Yes", ts);
    np2 = new TextButton("No", ts);
    npoints.add(np1);
    npoints.add(np2);
    np1.addListener(cl2);
    np2.addListener(cl2); 
    
    back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    table = new Table();
    stage.addActor(table);
  }

  
  public void initFromPrefs() {
    String sBoard = prefs.getString("BOARD", "B1");
    Array<Button> b = board.getButtons();
    for (int i=0; i<b.size;i++) {
      if (b.get(i).getName().equals(sBoard)) b.get(i).setChecked(true);
    }
    
    String sCheckers = prefs.getString("CHECKERS", "CS1");
    b = checkers.getButtons();
    for (int i=0; i<b.size;i++) {
      if (b.get(i).getName().equals(sCheckers)) b.get(i).setChecked(true);
    }
    
    String sDirection = prefs.getString("DIRECTION", "AntiClockwise");
    direction.setChecked(sDirection);
    
    String sPoints = prefs.getString("NPOINTS", "Yes");
    npoints.setChecked(sPoints);
  }

  
  public void savePrefs() {
    String sBoard = ((Button)board.getChecked()).getName();
    prefs.putString("BOARD", sBoard);
    String sCheckers = ((Button)checkers.getChecked()).getName();
    prefs.putString("CHECKERS", sCheckers);
    String sDirection = ((TextButton)direction.getChecked()).getText().toString();
    prefs.putString("DIRECTION", sDirection);
    String sPoints = ((TextButton)npoints.getChecked()).getText().toString(); 
    prefs.putString("NPOINTS", sPoints);
    prefs.flush();
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
  }
  
  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void initialize() {
    initTable();
    initFromPrefs();
    table.setColor(1,1,1,0);
    table.setX(-stage.getWidth());
  }
  
  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(animationTime),Actions.moveTo((stage.getWidth()-table.getWidth())/2, (stage.getHeight()-table.getHeight())/2, animationTime))));
  }

  
  @Override
  public void fadeOut() {
    table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(animationTime),Actions.moveTo(-stage.getWidth(), (stage.getHeight()-table.getHeight())/2, animationTime))));
  }

  
  public void initTable() {
    table.clear();
    
    table.setWidth(stage.getWidth()*0.9f);
    table.setHeight(stage.getHeight()*0.9f);
    table.setX((stage.getWidth()-table.getWidth())/2);
    table.setY((stage.getHeight()-table.getHeight())/2);
    
    table.add(new Label("APPEARANCE", GnuBackgammon.skin)).colspan(6);
    
    table.row();
    table.add().fill().expand().colspan(6);    
    
    table.row();
    table.add().fill().expand();
    table.add(new Label("Board:", GnuBackgammon.skin)).right().padRight(7);
    table.add(b1);
    table.add(b2);
    table.add(b3);
    table.add().fill().expand();
    
    table.row();
    table.add().fill().expand();
    table.add(new Label("Checkers:", GnuBackgammon.skin)).right().padRight(7);
    table.add(cs1).width(b1.getWidth());
    table.add(cs2).width(b1.getWidth());
    table.add(cs3).width(b1.getWidth());
    table.add().fill().expand();
    
    table.row();
    table.add().fill().expand();
    table.add(new Label("Direction:", GnuBackgammon.skin)).right().padRight(7);
    table.add(d1).width(b1.getWidth()).height(cs1.getHeight());
    table.add(d2).width(b1.getWidth()).height(cs1.getHeight());
    table.add().fill().expand().colspan(2);

    table.row();
    table.add().fill().expand();
    table.add(new Label("Numbered points:", GnuBackgammon.skin)).right().padRight(7);
    table.add(np1).width(b1.getWidth()).height(cs1.getHeight());
    table.add(np2).width(b1.getWidth()).height(cs1.getHeight());
    table.add().fill().expand().colspan(2);
    
    table.row();
    table.add().fill().expand().colspan(6);
    
    table.row();
    table.add(back).colspan(6).width(b1.getWidth()*1.7f).height(cs1.getHeight()).fill().expand();
  }
}
