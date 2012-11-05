package it.alcacoop.gnubackgammon.layers;

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
	}
	
	public void initBoard() {
		int nchecker = 0;
		
		//WHITE CHECKERS
		for (int i=23; i>=0; i--) {
			Vector2 _p = pos[23-i];
			
			for (int j=0;j<_board[0][i];j++) {
				wCheckers[nchecker].x = _p.x;
				
				if (i>11) wCheckers[nchecker].y = _p.y - (49*j);
				else wCheckers[nchecker].y = _p.y + (49*j);
				addActor(wCheckers[nchecker]);
				nchecker++;
			}
		}
		
		//BLACK CHECKERS
		nchecker = 0;
		for (int i=23; i>=0; i--) {
			Vector2 _p = pos[i];

			for (int j=0;j<_board[0][i];j++) {
				bCheckers[nchecker].x = _p.x;

				if (i>11) bCheckers[nchecker].y = _p.y + (49*j);
				else bCheckers[nchecker].y = _p.y - (49*j);
				addActor(bCheckers[nchecker]);
				nchecker++;
			}
		}
	}
}
