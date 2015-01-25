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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class MainMenuScreen extends BaseScreen {

  private Group g;
  private IconButton onePlayer, twoPlayers, options, appearance, howtoplay, about, rate, getpro, stats;
  private ImageButton scoreboards, achievements, gplus, twitter, facebook;
  private Image logo;
  private Table buttonGroup;

  public MainMenuScreen() {
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, ((IconButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };

    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (UIDialog.isOpened())
            return false;
          UIDialog.getQuitDialog();
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
    stats = new IconButton("Stats", GnuBackgammon.atlas.findRegion("stats"), tl);
    stats.addListener(cl);
    options = new IconButton("Options", GnuBackgammon.atlas.findRegion("opt"), tl);
    options.addListener(cl);

    appearance = new IconButton("Look", GnuBackgammon.atlas.findRegion("app"), tl);
    appearance.addListener(cl);

    howtoplay = new IconButton("How To Play", GnuBackgammon.atlas.findRegion("how"), tl);
    howtoplay.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        UIDialog.getHelpDialog(false);
      }
    });
    about = new IconButton("About", GnuBackgammon.atlas.findRegion("abt"), tl);
    about.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        UIDialog.getAboutDialog(false);
      }
    });
    rate = new IconButton("Rate it!", GnuBackgammon.atlas.findRegion("str"), tl);
    rate.addListener(cl);

    getpro = new IconButton("Remove Ads", GnuBackgammon.atlas.findRegion("pro"), tl);
    getpro.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.inAppBilling();
        super.clicked(event, x, y);
      }
    });

    g = new Group();
    g.setColor(1, 1, 1, 0);

    scoreboards = new ImageButton(new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("leaderboards")));
    scoreboards.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.gserviceOpenLeaderboards();
      }
    });
    achievements = new ImageButton(new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("achievements")));
    achievements.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.gserviceOpenAchievements();
      }
    });

    gplus = new ImageButton(new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("gplus")));
    twitter = new ImageButton(new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("twitter")));
    facebook = new ImageButton(new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("facebook")));

    gplus.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL("https://plus.google.com/104812306723791936806/posts");
      }
    });
    twitter.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL("twitter://user?screen_name=alcamobile", "http://mobile.twitter.com/alcamobile");
      }
    });
    facebook.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL("fb://page/145621258977312", "https://m.facebook.com/BackgammonMobile");
      }
    });

    buttonGroup = new Table();
    buttonGroup.setWidth(gplus.getWidth());
    buttonGroup.setHeight(gplus.getHeight() * 6);
    buttonGroup.add(achievements).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(scoreboards).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(gplus.getHeight() / 2);
    buttonGroup.add(gplus).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(twitter).width(gplus.getWidth()).height(gplus.getHeight()).fill();
    buttonGroup.row().spaceTop(0);
    buttonGroup.add(facebook).width(facebook.getWidth()).height(facebook.getHeight()).fill();
    buttonGroup.setPosition(0, -stage.getHeight());
    stage.addActor(buttonGroup);
  }


  private void createMenu() {
    g.setColor(1, 1, 1, 0);
    Table table = new Table();
    table.setFillParent(true);

    table.add(logo).colspan(2);

    table.row().pad(1);
    table.add().colspan(2).fill().expand();


    table.row().pad(1);
    table.add(onePlayer).expand().fill().colspan(2);
    table.row().pad(1);
    table.add(twoPlayers).expand().fill().colspan(2);

    table.row().pad(1);
    table.add().colspan(2).fill().expand();

    table.row().pad(1);
    Table t = new Table();
    t.add(options).expand().fill().width(table.getWidth() / 3);
    t.add(appearance).expand().fill().width(table.getWidth() / 3);
    t.add(stats).expand().fill().width(table.getWidth() / 3);

    table.add(t).expand().fill().colspan(2);
    // table.add(options).expand().fill().width(table.getWidth() / 2);
    // table.add(appearance).expand().fill().width(table.getWidth() / 2);

    table.row().pad(1);
    table.add().colspan(2).fill().expand();


    if (GnuBackgammon.Instance.nativeFunctions.isProVersion()) {
      table.row().pad(1);
      table.add(howtoplay).colspan(2).expand().fill();

      table.row().pad(1);
      table.add(rate).expand().fill().width(table.getWidth() / 2);
      table.add(about).expand().fill().width(table.getWidth() / 2);
    } else {
      table.row().pad(1);
      table.add(howtoplay).expand().fill().width(table.getWidth() / 2);
      table.add(about).expand().fill().width(table.getWidth() / 2);

      table.row().pad(1);
      table.add(rate).expand().fill().width(table.getWidth() / 2);
      table.add(getpro).expand().fill().width(table.getWidth() / 2);
    }

    g.clear();

    float w = 0.7f;
    if (GnuBackgammon.Instance.ss != 0)
      w = 0.74f;

    g.setWidth(stage.getWidth() * w);
    g.setHeight(stage.getHeight() * 0.88f);
    g.addActor(table);

    g.setX(-g.getWidth());
    g.setY((stage.getHeight() - g.getHeight()) / 2);

    stage.addActor(g);
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
    createMenu();
  }


  public void redraw() {
    createMenu();
    buttonGroup.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(0.2f), Actions.moveTo(0, (stage.getHeight() - buttonGroup.getHeight()) / 2, 0.2f))));
    g.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(0.2f), Actions.moveTo((stage.getWidth() - g.getWidth()) / 2, (stage.getHeight() - g.getHeight()) / 2, 0.2f))));
  }


  @Override
  public void show() {
    super.show();
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    buttonGroup.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(0.2f), Actions.moveTo(0, (stage.getHeight() - buttonGroup.getHeight()) / 2, 0.2f))));
    g.addAction(MyActions.sequence(Actions.parallel(Actions.fadeIn(0.2f), Actions.moveTo((stage.getWidth() - g.getWidth()) / 2, (stage.getHeight() - g.getHeight()) / 2, 0.2f))));
  }

  @Override
  public void fadeOut() {
    buttonGroup.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(0.2f), Actions.moveTo(0, -stage.getHeight(), 0.2f))));
    g.addAction(MyActions.sequence(Actions.parallel(Actions.fadeOut(0.2f), Actions.moveTo(-stage.getWidth(), (stage.getHeight() - g.getHeight()) / 2, 0.2f))));
  }

}
