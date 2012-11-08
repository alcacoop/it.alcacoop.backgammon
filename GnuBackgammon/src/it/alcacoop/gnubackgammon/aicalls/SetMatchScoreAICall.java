package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchScoreAICall implements Runnable {

  int AIScore; 
  int HumanScore;
  
  public SetMatchScoreAICall(int _AIScore, int _HumanScore) {
    AIScore = _AIScore;
    HumanScore = _HumanScore;
  }

  @Override
  public void run() {
    GnubgAPI.SetMatchScore(AIScore, HumanScore);
  }

}
