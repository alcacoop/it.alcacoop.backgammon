package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchScoreAICall implements Runnable {

  int AIScore; 
  int HumanScore;
  
  public SetMatchScoreAICall(int _AIScore, int _HumanScore) {
    AIScore = _AIScore;
    HumanScore = _HumanScore;
  }

  @Override
  public void run() {
    GnubgAPI.SetMatchScore(AIScore, HumanScore);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(GameFSM.Events.SET_MATCH_SCORE, 1);
      }
    });
  }
}