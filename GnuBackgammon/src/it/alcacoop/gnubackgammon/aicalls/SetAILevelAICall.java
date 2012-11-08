package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetAILevelAICall implements Runnable {

  private int level;
  
  public SetAILevelAICall(int l) {
    level = l;
  }

  @Override
  public void run() {
    GnubgAPI.SetAILevel(level);
  }

}
