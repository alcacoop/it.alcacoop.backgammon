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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.OrderedMap;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.actions.MyActions;
import it.alcacoop.backgammon.actors.Board;
import it.alcacoop.backgammon.actors.ChatBox;
import it.alcacoop.backgammon.actors.PlayerInfo;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.FIBSFSM;
import it.alcacoop.backgammon.fsm.GServiceFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.fsm.OldGServiceFSM;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.AILevels;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.ui.EndGameLayer;
import it.alcacoop.backgammon.ui.GameMenuPopup;
import it.alcacoop.backgammon.ui.UIDialog;


public class GameScreen extends BaseScreen {

  public Board board;
  private Table table;

  public PlayerInfo pInfo[];
  private GameMenuPopup menuPopup;
  private ImageButton menu;
  private TextureRegionDrawable wheel;
  public EndGameLayer endLayer;

  public ChatBox chatBox;

  public GameScreen() {
    stage.addListener(new InputListener() {
      @Override
      public boolean keyDown(InputEvent event, int keycode) {
        if (UIDialog.isOpened() || endLayer.isVisible())
          return false;

        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)) {
          if (chatBox.visible) {
            chatBox.hide();
          } else
          if ((MatchState.fMove == 0) || (MatchState.matchType > 0)) { // HUMAN IS PLAYING OR FIBS OR TWO PLS

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
        }

        if (Gdx.input.isKeyPressed(Keys.MENU) || Gdx.input.isKeyPressed(Keys.M)) {
          menuPopup.toggle();
        }
        return super.keyDown(event, keycode);
      }
    });

    board = GnuBackgammon.Instance.board;

    pInfo = new PlayerInfo[2];
    pInfo[0] = new PlayerInfo("AI():", 1, stage.getWidth() / 2.5f);
    pInfo[1] = new PlayerInfo("PL1:", 0, stage.getWidth() / 2.5f);

    table = new Table();
    table.setFillParent(true);
    stage.addActor(table);


    wheel = new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("wheel"));
    ImageButtonStyle ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("button"),
        GnuBackgammon.skin.getDrawable("button-down"),
        null,
        wheel,
        wheel,
        null
        );
    menu = new ImageButton(ibs);
    menu.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        GnuBackgammon.Instance.snd.playMoveStart();
        menuPopup.toggle();
      }
    });

    menuPopup = new GameMenuPopup(stage);
    stage.addActor(menuPopup);

    chatBox = new ChatBox(stage);
    stage.addActor(chatBox);

    endLayer = new EndGameLayer(stage);
    stage.addActor(endLayer);
  }


  private void initTable() {
    String l = "logo";
    TextureRegion r = GnuBackgammon.atlas.findRegion(l);
    Image i = new Image(r);
    i.setScale(0.8f);

    table.clear();
    table.add(i).left().padLeft(6 + 6 * (2 - GnuBackgammon.Instance.ss)).width(i.getWidth()).minHeight(44).padTop(3 + 2 * (2 - GnuBackgammon.Instance.ss)); // BANNER ON ldpi;

    Table t = new Table();
    t.add(pInfo[0]).left();
    t.row();
    t.add(pInfo[1]).left();

    table.add().expandX().fillX();
    table.add(t).fillX().padTop(3 + 3 * (2 - GnuBackgammon.Instance.ss)).right().padRight((2 + 3 * (2 - GnuBackgammon.Instance.ss)) * 2.5f);
    table.add(menu).fillY().width(stage.getWidth() / 10).padRight(6 + 6 * (2 - GnuBackgammon.Instance.ss)).padTop(3 + 3 * (2 - GnuBackgammon.Instance.ss));

    float gain = GnuBackgammon.Instance.ss==2?1.2f:1.05f;
    float th = i.getHeight()*gain;
    System.out.println(th);

    table.row().expand().fill();
    float w = stage.getWidth();
    float h = stage.getHeight()-th;
    board.setWidth(w);
    board.setHeight(h);
    table.add(board).colspan(4).expand().fill().bottom().left().width(w);
  }

  public void updatePInfo(String me, String opponent) {
    pInfo[0].setName(me);
    pInfo[1].setName(opponent);
    pInfo[0].update();
    pInfo[1].update();
  }

  public void updatePInfo() {
    pInfo[0].update();
    pInfo[1].update();
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(0.1f, 0.45f, 0.08f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    stage.act(delta);
    stage.draw();
    if ((MatchState.matchType >= 2) && (chatBox.isScrolling()))
      Gdx.graphics.requestRendering();
  }


  @Override
  public void initialize() {
    // INITIALIZING DICE GENERATOR
    if (GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("MER-TWS"))
      AICalls.Locking.InitRNG(MatchState.RNG_MERSENNE);
    else
      AICalls.Locking.InitRNG(MatchState.RNG_ISAAC);
    loadTextures();
    initTable();
    table.setY(stage.getHeight());

    if ((Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").exists()) && (MatchState.matchType == 0))
      restoreOldMatch();
    else
      initNewMatch();

    if (MatchState.matchType >= 2) {
      chatBox.reset();
    } else {
      chatBox.setVisible(false);
      chatBox.hardHide();
    }
  }

  @Override
  public void show() {
    super.show();
    GnuBackgammon.Instance.nativeFunctions.showAds(true);
    Gdx.input.setInputProcessor(stage);
    Gdx.input.setCatchBackKey(true);
    if ((Gdx.files.absolute(GnuBackgammon.Instance.fname + "json").exists()) && (MatchState.matchType == 0))
      table.addAction(MyActions.sequence(Actions.moveTo(0, 0, 0.3f)));
    else
      table.addAction(MyActions.sequence(
          Actions.moveTo(0, 0, 0.3f),
          Actions.run(new Runnable() {
            @Override
            public void run() {

              if (GnuBackgammon.fsm instanceof FIBSFSM)
                GnuBackgammon.fsm.state(FIBSFSM.States.OPENING_ROLL);
              if (GnuBackgammon.fsm instanceof GameFSM)
                GnuBackgammon.fsm.state(GameFSM.States.OPENING_ROLL);
              if (GnuBackgammon.fsm instanceof GServiceFSM)
                GnuBackgammon.fsm.state(GServiceFSM.States.OPENING_ROLL);
              if (GnuBackgammon.fsm instanceof OldGServiceFSM)
                GnuBackgammon.fsm.state(OldGServiceFSM.States.OPENING_ROLL);
            }
          })
          ));
  }

  @Override
  public void fadeOut() {
    table.addAction(MyActions.sequence(Actions.moveTo(0, stage.getHeight(), 0.3f)));
  }


  public void restoreOldMatch() {
    GnuBackgammon.Instance.rec.loadFromFile(GnuBackgammon.Instance.fname + "json");
    OrderedMap<String, Object> gi = GnuBackgammon.Instance.rec.getLastGameInfo();

    MatchState.SetCubeUse(((Float)gi.get("_cu")).intValue());

    MatchState.setBoardFromString((String)gi.get("_bb"), (String)gi.get("_bw"));
    board.initBoard(2);
    AICalls.Locking.SetBoard(GnuBackgammon.Instance.board._board[1], GnuBackgammon.Instance.board._board[0]);

    MatchState.SetMatchTo("" + gi.get("mi_length"));

    MatchState.SetAILevel(AILevels.getAILevelFromOrdinal((Integer)gi.get("_df")));

    MatchState.SetMatchScore((Integer)gi.get("mi_ws"), (Integer)gi.get("mi_bs"));
    MatchState.SetCrawford((Integer)gi.get("_cr"));
    MatchState.fCrafwordGame = (Boolean)gi.get("_cg");
    int cubeValue = (Integer)gi.get("_cv");
    int cubeOwner = (Integer)gi.get("_co");
    MatchState.UpdateMSCubeInfo(cubeValue, cubeOwner);
    MatchState.SetGameVariant(0);

    pInfo[0].setName("AI(" + (MatchState.currentLevel.ordinal() + 1) + "):");
    MatchState.pl0 = "AI(" + (MatchState.currentLevel.ordinal() + 1) + ")";
    pInfo[1].setName("PL1:");
    MatchState.pl1 = "PL1";
    pInfo[0].update();
    pInfo[1].update();

    // FIX bug #6 on github: correctly end game on resume from background or from saved.
    if (gi.get("re") != null) { // RESIGNED, FINISHED OR DOUBLE DROPPED
      String[] prevResult = gi.get("re").toString().split("\\+");
      MatchState.fMove = (prevResult[0].contains("W")) ? 1 : 0;
      GameFSM.skip_stats = true;
      GnuBackgammon.fsm.state(GameFSM.States.CHECK_END_MATCH);
      return;
    }

    // TODO: WORKAROUND!
    MatchState.fMove = 1;
    MatchState.SwitchTurn(false);

    boolean rolled;
    try {
      rolled = (Boolean)gi.get("_rl");
    } catch (Exception e) {
      rolled = false;
    }

    if (!rolled) {
      if (MatchState.matchType < 2)
        GnuBackgammon.fsm.state(GameFSM.States.HUMAN_TURN);
      MatchState.SetGameTurn(0, 0);
    } else {
      AICalls.Locking.SetGameTurn(0, 0);
      MatchState.fMove = 0;
      MatchState.fTurn = 0;

      int d[] = new int[2];
      d[0] = ((Float)gi.get("_d1")).intValue();
      d[1] = ((Float)gi.get("_d2")).intValue();

      board.rollDices(d[0], d[1]);

      if (MatchState.matchType < 2)
        GnuBackgammon.fsm.state(GameFSM.States.HUMAN_TURN);
      AICalls.GenerateMoves(board, d[0], d[1]);
    }

    GameMenuPopup.setDisabledButtons();
    board.setCube(cubeValue, cubeOwner);
  }

  public void initNewMatch() {
    board.initBoard();
    if (MatchState.matchType == 0) { // single player
      pInfo[0].setName("AI(" + (MatchState.currentLevel.ordinal() + 1) + "):");
      MatchState.pl0 = "AI(" + (MatchState.currentLevel.ordinal() + 1) + ")";
      pInfo[1].setName("PL1:");
      MatchState.pl1 = "PL1";
    } else if (MatchState.matchType == 1) { // two players
      pInfo[0].setName("PL1:");
      MatchState.pl0 = "PL1";
      pInfo[1].setName("PL2:");
      MatchState.pl1 = "PL2";
    }

    pInfo[0].update();
    pInfo[1].update();
  }

  @Override
  public void hide() {
    board.stopCheckers();
    UIDialog.setButtonsStyle("B1"); // RESTORE STANDARD THEME
    menuPopup.immediateHide();
    endLayer.hide();
    GnuBackgammon.Instance.nativeFunctions.showAds(false);
  }

  @Override
  public void resume() {
    Gdx.graphics.requestRendering();
  }

  private void loadTextures() {
    String sBoard = GnuBackgammon.Instance.appearancePrefs.getString("BOARD", "B1");
    GnuBackgammon.atlas.findRegion("board").setRegion(GnuBackgammon.atlas.findRegion(sBoard));
    GnuBackgammon.atlas.findRegion("boardbg").setRegion(GnuBackgammon.atlas.findRegion(sBoard + "-BG"));
    board.rollBtn.setStyle(GnuBackgammon.skin.get("button-" + sBoard, TextButtonStyle.class));
    board.doubleBtn.setStyle(GnuBackgammon.skin.get("button-" + sBoard, TextButtonStyle.class));
    menuPopup.setButtonsStyle(sBoard);
    UIDialog.setButtonsStyle(sBoard);
    String sCs = GnuBackgammon.Instance.appearancePrefs.getString("CHECKERS", "CS1");
    GnuBackgammon.atlas.findRegion("cb").setRegion(GnuBackgammon.atlas.findRegion(sCs + "-B"));
    GnuBackgammon.atlas.findRegion("cw").setRegion(GnuBackgammon.atlas.findRegion(sCs + "-W"));
    GnuBackgammon.atlas.findRegion("ch").setRegion(GnuBackgammon.atlas.findRegion(sCs + "-H"));
    ImageButtonStyle ibs = new ImageButtonStyle(
        GnuBackgammon.skin.getDrawable("button" + sBoard.charAt(1)), GnuBackgammon.skin.getDrawable("button" + sBoard.charAt(1) + "-down"), null,
        wheel, wheel, null
        );
    menu.setStyle(ibs);
  }

  public float getHeight() {
    return stage.getHeight();
  }

  public String getCurrentPinfo() {
    String n = "";
    if (MatchState.fMove == 1)
      n = pInfo[0].getPName();
    else
      n = pInfo[1].getPName();
    return n.substring(0, n.length() - 1);
  }

  @Override
  public void moveBG(float x) {}
}
