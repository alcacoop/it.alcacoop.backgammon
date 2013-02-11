/******************************************************************************
 * FinishedMatch.java - Describe one match that you've played
 * $Id: FinishedMatch.java,v 1.4 2011/01/01 06:10:12 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 * 
 * $Log: FinishedMatch.java,v $
 * Revision 1.4 2011/01/01 06:10:12 dick
 * Javadoc.
 *
 * Revision 1.3 2010/03/03 13:12:21 inim
 * Replaced (c) sign in comment mangled by CVS default encoding back to UTF-8
 *
 * Revision 1.2 2010/03/03 12:19:48 inim
 * Moved source to UTF8 encoding from CP1252 encoding. To this end all source files' (c) message was updated to "Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.". This replaces the (c) sign to UTF8, and adds the new year 2010.
 *
 * Revision 1.1 2010/02/04 05:57:53 inim
 * Mavenized project folder layout
 *
 * Revision 1.2 2009/02/25 07:49:23 dick
 * Store the duration of the match.
 *
 * Revision 1.1 2009/02/20 10:25:58 dick
 * Describe one match that you've played.
 *
 */

/* 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * The Original Code is BuckoFIBS, <http://www.buckosoft.com/BuckoFIBS/>.
 * The Initial Developer of the Original Code is Dick Balaska and BuckoSoft, Corp.
 * 
 */
package com.buckosoft.fibs.domain;

import java.util.Date;

/** Describe one match that you've played.
 * @author Dick Balaska
 * @since 2009/02/19
 * @version $Revision: 1.4 $ <br> $Date: 2011/01/01 06:10:12 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/domain/FinishedMatch.java">cvs FinishedMatch.java</a>
 */
public class FinishedMatch {
  private int matchId;
  private int opponentId;
  private int matchPoints;
  private int yourScore;
  private int opponentScore;
  private double ratingAfterMatch;
  private int duration;
  private Date date;

  /**
   * @return the id
   */
  public int getMatchId() {
    return matchId;
  }
  /** Set the database unique Id for this record.
   * @param matchId the matchIid to set
   */
  public void setMatchId(int matchId) {
    this.matchId = matchId;
  }
  /**
   * @return the opponentId
   */
  public int getOpponentId() {
    return opponentId;
  }
  /**
   * @param opponentId the opponentId to set
   */
  public void setOpponentId(int opponentId) {
    this.opponentId = opponentId;
  }
  /**
   * @return the matchPoints
   */
  public int getMatchPoints() {
    return matchPoints;
  }
  /**
   * @param matchPoints the matchPoints to set
   */
  public void setMatchPoints(int matchPoints) {
    this.matchPoints = matchPoints;
  }
  /**
   * @return the yourScore
   */
  public int getYourScore() {
    return yourScore;
  }
  /**
   * @param yourScore the yourScore to set
   */
  public void setYourScore(int yourScore) {
    this.yourScore = yourScore;
  }
  /**
   * @return the opponentScore
   */
  public int getOpponentScore() {
    return opponentScore;
  }
  /**
   * @param opponentScore the opponentScore to set
   */
  public void setOpponentScore(int opponentScore) {
    this.opponentScore = opponentScore;
  }
  /**
   * @return the ratingAfterMatch
   */
  public double getRatingAfterMatch() {
    return ratingAfterMatch;
  }
  /**
   * @param ratingAfterMatch the ratingAfterMatch to set
   */
  public void setRatingAfterMatch(double ratingAfterMatch) {
    this.ratingAfterMatch = ratingAfterMatch;
  }
  /** Get the number of seconds it took you to play this match
   * @return the duration
   */
  public int getDuration() {
    return duration;
  }
  /**
   * @param duration the duration to set
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }

  /** Fetch the timestamp that you finished the match
   * @return the date
   */
  public Date getDate() {
    return date;
  }
  /** Set the timestamp of when you finished this match
   * @param date the date to set
   */
  public void setDate(Date date) {
    this.date = date;
  }

}
