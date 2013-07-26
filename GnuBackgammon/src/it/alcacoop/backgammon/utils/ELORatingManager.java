package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;


public class ELORatingManager {
  
  private final static String SINGLE_BOARD = "CgkI9ZWZjusDEAIQAQ";
  private final static String MULTI_BOARD = "CgkI9ZWZjusDEAIQAg";
  private final static double CONVERT_ADDENDUM = 1500.00;
  private double matchValue;

  private static ELORatingManager instance;
  private double currentRating = 0.00; // in ELO
  private double opponentRating = 0.00; // in ELO
  
  private ELORatingManager() {}
  
  public synchronized static ELORatingManager getInstance() {
    if (instance == null) instance = new ELORatingManager();
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
    long score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("MULTIBOARD", "0"))*100);
    if (score>0)
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);
    score = (long)(Double.parseDouble(GnuBackgammon.Instance.optionPrefs.getString("SINGLEBOARD", "0"))*100);
    if (score>0)
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);
  }

  public void updateRating(boolean youWin) {
    if (!youWin) return;

    int matchLevel = MatchState.nMatchTo;
    double wp = 1-(1/(Math.pow(10, ((currentRating - opponentRating) * Math.sqrt(matchLevel)/2000)) + 1));
    matchValue = 4*Math.sqrt(matchLevel);

    currentRating += matchValue * (1-wp);
    updatePreferences(Math.round((currentRating - CONVERT_ADDENDUM) * 100) / 100d );
    long score = (long)((currentRating - CONVERT_ADDENDUM) * 100);

    if (MatchState.matchType == 3) {
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, MULTI_BOARD);
    } else if (MatchState.matchType == 0) {
      GnuBackgammon.Instance.nativeFunctions.gserviceSubmitRating(score, SINGLE_BOARD);
    }
  }
  
  private void updatePreferences(double newRating) {
    if (MatchState.matchType == 3) {
      GnuBackgammon.Instance.optionPrefs.putString("MULTIBOARD", newRating+"");
    } else if (MatchState.matchType == 0) {
      GnuBackgammon.Instance.optionPrefs.putString("SINGLEBOARD", newRating+"");
    }
    GnuBackgammon.Instance.optionPrefs.flush();
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
  }

}
