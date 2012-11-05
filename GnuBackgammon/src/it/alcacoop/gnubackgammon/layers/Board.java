package it.alcacoop.gnubackgammon.layers;

import it.alcacoop.gnubackgammon.actors.BoardImage;
import it.alcacoop.gnubackgammon.actors.Checker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.MoveTo;

public class Board extends Group {
	
	private int[][] _board = {
		{0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0}, 
       	{0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0}
	};
	
	Vector2 pos[];
	BoardImage bimg;
	Checker wCheckers[];
	Checker bCheckers[];
	
	
	public Board() {
		bimg = new BoardImage();
		bimg.x = 30;
		bimg.y = 20;
		addActor(bimg);
		
		wCheckers = new Checker[15];
		bCheckers = new Checker[15];
		for (int i = 0; i<15; i++) {
			wCheckers[i] = new Checker(0);
			bCheckers[i] = new Checker(1);
		}
		
		pos = new Vector2[24];
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
		
		initBoard();
		//this.action(Delay.$(MoveTo.$(45, 40, 1f), 1f));
	}
	
	
	public Vector2 getBoardCoord(int color, int x, int y){
		Vector2 ret = new Vector2();
		if (color==0) { //WHITE
			ret.x = pos[23-x].x;
			if (x>11) ret.y = pos[23-x].y - (49*y);
			else ret.y = pos[23-x].y + (49*y);
		} else { //BLACK
			ret.x = pos[x].x;
			if (x>11) ret.y = pos[x].y + (49*y);
			else ret.y = pos[x].y - (49*y);
		}
		return ret;
	}
	
	
	
	public void initBoard() {
		int nchecker = 0;
		//WHITE CHECKERS
		for (int i=23; i>=0; i--) {
			for (int j=0;j<_board[0][i];j++) {
				Vector2 _p = getBoardCoord(0, i, j);
				wCheckers[nchecker].x = _p.x;
				wCheckers[nchecker].y = _p.y;
				addActor(wCheckers[nchecker]);
				nchecker++;
			}
		}
		
		nchecker = 0;
		//BLACK CHECKERS
		for (int i=23; i>=0; i--) {
			for (int j=0;j<_board[0][i];j++) {
				Vector2 _p = getBoardCoord(1, i, j);
				bCheckers[nchecker].x = _p.x;
				bCheckers[nchecker].y = _p.y;
				addActor(bCheckers[nchecker]);
				nchecker++;
			}
		}
	}
}
