package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class RollDiceAICall implements Runnable {

  int dices[] = {0,0};
  
  @Override
  public void run() {
    GnubgAPI.RollDice(dices);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.ROLL_DICE, dices);    
      }
    });
  }

}
