package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.GameFSM;
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
        GameScreen.fsm.processEvent(GameFSM.Events.ACCEPT_RESIGN, ret);
      }
    });
  }

}