/******************************************************************************
 * SavedMatch.java - Describe a saved match that we have with a player
 * $Id: SavedMatch.java,v 1.3 2010/03/03 13:12:21 inim Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */
package com.buckosoft.fibs.domain;

/** Describe a saved match that we have with a player.
 * @author Dick Balaska
 * @since 2009/02/01
 * @version $Revision: 1.3 $ <br> $Date: 2010/03/03 13:12:21 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/domain/SavedMatch.java">cvs SavedMatch.java</a>
 */
public class SavedMatch {
  private String opponentName;
  private int matchLength;
  private int yourScore;
  private int opponentScore;

  /** Create an empty/default SavedMatch
  */
  public SavedMatch() {}

  /** Get the name of the opponent
   * @return the name
   */
  public String getOpponentName() {
    return opponentName;
  }

  /** Set the name of the opponent
   * @param name the name to set
   */
  public void setOpponentName(String name) {
    this.opponentName = name;
  }

  /** Return the number of games in this match
   * @return the matchLength
   */
  public int getMatchLength() {
    return matchLength;
  }

  /** Set the number of games in this match.
   * @param matchLength the matchLength to set
   */
  public void setMatchLength(int matchLength) {
    this.matchLength = matchLength;
  }

  /** Get Your score in this match
   * @return the yourScore
   */
  public int getYourScore() {
    return yourScore;
  }

  /** Set Your score in this match
   * @param yourScore the yourScore to set
   */
  public void setYourScore(int yourScore) {
    this.yourScore = yourScore;
  }

  /** Get the opponent's score in this match
   * @return the opponentScore
   */
  public int getOpponentScore() {
    return opponentScore;
  }

  /** Set the opponent's score in this match
   * @param opponentScore the opponentScore to set
   */
  public void setOpponentScore(int opponentScore) {
    this.opponentScore = opponentScore;
  }
}
