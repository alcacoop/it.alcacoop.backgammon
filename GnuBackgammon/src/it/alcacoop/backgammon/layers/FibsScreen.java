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
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class FibsScreen implements Screen {

  private Stage stage;
  private Group g;
  private Image bgImg;
  
  public String username = "";
  public String lastLogin;
  public String fibsRating;
  
  public Map<String, Player> fibsPlayers; 
  public Map<String, Integer> fibsInvitations;
  
  private Label LUsername, LLastLogin;
  
  private Table playerTable, invitationTable;
  private ScrollPane onlineList, invitationList;
  private float height, width;
  private LabelStyle evenLs;
  private TextureRegion readyRegion, busyRegion, playingRegion, iSended, iReceived;
  private Drawable evenbg;
  private ClickListener rowClicked, inviteClicked;
  private Image status;
  public String lastInvite;
  public boolean ready;
  private Timer timer;
  
  
  public FibsScreen(){
    fibsPlayers = Collections.synchronizedMap(new TreeMap<String, Player>());
    fibsInvitations = Collections.synchronizedMap(new HashMap<String, Integer>());
    playerTable = new Table();
    invitationTable = new Table();
    evenbg = GnuBackgammon.skin.getDrawable("even");

    readyRegion = GnuBackgammon.atlas.findRegion("ready");
    busyRegion = GnuBackgammon.atlas.findRegion("busy");
    playingRegion = GnuBackgammon.atlas.findRegion("playing");
    iSended = GnuBackgammon.atlas.findRegion("isended");
    iReceived = GnuBackgammon.atlas.findRegion("ireceived");
    
    status = new Image(readyRegion);
    
    TextureRegion  bgRegion = GnuBackgammon.atlas.findRegion("bg");
    bgImg = new Image(bgRegion);
    
    stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    //VIEWPORT DIM = VIRTUAL RES (ON SELECTED TEXTURE BASIS)
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
        String s = ((Label)event.getListenerActor()).getText().toString();
        GnuBackgammon.Instance.commandDispatcher.send("join "+s);
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
          //fibsInvitations.put(p.getName(), -1);
          //GnuBackgammon.Instance.commandDispatcher.dispatch(Command.INVITE, p.getName(), "1");
        }
      };
    };
    
    
    LUsername = new Label("", GnuBackgammon.skin);
    LLastLogin = new Label("", GnuBackgammon.skin);
    
    //TextButtonStyle ts = GnuBackgammon.skin.get("default", TextButtonStyle.class);
    evenLs = GnuBackgammon.skin.get("even", LabelStyle.class);
    
    width = stage.getWidth()*0.95f;
    height = stage.getHeight()*0.95f;
    ScrollPaneStyle sps = GnuBackgammon.skin.get("lists", ScrollPaneStyle.class);
    onlineList = new ScrollPane(playerTable, sps);
    onlineList.setFadeScrollBars(false);
    onlineList.setForceOverscroll(false, false);
    onlineList.setOverscroll(false, false);
    
    invitationList = new ScrollPane(invitationTable, sps);
    invitationList.setFadeScrollBars(false);
    invitationList.setForceOverscroll(false, false);
    invitationList.setOverscroll(false, false);
        
    Table table = new Table();
    //table.debug();
    Drawable d = GnuBackgammon.skin.getDrawable("default-window");
    //table.setBackground(d);
    table.setFillParent(true);
    
    
    ClickListener toggleStatus = new ClickListener() {
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.commandDispatcher.send("toggle ready");
        ready = !ready;
        TextureRegionDrawable d;
        if (ready ) d = new TextureRegionDrawable(readyRegion);
        else  d = new TextureRegionDrawable(busyRegion);
        status.setDrawable(d);
      };
    };
    status.addListener(toggleStatus);
    LUsername.addListener(toggleStatus);
    
    
    Table title = new Table();
    title.setBackground(d);
    title.add(status).left();
    title.add(LUsername).left();
    title.add(LLastLogin).expandX().right();
    
    table.add(title).colspan(2).expand().fill();
    
    table.row();
    table.add().fill().expand().colspan(2);
    
    table.row();
    table.add(new Label("ONLINE USERS",GnuBackgammon.skin)).expand().center();
    table.add(new Label("INVITATIONS",GnuBackgammon.skin)).expand().center();
    
    table.row();
    table.add(onlineList).fill().left().height(height*0.6f).width(width*0.59f);
    table.add(invitationList).fill().right().height(height*0.6f).width(width*0.39f);
    
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
    Table.drawDebug(stage);
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
    
    LUsername.setText(username+ " ("+fibsRating+")");
    Date expiry = new Date(Long.parseLong(lastLogin)*1000);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = formatter.format(expiry);
    LLastLogin.setText("Last login: "+formattedDate);
    
    fibsInvitations.clear();
    fibsPlayers.clear();
    refreshPlayerList();
    GnuBackgammon.Instance.commandDispatcher.send("who");
    
    g.addAction(MyActions.sequence(Actions.delay(0.1f),Actions.fadeIn(0.6f), Actions.run(new Runnable() {
      @Override
      public void run() {
        refreshPlayerList();
      }
    })));

    timer = new Timer(true);
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        refreshPlayerList();
        System.out.println("TIMER!");
      }
    };
    timer.schedule(task, 1000, 1500);
  }
  
  public void playerChanged(Player p) {
    if (p==null) return;
    if (p.getName()==null) return;
    if (p.getName().equals("")) return;
    if (p.getName().toLowerCase().equals(username.toLowerCase())) {
      LUsername.setText(p.getName()+" ("+p.getRating()+")");
      return;
    }
    String u = p.getName().toLowerCase();
    if (fibsPlayers.containsKey(u)) 
      GnuBackgammon.Instance.fibsPlayersPool.free(fibsPlayers.remove(u));
    fibsPlayers.put(u, p);
  }
  
  public void playerGone(String p) {
    String u = p.toLowerCase();
    if (u.equals(username.toLowerCase())) return;
    if (u.equals("")) return;
    if (fibsPlayers.containsKey(u)) {
      GnuBackgammon.Instance.fibsPlayersPool.free(fibsPlayers.remove(u));
    }
  }
  
  public void onInviation(String s) {
    fibsInvitations.put(s, 1); //1=INVITE IN
  }
  
  public void initGame() {
    GnuBackgammon.Instance.board.dices.clear();
    GnuBackgammon.Instance.board.initBoard(2);
    GnuBackgammon.Instance.goToScreen(4);
  }
  
  private void refreshPlayerList() {
    System.out.println("REFRESH PLAYER LIST");
    //float width = stage.getWidth()*0.95f*0.6f;
    playerTable.remove();
    playerTable.reset();
    invitationTable.remove();
    invitationTable.reset();
    
    float twidth = width*0.6f;
    float twidth2 = width*0.4f;
    int n = 0;
    
    synchronized (fibsInvitations) {
      for(Map.Entry<String,Integer> entry : fibsInvitations.entrySet()) {
        n++;
        String key = entry.getKey();
        int value = entry.getValue();
        Label user;
        if (n%2==0) user = new Label(" "+key, evenLs);
        else user = new Label(" "+key, GnuBackgammon.skin);
        
        Image type;
        if (value == 1)  type =new Image(iReceived);
        else type = new Image(iSended);
        Table t = new Table();
        if (n%2==0) t.setBackground(evenbg);
        t.add(type).expandX();
        
        if (value==1) user.addListener(inviteClicked);
        
        invitationTable.row();
        invitationTable.add(user).left().width(twidth2*0.7f).height(height*0.12f);
        invitationTable.add(t).expandX().fillX().height(height*0.12f);
      }
      invitationTable.row();
      invitationTable.add().expand().fill().colspan(2);
    }
    
    n=0;
    synchronized (fibsPlayers) {
      for(Map.Entry<String,Player> entry : fibsPlayers.entrySet()) {
        n++;
        Player value = entry.getValue();
        Image pstatus;
        if (value.isPlaying())  pstatus =new Image(playingRegion);
        else if (!value.isReady()) pstatus = new Image(busyRegion);
        else pstatus = new Image(readyRegion);
        
        Table t = new Table();
        if (n%2==0) t.setBackground(evenbg);
        t.add(pstatus).expandX();
        
        Label user;
        if (n%2==0) user = new Label(" "+value.getName()+" ("+value.getRating()+")", evenLs);
        else user = new Label(" "+value.getName()+" ("+value.getRating()+")", GnuBackgammon.skin);
        
        user.addListener(rowClicked);
        
        playerTable.row();
        playerTable.add(user).left().width(twidth*0.7f).height(height*0.12f);
        playerTable.add(t).expandX().fillX().height(height*0.12f);
      }
      
      playerTable.row();
      playerTable.add().expand().fill().colspan(2);
    }
    onlineList.setWidget(playerTable);
    invitationList.setWidget(invitationTable);
	Gdx.graphics.requestRendering();
  }
  
  public void resetStatus() {
    ready = true;
    TextureRegionDrawable d = new TextureRegionDrawable(readyRegion);
    status.setDrawable(d);    
  }

  @Override
  public void hide() {
    timer.cancel();
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }

  @Override
  public void dispose() {
  }
}
