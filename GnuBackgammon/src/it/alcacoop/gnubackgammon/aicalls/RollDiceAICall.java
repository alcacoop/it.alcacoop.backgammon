package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class RollDiceAICall implements Runnable {

  int dices[];
  
  public RollDiceAICall(int d[]) {
    dices = d;
  }

  @Override
  public void run() {
    GnubgAPI.RollDice(dices);
  }

}
