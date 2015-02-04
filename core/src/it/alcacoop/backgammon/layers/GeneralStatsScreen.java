/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Author: Domenico Martella                                     #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   29/08/2014                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2014   Alca Societa' Cooperativa                #
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
import it.alcacoop.backgammon.stats.StatManager;
import it.alcacoop.backgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class GeneralStatsScreen extends BaseScreen {

  private Table table;
  private TextButton backBtn, diceStatsBtn;
  private Image resetBtn;
  private String[] levels = {
      "Beginner",
      "Casual",
      "Intermediate",
      "Advanced",
      "Expert",
      "Worldclass",
      "Supremo",
      "Grandmaster"
  };


  public GeneralStatsScreen() {
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (UIDialog.isOpened())
          return true;
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
          GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
        }
        return super.keyDown(event, keycode);
      }
    });


    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, ((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
        GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
      };
    };

    resetBtn = new Image(GnuBackgammon.atlas.findRegion("reset"));
    resetBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "RESET");
        GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
      }
    });

    backBtn = new TextButton("BACK", GnuBackgammon.skin);
    backBtn.addListener(cl);
    diceStatsBtn = new TextButton("DICE STATS", GnuBackgammon.skin);
    diceStatsBtn.addListener(cl);
    table = new Table();
    stage.addActor(table);
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
    table.setColor(1, 1, 1, 0);
    table.setX(-stage.getWidth());
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(animationTime),
        Actions.moveTo((stage.getWidth() - table.getWidth()) / 2, (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }


  @Override
  public void fadeOut() {
    table.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(animationTime),
        Actions.moveTo(-stage.getWidth(), (stage.getHeight() - table.getHeight()) / 2, animationTime))));
  }


  public void initTable() {
    StatManager mgr = StatManager.getInstance();
    table.clear();

    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);

    table.add(new Label("WON GAMES STATISTICS", GnuBackgammon.skin)).colspan(6);

    table.row();
    table.add().fill().expand().colspan(6);

    Table data_table = new Table();

    data_table.add();
    data_table.add(new Label("YOU", GnuBackgammon.skin)).expand();
    data_table.add(new Label("CPU", GnuBackgammon.skin)).expand();

    for (int i = 0; i < levels.length; i++) {
      data_table.row();
      data_table.add(new Label(levels[i], GnuBackgammon.skin)).right();
      data_table.add(new Label("" + mgr.getGameStat(i, 0), GnuBackgammon.skin));
      data_table.add(new Label("" + mgr.getGameStat(i, 1), GnuBackgammon.skin));
    }

    data_table.row();
    data_table.add().expand().fill().colspan(3);

    data_table.row();
    data_table.add(new Label("TOTALS", GnuBackgammon.skin)).right();
    data_table.add(new Label("" + mgr.getGameStat(8, 0), GnuBackgammon.skin));
    data_table.add(new Label("" + mgr.getGameStat(8, 1), GnuBackgammon.skin));

    // DIRTY LAYOUT HACK
    Table wrapper = new Table();
    wrapper.setBackground(GnuBackgammon.skin.getDrawable("list"));
    wrapper.add().expand().fill();
    wrapper.add(data_table).expand().fill();
    wrapper.add().expand().fill();
    wrapper.addActor(resetBtn);
    resetBtn.setPosition(table.getWidth() - 1.8f * resetBtn.getWidth(), resetBtn.getHeight() / 2);

    table.row();
    table.add(wrapper).colspan(6).expand().fill();

    table.row();
    table.add().fill().expand().colspan(6);

    table.row();
    table.add(backBtn).colspan(3).width(stage.getWidth() / 4).fill().expand().height(stage.getHeight() / 8);
    table.add(diceStatsBtn).colspan(3).width(stage.getWidth() / 4).fill().expand().height(stage.getHeight() / 8);
  }
}
