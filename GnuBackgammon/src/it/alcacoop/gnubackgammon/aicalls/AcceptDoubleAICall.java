package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.GameScreen;
import it.alcacoop.gnubackgammon.logic.FSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptDoubleAICall implements Runnable {

  @Override
  public void run() {
    int ret = GnubgAPI.AcceptDouble();
    GameScreen.fsm.processEvent(FSM.Events.ACCEPT_DOUBLE, ret);
  }

}
