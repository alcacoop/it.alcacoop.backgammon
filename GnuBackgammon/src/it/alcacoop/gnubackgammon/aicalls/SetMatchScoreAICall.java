package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchScoreAICall implements Runnable {

  int AIScore; 
  int HumanScore;
  BaseFSM fsm;
  
  public SetMatchScoreAICall(int _AIScore, int _HumanScore) {
    AIScore = _AIScore;
    HumanScore = _HumanScore;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    GnubgAPI.SetMatchScore(AIScore, HumanScore);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_MATCH_SCORE, 1);
      }
    });
  }
}