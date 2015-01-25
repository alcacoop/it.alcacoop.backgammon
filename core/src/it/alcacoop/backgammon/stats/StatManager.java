package it.alcacoop.backgammon.stats;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;

import java.util.Arrays;

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
      try {
        stats[i] = Statistics.deserialize(strStat);
      } catch (Exception e) {
        e.printStackTrace();
        stats[i] = new Statistics();
        prefs.putString("STAT_" + i, stats[i].serialize());
      }
    }
  }

  public static StatManager getInstance() {
    if (instance == null) {
      instance = new StatManager();
    }
    return instance;
  }

  public static void resetInstance() {
    GnuBackgammon.out.println("===> STATS INSTANCE RESETTED!");
    instance = new StatManager();
  }


  // ADD ROLL TO STATS (CURRENT AI LEVEL AND TOTAL)
  public void addRoll(int player, int[] dices, int[][] board) {
    int curLev = MatchState.currentLevel.ordinal();

    // AVG_PIPS
    int pips = dices[0] == dices[1] ? dices[0] * 4 : dices[0] + dices[1];
    stats[curLev].dices.AVG_PIPS[player] =
        ((stats[curLev].dices.AVG_PIPS[player] * (float)stats[curLev].dices.ROLLS[player]) + (float)pips) / (stats[curLev].dices.ROLLS[player] + 1);
    stats[8].dices.AVG_PIPS[player] =
        ((stats[8].dices.AVG_PIPS[player] * (float)stats[8].dices.ROLLS[player]) + (float)pips) / (stats[8].dices.ROLLS[player] + 1);

    // ROLLS
    stats[curLev].dices.ROLLS[player]++;
    stats[8].dices.ROLLS[player]++;

    // DOUBLES
    if (dices[0] == dices[1]) {
      stats[curLev].dices.DOUBLES[player]++;
      stats[8].dices.DOUBLES[player]++;
    }

    // DOUBLES_IN_ROW
    byte d = dices[0] == dices[1] ? (byte)1 : (byte)0;
    for (int i = 5; i > 0; i--) {
      stats[curLev].dices.DOUBLE_HISTORY[player][i] = stats[curLev].dices.DOUBLE_HISTORY[player][i - 1];
    }
    stats[curLev].dices.DOUBLE_HISTORY[player][0] = d;
    String test = getBitStr(Arrays.copyOfRange(stats[curLev].dices.DOUBLE_HISTORY[player], 0, 3));
    if (test.equals("010")) {
      stats[curLev].dices.DOUBLES_ROW_1[player]++;
      stats[8].dices.DOUBLES_ROW_1[player]++;
    }
    test = getBitStr(Arrays.copyOfRange(stats[curLev].dices.DOUBLE_HISTORY[player], 0, 4));
    if (test.equals("0110")) {
      stats[curLev].dices.DOUBLES_ROW_2[player]++;
      stats[8].dices.DOUBLES_ROW_2[player]++;
    }
    test = getBitStr(Arrays.copyOfRange(stats[curLev].dices.DOUBLE_HISTORY[player], 0, 5));
    if (test.equals("01110")) {
      stats[curLev].dices.DOUBLES_ROW_3[player]++;
      stats[8].dices.DOUBLES_ROW_3[player]++;
    }
    test = getBitStr(stats[curLev].dices.DOUBLE_HISTORY[player]);
    if (test.equals("011110")) {
      stats[curLev].dices.DOUBLES_ROW_4[player]++;
      stats[8].dices.DOUBLES_ROW_4[player]++;
    }

    // ENTER ATTEMPT
    if (board[player][24] > 0) {
      int fOpponent = player == 1 ? 0 : 1;
      String boardAgainst = "";
      int nAgainst = 0;
      for (int i = 0; i < 6; i++) {
        if (board[fOpponent][i] >= 2) {
          boardAgainst += "1";
          nAgainst++;
        } else {
          boardAgainst += "0";
        }
      }
      if (nAgainst < 6) { // IF 6 NO ROLL!
        boolean entered = (boardAgainst.charAt(dices[0] - 1) == '0') || (boardAgainst.charAt(dices[1] - 1) == '0');

        stats[curLev].dices.BAR_ENTER_ATTEMPT[player]++;
        stats[8].dices.BAR_ENTER_ATTEMPT[player]++;
        if (entered) {
          stats[curLev].dices.BAR_ENTER[player]++;
          stats[8].dices.BAR_ENTER[player]++;
        }

        switch (nAgainst) {
          case 1:
            stats[curLev].dices.BAR_ENTER_ATTEMPT_P1[player]++;
            stats[8].dices.BAR_ENTER_ATTEMPT_P1[player]++;
            if (entered) {
              stats[curLev].dices.BAR_ENTER_P1[player]++;
              stats[8].dices.BAR_ENTER_P1[player]++;
            }
            break;
          case 2:
            stats[curLev].dices.BAR_ENTER_ATTEMPT_P2[player]++;
            stats[8].dices.BAR_ENTER_ATTEMPT_P2[player]++;
            if (entered) {
              stats[curLev].dices.BAR_ENTER_P2[player]++;
              stats[8].dices.BAR_ENTER_P2[player]++;
            }
            break;
          case 3:
            stats[curLev].dices.BAR_ENTER_ATTEMPT_P3[player]++;
            stats[8].dices.BAR_ENTER_ATTEMPT_P3[player]++;
            if (entered) {
              stats[curLev].dices.BAR_ENTER_P3[player]++;
              stats[8].dices.BAR_ENTER_P3[player]++;
            }
            break;
          case 4:
            stats[curLev].dices.BAR_ENTER_ATTEMPT_P4[player]++;
            stats[8].dices.BAR_ENTER_ATTEMPT_P4[player]++;
            if (entered) {
              stats[curLev].dices.BAR_ENTER_P4[player]++;
              stats[8].dices.BAR_ENTER_P4[player]++;
            }
            break;
          case 5:
            stats[curLev].dices.BAR_ENTER_ATTEMPT_P5[player]++;
            stats[8].dices.BAR_ENTER_ATTEMPT_P5[player]++;
            if (entered) {
              stats[curLev].dices.BAR_ENTER_P5[player]++;
              stats[8].dices.BAR_ENTER_P5[player]++;
            }
            break;
        }
      }
    }

    commit();
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
    String ret = "-";
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
        if (stats[level].dices.BAR_ENTER_ATTEMPT[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT[who]) * 100.00)) + "%";
        break;

      case 4: // 1 DOUBLES IN A ROW
        ret = round2((float)(((float)stats[level].dices.DOUBLES_ROW_1[who] / (float)stats[level].dices.ROLLS[who]) * 100.00)) + "%";
        break;
      case 5: // 2 DOUBLES IN A ROW
        ret = round2((float)(((float)stats[level].dices.DOUBLES_ROW_2[who] / (float)stats[level].dices.ROLLS[who]) * 100.00)) + "%";
        break;
      case 6: // 3 DOUBLES IN A ROW
        ret = round2((float)(((float)stats[level].dices.DOUBLES_ROW_3[who] / (float)stats[level].dices.ROLLS[who]) * 100.00)) + "%";
        break;
      case 7: // 4 DOUBLES IN A ROW
        ret = round2((float)(((float)stats[level].dices.DOUBLES_ROW_4[who] / (float)stats[level].dices.ROLLS[who]) * 100.00)) + "%";
        break;

      case 8: // ENTER AGAINST 1P
        if (stats[level].dices.BAR_ENTER_ATTEMPT_P1[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER_P1[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT_P1[who]) * 100.00)) + "%";
        break;
      case 9: // ENTER AGAINST 2P
        if (stats[level].dices.BAR_ENTER_ATTEMPT_P2[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER_P2[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT_P2[who]) * 100.00)) + "%";
        break;
      case 10: // ENTER AGAINST 3P
        if (stats[level].dices.BAR_ENTER_ATTEMPT_P3[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER_P3[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT_P3[who]) * 100.00)) + "%";
        break;
      case 11: // ENTER AGAINST 4P
        if (stats[level].dices.BAR_ENTER_ATTEMPT_P4[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER_P4[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT_P4[who]) * 100.00)) + "%";
        break;
      case 12: // ENTER AGAINST 5P
        if (stats[level].dices.BAR_ENTER_ATTEMPT_P5[who] > 0)
          ret = round2((float)(((float)stats[level].dices.BAR_ENTER_P5[who] / (float)stats[level].dices.BAR_ENTER_ATTEMPT_P5[who]) * 100.00)) + "%";
        break;
    }
    return ret;
  }
  public int getGameStat(int level, int who) {
    if (who == 0) { // HUMAN
      return stats[level].general.HUMAN;
    } else { // CPU
      return stats[level].general.CPU;
    }
  }

  // PRIVATE HELPERS
  private String getBitStr(byte[] bitmap) {
    String s = "";
    for (int i = 0; i < bitmap.length; i++)
      s += bitmap[i] == 1 ? "1" : "0";
    return s;
  }
  private float round2(float n) {
    if (Float.isNaN(n))
      return 0;
    return (float)Math.round(n * 100) / 100;
  }

  // SAVE STATS ON PREFS AND SYNC ON GMS
  public void commit() {
    int curLev = MatchState.currentLevel.ordinal();
    prefs.putString("STAT_" + curLev, stats[curLev].serialize()); // CURRENT LEVEL
    prefs.putString("STAT_8", stats[8].serialize()); // TOTALS
    prefs.flush();
  }

}
