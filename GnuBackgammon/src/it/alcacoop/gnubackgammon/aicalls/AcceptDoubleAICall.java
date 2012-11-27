package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptDoubleAICall implements Runnable {

  @Override
  public void run() {
    final int ret = GnubgAPI.AcceptDouble();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.ACCEPT_DOUBLE, ret);
      }
    });
  }
}
