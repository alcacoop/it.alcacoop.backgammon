package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetAILevelAICall implements Runnable {

  private int level;
  
  public SetAILevelAICall(int l) {
    level = l;
  }

  @Override
  public void run() {
    GnubgAPI.SetAILevel(level);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(FSM.Events.SET_AI_LEVEL, 1);        
      }
    });
  }
}