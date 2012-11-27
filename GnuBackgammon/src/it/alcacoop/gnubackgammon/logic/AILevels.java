package it.alcacoop.gnubackgammon.logic;


public enum AILevels {
  BEGINNER,
  CASUAL,
  INTERMEDIATE,
  ADVANCED,
  EXPERT,
  WORLDCLASS,
  SUPREMO,
  GRANDMASTER;
  
  public static AILevels getAILevelFromString(String s) {
    String us = s.toUpperCase();
    if (us.equals("BEGINNER")) return AILevels.BEGINNER;
    else if (us.equals("CASUAL")) return AILevels.CASUAL;
    else if (us.equals("INTERMEDIATE")) return AILevels.INTERMEDIATE;
    else if (us.equals("ADVANCED")) return AILevels.ADVANCED;
    else if (us.equals("EXPERT")) return AILevels.EXPERT;
    else if (us.equals("WORLDCLASS")) return AILevels.WORLDCLASS;
    else if (us.equals("SUPREMO")) return AILevels.SUPREMO;
    else if (us.equals("GRANDMASTER")) return AILevels.GRANDMASTER;
    else return AILevels.EXPERT; //DEFAULT VALUE
  }
}
