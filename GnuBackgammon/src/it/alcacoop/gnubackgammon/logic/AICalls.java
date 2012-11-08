package it.alcacoop.gnubackgammon.logic;


import it.alcacoop.gnubackgammon.aicalls.*;



public class AICalls {

  public static Runnable InitializaEnvironment(String str) {
    return new InitializeEnvironmentAICall(str);
  }

  public static Runnable SetAILevel(int l) {
    return new SetAILevelAICall(l);
  }
  
  public static Runnable RollDice(int d[]) {
    return new RollDiceAICall(d);
  }
  
  public static Runnable SetBoard(int b1[], int b2[]) {
    return new SetBoardAICall(b1, b2);
  }
  
  public static Runnable AcceptResign(int r) {
    return new AcceptResignAICall(r);
  }

  public static Runnable AcceptDouble() {
    return new AcceptDoubleAICall();
  }
  
  public static Runnable UpdateMSCubeInfo(int c, int f) {
    return new UpdateMSCubeInfoAICall(c, f);
  }
  
  public static Runnable AskForResignation() {
    return new AskForResignationAICall();
  }

  public static Runnable AskForDoubling() {
    return new AskForDoublingAICall();
  }

  public static Runnable EvaluateBestMove(int dices[], int moves[]) {
    return new EvaluateBestMoveAICall(dices, moves);
  }
  
  public static Runnable SetMatchScore(int AIScore, int HumanScore) {
    return new SetMatchScoreAICall(AIScore, HumanScore);
  }
  
  public static Runnable SetGameTurn(int fTurn, int fMove) {
    return new SetGameTurnAICall(fTurn, fMove);
  }
}
