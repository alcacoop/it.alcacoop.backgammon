package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AcceptResignAICall implements Runnable {

  int r;
  
  public AcceptResignAICall(int _r) {
    r = _r;
  }

  @Override
  public void run() {
    GnubgAPI.AcceptResign(r);
  }

}
