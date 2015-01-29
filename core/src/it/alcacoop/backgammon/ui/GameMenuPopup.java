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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.FIBSFSM;
import it.alcacoop.backgammon.fsm.GServiceFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.fsm.OldGServiceFSM;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.MatchState;


public final class GameMenuPopup extends Table {

  private Table t1;
  private Drawable background;
  private static TextButton undo;
  private static TextButton resign;
  private static TextButton abandon;
  private TextButton options;
  private Actor a;
  private boolean visible;


  public GameMenuPopup(Stage stage) {
    setWidth(stage.getWidth());
    setHeight(stage.getHeight() / (6.3f - GnuBackgammon.Instance.ss));
    setX(0);
    setY(-getHeight());

    background = GnuBackgammon.skin.getDrawable("popup-region");
    setBackground(background);

    a = new Actor();
    a.addListener(new InputListener() {
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        hide();
        return true;
      }
    });

    t1 = new Table();
    t1.setFillParent(true);

    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);

    undo = new TextButton("Undo Move", tl);
    undo.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        hide(new Runnable() {
          @Override
          public void run() {
            if (undo.isDisabled())
              return;
            GnuBackgammon.Instance.board.undoMove();
          }
        });
      }
    });

    resign = new TextButton("Resign Game", tl);
    resign.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        hide(new Runnable() {
          @Override
          public void run() {
            if (resign.isDisabled())
              return;

            if (GnuBackgammon.fsm instanceof FIBSFSM)
              GnuBackgammon.fsm.state(FIBSFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof GameFSM)
              GnuBackgammon.fsm.state(GameFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof GServiceFSM)
              GnuBackgammon.fsm.state(GServiceFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof OldGServiceFSM)
              GnuBackgammon.fsm.state(OldGServiceFSM.States.DIALOG_HANDLER);

            if (MatchState.fTurn == 0)
              AICalls.GetResignValue(GnuBackgammon.Instance.board._board[1], GnuBackgammon.Instance.board._board[0]);
            else
              AICalls.GetResignValue(GnuBackgammon.Instance.board._board[0], GnuBackgammon.Instance.board._board[1]);
          }
        });

      }
    });

    abandon = new TextButton("Abandon Match", tl);
    abandon.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        hide(new Runnable() {
          @Override
          public void run() {
            if (abandon.isDisabled())
              return;

            if (GnuBackgammon.fsm instanceof FIBSFSM)
              GnuBackgammon.fsm.state(FIBSFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof GameFSM)
              GnuBackgammon.fsm.state(GameFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof GServiceFSM)
              GnuBackgammon.fsm.state(GServiceFSM.States.DIALOG_HANDLER);
            if (GnuBackgammon.fsm instanceof OldGServiceFSM)
              GnuBackgammon.fsm.state(OldGServiceFSM.States.DIALOG_HANDLER);

            if (MatchState.matchType == 0)
              UIDialog.getLeaveDialog(Events.ABANDON_MATCH);
            else
              UIDialog.getYesNoDialog(Events.ABANDON_MATCH, "Really leave current match?");
          }
        });
      }
    });

    options = new TextButton("Options", tl);
    options.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        hide(new Runnable() {
          @Override
          public void run() {
            UIDialog.getOptionsDialog();
          }
        });
      }
    });

    float pad = getHeight() / 15;
    float w = stage.getWidth()/4-pad;

    add().width(pad).expand();
    add(undo).fill().expand().width(w);
    add().width(pad).expand();
    add(resign).fill().expand().width(w);
    add().width(pad).expand();
    add(abandon).fill().expand().width(w);
    add().width(pad).expand();
    add(options).fill().expand().width(w);
    add().width(pad).expand();

    visible = false;
    addActor(t1);
  }

  public static void setDisabledButtons() {
    if ((MatchState.matchType == 0) && (MatchState.fMove == 1)) { // CPU IS PLAYING
      undo.setDisabled(true);
      resign.setDisabled(true);
      abandon.setDisabled(true);
      undo.setColor(1, 1, 1, 0.4f);
      resign.setColor(1, 1, 1, 0.4f);
      abandon.setColor(1, 1, 1, 0.4f);
    } else if ((MatchState.matchType >= 2) && (MatchState.fMove == 1)) { // REMOTE TURN
      undo.setDisabled(true);
      resign.setDisabled(true);
      abandon.setDisabled(false);
      undo.setColor(1, 1, 1, 0.4f);
      resign.setColor(1, 1, 1, 0.4f);
      abandon.setColor(1, 1, 1, 1);
    } else {
      undo.setDisabled(false);
      resign.setDisabled(false);
      abandon.setDisabled(false);
      undo.setColor(1, 1, 1, 1);
      resign.setColor(1, 1, 1, 1);
      abandon.setColor(1, 1, 1, 1);
    }
  }

  private void show() {
    visible = true;
    addAction(MyActions.moveTo(0, 0, 0.1f));
  }
  public void immediateHide() {
    visible = false;
    setPosition(0, -getHeight());
  }
  private void hide() {
    visible = false;
    addAction(MyActions.moveTo(0, -getHeight(), 0.1f));
  }
  private void hide(Runnable r) {
    visible = false;
    addAction(MyActions.sequence(
        Actions.moveTo(0, -getHeight(), 0.1f),
        Actions.run(r)
        ));
  }

  public void toggle() {
    if (visible)
      hide();
    else {
      GnuBackgammon.Instance.board.points.reset();
      if (GnuBackgammon.Instance.board.selected != null)
        GnuBackgammon.Instance.board.selected.highlight(false);
      show();
    }
  }


  public Actor hit(float x, float y, boolean touchable) {
    Actor hit = super.hit(x, y, touchable);
    if (visible) {
      if (hit != null)
        return hit;
      else {
        return a;
      }

    } else {
      return hit;
    }
  }

  public void setButtonsStyle(String b) {
    undo.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    resign.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    abandon.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
    options.setStyle(GnuBackgammon.skin.get("button-" + b, TextButtonStyle.class));
  }
}
