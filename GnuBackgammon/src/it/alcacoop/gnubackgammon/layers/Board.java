package it.alcacoop.gnubackgammon.layers;

import java.util.Random;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.actors.BoardImage;
import it.alcacoop.gnubackgammon.actors.Checker;
import it.alcacoop.gnubackgammon.logic.FSM.Events;
import it.alcacoop.gnubackgammon.logic.MatchState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import java.util.Stack;



public class Board extends Group {
  
  public int[][] _board;
  public int[] bearedOff = {0,0};
  public Stack<int[]> moves;

  private Vector2 pos[];
  private BoardImage bimg;
  private Checker checkers[][];
  private Checker lastMoved = null;

  
  public Board() {
    _board = new int[2][25];

    moves = new Stack<int[]>();
    bimg = new BoardImage();
    bimg.setX(30);
    bimg.setY(20);
    addActor(bimg);

    checkers = new Checker[2][15]; //[0]=WHITE [1]=BLACK

    for (int i = 0; i<15; i++) {
      checkers[0][i] = new Checker(this, 0);
      checkers[1][i] = new Checker(this, 1);
      
      addActor(checkers[0][i]);
      addActor(checkers[1][i]);
    }

    pos = new Vector2[25];
    for (int i=0; i<24;i++) {
      pos[i] = new Vector2();
      if (i<6) {
        pos[i].x = bimg.getX()+(836-(59*i));
        pos[i].y = bimg.getY()+590;
      }
      if ((i>=6)&&(i<12)) {
        pos[i].x = bimg.getX()+(765-(59*i));
        pos[i].y = bimg.getY()+590;
      }
    }
    for (int i=0; i<12;i++) {
      pos[i+12] = new Vector2();
      if (i<6) {
        pos[i+12].x = bimg.getX()+(116+(59*i));
        pos[i+12].y = bimg.getY()+50;
      }
      if ((i>=6)&&(i<12)) {
        pos[i+12].x = bimg.getX()+(187+(59*i));
        pos[i+12].y = bimg.getY()+50;
      }
    }

    pos[24] = new Vector2();
    pos[24].x = 506;
    pos[24].y = 0;
  }


  public Vector2 getBoardCoord(int color, int x, int y){
    if (y>4) y=4;
    Vector2 ret = new Vector2();

    switch (x) {
    
      case -1: //BEAR OFF
        ret.x = 948.5f; 
        if (color==1) ret.y = 625-bearedOff[color]*14;
        else ret.y = 55+bearedOff[color]*14;
        break;
        
      case 24: //BAR
        ret.x = pos[x].x;
        if (color==0) ret.y=590 - (49*y);
        else ret.y=90 + (49*y);
        break;
        
      default: //ON THE TABLE
        if (color==0) { //WHITE
          ret.x = pos[23-x].x;
          if (x>11) ret.y = pos[23-x].y - (49*y);
          else ret.y = pos[23-x].y + (49*y);
        } else { //BLACK
          ret.x = pos[x].x;
          if (x>11) ret.y = pos[x].y + (49*y);
          else ret.y = pos[x].y - (49*y);
        }
    }
    return ret;
  }


  public void initBoard() {
    _board[0] = MatchState.board[0];
    _board[1] = MatchState.board[1];
    bearedOff[0] = 0;
    bearedOff[1] = 0;

    int nchecker = 0;
    for (int i=24; i>=0; i--) {
      for (int j=0;j<_board[0][i];j++) {
        Vector2 _p = getBoardCoord(0, i, j);
        checkers[0][nchecker].boardX = i;
        checkers[0][nchecker].boardY = j;
        checkers[0][nchecker].setX(_p.x);
        checkers[0][nchecker].setY(_p.y);
        nchecker++;
      }
    }
    //POSITIONING BLACK CHECKERS
    nchecker = 0;
    for (int i=24; i>=0; i--) {
      for (int j=0;j<_board[1][i];j++) {
        Vector2 _p = getBoardCoord(1, i, j);
        checkers[1][nchecker].boardX = i;
        checkers[1][nchecker].boardY = j;
        checkers[1][nchecker].setX(_p.x);
        checkers[1][nchecker].setY(_p.y);
        nchecker++;
      }
    }
  }


  Checker getChecker(int color, int x) {
    Checker _c = null;
    int y = _board[color][x]-1;
    for (int i = 0; i<15; i++) {
      Checker c = checkers[color][i];
      if ((c.boardX==x)&&(c.boardY==y))
        _c = c;
    }
    return _c;
  }


  public void printBoard(){
    for (int i=0; i<2; i++) {
      String s = "";
      for (int j=0; j<25; j++) {
        s+=" "+_board[i][j];
      }
      Gdx.app.log("BOARD"+i, s);
    }
  }


  public void setMoves(int _moves[]) {
    moves.clear();
    Gdx.app.log("MOVE: ",
        _moves[0]+"/"+_moves[1]+" "+_moves[2]+"/"+_moves[3]+
        "    "+_moves[4]+"/"+_moves[5]+" "+_moves[6]+"/"+_moves[7]);
    if (_moves.length<8) return;

    int m1[] = new int[2];
    int m2[] = new int[2];
    int m3[] = new int[2];
    int m4[] = new int[2];

    if (_moves[6]!=-1) {
      m4[0] = _moves[6];
      m4[1] = _moves[7];
      moves.push(m4);
    }

    if (_moves[4]!=-1) {
      m3[0] = _moves[4];
      m3[1] = _moves[5];
      moves.push(m3);
    }

    if (_moves[2]!=-1) {
      m2[0] = _moves[2];
      m2[1] = _moves[3];
      moves.push(m2);
    }

    if (_moves[0]!=-1) {
      m1[0] = _moves[0];
      m1[1] = _moves[1];
      moves.push(m1);
    }
    performNextMove();
  }


  public void performNextMove() {
    if (checkHit()==1) return;
    
    try {
      synchronized (moves) {
        int m[] = moves.pop();
        if (m!=null) {
          Checker c = getChecker(MatchState.fMove, m[0]);
          c.moveToDelayed(m[1], 0.2f);
          lastMoved = c;
        }  
      }
    } catch (Exception e) {
      GameScreen.fsm.processEvent(Events.NO_MORE_MOVES, null);
    }
    
  }

  
  public int checkHit() {
    if (lastMoved!=null) { 
      int c = lastMoved.getSpecularColor();
      int p = lastMoved.getSpecularPosition();
      if (_board[c][p]>0) {
        //CHECKER HITTED
        Checker ch = getChecker(c, p);
        ch.moveTo(24);
        return 1;
      }
    }
    return 0;
  }

  
  public void animate() {
    _board[0] = MatchState.board[4];
    _board[1] = MatchState.board[5];

    Random rnd = new Random();

    for (int i = 0; i<15; i++) {
      //RANDOM POS FOR WHITE CHECKERS
      int x = rnd.nextInt(260) + 570;
      int y = rnd.nextInt(70) + 290;
      checkers[0][i].setX(x);
      checkers[0][i].setY(y);

      //RANDOM POS FOR BLACK CHECKERS
      x = rnd.nextInt(260) + 160;
      y = rnd.nextInt(70) + 290;
      checkers[1][i].setX(x);
      checkers[1][i].setY(y);
    }

    //POSITIONING WHITE CHECKERS
    int nchecker = 0;
    for (int i=24; i>=0; i--) {
      for (int j=0;j<_board[0][i];j++) {
        Vector2 _p = getBoardCoord(0, i, j);
        checkers[0][nchecker].addAction(
            Actions.sequence(Actions.delay(1.5f*(nchecker+3)/4), Actions.moveTo(_p.x, _p.y, 0.2f))
        );
        checkers[0][nchecker].boardX = i;
        checkers[0][nchecker].boardY = j;
        nchecker++;
      }
    }
    //POSITIONING BLACK CHECKERS
    nchecker = 0;
    for (int i=24; i>=0; i--) {
      for (int j=0;j<_board[1][i];j++) {
        Vector2 _p = getBoardCoord(1, i, j);
        if (nchecker==14) {
          checkers[1][nchecker].addAction(
              Actions.sequence(
              Actions.delay(1.5f*(nchecker+18)/4),
              Actions.moveTo(_p.x, _p.y, 0.2f),
              Actions.delay(1.5f),
              new Action(){
                @Override
                public boolean act(float delta) {
                  //END ANIMATION
                  animate();
                  return true;
                }}
          ));
        } else {
          checkers[1][nchecker].addAction(
              Actions.sequence(Actions.delay(1.5f*(nchecker+18)/4), Actions.moveTo(_p.x, _p.y, 0.2f)));
        }
        checkers[1][nchecker].boardX = i;
        checkers[1][nchecker].boardY = j;
        nchecker++;
      }
    }
  }


} //END CLASS