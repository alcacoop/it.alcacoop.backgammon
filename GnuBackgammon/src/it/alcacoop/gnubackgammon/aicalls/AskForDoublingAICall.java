package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AskForDoublingAICall implements Runnable {


  @Override
  public void run() {
    final int ret = GnubgAPI.AskForDoubling();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(FSM.Events.ASK_FOR_DOUBLING, ret);
      }
    });
  }

}
