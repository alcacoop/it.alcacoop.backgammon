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
  
  public static AILevels getFromString(String s) {
    String us = s.toUpperCase();
    if (us.equals("BEGINNER")) return AILevels.BEGINNER;
    if (us.equals("CASUAL")) return AILevels.CASUAL;
    if (us.equals("INTERMEDIATE")) return AILevels.INTERMEDIATE;
    if (us.equals("ADVANCED")) return AILevels.ADVANCED;
    if (us.equals("EXPERT")) return AILevels.EXPERT;
    if (us.equals("WORLDCLASS")) return AILevels.WORLDCLASS;
    if (us.equals("SUPREMO")) return AILevels.SUPREMO;
    if (us.equals("GRANDMASTER")) return AILevels.GRANDMASTER;
    
    return null;
    
  }
}
