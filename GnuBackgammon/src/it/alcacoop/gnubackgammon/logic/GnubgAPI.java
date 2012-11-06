package it.alcacoop.gnubackgammon.logic;

public class GnubgAPI {
  
  public static native void InitializeEnvironment(String str);
  public static native void SetAILevel(int level);
  public static native void RollDice(int d[]);
  public static native void SetBoard(int b1[], int b2[]);
  public static native int AcceptResign(int b1);
  public static native int AcceptDouble();
  public static native void UpdateMSCubeInfo(int nBube, int fCubeOwner);
  public static native int AskForResignation();
  public static native int AskForDoubling();
  public static native void EvaluateBestMove(int dices[], int move[]);
  public static native void SetMatchScore(int AIScore, int HumanScore);
  public static native void SetGameTurn(int fturn, int fMove);
  public static native void SetMatchTo(int nMatchTo);
  
  public static native void TestAll();
  
}
