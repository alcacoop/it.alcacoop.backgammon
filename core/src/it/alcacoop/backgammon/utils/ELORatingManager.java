package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;


public class ELORatingManager {

  public final static String SINGLE_BOARD = "CgkI9ZWZjusDEAIQAQ";
  public final static String MULTI_BOARD = "CgkI9ZWZjusDEAIQAg";
  public final static String TIGA_BOARD = "CgkI9ZWZjusDEAIQIg";
  public final static String FIBS_BOARD2 = "CgkI9ZWZjusDEAIQIw";

  private final static double CONVERT_ADDENDUM = 1500.00;
  private double matchValue;

  private static ELORatingManager instance;
  private double currentRating = 0.00; // in ELO
  private double opponentRating = 0.00; // in ELO

  private ELORatingManager() {}

  public synchronized static ELORatingManager getInstance() {
    if (instance == null)
      instance = new ELORatingManager();
    return instance;
  }

  public void setRatings(double opponentRating) {
    if (MatchState.matchType == 3) {
      this.opponentRating = opponentRating + CONVERT_ADDENDUM;
      this.currentRating = Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("MULTIBOARD", "0")) + CONVERT_ADDENDUM;
    } else if (MatchState.matchType == 0) {
      this.opponentRating = opponentRating;
      this.currentRating = Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("SINGLEBOARD", "0")) + CONVERT_ADDENDUM;
    }
  }

  public void syncLeaderboards() {
    long score;

    score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("MULTIBOARD", "0")) * 100);
    GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);

    score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("SINGLEBOARD", "0")) * 100);
    GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);

    score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("FIBSBOARD2", "0")) * 100);
    GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, FIBS_BOARD2);

    score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("TIGABOARD", "0")) * 100);
    GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, TIGA_BOARD);
  }


  public void updateRating(int server, double increment) {
    String kboard = TIGA_BOARD;
    String sboard = "TIGABOARD";
    if (server != 0) {
      kboard = FIBS_BOARD2;
      sboard = "FIBSBOARD2";
    }

    double start = Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString(sboard, "0"));
    long score = (long)((start + increment) * 100);
    if (score < 0)
      return;

    GnuBackgammon.Instance.optionPrefs.putString(sboard, score / 100 + "");
    GnuBackgammon.Instance.optionPrefs.flush();
    GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, kboard);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
    GnuBackgammon.out.println("---> SUBMIT SCORE TO " + sboard + " START: " + start + " INCREMENT: " + increment + " SUBMITTING: " + score);
  }

  public void updateRating(boolean youWin) {
    if (!youWin) {
      if (MatchState.matchType == 3) {
        GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(0, MULTI_BOARD);
      } else if (MatchState.matchType == 0) {
        GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(0, SINGLE_BOARD);
      }
      updatePreferences(0);
      return;
    }

    int matchLevel = MatchState.nMatchTo;
    double wp = 1 / (Math.pow(10, (Math.abs(currentRating - opponentRating) * Math.sqrt(matchLevel) / 2000)) + 1);
    matchValue = 4 * Math.sqrt(matchLevel);

    if (currentRating <= opponentRating) {
      currentRating += matchValue * (1 - wp);
    } else {
      currentRating += matchValue * wp;
    }
    updatePreferences(Math.round((currentRating - CONVERT_ADDENDUM) * 100) / 100d);
    long score = (long)((currentRating - CONVERT_ADDENDUM) * 100);

    if (MatchState.matchType == 3) {
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);
    } else if (MatchState.matchType == 0) {
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);
    }
  }

  private void updatePreferences(double newRating) {
    if (newRating < 0)
      newRating = 0.00; // FIX ON OLD RATING CALCULATOR
    if (MatchState.matchType == 3) {
      GnuBackgammon.Instance.optionPrefs.putString("MULTIBOARD", newRating + "");
    } else if (MatchState.matchType == 0) {
      GnuBackgammon.Instance.optionPrefs.putString("SINGLEBOARD", newRating + "");
    }

    GnuBackgammon.Instance.optionPrefs.flush();
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
  }

}
