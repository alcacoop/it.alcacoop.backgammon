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
  public void addRoll(int player, int[] dices) {
    int curLev = MatchState.currentLevel.ordinal();
    System.out.println("ROLLED: " + dices[0] + "/" + dices[1] + " BY " + player);

    // AVG_PIPS
    int pips = dices[0] == dices[1] ? dices[0] * 4 : dices[0] + dices[1];
    stats[curLev].dices.AVG_PIPS[player] =
        ((stats[curLev].dices.AVG_PIPS[player] * (float)stats[curLev].dices.ROLLS[player]) + (float)pips) / (stats[curLev].dices.ROLLS[player] + 1);

    // ROLLS
    stats[curLev].dices.ROLLS[player]++;
    stats[8].dices.ROLLS[player]++;

    // DOUBLES
    if (dices[0] == dices[1]) {
      stats[curLev].dices.DOUBLES[player]++;
      stats[8].dices.DOUBLES[player]++;
    }


    commit();
    System.out.println(" AVG_PIPS: " + stats[curLev].dices.AVG_PIPS[player] + " FOR " + player);
  }

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
  public void resetRollStats() {
    for (int i = 0; i < 9; i++) {
      stats[i].dices = stats[i].new Dices();
      prefs.putString("STAT_" + i, stats[i].serialize()); // CURRENT LEVEL
    }
    prefs.flush();
  }

  // RESET GAME STATISTICS
  public void resetGameStats() {
    for (int i = 0; i < 9; i++) {
      stats[i].general = stats[i].new General();
      prefs.putString("STAT_" + i, stats[i].serialize()); // CURRENT LEVEL
    }
    prefs.flush();
  }


  public String getRollStat(int statType, int level, int who) {
    String ret = "";
    switch (statType) {
      case 0: // # ROLLS
        ret = stats[level].dices.ROLLS[who] + "";
        break;
      case 1: // DOUBLES
        ret = round2((float)(((float)stats[level].dices.DOUBLES[who] / (float)stats[level].dices.ROLLS[who]) * 100.00)) + "%";
        break;
      case 2: // AVG_PIPS
        ret = round2(stats[level].dices.AVG_PIPS[who]) + "";
        break;
      case 3: // ENTER FROM BAR
        ret = 0.0 + "%";
        break;

      case 4: // 1 DOUBLES IN A ROW
      case 5: // 2 DOUBLES IN A ROW
      case 6: // 3 DOUBLES IN A ROW
      case 7: // 4 DOUBLES IN A ROW
        ret = 0.0 + "%";
        break;

      case 8: // ENTER AGAINST 1P
      case 9: // ENTER AGAINST 2P
      case 10: // ENTER AGAINST 3P
      case 11: // ENTER AGAINST 4P
      case 12: // ENTER AGAINST 5P
        ret = 0.0 + "%";
        break;
    }
    return ret;
  }


  private float round2(float n) {
    if (Float.isNaN(n))
      return 0;
    return (float)Math.round(n * 100) / 100;
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
