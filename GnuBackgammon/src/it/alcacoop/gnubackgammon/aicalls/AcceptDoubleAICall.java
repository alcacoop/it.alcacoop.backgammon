package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptDoubleAICall implements Runnable {

  BaseFSM fsm;
  
  public AcceptDoubleAICall() {
    fsm = GnuBackgammon.fsm;
  }
  
  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    final int ret = GnubgAPI.AcceptDouble();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.ACCEPT_DOUBLE, ret);
      }
    });
  }
}
