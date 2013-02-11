/******************************************************************************
 * Line.java - A single line of text from FIBS
 * $Id: Line.java,v 1.3 2010/03/03 13:12:21 inim Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */
package com.buckosoft.fibs.domain;

/** A single line of text from FIBS. <br>
 * The only line we really care about is 'board' which is used by the GameManager
 * to log game moves. 
 * @author Dick Balaska
 * @since 2009/01/30
 * @version $Revision: 1.3 $ <br> $Date: 2010/03/03 13:12:21 $
 * @see com.buckosoft.fibs.BuckoFIBS.GameManager
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/domain/Line.java">cvs Line.java</a>
 */
public class Line {
  private int cookie;
  private String line;
  private int checkersToMove;

  /** Create an empty Line
  */
  public Line() {}

  /** Create a line with these values.
   * @param cookie The FIBSMessages cookie that was parsed.
   * @param line The line of text received from FIBS
   */
  public Line(int cookie, String line) {
    this.cookie = cookie;
    this.line = line;
  }

  /** Get the cookie of this Line.
   * @see com.buckosoft.fibs.net.CookieMonster
   * @return the cookie
   */
  public int getCookie() {
    return cookie;
  }

  /** Set the cookie of this Line
   * @param cookie the cookie to set
   */
  public void setCookie(int cookie) {
    this.cookie = cookie;
  }

  /** Return the text of this Line
   * @return the line
   */
  public String getLine() {
    return line;
  }

  /** Set the text of this Line
   * @param line the line to set
   */
  public void setLine(String line) {
    this.line = line;
  }

  /** If this board is your move, then this is the number of checkers to move.
   * If it is not your move, this value is 0.
   * This is only valid if you are playing, not watching.
   * @return the diceToMove
   */
  public int getCheckersToMove() {
    return checkersToMove;
  }
  /** Set the number of checkers to move on this board.
   * @param diceToMove the diceToMove to set
   */
  public void setCheckersToMove(int diceToMove) {
    this.checkersToMove = diceToMove;
  }
}
