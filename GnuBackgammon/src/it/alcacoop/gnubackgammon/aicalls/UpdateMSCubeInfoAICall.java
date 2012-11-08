package it.alcacoop.gnubackgammon.aicalls;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;

public class UpdateMSCubeInfoAICall implements Runnable {

  int nCube; 
  int fCubeOwner;
  
  public UpdateMSCubeInfoAICall(int _nCube, int _fCubeOwner) {
    nCube = _nCube;
    fCubeOwner = _fCubeOwner;
  }

  @Override
  public void run() {
    GnubgAPI.UpdateMSCubeInfo(nCube, fCubeOwner);
  }

}
