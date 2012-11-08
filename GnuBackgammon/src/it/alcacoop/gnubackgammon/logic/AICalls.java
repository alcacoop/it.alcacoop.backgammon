package it.alcacoop.gnubackgammon.logic;


import it.alcacoop.gnubackgammon.aicalls.*;



public class AICalls {

  static Runnable InitializaEnvironment(String str) {
    return new InitializeEnvironmentAICall(str);
  }

  static Runnable SetAILevel(int l) {
    return new SetAILevelAICall(l);
  }
  
  static Runnable RollDice(int d[]) {
    return new RollDiceAICall(d);
  }
  
  static Runnable SetBoard(int b1[], int b2[]) {
    return new SetBoardAICall(b1, b2);
  }
  
  static Runnable AcceptResign(int r) {
    return new AcceptResignAICall(r);
  }

  static Runnable AcceptDouble() {
    return new AcceptDoubleAICall();
  }
  
  static Runnable UpdateMSCubeInfo(int c, int f) {
    return new UpdateMSCubeInfoAICall(c, f);
  }
  
  static Runnable AskForResignation() {
    return new AskForResignationAICall();
  }

  static Runnable AskForDoubling() {
    return new AskForDoublingAICall();
  }

  static Runnable EvaluateBestMove(int dices[], int moves[]) {
    return new EvaluateBestMoveAICall(dices, moves);
  }
  
  static Runnable SetMatchScore(int AIScore, int HumanScore) {
    return new SetMatchScoreAICall(AIScore, HumanScore);
  }
  
  static Runnable SetGameTurn(int fTurn, int fMove) {
    return new SetGameTurnAICall(fTurn, fMove);
  }
}
