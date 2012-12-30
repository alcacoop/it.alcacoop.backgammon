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
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GameFSM.States;
import it.alcacoop.backgammon.logic.MatchState;

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


public final class GameMenuPopup extends Table {

  private Table t1;
  private Drawable background;
  private static TextButton undo;
  private static TextButton resign;
  private static TextButton abandon;
  private TextButton options;
  private Actor a;
  private Runnable noop;
  
  private boolean visible;
  
  
  
  public GameMenuPopup(Stage stage) {
    noop = new Runnable(){
      @Override
      public void run() {
      }
    };
        
    setWidth(stage.getWidth());
    setHeight(stage.getHeight()/(6.8f-GnuBackgammon.ss));
    setX(0);
    setY(-getHeight());
    
    background = GnuBackgammon.skin.getDrawable("popup-region");
    setBackground(background);
    
    a = new Actor();
    a.addListener(new InputListener(){
      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        hide(noop);
        return true;
      }
    });
    
    t1 = new Table();
    t1.setFillParent(true);
    
    TextButtonStyle tl = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    
    undo = new TextButton("Undo Move", tl);
    undo.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
        @Override
        public void run() {
          if (undo.isDisabled()) return;
          GnuBackgammon.Instance.board.undoMove();
        }});
      }
    });
    
    resign = new TextButton("Resign Game", tl);
    resign.addListener(new ClickListener(){
      @Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
          @Override
          public void run() {
            if (resign.isDisabled()) return;
            GnuBackgammon.fsm.state(States.DIALOG_HANDLER);
            GnuBackgammon.fsm.processEvent(Events.ACCEPT_RESIGN, 0);
        }});
        
      }
    });
    
    abandon = new TextButton("Abandon Match", tl);
    abandon.addListener(new ClickListener(){@Override
    public void clicked(InputEvent event, float x, float y) {
      hide(new Runnable(){
      @Override
      public void run() {
        if (abandon.isDisabled()) return;
        GnuBackgammon.fsm.state(States.DIALOG_HANDLER);
        UIDialog.getYesNoDialog(
            Events.ABANDON_MATCH, 
            "Really exit this match?", 
            0.82f,
            GnuBackgammon.Instance.board.getStage());
      }});
    }});
    
    options = new TextButton("Options", tl);
    options.addListener(new ClickListener(){@Override
      public void clicked(InputEvent event, float x, float y) {
        hide(new Runnable(){
        @Override
        public void run() {
          UIDialog.getOptionsDialog(0.95f, GnuBackgammon.Instance.board.getStage());
        }});
      }});
    
    float pad = getHeight()/15;
    float w = getWidth()/3.3f - pad;
    
    add(undo).fill().expand().pad(pad).width(w);
    add(resign).fill().expand().pad(pad).width(w);
    add(abandon).fill().expand().pad(pad).width(w);
    add(options).fill().expand().pad(pad).width(w);
    
    visible = false;
    addActor(t1);
  }

  public static void setDisabledButtons() {
    if ((MatchState.matchType==0) && (MatchState.fMove==1)) { //CPU IS PLAYING
      undo.setDisabled(true);
      resign.setDisabled(true);
      abandon.setDisabled(true);
      undo.setColor(1,1,1,0.4f);
      resign.setColor(1,1,1,0.4f);
      abandon.setColor(1,1,1,0.4f);
    } else {
      undo.setDisabled(false);
      resign.setDisabled(false);
      abandon.setDisabled(false);
      undo.setColor(1,1,1,1);
      resign.setColor(1,1,1,1);
      abandon.setColor(1,1,1,1);
    }
  }
  
  private void show() {
    visible = true;
    addAction(Actions.moveTo(0, 0, 0.1f));
  }
  
  private void hide(Runnable r) {
    visible = false;
    addAction(Actions.sequence(
      Actions.moveTo(0, -getHeight(), 0.1f),
      Actions.run(r)
    ));
  }
  
  public void toggle() {
    if (visible) hide(noop);
    else {
      GnuBackgammon.Instance.board.points.reset();
      if (GnuBackgammon.Instance.board.selected!=null) 
        GnuBackgammon.Instance.board.selected.highlight(false);
      show();
    }
  }
  
  
  public Actor hit (float x, float y, boolean touchable) {
    Actor hit = super.hit(x, y, touchable);
    
    if (visible) {
      if (hit != null) return hit;
      else {
        return a;
      }
      
    } else {
      return hit;  
    }
    
  }

  
}
