/******************************************************************************
 * CommandDispatcher.java - Interface for dispatching commands through the system
 * $Id: CommandDispatcher.java,v 1.5 2010/12/22 04:35:16 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */
package it.alcacoop.fibs;


/** Interface for dispatching commands through the BuckoFIBS system.
 * @author Dick Balaska
 * @since 2009/01/17
 * @version $Revision: 1.5 $ <br> $Date: 2010/12/22 04:35:16 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/BuckoFIBS/CommandDispatcher.java">cvs CommandDispatcher.java</a>
 */
public interface CommandDispatcher {
  public enum Command {
    SHOW_CONNECTION_DIALOG,
    SHOW_NEW_ACCOUNT_DIALOG,
    DISCONNECT_FROM_NETWORK,
    SHOW_PREFERENCES_DIALOG,
    SHOW_PREFS4MSGS_DIALOG,
    SHOW_ABOUT_DIALOG,
    SHOW_RATINGS_GRAPH_DIALOG,
    SET_RATING_GRAPH_CONFIG,
    REGISTER_NEW_USER, // Includes a roundabout way to CONNECT_TO_SERVER
    BAD_NEW_USER, // Server says pick someone else
    CONNECT_TO_SERVER,
    SYSTEM_MESSAGE,
    SHUTTING_DOWN,
    NETWORK_CONNECTED,
    // RECEIVE_NETWORK_MESSAGE,
    PLAYER_CHANGED,
    PLAYER_GONE,
    INVITE,
    INVITED,
    INVITE_WARNING,
    WATCHING,
    ACCEPT_INVITATION,
    DECLINE_INVITATION,
    UNINVITED,
    YOU_INVITED,
    START_GAME,
    MATCH_OVER,
    TOGGLE_READY_TO_PLAY,
    TOGGLE_GREEDY_BEAROFF,
    READY_TO_PLAY,
    TOGGLE_DOUBLE,
    SEND_MOVE,
    /** Bearing off is automatic, and just does end of turn handling */
    BEAR_OFF,
    SEND_COMMAND,
    YOUR_MOVE,
    ROLL_OR_DOUBLE,
    SEND_ROLL,
    SEND_DOUBLE,
    ACCEPT_OR_DECLINE_DOUBLE,
    SEND_ACCEPT,
    SEND_REJECT,
    SEND_RESIGN,
    /** The 2nd line received from FIBS. Describe my settings. */
    OWN_INFO,
    /** A Saved Match line from FIBS */
    SAVED_MATCH,
    /** A MissManners warning from FIBS */
    MISS_MANNERS,
    /** Board and double messages get routed through the GameManager */
    GAME_MOVE,
    /** Parse a board received from FIBS */
    FIBS_BOARD,
    /** Either player is pushing the cube */
    DOUBLE,
    WATCH,
    GET_PLAYER_REPORT,
    PLAY_CUE
  }



  /** Primary entry point to the command dispatcher
   * @param command The command to execute
   */
  void dispatch(Command command);

  /** Primary entry point to the command dispatcher
   * what takes a single string as an argument
   * @param command The Command to Execute
   * @param arg1 A string that the dispatched function wants
   */
  void dispatch(Command command, String arg1);

  /** Primary entry point to the command dispatcher
   * what takes two strings as an argument
   * @param command The Command to Execute
   * @param arg1 A string that the dispatched function wants
   * @param arg2 Another string that the dispatched function wants
   */
  void dispatch(Command command, String arg1, String arg2);

  /** Primary entry point to the command dispatcher
   * what takes an Object as an argument
   * @param command The Command to Execute
   * @param obj An Object that the dispatched function wants
   */
  void dispatch(Command command, Object obj);

  
  /** Send a message to the fibs server appending a crlf to the end
   * @param s The message to send
   * @return success
   */
  boolean writeNetworkMessageln(String s);

  /** Write a message to the chat message pane in the normal color.
   * Terminate the message with a crlf.
   * @param name The user what sent the message
   * @param cookie The mode used to send the message (shout, whisper, kibitz, etc)
   * @param text The message to write
   */
  void writeChatMessageln(String name, int cookie, String text);

  /** Write a message to the game pane appending a crlf to the end.
   * @param s The message to write
   */
  void writeGameMessageln(String s);

  /** User has changed the state of the Ready, Online, or Playing button
   * @param rop The current/new state of the Ready[0], Online[1], and Playing[2] buttons.
   */ 
  void ropChanged(boolean[] rop);
}
