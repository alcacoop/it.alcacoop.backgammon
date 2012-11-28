package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetGameTurnAICall implements Runnable {

  int fTurn; 
  int fMove;
  BaseFSM fsm;
  
  public SetGameTurnAICall(int _fTurn, int _fMove) {
    fTurn = _fTurn;
    fMove = _fMove;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    GnubgAPI.SetGameTurn(fTurn, fMove);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.SET_GAME_TURN, 1);
      }
    });
  }
}