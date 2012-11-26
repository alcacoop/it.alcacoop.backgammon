package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetAILevelAICall implements Runnable {

  private int level;
  
  public SetAILevelAICall(AILevels l) {
    level = l.ordinal();
  }

  @Override
  public void run() {
    GnubgAPI.SetAILevel(level);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(GameFSM.Events.SET_AI_LEVEL, 1);        
      }
    });
  }
}