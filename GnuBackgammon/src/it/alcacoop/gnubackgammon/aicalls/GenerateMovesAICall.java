package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.GameFSM;
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
    final int mv[][];
    
    if (MatchState.fMove==1)
      mv = GnubgAPI.GenerateMoves(b._board[0], b._board[1], d1, d2);
    else 
      mv = GnubgAPI.GenerateMoves(b._board[1], b._board[0], d1, d2);
    
    
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(GameFSM.Events.GENERATE_MOVES, mv);        
      }
    });
  }
  
}