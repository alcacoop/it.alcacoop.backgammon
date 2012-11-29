package it.alcacoop.gnubackgammon.aicalls;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetAILevelAICall implements Runnable {

  private int level;
  BaseFSM fsm;
  
  public SetAILevelAICall(AILevels l) {
    level = l.ordinal();
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    if (fsm != GnuBackgammon.fsm) return;
    GnubgAPI.SetAILevel(level);
  }
}