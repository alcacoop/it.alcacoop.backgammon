/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella                                    #
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
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.fibs.Player;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class FibsScreen implements Screen {

  private Stage stage;
  private Group g;
  private Image bgImg;
  
  public String username = "";
  public String lastLogin;
  
  public Map<String, Player> fibsPlayers; 
  public Map<String, Integer> fibsInvitations;

  private Label LLastLogin;
  private Player me;
  private ScrollPane onlineList, invitationList;
  private float height, width;
  private LabelStyle evenLs;
  private TextureRegion iSended, iReceived;
  private Drawable evenbg;
  private ClickListener rowClicked, inviteClicked;
  public String lastInvite;
  public boolean showWho = false;
  
  
  public FibsScreen() {
    me = new Player();
    me.parsePlayer(username+" - - 1 0 1500 1 0 1 1 - - -");
    
    fibsPlayers = Collections.synchronizedMap(new TreeMap<String, Player>());
    fibsInvitations = Collections.synchronizedMap(new HashMap<String, Integer>());
    evenbg = GnuBackgammon.skin.getDrawable("even");
    iSended = GnuBackgammon.atlas.findRegion("isended");
    iReceived = GnuBackgammon.atlas.findRegion("ireceived");
    
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    stage.setViewport(GnuBackgammon.Instance.resolution[0], GnuBackgammon.Instance.resolution[1], false);
    stage.addActor(bgImg);

    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if(Gdx.input.isKeyPressed(Keys.BACK)||Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
        }
        return super.keyDown(event, keycode);
      }
    });
    
    ClickListener cl = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED,((TextButton)event.getListenerActor()).getText().toString().toUpperCase());
      };
    };

    inviteClicked = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        String s = ((Label)event.getListenerActor()).getText().toString().trim();
        if ((fibsInvitations.containsKey(s))&&((int)fibsInvitations.get(s)>0)) {
          lastInvite = s;
          UIDialog.getInviteClickedDialog(s, 1, stage);
        }
      };
    };
    
    rowClicked = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        String s = ((Label)event.getListenerActor()).getText().toString();
        String arg[] = s.split(" ");
        String u = arg[1].toLowerCase();
        Player p = null;
        if (fibsPlayers.containsKey(u))
          p = fibsPlayers.get(u);
        
        if (p.isPlaying())
          UIDialog.getFlashDialog(Events.NOOP, "User \""+p.getName()+"\" is playing.. You can't invite him", stage);
        else if (!p.isReady())
          UIDialog.getFlashDialog(Events.NOOP, "User \""+p.getName()+"\" is busy.. You can't invite him", stage);
        else {
          lastInvite = p.getName();
          UIDialog.getYesNoDialog(Events.FIBS_INVITE_SENDED, "Really invite \""+p.getName()+"\" to new 1 point match?", stage);
        }
      };
    };
    
    
    LLastLogin = new Label("", GnuBackgammon.skin);

    evenLs = GnuBackgammon.skin.get("even", LabelStyle.class);
    
    width = stage.getWidth()*0.95f;
    height = stage.getHeight()*0.95f;
    
    ScrollPaneStyle sps = GnuBackgammon.skin.get("lists", ScrollPaneStyle.class);
    onlineList = new ScrollPane(new Table(), sps);
    onlineList.setFadeScrollBars(false);
    onlineList.setForceOverscroll(false, false);
    onlineList.setOverscroll(false, false);
    
    invitationList = new ScrollPane(new Table(), sps);
    invitationList.setFadeScrollBars(false);
    invitationList.setForceOverscroll(false, false);
    invitationList.setOverscroll(false, false);
        
    Table table = new Table();
    Drawable d = GnuBackgammon.skin.getDrawable("default-window");
    table.setFillParent(true);
    
    
    ClickListener toggleStatus = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.commandDispatcher.send("toggle ready");
        me.toggleReady();
        GnuBackgammon.Instance.commandDispatcher.send("who "+username);
      };
    };

    me.getStatusImage().addListener(toggleStatus);
    me.getLabel().addListener(toggleStatus);
    
    
    Table title = new Table();
    title.setBackground(d);
    title.add(me.getStatusImage()).left();
    title.add(me.getLabel()).left();
    title.add(LLastLogin).expandX().right();
    
    table.add(title).colspan(2).expand().fill();
    
    table.row();
    table.add().colspan(2).expand().fill();
    
    Label l1 = new Label("ONLINE USERS",GnuBackgammon.skin);
    l1.setAlignment(Align.top);
    
    Table t1 = new Table();
    t1.setBackground(GnuBackgammon.skin.getDrawable("list"));
    t1.add(l1).center().height(height*0.075f);
    t1.row();
    t1.add(onlineList).fill().left().height(height*0.48f).width(width*0.52f);
    
    Label l2 = new Label("INVITATIONS",GnuBackgammon.skin);
    l2.setAlignment(Align.top);
    Table t2 = new Table();
    t2.setBackground(GnuBackgammon.skin.getDrawable("list"));
    t2.add(l2).center().height(height*0.075f);
    t2.row();
    t2.add(invitationList).fill().left().height(height*0.48f).width(width*0.32f);
    
    table.row();
    table.add(t1);
    table.add(t2);
    
    table.row();
    table.add().fill().expand().colspan(2);

    TextButton back = new TextButton("BACK", GnuBackgammon.skin);
    back.addListener(cl);
    table.row();
    Table bt = new Table();
    bt.add(back).width(width*0.3f).fill().expand();
    table.add(bt).colspan(2).height(height*0.11f);
    
    g = new Group();
    g.setWidth(width);
    g.setHeight(height);
    g.addActor(table);
    
    g.setX((stage.getWidth()-g.getWidth())/2);
    g.setY((stage.getHeight()-g.getHeight())/2);
    
    stage.addActor(g);
  }
  
  public Stage getStage() {
    return stage;
  }


  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0, 0, 0, 0);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
  }


  @Override
  public void resize(int width, int height) {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
  }

  
  @Override
  public void show() {
    bgImg.setWidth(stage.getWidth());
    bgImg.setHeight(stage.getHeight());
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    g.setColor(1,1,1,0);
    GnuBackgammon.Instance.nativeFunctions.hideChatBox();
    
    Date expiry = new Date(Long.parseLong(lastLogin)*1000);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = formatter.format(expiry);
    LLastLogin.setText("Last login: "+formattedDate);
    //Gdx.graphics.setContinuousRendering(true);
    fibsPlayers.clear();
    g.addAction(MyActions.sequence(Actions.fadeIn(0.6f),Actions.run(new Runnable() {
      @Override
      public void run() {
        showWho = true;
        GnuBackgammon.Instance.commandDispatcher.send("who");
      }
    })));
  }
  
  public synchronized void playerChanged(Player p) {
    if (p==null) return;
    if (p.getName()==null) return;
    if (p.getName().equals("")) return;
    if (p.getName().toLowerCase().equals(username.toLowerCase())) {
      me.parsePlayer(p.fibsPlayer);
      return;
    }
    
    //GESIONE INVITI
    if ((p.isPlaying())&&(fibsInvitations.containsKey(p.getName()))) {
      fibsInvitations.remove(p.getName());
      refreshInvitationList();
    }
    
    String u = p.getName().toLowerCase();
    if (fibsPlayers.containsKey(u)) {
      fibsPlayers.get(u).parsePlayer(p.fibsPlayer);
      GnuBackgammon.Instance.fibsPlayersPool.free(p);
      Gdx.graphics.requestRendering();
    } else {
      fibsPlayers.put(u, p);
    }
  }
  
  
  public synchronized void playerGone(String p) {
    String u = p.toLowerCase();
    if (u.equals(username.toLowerCase())) return;
    if (u.equals("")) return;
    
    //GESIONE INVITI
    if (fibsInvitations.containsKey(p)) {
      fibsInvitations.remove(p);
      refreshInvitationList();
    }
    
    if (fibsPlayers.containsKey(u)) {
      GnuBackgammon.Instance.fibsPlayersPool.free(fibsPlayers.remove(u));
      refreshPlayerList();
    }
  }
  
  
  public synchronized void playerLogged(String p) {
    String u = p.toLowerCase();
    if (u.equals("")) return;
    if (!fibsPlayers.containsKey(u)) {
      Player pl = GnuBackgammon.Instance.fibsPlayersPool.obtain();
      pl.parsePlayer(u+" - - 1 0 1500 1 0 1 1 - - -");
      fibsPlayers.put(u, pl);
      refreshPlayerList();
      GnuBackgammon.Instance.commandDispatcher.send("who "+u);
    }
  }
  
  
  public void initGame() {
    GnuBackgammon.Instance.board.dices.clear();
    GnuBackgammon.Instance.board.initBoard(2);
    GnuBackgammon.Instance.goToScreen(4);
  }
  
  
  public synchronized void clearSendedInvitations() {
    Iterator<Entry<String, Integer>> i = fibsInvitations.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry<String,Integer> entry = i.next();
      int value = entry.getValue();
      if (value==-1) //SENDED
        i.remove();
    }
  }
  
  
  public synchronized void refreshInvitationList() {
    float twidth2 = width*0.35f;
    int n = 0;
    Table it = new Table();
    for(Map.Entry<String,Integer> entry : fibsInvitations.entrySet()) {
      n++;
      String key = entry.getKey();
      int value = entry.getValue();
      Label user;
      if (n%2!=0) user = new Label(" "+key, evenLs);
      else user = new Label(" "+key, GnuBackgammon.skin);
      Image type;
      if (value == 1)  type =new Image(iReceived);
      else type = new Image(iSended);
      Table t = new Table();
      if (n%2!=0) t.setBackground(evenbg);
      t.add(type).expandX();
      if (value==1) user.addListener(inviteClicked);
      it.row();
      it.add(user).left().width(twidth2*0.7f).height(height*0.12f);
      it.add(t).expandX().fillX().height(height*0.12f);
    }
    it.row();
    it.add().expand().fill().colspan(2);
    invitationList.setWidget(it);
    Gdx.graphics.requestRendering();        
  }

  
  public synchronized void refreshPlayerList() {
    float twidth = width*0.5f;
    int n=0;
    Table pt = new Table();
    for(Map.Entry<String,Player> entry : fibsPlayers.entrySet()) {
      n++;
      Player value = entry.getValue();
      Label l = value.getLabel();
      l.addListener(rowClicked);
      Table t = new Table();
      if (n%2!=0) t.setBackground(evenbg);
      t.add(l).left().width(twidth*0.86f).height(height*0.12f).fillX();
      t.add().expandX();
      t.add(value.getStatusImage()).left();
      t.add().expandX();
      pt.row();
      pt.add(t).fillX().expandX();
    }
    onlineList.setWidget(pt);
    Gdx.graphics.requestRendering();
  }
  
  @Override
  public void hide() {
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        invitationList.setWidget(new Table());
        onlineList.setWidget(new Table());
      }
    });
  }

  @Override
  public void pause() {
    GnuBackgammon.fsm.processEvent(Events.BUTTON_CLICKED, "BACK");
  }

  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }

  @Override
  public void dispose() {}
}
