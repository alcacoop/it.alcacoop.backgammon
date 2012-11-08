package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetGameTurnAICall implements Runnable {

  int fTurn; 
  int fMove;
  
  public SetGameTurnAICall(int _fTurn, int _fMove) {
    fTurn = _fTurn;
    fMove = _fMove;
  }

  @Override
  public void run() {
    GnubgAPI.SetGameTurn(fTurn, fMove);
  }

}
