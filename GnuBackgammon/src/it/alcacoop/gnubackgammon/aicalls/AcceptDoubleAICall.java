package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptDoubleAICall implements Runnable {

  @Override
  public void run() {
    GnubgAPI.AcceptDouble();
  }

}
