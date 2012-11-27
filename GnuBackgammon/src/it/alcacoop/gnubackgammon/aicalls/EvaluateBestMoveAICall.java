package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.logic.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class EvaluateBestMoveAICall implements Runnable {

  int dices[];
  int moves[] = {0,0,0,0,0,0,0,0};
  
  public EvaluateBestMoveAICall(int _dices[]) {
    dices = _dices;
  }
  
  @Override
  public void run() {
    GnubgAPI.EvaluateBestMove(dices, moves);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.EVALUATE_BEST_MOVE, moves);
      }
    });
  }
}