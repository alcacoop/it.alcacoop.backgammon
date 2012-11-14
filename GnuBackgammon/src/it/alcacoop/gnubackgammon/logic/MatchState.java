package it.alcacoop.gnubackgammon.logic;


public class MatchState {
  
  public static int[][] board = {
    
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0},//BLACK (HUMAN) BGV 
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0},//WHITE (PC)
    {0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0},//BLACK (HUMAN) NGV 
    {0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0},
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5},//BLACK (HUMAN) 
    {0, 0, 0, 0, 0, 5, 0, 1, 0, 0, 0, 0, 5, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 1},
    {2, 2, 2, 1, 1, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {2, 2, 2, 2, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {2, 3, 3, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0}
    
  };
  
  /* NOTE: ON NATIVE API HUMAN=1 AND PC=0 */
  public static int fMove = 1; //CHI HA I DADI IN MANO (1=PC 0=HUMAN)
  public static int fTurn = 1; //CHI DEVE DECIDERE (1=PC 0=HUMAN)
  public static int fCubeOwner = -1; //(-1=BAR O=PC 1=HUMAN)
  public static int nCube = 1; //VALORE ATTUALE DEL CUBO
  public static int fCrawford = 0; //REGOLA DI CRAWFORD
  public static int nMatchTo = 7; 
  public static int[] anScore = {0,0}; //MATCH SCORES
  public static int bgv = 0; //0=BACKGAMMON 1=NACKGAMMON
  public static int fCubeUse = 1; //USING CUBE
  
  
  public static void switchTurn() {
    if (fMove == 0) {
      fMove=1;
      fTurn=1;
    } else {
      fMove=0;
      fTurn=0;
    }
    AICalls.SetGameTurn(MatchState.fTurn, MatchState.fMove);
  }
  
}
