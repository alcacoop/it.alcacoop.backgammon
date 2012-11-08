package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AskForDoublingAICall implements Runnable {


  @Override
  public void run() {
    GnubgAPI.AskForDoubling();
  }

}
