package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AskForResignationAICall implements Runnable {


  @Override
  public void run() {
    final int ret = GnubgAPI.AskForResignation();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(FSM.Events.ASK_FOR_RESIGNATION, ret); 
      }
    });
  }

}
