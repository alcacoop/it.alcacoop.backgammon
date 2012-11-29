package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class RollDiceAICall implements Runnable {

  int dices[] = {0,0};
  BaseFSM fsm;
  
  public RollDiceAICall() {
    fsm = GnuBackgammon.fsm;
  }
  
  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    GnubgAPI.RollDice(dices);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.ROLL_DICE, dices);    
      }
    });
  }

}
