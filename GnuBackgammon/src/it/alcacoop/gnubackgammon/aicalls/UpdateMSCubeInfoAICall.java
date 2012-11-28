package it.alcacoop.gnubackgammon.aicalls;

import com.badlogic.gdx.Gdx;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.fsm.BaseFSM;
import it.alcacoop.gnubackgammon.fsm.GameFSM;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class UpdateMSCubeInfoAICall implements Runnable {

  int nCube; 
  int fCubeOwner;
  BaseFSM fsm;
  
  public UpdateMSCubeInfoAICall(int _nCube, int _fCubeOwner) {
    nCube = _nCube;
    fCubeOwner = _fCubeOwner;
    fsm = GnuBackgammon.fsm;
  }

  @Override
  public void run() {
    GnubgAPI.UpdateMSCubeInfo(nCube, fCubeOwner);
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        if (fsm == GnuBackgammon.fsm)
          GnuBackgammon.fsm.processEvent(GameFSM.Events.UPDATE_MS_CUBEINFO, 1);        
      }
    });
  }
}