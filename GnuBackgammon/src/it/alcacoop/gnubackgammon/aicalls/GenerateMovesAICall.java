package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import it.alcacoop.gnubackgammon.logic.MatchState;

public class GenerateMovesAICall implements Runnable {

  int d1, d2; 
  Board b;
  BaseFSM fsm;
  
  public GenerateMovesAICall(Board _b, int _d1, int _d2) {
    d1 = _d1;
    d2 = _d2;
    b = _b;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    final int mv[][];
    
    if (MatchState.fMove==1)
      mv = GnubgAPI.GenerateMoves(b._board[0], b._board[1], d1, d2);
    else 
      mv = GnubgAPI.GenerateMoves(b._board[1], b._board[0], d1, d2);
    
    
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.GENERATE_MOVES, mv);        
      }
    });
  }
  
}