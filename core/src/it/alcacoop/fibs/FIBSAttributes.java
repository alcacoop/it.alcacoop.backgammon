/******************************************************************************
 * FIBSAttributes.java - Fetch what we need to connect to the server
 * $Id: FIBSAttributes.java,v 1.3 2010/03/03 13:12:21 inim Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */

package it.alcacoop.fibs;

/** Fetch what we need to connect to the server.
 * @author Dick Balaska
 * @since 2009/01/11
 * @version $Revision: 1.3 $ <br> $Date: 2010/03/03 13:12:21 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/net/FIBSAttributes.java">cvs FIBSAttributes.java</a>
 */
public interface FIBSAttributes {

  /** Get the name of the server
   * (probably either <code>fibs.com</code> or <code>lbd.buckosoft.com</code>)
   * @return The name of the server
   */
  String getServerName();

  /** Get the port number that we are connecting to.
   * @return probably 4321 
   */
  int getServerPort();

  /** Get the user name
   * @return The user name
   */
  String getUserName();

  /** Get the user's password in clear text
   * @return The clear text password
   */
  String getUserPassword();

  /** Get the client app signature, the name and version.
   * @return Something like "BuckoFIBS_1.0"
   */
  String getAppSignature();

  /** Should we display messages as we send them?
   * @return true = yes
   */
  boolean isDisplayXmit();

  /** Should messages that are received be sent to stdout for logging?
   * @return true = log messages to stdout
   */
  boolean isStdoutNetworkMessages();
}
