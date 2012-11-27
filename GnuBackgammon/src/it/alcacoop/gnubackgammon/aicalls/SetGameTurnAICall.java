package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
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
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_GAME_TURN, 1);
      }
    });
  }
}