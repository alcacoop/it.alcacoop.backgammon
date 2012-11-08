package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetBoardAICall implements Runnable {

  int b1[];
  int b2[];
  
  public SetBoardAICall(int _b1[], int _b2[]) {
    b1 = _b1;
    b2 = _b2;
  }

  @Override
  public void run() {
    GnubgAPI.SetBoard(b1, b2);
  }

}
