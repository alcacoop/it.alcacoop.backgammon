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
import it.alcacoop.backgammon.ui.IconButton;
import it.alcacoop.backgammon.ui.UIDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class TwoPlayersScreen extends BaseScreen {

  private Label connecting;
  private Table table;
  private FixedButtonGroup type;

  private Label llocal;
  private Label lfibs;
  private Label ltiga;
  private Label lplay;

  private ScrollPane sp;

  private int variant = 0; // 0=LOCAL,1=FIBS,2=TIGA,3=GPLAY


  public TwoPlayersScreen() {
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        String s = ((TextButton)event.getListenerActor()).getText().toString().toUpperCase();
        if (!s.equals("BACK"))
          s += variant;
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, s);
      };
    };

    llocal = new Label("", GnuBackgammon.skin);
    llocal.setWrap(true);

    String sl = "LOCAL\n\n" +
        "Play against human player on the same device\n" +
        "As on single player mode, you can choose from 1 to 15 points match," +
        "with or without cube, an between two variant: " +
        "\n1. Backgammon" +
        "\n2. Nackgammon";
    llocal.setText(sl);

    ltiga = new Label("", GnuBackgammon.skin);
    ltiga.setWrap(true);
    String st = "TIGERGAMMON\n\n" +
        "TigerGammon is our favorite backgammon server. It has a high performance and many features.\n\n" +
        "You will find human players from all over the world playing on TigerGammon. And you also will " +
        "find bots so that you will always be able to find an opponent on your favorite level of skill.\n" +
        "For more information see http://tigergammon.com.\n\n" +
        "To play on TigerGammon you have to chose a username and password. Please do not forget your password. " +
        "There is currently no way to retrieve this information.\n" +
        "Validation of the account is NOT neccessary.";

    /**
     * OLD TEXT
     * "TigerGammon is just another backgammon server like FIBS (First internet Backgammon Server).\n" +
     * "TigerGammon wants to keep the institution FIBS alive. " +
     * "TigerGammon works just like FIBS. Over time you will see features " +
     * "that exceed, what you can see on FIBS\n\n" +
     * "ANDREAS HAUSMANN features TigerGammon. He is another Fibster discontent " +
     * "with the flaws of FIBS just like so many others.\n\n" +
     * "Like FIBS, TigerGammon needs a username/password account.\nNOTE: FIBS account is " +
     * "not compatible with TigerGammon account - You must create another one, with different ranking\n\n" +
     * "Please do not forget your password. There is currently no way " +
     * "for the TigerGammon administrator to retrieve this information. If you " +
     * "forget your password then you must start again under a new username.";
     **/
    ltiga.setText(st);


    lfibs = new Label("", GnuBackgammon.skin);
    lfibs.setWrap(true);
    String sf = "FIBS\n\n" +
        "FIBS is the First Internet Backgammon Server, " +
        "it allows Internet users to play backgammon in real-time against " +
        "real people (and even some bots). There are players of every " +
        "conceivable ability logging onto FIBS, from absolute beginners " +
        "to serious backgammon champion contenders. \n\n" +
        "NOTE: At the moment FIBS needs validation for new users, so " +
        "you can't create accounts within Backgammon Mobile; " +
        "more info available at 'http://fibs.com'\n\n" +
        "Alternatively you can get a try on our primary choice: TigerGammon!";
    lfibs.setText(sf);


    lplay = new Label("", GnuBackgammon.skin);
    lplay.setWrap(true);
    String sg = "GOOGLE PLAY GAMES\n\n" +
        "Play against your Google+ friends or random opponent\n\n" +
        " - Invite your Google+ friends on involving matches\n" +
        " - Keep track of your progress, trying to unlock all achievements\n" +
        " - Share your scores on global leaderboards, with daily, weekly, and all-time lists\n" +
        " - Save your settings and game progress on the cloud, and share it among all your devices\n" +
        " - Share the right things with the right people, using Google+ circles!";
    lplay.setText(sg);

    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });

    type = new FixedButtonGroup();

    Label titleLabel = new Label("TWO PLAYERS SETTINGS", GnuBackgammon.skin);

    float height = stage.getHeight() / 8.5f;
    float pad = 0;

    table = new Table();
    table.setWidth(stage.getWidth() * 0.9f);
    table.setHeight(stage.getHeight() * 0.9f);
    table.setX((stage.getWidth() - table.getWidth()) / 2);
    table.setY((stage.getHeight() - table.getHeight()) / 2);


    TextButton play = new TextButton("PLAY", GnuBackgammon.skin);
    play.addListener(cl);
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);

    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    IconButton local = new IconButton("Local", GnuBackgammon.atlas.findRegion("dp"), ts);
    IconButton fibs = new IconButton("FIBS", GnuBackgammon.atlas.findRegion("mpl"), ts);
    IconButton tiga = new IconButton("TigerGammon", GnuBackgammon.atlas.findRegion("mpl"), ts);
    IconButton gplay = new IconButton("Google Play Games", GnuBackgammon.atlas.findRegion("gpl"), ts);
    type.add(local);
    type.add(fibs);
    type.add(tiga);
    type.add(gplay);

    local.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        Table text = new Table();
        text.add(llocal).left().top().expandX().fillX();
        text.row();
        text.add().fill().expand();
        sp.setWidget(text);
        variant = 0;
        GnuBackgammon.Instance.server = "";
      }
    });

    fibs.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        Table text = new Table();
        text.add(lfibs).left().top().expand().fill();
        text.row();
        text.add().fill().expand();
        sp.setWidget(text);
        variant = 1;
        GnuBackgammon.Instance.server = "fibs.com";
      }
    });

    tiga.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        Table text = new Table();
        text.add(ltiga).left().top().expand().fill();
        text.row();
        text.add().fill().expand();
        sp.setWidget(text);
        variant = 2;
        GnuBackgammon.Instance.server = "ti-ga.com";
      }
    });

    gplay.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        Table text = new Table();
        text.add(lplay).left().top().expand().fill();
        text.row();
        text.add().fill().expand();
        sp.setWidget(text);
        variant = 3;
      }
    });

    table.add(titleLabel).colspan(7);

    table.row();
    table.add().fill().expandX().colspan(7).height(height / 2);

    Table t1 = new Table();

    t1.add().expandX().fill().height(height / 10);
    t1.row();
    t1.add(local).fillX().expandX().height(height).padRight(pad);
    t1.row();
    t1.add().expandX().fill().height(height / 10);

    t1.row();
    t1.add(gplay).fillX().expandX().height(height).padRight(pad);
    t1.row();
    t1.add().expandX().fill().height(height / 10);

    t1.row();
    t1.add(tiga).fillX().expandX().height(height).padRight(pad);
    t1.row();
    t1.add().expandX().fill().height(height / 10);

    t1.row();
    t1.add(fibs).fillX().expandX().height(height).padRight(pad);
    t1.row();
    t1.add().expand().fill();


    Table text = new Table();
    text.add(llocal).expandX().fillX();
    text.row();
    text.add().fillY().expandY();
    sp = new ScrollPane(text, GnuBackgammon.skin.get("info", ScrollPaneStyle.class));
    sp.setFadeScrollBars(false);
    sp.setForceScroll(false, false);
    sp.setOverscroll(false, false);

    table.row();
    table.add(t1).colspan(3).fill().expand();
    table.add(sp).colspan(4).fill().expand().padLeft(stage.getWidth() / 20);

    table.row();
    table.add().fill().expand().colspan(7).height(height / 2);

    table.row().height(height);
    table.add();
    table.add(back).fill().colspan(2);
    table.add();
    table.add(play).fill().colspan(2);
    table.add();

    stage.addActor(table);

    connecting = new Label("Connecting to server...", GnuBackgammon.skin);
    connecting.setVisible(false);
    connecting.setX((stage.getWidth() - connecting.getWidth()) / 2);
    connecting.setY(height * 1.5f);
    connecting.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.5f, 0.4f), Actions.alpha(1, 0.4f))));
    stage.addActor(connecting);
  }


  public void hideConnecting() {
    connecting.setVisible(false);
    Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }

  public void showConnecting(String msg) {
    connecting.setText(msg);
    connecting.setX((stage.getWidth() - connecting.getWidth()) / 2);
    connecting.setVisible(true);
    Gdx.graphics.setContinuousRendering(true);
    Gdx.graphics.requestRendering();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
    if (sp.getVelocityY() != 0)
      Gdx.graphics.requestRendering();
  }


  @Override
  public void initialize() {
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
}
