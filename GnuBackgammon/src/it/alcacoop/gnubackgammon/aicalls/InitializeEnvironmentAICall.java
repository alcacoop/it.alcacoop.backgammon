package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class InitializeEnvironmentAICall implements Runnable {

  String str;
  
  public InitializeEnvironmentAICall(String _str) {
    this.str = _str;
  }

  @Override
  public void run() {
    GnubgAPI.InitializeEnvironment(str);
  }

}
