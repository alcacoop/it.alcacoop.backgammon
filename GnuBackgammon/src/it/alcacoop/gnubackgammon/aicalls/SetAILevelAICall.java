package it.alcacoop.gnubackgammon.aicalls;


import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetAILevelAICall implements Runnable {

  private int level;
  
  public SetAILevelAICall(AILevels l) {
    level = l.ordinal();
  }

  @Override
  public void run() {
    GnubgAPI.SetAILevel(level);
  }
}