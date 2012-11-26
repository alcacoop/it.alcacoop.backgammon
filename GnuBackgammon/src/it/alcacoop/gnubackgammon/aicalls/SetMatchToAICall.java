package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.layers.GameScreen;
import it.alcacoop.gnubackgammon.logic.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class SetMatchToAICall implements Runnable {

  int nMatchTo; 
  
  public SetMatchToAICall(int _nMatchTo) {
    nMatchTo = _nMatchTo;
  }

  @Override
  public void run() {
    GnubgAPI.SetMatchTo(nMatchTo);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        GameScreen.fsm.processEvent(GameFSM.Events.SET_MATCH_TO, 1);        
      }
    });
  }
}