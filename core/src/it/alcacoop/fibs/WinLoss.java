/******************************************************************************
 * WinLoss - Describe the Win Loss record against one player.
 * $Id: WinLoss.java,v 1.2 2010/12/30 17:38:19 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright 2010 - Dick Balaska - BuckoSoft, Corp.
 */

package it.alcacoop.fibs;


/** A class to manage wins/losses for a Player.
 * We need to be able to deal with them as numbers and present them as a string.
 * @author dick
 * @since 2010/12/23
 * @version $Revision: 1.2 $ <br> $Date: 2010/12/30 17:38:19 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/main/java/com/buckosoft/fibs/domain/WinLoss.java">cvs WinLoss.java</a>
 *
 */
public class WinLoss implements Comparable<WinLoss> {
  private int wins;
  private int losses;

  /** Convienence constructor that takes the wins and losses
   * @param wins The number of times you have beaten this opponent.
   * @param losses The number of times you have lost to this opponent.
   */
  public WinLoss(int wins, int losses) {
    this.wins = wins;
    this.losses = losses;
  }

  public WinLoss() {
  }

  /** Get the numeric value of this WinLoss
   * @return wins/losses, more or less
   */
  public double getValue() {
    if (losses == 0)
      return(wins*10);
    if (wins == 0)
      return(-losses);
    return((double)wins/(double)losses);
  }

  /** Get the String value of this WinLoss
   * @return "W-L"
   */
  public String toString() {
    return("" + wins + "-" + losses);
  }

  /** String compare the name of this player with that one.
   * @param o The other player to compare
   */
  @Override
  public int compareTo(WinLoss o) {
    if (this.getValue() < o.getValue())
      return(-1);
    if (this.getValue() > o.getValue())
      return(1);
    return(0);
  }
}

