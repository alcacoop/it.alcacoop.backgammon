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

package it.alcacoop.backgammon.actors;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.fsm.GServiceFSM;
import it.alcacoop.backgammon.fsm.GameFSM;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.logic.AvailableMoves;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.logic.Move;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.JSONProperties;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class Board extends Group {

  private Label thinking;
  private Label waiting;
  private DoublingCube doublingCube;
  private BaseFSM fsm;

  public int[][] _board;
  public int[] bearedOff = { 0, 0 };
  public Stack<Move> moves;
  public Stack<Move> playedMoves;

  public Vector2 pos[];
  public Checker checkers[][];
  private Checker lastMoved = null;
  public Checker selected = null;

  public Points points;
  public Dices dices;
  public AvailableMoves availableMoves;

  private Image boardbg, larrow, rarrow;
  public BoardImage board;
  public JSONProperties jp;

  public TextButton rollBtn;
  public TextButton doubleBtn;

  private Label ns[];


  public Board() {

    jp = new JSONProperties(Gdx.files.internal(GnuBackgammon.Instance.getResName() + "/pos.json"));
    _board = new int[2][25];

    moves = new Stack<Move>();
    playedMoves = new Stack<Move>();
    availableMoves = new AvailableMoves(this);
    checkers = new Checker[2][15]; // [0]=WHITE [1]=BLACK

    board = new BoardImage(0, 0);

    TextureRegion r1 = GnuBackgammon.atlas.findRegion("boardbg");
    boardbg = new Image(r1);
    boardbg.setX(0);
    boardbg.setY(0);
    addActor(boardbg);

    pos = new Vector2[25];
    for (int i = 0; i < 24; i++) {
      pos[i] = new Vector2();
      pos[i].x = board.getX() + GnuBackgammon.Instance.jp.asFloat("pos" + i, 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0) / 2;
      if (i < 12)
        pos[i].y = board.getY() + GnuBackgammon.Instance.jp.asFloat("down", 0);
      else
        pos[i].y = board.getY() + GnuBackgammon.Instance.jp.asFloat("up", 0);
    }
    pos[24] = new Vector2(); // HITTED
    pos[24].x = board.getX() + GnuBackgammon.Instance.jp.asFloat("pos24", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0);
    pos[24].y = board.getY();

    for (int i = 0; i < 15; i++) {
      checkers[0][i] = new Checker(this, 0); // BLACK
      checkers[1][i] = new Checker(this, 1); // WHITE
    }

    points = new Points(this);
    addActor(points);
    addActor(board);

    for (int i = 0; i < 15; i++) {
      addActor(checkers[0][i]);
      addActor(checkers[1][i]);
    }

    dices = new Dices(this);
    addActor(dices);

    thinking = new Label("... Thinking ...", GnuBackgammon.skin);
    thinking.setX(getX() + (boardbg.getWidth() - thinking.getWidth()) / 2);
    thinking.setY(getY() + (boardbg.getHeight() - thinking.getHeight()) / 2);
    thinking.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.7f, 0.4f), Actions.alpha(1, 0.5f))));
    thinking.setVisible(false);
    addActor(thinking);

    waiting = new Label("... Wait ...", GnuBackgammon.skin);
    waiting.setX(getX() + (boardbg.getWidth() - waiting.getWidth()) / 2);
    waiting.setY(getY() + (boardbg.getHeight() - waiting.getHeight()) / 2);
    waiting.addAction(Actions.forever(Actions.sequence(Actions.alpha(0.7f, 0.4f), Actions.alpha(1, 0.5f))));
    waiting.setVisible(false);
    addActor(waiting);

    larrow = new Image(GnuBackgammon.atlas.findRegion("larrow"));
    rarrow = new Image(GnuBackgammon.atlas.findRegion("rarrow"));
    larrow.setVisible(false);
    rarrow.setVisible(false);
    addActor(larrow);
    addActor(rarrow);


    TextButtonStyle ts = GnuBackgammon.skin.get("button", TextButtonStyle.class);
    rollBtn = new TextButton("Roll", ts);
    rollBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Board.this.rollBtn.remove();
        Board.this.doubleBtn.remove();
        if (MatchState.matchType < 2)
          if (!GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS").equals("Manual")) {
            Board.this.rollDices();
          } else {
            UIDialog.getDicesDialog(false);
          }
        else
          GnuBackgammon.fsm.processEvent(Events.ROLL_DICE, null);
      }
    });
    rollBtn.setWidth(boardbg.getWidth() / 5);
    rollBtn.setHeight(boardbg.getHeight() / 9);
    rollBtn.setX(board.getX() + GnuBackgammon.Instance.jp.asFloat("dice0", 0) - rollBtn.getWidth() / 2);
    rollBtn.setY(board.getY() + boardbg.getHeight() / 2 - rollBtn.getHeight() / 2);


    doubleBtn = new TextButton("Double", ts);
    doubleBtn.addListener(new ClickListener() {
      @Override
      public void clicked(InputEvent event, float x, float y) {
        Board.this.doubleBtn.remove();
        if (GnuBackgammon.fsm instanceof GameFSM)
          GnuBackgammon.fsm.state(GameFSM.States.DIALOG_HANDLER);
        else if (GnuBackgammon.fsm instanceof GServiceFSM)
          GnuBackgammon.fsm.state(GServiceFSM.States.DIALOG_HANDLER);
        GnuBackgammon.fsm.processEvent(Events.DOUBLE_REQUEST, null);
      }
    });
    doubleBtn.setWidth(boardbg.getWidth() / 5);
    doubleBtn.setHeight(boardbg.getHeight() / 9);
    doubleBtn.setX(board.getX() + jp.asFloat("dice1", 0) - doubleBtn.getWidth() / 2);
    doubleBtn.setY(board.getY() + boardbg.getHeight() / 2 - doubleBtn.getHeight() / 2);

    doublingCube = new DoublingCube(this);
    addActor(doublingCube);

    ns = new Label[24];
    BitmapFont f = GnuBackgammon.skin.getFont("alternate-font");
    f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    LabelStyle stl = new LabelStyle(f, Color.WHITE);
    for (int i = 0; i < 24; i++)
      ns[i] = new Label((i + 1) + "", stl);
  }


  public Vector2 getBoardCoord(int color, int x, int y) {
    float offset = checkers[1][1].getWidth()/2;

    if ((y > 4) && (x != -1))
      y = 4;
    float cdim = checkers[0][0].getHeight() - checkers[0][0].getHeight() / 18;
    Vector2 ret = new Vector2();

    switch (x) {

      case -1: // BEAR OFF
        if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise").equals("AntiClockwise")) {
          ret.x = board.getX() + GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0) / 2;
        } else {
          ret.x = board.getX() + boardbg.getWidth() - GnuBackgammon.Instance.jp.asFloat("pos_bo", 0) - GnuBackgammon.Instance.jp.asFloat("pos", 0) / 3;
        }
        if (color == 0)
          ret.y = board.getY() + GnuBackgammon.Instance.jp.asFloat("down", 0) + GnuBackgammon.Instance.jp.asFloat("pos", 0) * y;
        else
          ret.y = board.getY() + GnuBackgammon.Instance.jp.asFloat("up", 0) - cdim - GnuBackgammon.Instance.jp.asFloat("pos", 0) * y;
        break;

      case 24: // BAR
        ret.x = pos[x].x - GnuBackgammon.Instance.jp.asFloat("pos", 0) / 2;
        if (color == 0)
          ret.y = board.getY() + GnuBackgammon.Instance.jp.asFloat("up", 0) - cdim * y - cdim * 3 / 2;
        else
          ret.y = board.getY() + GnuBackgammon.Instance.jp.asFloat("down", 0) + cdim * y + cdim / 2;
        break;

      default: // ON THE TABLE
        if (color == 0) { // BLACK
          if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise").equals("AntiClockwise")) {
            ret.x = pos[x].x;
          }
          else {
            if (x > 11)
              ret.x = pos[35 - x].x;
            else
              ret.x = pos[11 - x].x;
          }
          if (x < 12)
            ret.y = board.getY() + pos[x].y + cdim * y;
          else
            ret.y = board.getY() + pos[x].y - cdim * (y + 1);
        } else { // WHITE
          if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise").equals("AntiClockwise")) {
            ret.x = pos[23 - x].x;
          } else {
            if (x <= 11)
              ret.x = pos[35 - (23 - x)].x;
            else
              ret.x = pos[11 - (23 - x)].x;
          }
          if (x < 12)
            ret.y = board.getY() + pos[23 - x].y - cdim * (y + 1);
          else
            ret.y = board.getY() + pos[23 - x].y + cdim * y;
        }
    }
    ret.x -= offset;
    return ret;
  }

  public void initBoard(int type) {
    MatchState.SetGameVariant(type);
    initBoard();
  }
  public void initBoard() {
    if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise").equals("AntiClockwise")) {
      for (int i = 0; i < 24; i++)
        ns[i].setText((i + 1) + "");
    } else {
      for (int i = 12; i < 24; i++)
        ns[i].setText((36 - i) + "");
      for (int i = 0; i < 12; i++)
        ns[i].setText((12 - i) + "");
    }
    abandon();
    showArrow();
    points.resetBoff();
    larrow.setVisible(false);
    rarrow.setVisible(false);
    MatchState.resignValue = 0;
    MatchState.fMove = 0;
    MatchState.fTurn = 0;

    if (MatchState.fCubeUse == 0) { // NOT DOUBLING
      doublingCube.setVisible(false);
      doubleBtn.remove();
    } else {
      doublingCube.setVisible(true);
      if ((MatchState.fCrawford == 1) && (MatchState.fCrafwordGame)) {
        doublingCube.setVisible(false);
        doubleBtn.remove();
      }
    }

    if (MatchState.matchType == 3) { // ALWAYS USING CUBE ON GSERVICE GAMES...
      doublingCube.setVisible(true);
      AICalls.Locking.SetCubeUse(1);
    }

    doublingCube.reset();
    MatchState.UpdateMSCubeInfo(1, -1);
    initBoard(MatchState.board[MatchState.bgv * 2], MatchState.board[MatchState.bgv * 2 + 1]);
  }


  public void initBoard(int[] b1, int[] b2) {
    int i0 = 0;
    int i1 = 0;

    for (int i = 0; i <= 24; i++) {
      _board[0][i] = b1[i];
      i0 += b1[i]; // TOT CHECKERS ON BOARD
      _board[1][i] = b2[i];
      i1 += b2[i]; // TOT CHECKERS ON BOARD
    }
    bearedOff[0] = 0;
    bearedOff[1] = 0;

    int nchecker = 0;
    // POSITIONING BLACK CHECKERS
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < _board[0][i]; j++) {
        checkers[0][nchecker].reset(i, j);
        addActor(checkers[0][nchecker]);
        nchecker++;
      }
    }
    for (int i = 0; i < 15 - i0; i++) {
      checkers[0][nchecker].reset(-1, i);
      addActor(checkers[0][nchecker]);
      bearedOff[0]++;
      nchecker++;
    }

    // POSITIONING WHITE CHECKERS
    nchecker = 0;
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < _board[1][i]; j++) {
        checkers[1][nchecker].reset(i, j);
        addActor(checkers[1][nchecker]);
        nchecker++;
      }
    }
    for (int i = 0; i < 15 - i1; i++) {
      checkers[1][nchecker].reset(-1, i);
      addActor(checkers[1][nchecker]);
      bearedOff[1]++;
      nchecker++;
    }
  }

  public Checker getChecker(int color, int x) {
    Checker _c = null;
    int y = 0;
    if (x >= 0)
      y = _board[color][x] - 1;
    else
      y = bearedOff[color] - 1;

    for (int i = 0; i < 15; i++) {
      Checker c = checkers[color][i];
      if ((c.boardX == x) && (c.boardY == y))
        _c = c;
    }
    return _c;
  }


  public void humanMove(int _moves[]) { // HUMAN SET A SINGLE MOVE
    fsm = GnuBackgammon.fsm;
    moves.clear();
    Move m = new Move(this, _moves[0], _moves[1]);
    Checker c = getChecker(MatchState.fMove, m.from);
    if ((c != null) && (m.from != m.to)) {
      playedMoves.push(m);
      m.setRemovedMoves(availableMoves.removeMoves(m.from, m.to));
      c.moveTo(m.to);
      lastMoved = c;
    } else {
      GnuBackgammon.fsm.state(GameFSM.States.HUMAN_TURN);
    }
  }


  public void setMoves(int _moves[]) { // CPU SET GROUP OF MOVES
    fsm = GnuBackgammon.fsm;
    moves.clear();

    if (_moves.length < 8)
      return;
    for (int i = 3; i >= 0; i--) {
      if (_moves[2 * i] != -1) {
        Move m = new Move(this, _moves[2 * i], _moves[2 * i + 1]);
        moves.push(m);
      }
    }
    performNextMove();
  }

  public void performNextMove() {
    try {
      Move m = moves.pop();

      if (m != null) {
        playedMoves.push(m);
        m.setRemovedMoves(availableMoves.removeMoves(m.from, m.to));
        Checker c = getChecker(MatchState.fMove, m.from);
        c.moveToDelayed(m.to, 0.2f);
        lastMoved = c;
      }
    } catch (Exception e) {
      if (fsm == GnuBackgammon.fsm) // SAME FSM FROM MOVING START...
        fsm.processEvent(Events.NO_MORE_MOVES, null);
    }
  }


  public boolean checkHit() {
    if (lastMoved != null) {
      if (lastMoved.boardX == -1)
        return false; // BEARED OFF

      int c = lastMoved.getSpecularColor();
      int p = lastMoved.getSpecularPosition();
      if (_board[c][p] == 1) {
        // CHECKER HITTED
        Checker ch = getChecker(c, p);
        ch.moveTo(24);
        playedMoves.lastElement().hitted = true;
        return true;
      }
    }
    return false;
  }


  public int getAutoDestination(int x) {
    int ps[] = availableMoves.getPoints(x);
    if ((ps != null) && (ps.length == 1))
      return ps[0];
    else if ((ps != null) && (ps.length == 2)) {
      if (dices.getDiceOrder()) // FIRST BIGGER
        return ps[0] <= ps[1] ? ps[0] : ps[1];
      else
        // FIRST SMALLER
        return ps[0] >= ps[1] ? ps[0] : ps[1];
    }
    else
      return -2;
  }


  public void selectChecker(int x) {
    if (_board[MatchState.fMove][x] > 0) {

      points.reset();
      if (selected != null)
        selected.highlight(false);

      Checker c = getChecker(MatchState.fMove, x);

      if ((selected != null) && (c.boardX == selected.boardX)) {
        selected = null;
        return;
      }

      int ps[] = availableMoves.getPoints(x);
      if ((ps == null) || (ps.length == 0)) { // NO MOVES FROM HERE!
        c.highlight(false);
        selected = null;
      } else {
        c.highlight(true);
        selected = c;
        for (int i = 0; i < ps.length; i++)
          points.get(ps[i]).highlight();
      }

    }
  }


  public int[] getGreedyBearoffMove(int[][] moves) {
    if (hasContact() || (bearingOff() < 0) || GnuBackgammon.Instance.optionPrefs.getString("GREEDY", "No").equals("No"))
      return null;

    int ds[] = dices.get();
    int cMoves = ds[0] == ds[1] ? 4 : 2;
    cMoves = Math.min(cMoves, (15 - bearedOff[MatchState.fMove]));

    for (int i = 0; i < moves.length; i++) {
      for (int iMove = 0; iMove < cMoves; iMove++) {
        if ((moves[i][iMove * 2] < 0) || (moves[i][(iMove * 2) + 1] != -1)) {
          break;
        } else if (iMove == cMoves - 1) {
          return moves[i];
        }
      }
    }
    return null;
  }


  public boolean hasContact() {
    int myMaxPoint = 0;
    int opMinPoint = 24;
    for (int i = 0; i < 25; i++) {
      if (_board[0][i] > 0) {
        myMaxPoint = i > myMaxPoint ? i : myMaxPoint;
      }
      if (_board[1][24 - i] > 0) {
        opMinPoint = i < opMinPoint ? i : opMinPoint;
      }
    }
    opMinPoint--;
    return opMinPoint < myMaxPoint;
  }


  public int bearingOff() {
    int count = 0;
    for (int i = 6; i < 25; i++) {
      if (_board[MatchState.fMove][i] > 0) {
        count += _board[MatchState.fMove][i];
      }
    }// if count = 0 here, we're in bear off

    int max_point = 0;
    if (count == 0) { // bear off
      for (int i = 0; i < 6; i++) {
        if (_board[MatchState.fMove][i] > 0) {
          max_point = i;
        }
      }// max_point here is the bigger point with checkers
    }
    if (count != 0)
      return -1;
    else
      return max_point;
  }

  public int getPIPS() {
    return getPIPS(MatchState.fMove);
  }
  public int getPIPS(int color) {
    int pips = 0;
    for (int i = 0; i < _board[color].length; i++) {
      pips += _board[color][i] * (i + 1);
    }
    return pips;
  }

  public boolean gameFinished() {
    if (getPIPS() > 0)
      return false;
    else
      return true;
  }

  public boolean specularPointFree(int nPoint) {
    if (MatchState.fMove == 1)
      return (_board[0][23 - nPoint] <= 1);
    else
      return (_board[1][23 - nPoint] <= 1);
  }

  public void undoMove() {
    if (playedMoves.size() > 0) {
      playedMoves.pop().undo();
      updatePInfo();
      GnuBackgammon.fsm.hnmove--;
    }
  }

  public void switchTurn() {
    playedMoves.clear();
    MatchState.SwitchTurn();
  }

  private void abandon() {
    if (dices != null)
      dices.clear();
    if (moves != null)
      moves.clear();
    if (playedMoves != null)
      playedMoves.clear();
    points.reset();
    rollBtn.remove();
    if (MatchState.fCubeUse == 1)
      doubleBtn.remove();
  }

  public void animate(float t) {
    int i0 = 0;
    int i1 = 0;

    for (int i = 0; i <= 24; i++) {
      _board[0][i] = MatchState.board[0][i];
      i0 += MatchState.board[0][i];
      _board[1][i] = MatchState.board[1][i];
      i1 += MatchState.board[1][i];
    }
    bearedOff[0] = 0;
    bearedOff[1] = 0;

    int nchecker = 0;
    // POSITIONING BLACK CHECKERS
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < _board[0][i]; j++) {
        checkers[0][nchecker].reset(i, j, t);
        nchecker++;
      }
    }
    for (int i = 0; i < 15 - i0; i++) {
      checkers[0][nchecker].reset(-1, i, t);
      bearedOff[0]++;
      nchecker++;
    }

    // POSITIONING WHITE CHECKERS
    nchecker = 0;
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < _board[1][i]; j++) {
        checkers[1][nchecker].reset(i, j, t);
        nchecker++;
      }
    }
    for (int i = 0; i < 15 - i1; i++) {
      checkers[1][nchecker].reset(-1, i, t);
      bearedOff[1]++;
      nchecker++;
    }
  }

  public void updatePInfo() {
    GnuBackgammon.Instance.gameScreen.updatePInfo();
  }

  @Override
  public float getHeight() {
    return boardbg.getHeight();
  }

  @Override
  public float getWidth() {
    return boardbg.getWidth();
  }

  @Override
  public void setWidth(float width) {
    setScaleX(width / boardbg.getWidth());
  }

  @Override
  public void setHeight(float height) {
    setScaleY(height / boardbg.getHeight());
  }

  public void thinking(boolean v) {
    thinking.setVisible(v);
    Gdx.graphics.requestRendering();
    if (v)
      Gdx.graphics.setContinuousRendering(true);
    else
      Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }

  public void waiting(boolean v) {
    waiting.setVisible(v);
    Gdx.graphics.requestRendering();
    if (v)
      Gdx.graphics.setContinuousRendering(true);
    else
      Gdx.graphics.setContinuousRendering(false);
    Gdx.graphics.requestRendering();
  }

  public int gameScore(int loser) {
    if ((bearedOff[0] != 15) && (bearedOff[1] != 15))
      return 1;// CUBE NOT ACCEPTED
    if (bearedOff[loser] > 0)
      return 1;
    else {
      boolean backgammon = false;
      for (int i = 18; i < 25; i++) {
        backgammon |= (_board[loser][i] > 0);
      }// if count = 0 here, we're in bear off
      if (backgammon)
        return 3;
      else
        return 2;
    }
  }

  public void doubleCube() {
    doublingCube.setValue(MatchState.nCube);
  }

  public void rollDices() {
    GnuBackgammon.Instance.snd.playRoll();
    dices.clear();
    int[] ds = AICalls.Locking.RollDice();

    // SHOW ALWAYS BIGGER DICE ON LEFT
    if (ds[0] >= ds[1])
      dices.animate(ds[0], ds[1]);
    else
      dices.animate(ds[1], ds[0]);
  }

  public void rollDices(int d1, int d2) {
    dices.clear();
    // SHOW ALWAYS BIGGER DICE ON LEFT
    if (d1 > d2)
      dices.show(d1, d2, false);
    else
      dices.show(d2, d1, false);
  }

  public void animateDices(int d1, int d2) {
    dices.clear();
    dices.animate(d1, d2, false);
  }
  public void animateDices(int d1, int d2, boolean evt) {
    dices.clear();
    dices.animate(d1, d2, true);
  }

  public void showArrow() {
    if (GnuBackgammon.Instance.appearancePrefs.getString("NPOINTS", "Yes").equals("No")) {
      larrow.setVisible(false);
      rarrow.setVisible(false);
      showNumbers();
      return;
    }

    Vector2 p = getBoardCoord(MatchState.fMove, -1, 0);
    if (MatchState.fMove == 0)
      p.y -= larrow.getHeight() * 2.8f / 2;
    else
      p.y += 2 * GnuBackgammon.Instance.jp.asFloat("pos", 0) + larrow.getHeight() * 3f / 2;
    p.x -= larrow.getWidth() / 2;
    if (GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise").equals("AntiClockwise")) {
      // RARROW
      larrow.setVisible(false);
      rarrow.setVisible(true);
      rarrow.setPosition(p.x, p.y);
    } else {
      // LARROW
      larrow.setVisible(true);
      rarrow.setVisible(false);
      larrow.setPosition(p.x, p.y);
    }

    showNumbers();
  }

  public void showNumbers() {
    if (GnuBackgammon.Instance.appearancePrefs.getString("NPOINTS", "Yes").equals("No")) {
      for (int i = 0; i < 24; i++)
        ns[i].remove();
      return;
    }
    for (int i = 0; i < 24; i++) {
      ns[i].setX(pos[i].x - ns[i].getWidth() / 2);
    }
    float top, bottom;
    if (GnuBackgammon.Instance.ss == 0) { // HDPI
      bottom = ns[23].getHeight() * 0.65f;
      top = getHeight() - ns[23].getHeight() * 1.6f;
    } else if (GnuBackgammon.Instance.ss == 1) { // MDPI
      bottom = ns[23].getHeight() * 0.5f;
      top = getHeight() - ns[23].getHeight() * 1.4f;
    } else { // LDPI
      bottom = ns[23].getHeight() * 0.4f;
      top = getHeight() - ns[23].getHeight() * 1.3f;
    }

    for (int i = 0; i < 12; i++) {
      if (MatchState.fMove == 0)
        ns[i].setY(bottom); // BOTTOM
      else
        ns[i].setY(top); // TOP
      addActor(ns[i]);
    }

    for (int i = 12; i < 24; i++) {
      if (MatchState.fMove == 0)
        ns[i].setY(top); // TOP
      else
        ns[i].setY(bottom); // BOTTOM
      addActor(ns[i]);
    }

  }

  public void setCube(int v, int o) {
    doublingCube.setValue(v, o);
  }

  public void stopCheckers() {
    for (int i = 0; i < 15; i++) {
      checkers[0][i].resetActions();
      checkers[1][i].resetActions();
    }
  }

  public String getBoardAsString(int c) {
    String s = "";
    for (int i = 0; i < 24; i++) {
      s += _board[c][i] + ":";
    }
    return s + _board[c][24];
  }
} // END CLASS
