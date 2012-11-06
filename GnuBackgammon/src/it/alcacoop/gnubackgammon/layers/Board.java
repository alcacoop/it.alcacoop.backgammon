package it.alcacoop.gnubackgammon.layers;

import java.util.Random;
import it.alcacoop.gnubackgammon.actors.BoardImage;
import it.alcacoop.gnubackgammon.actors.Checker;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.MoveTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;


public class Board extends Group {

  public int[][] _board = {
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5},//WHITE (HUMAN) 
    {0, 0, 0, 0, 0, 5, 0, 1, 0, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 2}
  };

  Vector2 pos[];
  BoardImage bimg;
  Checker checkers[][];

  public void printBoard(){
    for (int i=0; i<2; i++) {
      String s = "";
      for (int j=0; j<25; j++) {
        s+=" "+_board[i][j];
      }
      Gdx.app.log("BOARD"+i, s);
    }
  }

  public Board() {
    bimg = new BoardImage();
    bimg.x = 30;
    bimg.y = 20;
    addActor(bimg);

    
    checkers = new Checker[2][15];
    Random rnd = new Random();

    for (int i = 0; i<15; i++) {
      checkers[0][i] = new Checker(this, 0);
      checkers[1][i] = new Checker(this, 1);

      //RANDOM POS FOR WHITE CHECKERS
      int x = rnd.nextInt(260) + 570;
      int y = rnd.nextInt(70) + 290;
      checkers[0][i].x = x;
      checkers[0][i].y = y;
      addActor(checkers[0][i]);

      //RANDOM POS FOR BLACK CHECKERS
      x = rnd.nextInt(260) + 160;
      y = rnd.nextInt(70) + 290;
      checkers[1][i].x = x;
      checkers[1][i].y = y;
      addActor(checkers[1][i]);
    }

    pos = new Vector2[25];
    
    for (int i=0; i<24;i++) {
      pos[i] = new Vector2();
      if (i<6) {
        pos[i].x = bimg.x+(836-(59*i));
        pos[i].y = bimg.y+590;
      }
      if ((i>=6)&&(i<12)) {
        pos[i].x = bimg.x+(765-(59*i));
        pos[i].y = bimg.y+590;
      }
    }

    for (int i=0; i<12;i++) {
      pos[i+12] = new Vector2();
      if (i<6) {
        pos[i+12].x = bimg.x+(116+(59*i));
        pos[i+12].y = bimg.y+50;
      }
      if ((i>=6)&&(i<12)) {
        pos[i+12].x = bimg.x+(187+(59*i));
        pos[i+12].y = bimg.y+50;
      }
    }
    
    pos[24] = new Vector2();
    pos[24].x = 506;
    pos[24].y = 0;
  }


  public Vector2 getBoardCoord(int color, int x, int y){
    if (y>4) y=4;
    Vector2 ret = new Vector2();
    
    if (x==24) {
      ret.x = pos[x].x;
      if (color==0) ret.y=590 - (49*y);
      else ret.y=90 + (49*y);
    } else {
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
    //POSITIONING WHITE CHECKERS
    int nchecker = 0;
    for (int i=24; i>=0; i--) {
      for (int j=0;j<_board[0][i];j++) {
        Vector2 _p = getBoardCoord(0, i, j);
        checkers[0][nchecker].action(Sequence.$(Delay.$(1.5f*(nchecker+3)/4), MoveTo.$(_p.x, _p.y, 0.2f)));
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
        checkers[1][nchecker].action(Sequence.$(Delay.$(1.5f*(nchecker+18)/4), MoveTo.$(_p.x, _p.y, 0.2f)));
        checkers[1][nchecker].boardX = i;
        checkers[1][nchecker].boardY = j;
        nchecker++;
      }
    }

    Checker c = getChecker(1, 17);
    c.moveToDelayed(12, 15);
    //c.moveToDelayed(10, 13);
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

  
  public void performNextMove(){
    Checker c = getChecker(1, 12);
    if (c!=null)
      c.moveToDelayed(10, 0.2f);
  }

} //END CLASS
