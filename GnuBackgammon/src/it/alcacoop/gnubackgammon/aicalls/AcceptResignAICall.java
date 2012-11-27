package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptResignAICall implements Runnable {

  int r;
  
  public AcceptResignAICall(int _r) {
    r = _r;
  }

  @Override
  public void run() {
    final int ret = GnubgAPI.AcceptResign(r);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.ACCEPT_RESIGN, ret);
      }
    });
  }

}