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

package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public final class UIDialog extends Table {

  private Table t1, t2, t3;
  private TextButton bContinue;
  private TextButton bYes;
  private TextButton bNo;
  private TextButton bCancel;
  private TextButton bExport;
  private TextButton bAccept;
  private TextButton bReject;

  private Label label;
  private Label diceLabel;
  private Drawable background;
  private static ClickListener cl;

  private Map<String, DiceButton> diceButtonMap;
  private GetRandomDiceButton randomDicesButton;

  private static UIDialog instance;

  private BaseFSM.Events evt;
  private boolean quitWindow = false;
  private boolean optionsWindow = false;
  private boolean leaveWindow = false;
  private boolean dicesWindow = false;
  private boolean visible = false;
  private GameOptionsTable opts;
  private static float alpha = 0.9f;

  static {
    instance = new UIDialog();
    instance.setSkin(GnuBackgammon.skin);
  }

  private UIDialog() {
    cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        final String s;
        GnuBackgammon.Instance.snd.playMoveStart();
        if (event.getTarget() instanceof Label) {
          s = ((Label)event.getTarget()).getText().toString().toUpperCase();
        } else if (event.getTarget() instanceof DiceButton) {
          s = ((DiceButton)event.getTarget()).getName();
        } else if (event.getTarget() instanceof GetRandomDiceButton) {
          int ds[] = AICalls.Locking.RollDice();
          if (ds[0] > ds[1])
            s = ds[0] + "x" + ds[1];
          else
            s = ds[1] + "x" + ds[0];
        } else {
          s = ((TextButton)event.getTarget()).getText().toString().toUpperCase();
        }

        instance.addAction(MyActions.sequence(
            Actions.run(new Runnable() {
              @Override
              public void run() {
                // WORKAROUND: ON FIBS_MULTIPLAYER SOME DIALOG LOCK EXECUTION..
              }
            }),
            Actions.fadeOut(0.3f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                instance.remove();
                if (leaveWindow) {
                  GnuBackgammon.fsm.processEvent(instance.evt, s);
                  return;
                }

                boolean ret = s.equals("YES") || s.equals("OK");

                if ((instance.quitWindow) && (ret)) {
                  Gdx.app.exit();
                } else {
                  GnuBackgammon.fsm.processEvent(instance.evt, ret);
                  if (instance.optionsWindow)
                    opts.savePrefs();
                }

                if ((instance.dicesWindow) && (!ret)) {
                  // MANUAL DICES CLOSE
                  GnuBackgammon.Instance.nativeFunctions.showAds(true);
                  String[] ret2 = s.split("x");
                  int[] intArray = new int[ret2.length];
                  for (int i = 0; i < ret2.length; i++) {
                    intArray[i] = Integer.parseInt(ret2[i]);
                  }
                  GnuBackgammon.fsm.processEvent(GameFSM.Events.DICES_ROLLED, intArray);
                }

                Gdx.graphics.setContinuousRendering(false);
                Gdx.graphics.requestRendering();
                visible = false;
              }
            })
            ));
      };
    };

    label = new Label("", GnuBackgammon.skin);
    diceLabel = new Label("", GnuBackgammon.skin);

    diceButtonMap = new HashMap<String, DiceButton>();
    for (int i = 1; i < 7; i++) {
      for (int j = 1; j < 7; j++) {
        if (j <= i) {
          DiceButton b = new DiceButton(i, j);
          b.addListener(cl);
          diceButtonMap.put(i + "x" + j, b);
        }
      }
    }

    randomDicesButton = new GetRandomDiceButton();
    randomDicesButton.addListener(cl);

    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);

    bYes = new TextButton("Yes", tl);
    bYes.addListener(cl);
    bNo = new TextButton("No", tl);
    bNo.addListener(cl);
    bContinue = new TextButton("Ok", tl);
    bContinue.addListener(cl);
    bCancel = new TextButton("Cancel", tl);
    bCancel.addListener(cl);
    bExport = new TextButton("Export Match", tl);
    bExport.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.shareMatch(GnuBackgammon.Instance.rec);
        super.clicked(event, x, y);
      }
    });
    bAccept = new TextButton("Accept", tl);
    bAccept.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        instance.addAction(MyActions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                String u = GnuBackgammon.Instance.fibsScreen.lastInvite;
                GnuBackgammon.Instance.fibsScreen.fibsInvitations.remove(u);
                GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
                GnuBackgammon.Instance.commandDispatcher.send("join " + GnuBackgammon.Instance.fibsScreen.lastInvite);
                instance.remove();
                Gdx.graphics.setContinuousRendering(false);
                Gdx.graphics.requestRendering();
              }
            })));
      }
    });
    bReject = new TextButton("Reject", tl);
    bReject.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        instance.addAction(MyActions.sequence(
            Actions.fadeOut(0.3f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                instance.remove();
                String u = GnuBackgammon.Instance.fibsScreen.lastInvite;
                GnuBackgammon.Instance.commandDispatcher.send("tell " + u + " Sorry, not now. Thanks for the invitation.");
                GnuBackgammon.Instance.fibsScreen.fibsInvitations.remove(u);
                GnuBackgammon.Instance.fibsScreen.refreshInvitationList();
                Gdx.graphics.setContinuousRendering(false);
                Gdx.graphics.requestRendering();
              }
            })));
      }
    });

    background = GnuBackgammon.skin.getDrawable("default-window");
    setBackground(background);

    opts = new GameOptionsTable(false, cl);

    t1 = new Table();
    t1.setFillParent(true);
    t1.add(label).fill().expand().center();

    t2 = new Table();
    t2.setFillParent(true);
    t2.add().colspan(2).expand();
    t2.add(bContinue).fill().expand();
    t2.add().colspan(2).expand();

    t3 = new Table();
    t3.setFillParent(true);
    t3.add().expand();
    t3.add(bNo).fill().expand();
    t3.add().expand();
    t3.add(bYes).fill().expand();
    t3.add().expand();

    setColor(1, 1, 1, 0);

  }
  private void setText(String t) {
    label.setText(t);
  }


  public static void getYesNoDialog(BaseFSM.Events evt, String text) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.78f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getContinueDialog(BaseFSM.Events evt, String text) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(3).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getEndGameDialog(BaseFSM.Events evt, String text, String text1, String score1, String score2) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();

    float height = stage.getHeight() * 0.6f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add().expand();
    instance.add(text1).colspan(2).expand().align(Align.center);
    instance.add().expand();

    instance.row();
    instance.add().expand();
    instance.add("Overall Score " + text).colspan(2).expand().align(Align.center);
    instance.add().expand();
    instance.row();
    instance.add().expand();
    instance.add(score1).expand().align(Align.center);
    instance.add(score2).expand().align(Align.center);
    instance.add().expand();

    Table t1 = new Table();
    t1.row().expand().fill();
    t1.add();
    t1.add(instance.bContinue).colspan(2).fill().expand().height(height * 0.15f).width(width / 3);
    if ((MatchState.anScore[0] >= MatchState.nMatchTo || MatchState.anScore[1] >= MatchState.nMatchTo) && (MatchState.matchType == 0)) {
      t1.add();
      t1.add(instance.bExport).colspan(2).fill().expand().height(height * 0.15f).width(width / 3);
    }
    t1.add();
    instance.row();
    instance.add(t1).colspan(4).fill().padBottom(width / 25);

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getFlashDialog(BaseFSM.Events evt, String text) {
    getFlashDialog(evt, text, 1.5f);
  }

  public static void getFlashDialog(BaseFSM.Events evt, String text, float waitTime) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();
    instance.setText(text);

    float height = stage.getHeight() * 0.3f;
    float width = stage.getWidth() * 0.75f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.add(instance.label).expand().align(Align.center);

    stage.addActor(instance);
    instance.addAction(MyActions.sequence(
        Actions.alpha(alpha, 0.3f),
        Actions.delay(waitTime),
        Actions.fadeOut(0.3f),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            instance.remove();
            GnuBackgammon.fsm.processEvent(instance.evt, true);
          }
        })
        ));
  }


  public static void getQuitDialog() {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = true;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.dicesWindow = false;
    instance.remove();
    instance.setText("Really quit the game?");

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.5f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getLeaveDialog(BaseFSM.Events evt) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = true;
    instance.dicesWindow = false;
    instance.evt = evt;
    instance.remove();

    instance.setText("You are leaving current match.");

    float height = stage.getHeight() * 0.45f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(7).expand().align(Align.center);
    instance.row().padTop(width / 45);
    instance.add(new Label("Do you want to save it?", GnuBackgammon.skin)).colspan(7).expand().align(Align.center);

    instance.row().padTop(width / 25);
    instance.add();
    instance.add(instance.bYes).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();
    instance.add(instance.bNo).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();
    instance.add(instance.bCancel).fill().expand().height(height * 0.25f).width(width / 4.5f);
    instance.add();

    instance.row().padBottom(width / 35);
    instance.add();


    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static void getHelpDialog(Boolean cb) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = false;
    instance.remove();
    Label l = new Label(
        "GAME TYPE\n" +
            "You can choose two game type, and several options:\n" +
            "Backgammon - usual starting position\n" +
            "Nackgammon - Nack's starting position, attenuates lucky starting roll\n" +
            "Doubling Cube: use or not the doubling cube, with or without Crawford rule\n\n" +
            "START TURN\n" +
            "If cube isn't available, dices are rolled automatically,\n" +
            "else you must click on 'Double' or 'Roll' button\n\n" +
            "MOVING MECHANIC\n" +
            "You can choose two moves mechanic (Options->Move Logic):\n" +
            "TAP - once you rolled dices, select the piece you would move.\n" +
            "If legal moves for that piece are available, they will be shown.\n" +
            "Click an available point and the piece will move there.\n" +
            "AUTO - click on a piece and it moves automatically to destination point.\n" +
            "Bigger dice is played first. You can change dice order clicking on dices\n\n" +
            "You can cancel your moves in current hand just clicking the UNDO button\n" +
            "in the game options menu popup.\n\n" +
            "END TURN\n" +
            "When you finish your turn, click again the dices to take back them and change turn.\n"
        , GnuBackgammon.skin);
    l.setWrap(true);

    ScrollPane sc = new ScrollPane(l, GnuBackgammon.skin);
    sc.setFadeScrollBars(false);
    sc.setOverscroll(false, false);


    float height = stage.getHeight() * 0.85f;
    float width = stage.getWidth() * 0.9f;

    instance.clear();
    instance.row().padTop(width / 25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width / 25).padLeft(width / 35).padRight(width / 35);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height * 0.15f).width(width / 4);
    instance.add();

    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(
        MyActions.sequence(
            Actions.parallel(
                Actions.color(new Color(1, 1, 1, alpha), 0.2f),
                Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            Gdx.graphics.setContinuousRendering(true);
          }
        })
        ));
  }


  public static void getAboutDialog(Boolean cb) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = false;
    instance.remove();

    final String gnuBgLink = "http://www.gnubg.org";
    final String gplLink = "http://www.gnu.org/licenses/gpl.html";
    final String githubLink1 = "https://github.com/alcacoop/it.alcacoop.backgammon";
    final String githubLink2 = "https://github.com/alcacoop/libgnubg-android";
    final String wikipediaLink = "http://en.wikipedia.org/wiki/Backgammon#Rules";

    Table t = new Table();
    t.add(new Label("ABOUT BACKGAMMON MOBILE", GnuBackgammon.skin)).expand();
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Backgammon Mobile is based on GNUBackgammon (gnubg)", GnuBackgammon.skin));
    Label link1 = new Label(gnuBgLink, GnuBackgammon.skin);
    link1.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL(gnuBgLink);
      };
    });
    t.row();
    t.add(link1);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Its source code is released under a GPLv3 License", GnuBackgammon.skin));
    Label link2 = new Label(gplLink, GnuBackgammon.skin);
    link2.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL(gplLink);
      };
    });
    t.row();
    t.add(link2);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("and is available on GitHub at:", GnuBackgammon.skin));
    Label link3 = new Label(githubLink1, GnuBackgammon.skin);
    link3.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL(githubLink1);
      };
    });
    Label link4 = new Label(githubLink2, GnuBackgammon.skin);
    link4.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL(githubLink2);
      };
    });
    t.row();
    t.add(link3);
    t.row();
    t.add(link4);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("You can find a detailed description of game rules on Wikipedia:", GnuBackgammon.skin));
    Label link5 = new Label(wikipediaLink, GnuBackgammon.skin);
    link5.addListener(new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.Instance.nativeFunctions.openURL(wikipediaLink);
      };
    });
    t.row();
    t.add(link5);
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("If you enjoy our game support us rating on the Play Store", GnuBackgammon.skin));
    t.row();
    t.add(new Label(" ", GnuBackgammon.skin)).fill().expand();
    t.row();
    t.add(new Label("Copyright 2012 - Alca Soc. Coop.", GnuBackgammon.skin));


    ScrollPane sc = new ScrollPane(t, GnuBackgammon.skin);
    sc.setFadeScrollBars(false);
    sc.setOverscroll(false, false);

    float height = stage.getHeight() * 0.85f;
    float width = stage.getWidth() * 0.95f;

    instance.clear();
    instance.row().padTop(width / 25);
    instance.add(sc).colspan(3).expand().fill().align(Align.center).padTop(width / 25).padLeft(width / 35).padRight(width / 35);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bContinue).fill().expand().height(height * 0.15f).width(width / 4);
    instance.add();

    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(Actions.sequence(
        MyActions.sequence(
            Actions.parallel(
                Actions.color(new Color(1, 1, 1, alpha), 0.2f),
                Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))),
        Actions.run(new Runnable() {
          @Override
          public void run() {
            Gdx.graphics.setContinuousRendering(true);
          }
        })
        ));
  }


  public static void getOptionsDialog() {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = true;
    instance.dicesWindow = false;
    instance.remove();

    instance.opts.initFromPrefs();

    float width = stage.getWidth() * 0.85f;
    float height = stage.getHeight() * 0.95f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.add(instance.opts).expand().fill();

    stage.addActor(instance);
    instance.setY(stage.getHeight());
    instance.addAction(MyActions.sequence(Actions.parallel(Actions.color(new Color(1, 1, 1, alpha), 0.2f),
        Actions.moveTo((stage.getWidth() - width) / 2, (stage.getHeight() - height) / 2, 0.2f))));
  }


  public static boolean isOpened() {
    return instance.hasParent();
  }

  public static void getLoginDialog() {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = true;
    instance.remove();

    String usr = "";
    String pwd = "";

    if (GnuBackgammon.Instance.server.equals("fibs.com")) {
      usr = GnuBackgammon.Instance.fibsPrefs.getString("fusername");
      pwd = GnuBackgammon.Instance.fibsPrefs.getString("fpassword");
    } else {
      usr = GnuBackgammon.Instance.fibsPrefs.getString("tusername");
      pwd = GnuBackgammon.Instance.fibsPrefs.getString("tpassword");
    }

    float width = stage.getWidth() * 0.65f;
    float height = stage.getHeight() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.add(new Label("Logint to server..", GnuBackgammon.skin)).colspan(3);

    instance.row();
    instance.add().colspan(3).expand().fill();

    instance.row();
    instance.add(new Label("Username:", GnuBackgammon.skin));
    final TextField username = new TextField(usr, GnuBackgammon.skin);
    instance.add(username).colspan(2).fillX().expandX();

    instance.row();
    instance.add(new Label("Password:", GnuBackgammon.skin));
    final TextField password = new TextField(pwd, GnuBackgammon.skin);
    instance.add(password).colspan(2).fillX().expandX();

    instance.row();
    instance.add().colspan(3).expand().fill();

    instance.row();
    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    TextButton t1 = new TextButton("Cancel", tl);
    TextButton t2 = new TextButton("Create", tl);
    TextButton t3 = new TextButton("Login", tl);
    instance.add(t1).fill().expand();
    instance.add(t2).fill().expand();
    instance.add(t3).fill().expand();


    t1.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
        instance.remove();
        super.clicked(event, x, y);
      }
    });

    t2.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        UIDialog.getCreateAccountDialog();
        super.clicked(event, x, y);
      }
    });

    t3.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        String u = username.getText();
        String p = password.getText();
        if (u.length() > 0 && p.length() > 0) {
          GnuBackgammon.Instance.commandDispatcher.sendLogin(u, p);
          instance.remove();
        }
        super.clicked(event, x, y);
      }
    });

    Gdx.graphics.setContinuousRendering(true);
    stage.addActor(instance);
    instance.addAction(Actions.alpha(alpha, 0.3f));
  }


  public static void getCreateAccountDialog() {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = true;
    instance.remove();

    float width = stage.getWidth() * 0.65f;
    float height = stage.getHeight() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.add(new Label("Create Account..", GnuBackgammon.skin)).colspan(3);

    instance.row();
    instance.add().colspan(3).expand().fill();

    instance.row();
    instance.add(new Label("Username:", GnuBackgammon.skin));
    final TextField username = new TextField("", GnuBackgammon.skin);
    instance.add(username).colspan(2).fillX().expandX();

    instance.row();
    instance.add(new Label("Password:", GnuBackgammon.skin));
    final TextField password = new TextField("", GnuBackgammon.skin);
    instance.add(password).colspan(2).fillX().expandX();

    instance.row();
    instance.add(new Label("Password:", GnuBackgammon.skin));
    final TextField password2 = new TextField("", GnuBackgammon.skin);
    instance.add(password2).colspan(2).fillX().expandX();

    instance.row();
    instance.add().colspan(3).expand().fill();

    instance.row();
    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    TextButton t1 = new TextButton("Cancel", tl);
    TextButton t2 = new TextButton("Create", tl);
    instance.add(t1).fill().expand();
    instance.add().fill().expand();
    instance.add(t2).fill().expand();

    t1.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        GnuBackgammon.fsm.processEvent(Events.FIBS_CANCEL, null);
        instance.remove();
        super.clicked(event, x, y);
      }
    });


    stage.addActor(instance);
    Gdx.graphics.setContinuousRendering(true);
    instance.addAction(Actions.alpha(alpha, 0.3f));
  }

  public static void getInviteClickedDialog(String username) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.quitWindow = false;
    instance.optionsWindow = false;
    instance.leaveWindow = false;
    instance.remove();
    instance.setText("User \"" + username + "\" invited you...");

    float height = stage.getHeight() * 0.4f;
    float width = stage.getWidth() * 0.6f;

    instance.clear();
    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);

    instance.row().padTop(width / 25);
    instance.add(instance.label).colspan(5).expand().align(Align.center);

    instance.row().pad(width / 25);
    instance.add();
    instance.add(instance.bReject).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();
    instance.add(instance.bAccept).fill().expand().height(height * 0.25f).width(width / 4);
    instance.add();

    stage.addActor(instance);
    instance.addAction(MyActions.alpha(alpha, 0.3f));
  }


  public static void setButtonsStyle(String b) {
    instance.bContinue.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bYes.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bNo.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bCancel.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bExport.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bAccept.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.bReject.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    instance.opts.setButtonsStyle(b);
  }


  public static void getDicesDialog(Boolean cb) {
    Stage stage = GnuBackgammon.Instance.currentScreen.getStage();
    instance.visible = true;
    instance.evt = Events.NOOP;
    instance.quitWindow = false;
    instance.leaveWindow = false;
    instance.optionsWindow = false;
    instance.dicesWindow = true;
    instance.remove();

    float height = stage.getHeight() * 0.94f;
    float width = stage.getWidth() * 0.92f;

    instance.setWidth(width);
    instance.setHeight(height);
    instance.setX((stage.getWidth() - width) / 2);
    instance.setY((stage.getHeight() - height) / 2);
    instance.clear();

    Table t = new Table();

    instance.add(t).fill().expand();
    float cellWidth = width / 7.2f;
    float cellHeight = height / 7.5f;

    for (int i = 1; i < 7; i++) {
      t.row().space(cellHeight * 0.125f);
      for (int j = 1; j < 7; j++) {
        if ((i == 2) && (j == 5)) {
          t.add().width(cellWidth).height(cellHeight).fill();
          t.add().width(cellWidth).height(cellHeight).fill();
          t.add(instance.randomDicesButton).width(cellWidth * 2).colspan(2).right().fill().height(cellHeight);
        }
        if (j <= i) {
          t.add(instance.diceButtonMap.get(i + "x" + j)).width(cellWidth).height(cellHeight).fill();
        }
      }
      if (i == 1) {
        instance.diceLabel.setText("Choose " + GnuBackgammon.Instance.gameScreen.getCurrentPinfo() + " dices");
        t.add(instance.diceLabel).right().top().colspan(5);
      }
    }

    stage.addActor(instance);
    instance.addAction(MyActions.sequence(Actions.run(new Runnable() {
      @Override
      public void run() {
        // MANUAL DICES OPEN
        GnuBackgammon.Instance.nativeFunctions.showAds(false);
      }
    }), Actions.alpha(alpha, 0.3f)));
  }
  public Actor hit(float x, float y, boolean touchable) {
    Actor hit = super.hit(x, y, touchable);
    if (visible) {
      if (hit != null)
        return hit;
      else {
        return this;
      }

    } else {
      return hit;
    }
  }
}
