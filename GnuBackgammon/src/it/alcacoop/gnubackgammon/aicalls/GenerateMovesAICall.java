package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.layers.Board;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import it.alcacoop.gnubackgammon.logic.MatchState;

public class GenerateMovesAICall implements Runnable {

  int d1, d2; 
  Board b;
  
  public GenerateMovesAICall(Board _b, int _d1, int _d2) {
    d1 = _d1;
    d2 = _d2;
    b = _b;
  }

  @Override
  public void run() {
    
    int mv[] = new int[8];
    int r = 0;
    
    if (MatchState.fMove==1)
      r = GnubgAPI.GenerateMoves(b._board[0], b._board[1], d1, d2, mv);
    else 
      r = GnubgAPI.GenerateMoves(b._board[1], b._board[0], d1, d2, mv);
    
    final int res[] = new int[9];
    res[0] = r;
    for (int i=0;i<8;i++) 
      res[i+1] = mv[i];
    
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(FSM.Events.GENERATE_MOVES, res);        
      }
    });
  }
}