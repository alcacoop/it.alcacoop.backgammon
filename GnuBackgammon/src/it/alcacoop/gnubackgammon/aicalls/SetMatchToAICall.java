package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchToAICall implements Runnable {

  int nMatchTo;
  BaseFSM fsm;
  
  public SetMatchToAICall(int _nMatchTo) {
    nMatchTo = _nMatchTo;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    GnubgAPI.SetMatchTo(nMatchTo);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_MATCH_TO, 1);        
      }
    });
  }
}