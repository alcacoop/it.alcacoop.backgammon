package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class EvaluateBestMoveAICall implements Runnable {

  int dices[];
  int moves[] = {0,0,0,0,0,0,0,0};
  BaseFSM fsm;
  
  public EvaluateBestMoveAICall(int _dices[]) {
    dices = _dices;
    fsm = GnuBackgammon.fsm;
  }
  
  @Override
  public void run() {
    GnubgAPI.EvaluateBestMove(dices, moves);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.EVALUATE_BEST_MOVE, moves);
      }
    });
  }
}