package it.alcacoop.gnubackgammon.logic;


public class MatchState {
  
  public static int[][] board = {
    
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0},//BLACK (HUMAN) BGV 
    {0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0},//WHITE (PC)
    {0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0},//BLACK (HUMAN) NGV 
    {0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0},//WHITE (PC) NGV
    
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//ALL BEARED OFF 
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},//ALL BEARED OFF
    
  };
  
  /* NOTE: ON NATIVE API HUMAN=1 AND PC=0 */
  public static AILevels currentLevel = AILevels.EXPERT;
  public static int fMove = 1; //CHI HA I DADI IN MANO (1=PC 0=HUMAN)
  public static int fTurn = 1; //CHI DEVE DECIDERE (1=PC 0=HUMAN)
  public static int fCubeOwner = -1; //(-1=BAR 1=PC 0=HUMAN)
  public static int nCube = 1; //VALORE ATTUALE DEL CUBO
  public static int fCrawford = 0; //REGOLA DI CRAWFORD
  public static int nMatchTo = 0; 
  public static int[] anScore = {0,0}; //MATCH SCORES
  public static int bgv = 0; //0=BACKGAMMON 1=NACKGAMMON
  public static int fCubeUse = 0; //USING CUBE
  public static int matchType = 0; //0=SINGLE PLAYER, 1=TWO PLAYERS
  
  
  
  public static void SwitchTurn() {
    if (fMove == 0) {
      SetGameTurn(1, 1);
    } else {
      SetGameTurn(0, 0);
    }
  }
  
  public static void SetGameVariant(int type) {
    MatchState.bgv = type;
    GnubgAPI.SetGameVariant(type);
  }
  
  public static void UpdateMSCubeInfo(int cubeValue, int owner) {
    MatchState.fCubeOwner = owner;
    MatchState.nCube = cubeValue;
    GnubgAPI.UpdateMSCubeInfo(MatchState.nCube, MatchState.fCubeOwner);
  }
  
  public static void SetMatchTo(String sMatchTo) {
    MatchState.nMatchTo = Integer.parseInt(sMatchTo);
    GnubgAPI.SetMatchTo(MatchState.nMatchTo);
  }
  
  public static void SetMatchScore(int AIScore, int HumanScore) {
    MatchState.anScore[0] = HumanScore;
    MatchState.anScore[1] = AIScore;
    GnubgAPI.SetMatchScore(MatchState.anScore[1], MatchState.anScore[0]);
  }
  
  public static void SetAILevel(AILevels level) {
    MatchState.currentLevel = level;
    GnubgAPI.SetAILevel(level.ordinal());
  }
  
  public static void SetGameTurn(int fTurn, int fMove) {
    MatchState.fMove = fMove;
    MatchState.fTurn = fTurn;
    AICalls.SetGameTurn(MatchState.fTurn, MatchState.fMove);
  }
  
  public static void SetCubeUse(int fCubeUse) {
    MatchState.fCubeUse = fCubeUse;
    GnubgAPI.SetCubeUse(fCubeUse);
  }
  
  public static void SetCrawford(int fCrawford) {
    MatchState.fCrawford = fCrawford;
    GnubgAPI.SetCrawford(fCrawford);
  }
}
