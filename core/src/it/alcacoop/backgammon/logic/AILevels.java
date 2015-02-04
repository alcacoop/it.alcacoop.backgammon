/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon.logic;


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
  
  public static AILevels getAILevelFromOrdinal(int l) {
    return AILevels.values()[l];
  }

  public static double getAILevelELO(AILevels ail) {
    double[] ratings = {400, 650, 900, 1500, 1700, 1900, 2100, 2300};
    return ratings[ail.ordinal()];
  }
}
