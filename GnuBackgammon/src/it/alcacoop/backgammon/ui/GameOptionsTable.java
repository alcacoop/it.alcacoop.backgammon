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
import it.alcacoop.backgammon.actors.FixedButtonGroup;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class GameOptionsTable extends Table {
  
  private FixedButtonGroup speed;
  private FixedButtonGroup sound;
  private FixedButtonGroup automoves;
  private FixedButtonGroup lmoves;
  private FixedButtonGroup npoints;
  private TextButton lm1, lm2; 

  
  public GameOptionsTable(boolean decoration) {
    
    setFillParent(decoration);
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    
    speed = new FixedButtonGroup();
    TextButton sp1 = new TextButton("Fast", ts);
    TextButton sp2 = new TextButton("Slow", ts);
    speed.add(sp1);
    speed.add(sp2);
    
    sound = new FixedButtonGroup();
    TextButton sn1 = new TextButton("Yes", ts);
    TextButton sn2 = new TextButton("No", ts);
    sound.add(sn1);
    sound.add(sn2);
    
    automoves = new FixedButtonGroup();
    TextButton am1 = new TextButton("Tap", ts);
    TextButton am2 = new TextButton("Auto", ts);
    automoves.add(am1);
    automoves.add(am2);
    
    lmoves = new FixedButtonGroup();
    lm1 = new TextButton("Yes", ts);
    lm2 = new TextButton("No", ts);
    lmoves.add(lm1);
    lmoves.add(lm2);
    
    npoints = new FixedButtonGroup();
    TextButton np1 = new TextButton("Yes", ts);
    TextButton np2 = new TextButton("No", ts);
    npoints.add(np1);
    npoints.add(np2);
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        String s = ((TextButton)event.getListenerActor()).getText().toString().toUpperCase();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, s);
      };
    };
    ClickListener cl2 = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        String s = ((TextButton)event.getListenerActor()).getText().toString().toUpperCase();
        if (s.equals("TAP")) setDisabledLmoves(false);
        if (s.equals("AUTO")) setDisabledLmoves(true);
      };
    };
    ClickListener cl3 = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        GnuBackgammon.Instance.board.showArrow();
      };
    };
    am1.addListener(cl2);
    am2.addListener(cl2);
    np1.addListener(cl3);
    np2.addListener(cl3);
    
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    
    Label l = new Label("Animation Speed:", GnuBackgammon.skin);
    
    float width = l.getWidth()*0.8f;
    float height = l.getHeight()*1.8f;
    
    
    if (decoration) {
      add(new Label("GAME OPTIONS", GnuBackgammon.skin)).expand().colspan(5);
      row();
      add().fill().expand().colspan(5);
    }

    row().height(height*1.3f);
    add().fill().height(height*1.3f).expandX();
    add(new Label("Sounds:", GnuBackgammon.skin)).right().spaceRight(6).height(height);
    add(sn1).width(width).fillY().height(height).spaceRight(6);
    add(sn2).width(width).fillY().height(height);
    add().fill().height(height).expandX();
    
    
    row().height(height*1.3f);
    add().fill().height(height*1.3f).expandX();
    add(l).right().spaceRight(6);
    add(sp1).height(height).width(width).spaceRight(6);
    add(sp2).height(height).width(width);
    add().fill().height(height).expandX();
    
    
    row().height(height*1.3f);
    add().fill().height(height*1.3f).expandX();
    add(new Label("Move Logic:", GnuBackgammon.skin)).right().spaceRight(6);
    add(am1).height(height).width(width).spaceRight(6);
    add(am2).height(height).width(width);
    add().fill().height(height).expandX();

    
    row().height(height*1.3f);
    add().fill().height(height*1.3f).expandX();
    add(new Label("Legal Moves:", GnuBackgammon.skin)).right().spaceRight(6);
    add(lm1).height(height).width(width).spaceRight(6);
    add(lm2).height(height).width(width);
    add().fill().height(height).expandX();
    
    row();
    add().fill().height(height*1.3f).expandX();
    add(new Label("Numbered points:", GnuBackgammon.skin)).right().spaceRight(6);
    add(np1).height(height).width(width).spaceRight(6);
    add(np2).height(height).width(width);
    add().fill().height(height).expandX();
    
    if (decoration) {
      row();
      add().fill().expand().colspan(5);
      row();
      add(back).expand().fill().colspan(5).height(height).width(1.5f*width);
    }
    
    initFromPrefs();
  }
  
  private void setDisabledLmoves(boolean disabled) {
    if (disabled) {
      lm1.setDisabled(true);
      lm1.setColor(1,1,1,0.4f);
      lm2.setDisabled(true);
      lm2.setColor(1,1,1,0.4f);
    } else {
      lm1.setDisabled(false);
      lm1.setColor(1,1,1,1);
      lm2.setDisabled(false);
      lm2.setColor(1,1,1,1);
    }
  } 
  
  
  
  public void initFromPrefs() {
    String sound = GnuBackgammon.Instance.prefs.getString("SOUND", "Yes");
    this.sound.setChecked(sound);
    String speed = GnuBackgammon.Instance.prefs.getString("SPEED", "Fast");
    this.speed.setChecked(speed);
    String automoves = GnuBackgammon.Instance.prefs.getString("AMOVES", "Tap");
    this.automoves.setChecked(automoves);
    
    if (automoves.equals("Tap")) setDisabledLmoves(false);
    if (automoves.equals("Auto")) setDisabledLmoves(true);
    
    String lmoves = GnuBackgammon.Instance.prefs.getString("LMOVES", "Yes");
    this.lmoves.setChecked(lmoves);
    
    String points = GnuBackgammon.Instance.prefs.getString("NPOINTS", "Yes");
    this.npoints.setChecked(points);
  }
  
  public void savePrefs() {
    String sound = ((TextButton)this.sound.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("SOUND", sound);
    String speed = ((TextButton)this.speed.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("SPEED", speed);
    String amoves = ((TextButton)this.automoves.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("AMOVES", amoves);
    String lmoves = ((TextButton)this.lmoves.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("LMOVES", lmoves);
    String points = ((TextButton)this.npoints.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.prefs.putString("NPOINTS", points);
    
    GnuBackgammon.Instance.prefs.flush();
  }

  
  public void setButtonsStyle(String b) {
    Array<Button> a;
    a = speed.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
    a = sound.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
    a = automoves.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
    a = lmoves.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
    a = npoints.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
  }

}
