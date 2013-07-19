package it.alcacoop.backgammon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class ELORatingManager {
  
  public final static int ML = 1;
  public final static double INITIAL_RATING = 1500.0;
  public final static double matchValue = 4*Math.sqrt(ML);

  private static ELORatingManager instance;
  private double currentRating = 0.0;
  private double opponentRating = 0.0;
  private Preferences prefs;
  
  //TODO: Usare leaderboardID come chiave per la preference!
  private ELORatingManager() {
    prefs = Gdx.app.getPreferences("Rating");
    currentRating = prefs.getFloat("RATING", (float) INITIAL_RATING);
  }
  
  public synchronized static ELORatingManager getInstance() {
    if (instance == null) instance = new ELORatingManager();
    return instance;
  }
  
  public double getRating() {
    return Math.round(currentRating * 100) / 100;
  }
  
  public void setOpponentRating(double opponentRating) {
    this.opponentRating = opponentRating;
  }
  
  public double updateRating(boolean youWin) {
    double wp = 1-(1/(Math.pow(10, ((currentRating - opponentRating) * Math.sqrt(ML)/2000)) + 1));
    double newRating = currentRating;

    if (youWin) {
      newRating += matchValue * (1-wp);
    } else {
      newRating -= matchValue * wp;
    }
    
    updatePreferences(newRating);
    currentRating = newRating;
    return currentRating;
  }
  
  
  /**
   * PRIVATE
   */
  private void updatePreferences(double newRating) {
    prefs.putFloat("RATING", (float) newRating);
    prefs.flush();
  }

}
