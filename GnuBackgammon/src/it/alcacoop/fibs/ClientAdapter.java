/******************************************************************************
 * ClientAdapter.java - Hook up the network to the application
 * $Id: ClientAdapter.java,v 1.3 2010/03/03 13:12:21 inim Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */ 
 
package it.alcacoop.fibs;

/** Hook up the network to the application.
 * 
 * @author Dick Balaska
 * @since 2009/01/11
 * @version $Revision: 1.3 $ <br> $Date: 2010/03/03 13:12:21 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/net/ClientAdapter.java">cvs ClientAdapter.java</a>
 */
public interface ClientAdapter {
  /** Define what kind of message this is
  */
  enum MessageRoute {
    NETWORKOUT,
    SYSTEM,
    ERROR,
    DEBUG
  }

  
  /** Dispatch a received message (line) from FIBS
   * @param cookie The parsed {@link FIBSMessages} cookie
   * @param s The line received
   */
  void dispatch(int cookie, String s);

  /** The network connection to FIBS terminated unexpectedly
  */
  void connectionAborted();
}
