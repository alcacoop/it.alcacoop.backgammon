package it.alcacoop.gnubackgammon.layers;

import java.util.Random;

import it.alcacoop.gnubackgammon.actors.BoardImage;
import it.alcacoop.gnubackgammon.actors.Checker;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

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
		Random rnd = new Random();
		
		for (int i = 0; i<15; i++) {
			wCheckers[i] = new Checker(this, 0);
			bCheckers[i] = new Checker(this, 1);
			
			//RANDOM POS FOR WHITE CHECKERS
			int x = rnd.nextInt(260) + 570;
			int y = rnd.nextInt(70) + 290;
			wCheckers[i].x = x;
			wCheckers[i].y = y;
			addActor(wCheckers[i]);

			//RANDOM POS FOR BLACK CHECKERS
			x = rnd.nextInt(260) + 160;
			y = rnd.nextInt(70) + 290;
			bCheckers[i].x = x;
			bCheckers[i].y = y;
			addActor(bCheckers[i]);
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
		//POSITIONING WHITE CHECKERS
		int nchecker = 0;
		for (int i=23; i>=0; i--) {
			for (int j=0;j<_board[0][i];j++) {
				wCheckers[nchecker].setPositionDelayed(i, j, 1.5f*nchecker/4);
				nchecker++;
			}
		}
		//POSITIONING BLACK CHECKERS
		nchecker = 0;
		for (int i=23; i>=0; i--) {
			for (int j=0;j<_board[1][i];j++) {
				bCheckers[nchecker].setPositionDelayed(i, j, 1.5f*(nchecker+15)/4);
				nchecker++;
			}
		}
		
		wCheckers[1].setPositionDelayed(13, 0, 15);
	}


} //END CLASS
