package it.alcacoop.backgammon.stats;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.Preferences;

public class StatManager {

  private Statistics stats[];
  private static StatManager instance = null;
  private Preferences prefs;

  private StatManager() {
    prefs = GnuBackgammon.Instance.optionPrefs;
    stats = new Statistics[9];

    for (int i = 0; i < 9; i++) {
      String strStat = prefs.getString("STAT_" + i, "");
      if (strStat == "") {
        stats[i] = new Statistics();
        prefs.putString("STAT_" + i, stats[i].serialize());
      } else {
        stats[i] = Statistics.deserialize(strStat);
      }
    }
  }

  public static StatManager getInstance() {
    if (instance == null) {
      instance = new StatManager();
    }
    return instance;
  }

  // ADD ROLL TO STATS (CURRENT AI LEVEL AND TOTAL)
  public void addRoll() {}

  // ADD GAME RESULT TO STATS (CURRENT AI LEVEL AND TOTAL)
  public void addGame(int winner) {
    int curLev = MatchState.currentLevel.ordinal();
    if (winner == 0) { // HUMAN WINS
      stats[curLev].general.HUMAN++;
      stats[8].general.HUMAN++;
    } else { // CPU WINS
      stats[curLev].general.CPU++;
      stats[8].general.CPU++;
    }
    commit();
  }


  // RESET ROLL STATISTICS
  public void resetRollStats() {}

  // RESET GAME STATISTICS
  public void resetGameStats() {
    for (int i = 0; i < 9; i++) {
      stats[i].general.CPU = 0;
      stats[i].general.HUMAN = 0;
      prefs.putString("STAT_" + i, stats[i].serialize()); // CURRENT LEVEL
      prefs.flush();
    }
  }


  public int getGameStat(int level, int who) {
    if (who == 0) { // HUMAN
      return stats[level].general.HUMAN;
    } else { // CPU
      return stats[level].general.CPU;
    }
  }

  // SAVE STATS ON PREFS AND SYNC ON GMS
  public void commit() {
    int curLev = MatchState.currentLevel.ordinal();
    prefs.putString("STAT_" + curLev, stats[curLev].serialize()); // CURRENT LEVEL
    prefs.putString("STAT_8", stats[8].serialize()); // TOTALS
    prefs.flush();
  }

}
