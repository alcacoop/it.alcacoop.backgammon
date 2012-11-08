package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class AskForResignationAICall implements Runnable {


  @Override
  public void run() {
    GnubgAPI.AskForResignation();
  }

}
