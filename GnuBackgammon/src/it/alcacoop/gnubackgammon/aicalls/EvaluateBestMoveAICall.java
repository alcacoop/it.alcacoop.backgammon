package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class EvaluateBestMoveAICall implements Runnable {

  int dices[];
  int moves[];
  
  public EvaluateBestMoveAICall(int _dices[], int _moves[]) {
    dices = _dices;
    moves = _moves;
  }
  
  @Override
  public void run() {
    GnubgAPI.EvaluateBestMove(dices, moves);
  }

}
