package it.alcacoop.gnubackgammon.logic;

public class GnubgAPI {
  
  public static native void InitializeEnvironment(String str);
  public static native void SetAILevel(int level);
  public static native void RollDice(int d[]);
  public static native void SetBoard(int b1[], int b2[]);
  public static native int AcceptResign(int r);
  public static native int AcceptDouble();
  public static native void UpdateMSCubeInfo(int nCube, int fCubeOwner);
  public static native int AskForResignation();
  public static native int AskForDoubling();
  public static native void EvaluateBestMove(int dices[], int move[]);
  public static native void SetMatchScore(int AIScore, int HumanScore);
  public static native void SetGameTurn(int fTurn, int fMove);
  public static native void SetMatchTo(int nMatchTo);
  public static native int[][] GenerateMoves(int b1[], int b2[], int d1, int d2);
  
}
