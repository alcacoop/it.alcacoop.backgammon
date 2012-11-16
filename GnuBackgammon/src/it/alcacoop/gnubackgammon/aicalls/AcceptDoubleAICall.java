package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptDoubleAICall implements Runnable {

  @Override
  public void run() {
    final int ret = GnubgAPI.AcceptDouble();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(FSM.Events.ACCEPT_DOUBLE, ret);
      }
    });
  }
}
