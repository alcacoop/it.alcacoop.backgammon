package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchToAICall implements Runnable {

  int nMatchTo; 
  
  public SetMatchToAICall(int _nMatchTo) {
    nMatchTo = _nMatchTo;
  }

  @Override
  public void run() {
    GnubgAPI.SetMatchTo(nMatchTo);
  }

}
