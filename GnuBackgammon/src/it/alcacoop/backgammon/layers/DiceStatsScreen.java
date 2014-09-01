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
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class DiceStatsScreen extends BaseScreen {

  private Table table;
  private TextButton backBtn;
  private Image resetBtn;
  private SelectBox sb;
  private int statLevel;

  private String[] levels = {
      "BEGINNER",
      "CASUAL",
      "INTERMEDIATE",
      "ADVANCED",
      "EXPERT",
      "WORLDCLASS",
      "SUPREMO",
      "GRANDMASTER",
      "TOTALS"
  };

  private String dataStrings[][] = {
      { "Rolls", " - " },
      { "Doubles", "(16.7%)" },
      { "Average PIPS", "(8.17)" },
      { "Enter from the bar", "(~ 68%)" },

      { "1 In a row", "(11.6%)" },
      { "2 In a row", "(1.93%)" },
      { "3 In a row", "(0.32%)" },
      { "4 In a row", "(0.05%)" },

      { "1 Point board", "(97%)" },
      { "2 Point board", "(89%)" },
      { "3 Point board", "(75%)" },
      { "4 Point board", "(56%)" },
      { "5 Point board", "(30%)" }
  };


  public DiceStatsScreen() {
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (UIDialog.isOpened())
          return true;
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });


    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, ((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };

    sb = new SelectBox(levels, GnuBackgammon.skin);
    sb.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        statLevel = sb.getSelectionIndex();
        initTable();
      }
    });

    resetBtn = new Image(GnuBackgammon.atlas.findRegion("reset"));
    resetBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "RESET");
      }
    });

    backBtn = new TextButton("BACK", GnuBackgammon.skin);
    backBtn.addListener(cl);
    table = new Table();
    stage.addActor(table);
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void initialize() {
    statLevel = 8;
    sb.setSelection(statLevel);
    initTable();
    table.setColor(1, 1, 1, 0);
    table.setX(-stage.getWidth());
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    Gdx.graphics.setContinuousRendering(true);
    table.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(animationTime),
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

    float w1 = stage.getWidth() * 0.30f;
    float w2 = stage.getWidth() * 0.13f;


    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);

    table.add(new Label("DICE STATISTICS", GnuBackgammon.skin));

    table.row();
    table.add().fill().expand().height(stage.getHeight() / 40);


    Table controls = new Table();
    if (GnuBackgammon.Instance.ss != 2)
      controls.setBackground(GnuBackgammon.skin.getDrawable("list"));
    controls.row();
    controls.add(new Label("SELECT LEVEL:", GnuBackgammon.skin)).fill();
    controls.add(sb).fill().padLeft(stage.getWidth() / 85);
    if (GnuBackgammon.Instance.ss != 2) {
      Label note = new Label("For less than 2000 rolls data have\n large variation from expected values", GnuBackgammon.skin);
      note.setAlignment(Align.right, Align.right);
      controls.add(note).expand().fill();
    }


    table.row();
    table.add(controls).left().fill().expand();


    Table data_table = new Table();
    data_table.row().width(w1 + 3 * w2);

    Table t0 = new Table();
    t0.add().expand().width(w1);
    Label l = new Label("YOU", GnuBackgammon.skin);
    l.setAlignment(0, 1);
    t0.add(l).uniform().center().width(w2);
    t0.add(new Label("CPU", GnuBackgammon.skin)).uniform().center();
    t0.add(new Label("EXPT", GnuBackgammon.skin)).uniform().center();
    data_table.add(t0).fill();

    data_table.row();
    data_table.add(new Label("GENERAL", GnuBackgammon.skin, "even")).left().padBottom(stage.getHeight() / 50);
    Table t1 = new Table();
    t1.setBackground(GnuBackgammon.skin.getDrawable("even"));
    for (int i = 0; i < 4; i++) {
      t1.row();
      t1.add().expand().width(w1 * 0.15f);
      t1.add(new Label(dataStrings[i][0], GnuBackgammon.skin)).expand().width(w1 * 0.85f);
      Label l1 = new Label(mgr.getRollStat(i, statLevel, 0), GnuBackgammon.skin);
      l1.setAlignment(0, 1);
      t1.add(l1).uniform().center().width(w2);
      t1.add(new Label(mgr.getRollStat(i, statLevel, 1), GnuBackgammon.skin)).uniform().center();
      t1.add(new Label(dataStrings[i][1], GnuBackgammon.skin)).uniform().center();
    }
    data_table.row();
    data_table.add(t1).colspan(4).fill();


    data_table.row().padTop(stage.getHeight() / 20);
    data_table.add(new Label("DOUBLE IN A ROW", GnuBackgammon.skin, "even")).left().padBottom(stage.getHeight() / 50);
    Table t2 = new Table();
    t2.setBackground(GnuBackgammon.skin.getDrawable("even"));
    for (int i = 4; i < 8; i++) {
      t2.row();
      t2.add().expand().width(w1 * 0.15f);
      t2.add(new Label(dataStrings[i][0], GnuBackgammon.skin)).expand().width(w1 * 0.85f);
      Label l1 = new Label(mgr.getRollStat(i, statLevel, 0), GnuBackgammon.skin);
      l1.setAlignment(0, 1);
      t2.add(l1).uniform().center().width(w2);
      t2.add(new Label(mgr.getRollStat(i, statLevel, 1), GnuBackgammon.skin)).uniform().center();
      t2.add(new Label(dataStrings[i][1], GnuBackgammon.skin)).uniform().center();
    }
    data_table.row();
    data_table.add(t2).colspan(4).fill();


    data_table.row().padTop(stage.getHeight() / 20);
    data_table.add(new Label("ENTER AGAINST", GnuBackgammon.skin, "even")).left().padBottom(stage.getHeight() / 50);
    Table t3 = new Table();
    t3.setBackground(GnuBackgammon.skin.getDrawable("even"));
    for (int i = 8; i < dataStrings.length; i++) {
      t3.row();
      t3.add().expand().width(w1 * 0.15f);
      t3.add(new Label(dataStrings[i][0], GnuBackgammon.skin)).expand().width(w1 * 0.85f);
      Label l1 = new Label(mgr.getRollStat(i, statLevel, 0), GnuBackgammon.skin);
      l1.setAlignment(0, 1);
      t3.add(l1).uniform().center().width(w2);
      t3.add(new Label(mgr.getRollStat(i, statLevel, 1), GnuBackgammon.skin)).uniform().center();
      t3.add(new Label(dataStrings[i][1], GnuBackgammon.skin)).uniform().center();
    }
    data_table.row();
    data_table.add(t3).colspan(4).fill();


    ScrollPane sp = new ScrollPane(data_table, GnuBackgammon.skin);
    sp.setFadeScrollBars(false);

    Table wrapper = new Table();
    wrapper.setBackground(GnuBackgammon.skin.getDrawable("list"));
    wrapper.add().expand().fill();
    wrapper.add(sp).expand().fill();
    wrapper.add().expand().fill();
    wrapper.addActor(resetBtn);
    resetBtn.setPosition(table.getWidth() - 1.5f * resetBtn.getWidth(), resetBtn.getHeight() / 2);

    table.row();
    table.add(wrapper).expand().fill();

    table.row();
    table.add().fill().expand().height(stage.getHeight() / 40);

    table.row();
    table.add(backBtn).width(stage.getWidth() / 4).fill().expand().height(stage.getHeight() / 8);
  }
}
