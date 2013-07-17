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
  private FixedButtonGroup mdices;
  private TextButton lm1, lm2, ok; 

  
  public GameOptionsTable(boolean decoration, ClickListener _cl) {
    
    setFillParent(decoration);
    TextButtonStyle ts = GnuBackgammon.skin.get("toggle", TextButtonStyle.class);
    
    ClickListener cls = new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        super.clicked(event, x, y);
      }
    };
    
    speed = new FixedButtonGroup();
    TextButton sp1 = new TextButton("Fast", ts);
    TextButton sp2 = new TextButton("Slow", ts);
    speed.add(sp1);
    speed.add(sp2);
    sp1.addListener(cls);
    sp2.addListener(cls);
    
    sound = new FixedButtonGroup();
    TextButton sn1 = new TextButton("Yes", ts);
    TextButton sn2 = new TextButton("No", ts);
    sound.add(sn1);
    sound.add(sn2);
    sn1.addListener(cls);
    sn2.addListener(cls);
    
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
    lm1.addListener(cls);
    lm2.addListener(cls);
    
    mdices = new FixedButtonGroup();
    TextButton md1 = new TextButton("Yes", ts);
    TextButton md2 = new TextButton("No", ts);
    mdices.add(md1);
    mdices.add(md2);
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        savePrefs();
        String s = ((TextButton)event.getListenerActor()).getText().toString().toUpperCase();
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, s);
      };
    };
    ClickListener cl2 = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        String s = ((TextButton)event.getListenerActor()).getText().toString().toUpperCase();
        if (s.equals("TAP")) setDisabledLmoves(false);
        if (s.equals("AUTO")) setDisabledLmoves(true);
      };
    };
    ClickListener cl3 = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        savePrefs();
      };
    };
    am1.addListener(cl2);
    am2.addListener(cl2);
    md1.addListener(cl3);
    md2.addListener(cl3);
    
    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    
    ok = new TextButton("OK", GnuBackgammon.skin);
    ok.addListener(_cl);
    
    Label l = new Label("Animation Speed:", GnuBackgammon.skin);
    
    float width = l.getWidth()*0.9f;
    float height = l.getHeight()*1.8f;
    
    
    if (decoration) {
      add(new Label("GAME OPTIONS", GnuBackgammon.skin)).expand().colspan(5);
      row();
      add().fill().expand().colspan(5);
    } else {
      row();
      add().fill().expand().colspan(5);
    }

    row().spaceBottom(height*0.05f);
    add().fill().expandX();
    add(new Label("Sounds:", GnuBackgammon.skin)).right().spaceRight(6).height(height);
    add(sn1).width(width).fillY().height(height*1.3f);
    add(sn2).width(width).fillY().height(height*1.3f);
    add().fill().expandX();
    
    
    row().spaceBottom(height*0.05f);
    add().fill().expandX();
    add(l).right().spaceRight(6);
    add(sp1).height(height*1.3f).width(width);
    add(sp2).height(height*1.3f).width(width);
    add().fill().expandX();
    
    
    row().spaceBottom(height*0.05f);
    add().fill().height(height*1.1f).expandX();
    add(new Label("Move Logic:", GnuBackgammon.skin)).right().spaceRight(6);
    add(am1).height(height*1.3f).width(width);
    add(am2).height(height*1.3f).width(width);
    add().fill().expandX();

    
    row().spaceBottom(height*0.05f);
    add().fill().height(height*1.1f).expandX();
    add(new Label("Legal Moves:", GnuBackgammon.skin)).right().spaceRight(6);
    add(lm1).height(height*1.3f).width(width);
    add(lm2).height(height*1.3f).width(width);
    add().fill().expandX();
    
    row().spaceBottom(height*0.1f);
    add().fill().height(height*1.05f).expandX();
    add(new Label("Manual dices:", GnuBackgammon.skin)).right().spaceRight(6);
    add(md1).height(height*1.3f).width(width);
    add(md2).height(height*1.3f).width(width);
    add().fill().expandX();

    row();
    add().fill().expand().colspan(5);
    
    if (decoration) {
      row();
      add(back).expand().fill().colspan(5).height(height*1.3f).width(1.5f*width);
    } else {
      row();
      add(ok).expandX().fillX().colspan(5).height(height*1.3f).width(1.5f*width);
    }
    row();
    add().fill().expand().colspan(5);
    
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
    String sound = GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes");
    this.sound.setChecked(sound);
    String speed = GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast");
    this.speed.setChecked(speed);
    String automoves = GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap");
    this.automoves.setChecked(automoves);
    
    if (automoves.equals("Tap")) setDisabledLmoves(false);
    if (automoves.equals("Auto")) setDisabledLmoves(true);
    
    String lmoves = GnuBackgammon.Instance.optionPrefs.getString("LMOVES", "Yes");
    this.lmoves.setChecked(lmoves);
    String manualdices = GnuBackgammon.Instance.optionPrefs.getString("MDICES", "No");
    this.mdices.setChecked(manualdices);
  }
  
  public void savePrefs() {
    String sound = ((TextButton)this.sound.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.optionPrefs.putString("SOUND", sound);
    String speed = ((TextButton)this.speed.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.optionPrefs.putString("SPEED", speed);
    String amoves = ((TextButton)this.automoves.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.optionPrefs.putString("AMOVES", amoves);
    String lmoves = ((TextButton)this.lmoves.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.optionPrefs.putString("LMOVES", lmoves);
    String manualdices = ((TextButton)this.mdices.getChecked()).getText().toString(); 
    GnuBackgammon.Instance.optionPrefs.putString("MDICES", manualdices);
    
    GnuBackgammon.Instance.optionPrefs.flush();
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
    a = mdices.getButtons();
    for (int i=0;i<a.size; i++)
      a.get(i).setStyle(GnuBackgammon.skin.get("toggle-"+b, TextButtonStyle.class));
    
    ok.setStyle(GnuBackgammon.skin.get("button-"+b, TextButtonStyle.class));
  }

}
