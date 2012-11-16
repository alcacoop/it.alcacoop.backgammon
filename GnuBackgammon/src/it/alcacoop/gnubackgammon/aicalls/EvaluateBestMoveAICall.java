package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
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
        GameScreen.fsm.processEvent(FSM.Events.EVALUATE_BEST_MOVE, moves);
      }
    });
  }
}