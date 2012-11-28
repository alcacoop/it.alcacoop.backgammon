package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AskForResignationAICall implements Runnable {

  BaseFSM fsm;

  public AskForResignationAICall() {
    fsm = GnuBackgammon.fsm;
  }
  
  @Override
  public void run() {
    final int ret = GnubgAPI.AskForResignation();
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.ASK_FOR_RESIGNATION, ret); 
      }
    });
  }

}
