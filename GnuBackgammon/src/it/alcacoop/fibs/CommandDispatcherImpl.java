/******************************************************************************
 * CommandDispatcherImpl.java - Dispatch commands through the system
 * $Id: CommandDispatcherImpl.java,v 1.8 2010/12/29 07:46:59 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */

package it.alcacoop.fibs;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.fibs.ClientReceiveParser.Mode;


/** Dispatch commands through the system.
 * @author Dick Balaska
 * @since 2008/03/30
 * @version $Revision: 1.8 $ <br> $Date: 2010/12/29 07:46:59 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/BuckoFIBS/CommandDispatcherImpl.java">cvs CommandDispatcherImpl.java</a>
 */
public class CommandDispatcherImpl implements CommandDispatcher, FIBSMessages {

  private ClientConnection clientConnection;
  private ClientReceiveParser parser;
  private final static String eol = "\r\n";
  

  /** Primary entry point to the command dispatcher
   * @param command The command to execute
   */
  public void dispatch(Command command) {
    System.out.println("COMMAND: "+command);
    switch (command) {
      case DISCONNECT_FROM_NETWORK:
        onNetworkDisconnect();
        break;
      case CONNECT_TO_SERVER:
        onConnectToServer();
        break;
      case START_GAME:
        onStartGame();
        break;
      case SHUTTING_DOWN:
        onShutdown();
        break;
      case NETWORK_CONNECTED:
        onNetworkConnected();
        break;
      case WATCHING:
        onWatching();
        break;
      case ROLL_OR_DOUBLE:
        onRollOrDouble();
        break;
      case ACCEPT_OR_DECLINE_DOUBLE:
        onAcceptOrDeclineDouble();
        break;
      case TOGGLE_READY_TO_PLAY:
        onToggleReadyToPlay();
        break;
      case TOGGLE_GREEDY_BEAROFF:
        onToggleGreedyBearoff();
        break;
      case BEAR_OFF:
        break;
      case SEND_ROLL:
        this.writeNetworkMessageln("roll"); 
        break;
      case SEND_DOUBLE:
        this.writeNetworkMessageln("double"); 
        break;
      case SEND_ACCEPT:
        this.writeNetworkMessageln("accept"); 
        break;
      case SEND_REJECT:
        this.writeNetworkMessageln("reject"); 
        break;

      default:
        System.out.println("Dispatcher: Unhandled command " + command);
        throw new RuntimeException("Unhandled command " + command);
    }
  }

  /** Primary entry point to the command dispatcher
   * what takes a single string as an argument
   * @param command The Command to Execute
   * @param arg1 A string that the dispatched function wants
   */
  public void dispatch(Command command, String arg1) {
    System.out.println("COMMAND: "+command+" ARG1: "+arg1);
    switch (command) {
      case SYSTEM_MESSAGE:
        break;
      case PLAYER_CHANGED:
        Player p = GnuBackgammon.Instance.fibsPlayersPool.obtain();
        p.parsePlayer(arg1);
        GnuBackgammon.fsm.processEvent(Events.FIBS_PLAYER_CHANGED, p);
        break;
      case PLAYER_GONE:
        GnuBackgammon.fsm.processEvent(Events.FIBS_PLAYER_LOGOUT, arg1);
        break;
      case SAVED_MATCH:
        onSavedMatch(arg1);
        break;
      case MISS_MANNERS:
        onMissManners(arg1);
        break;
      case WATCH:
        onWatch(arg1);
        break;
      case GET_PLAYER_REPORT:
        onGetPlayerReport(arg1);
        break;
      case ACCEPT_INVITATION:
        onAcceptInvitation(arg1);
        break;
      case UNINVITED:
        onUninvited(arg1);
        break;
      case YOU_INVITED:
        this.onYouInvited(arg1);
        break;
      case OWN_INFO:
        parseOwnInfo(arg1);
        break;
      case FIBS_BOARD:
        parseFibsBoard(arg1);
        break;
      case DOUBLE:
        break;
      case YOUR_MOVE:
        onYourMove(Integer.parseInt(arg1));
        break;
      case BAD_NEW_USER:
        break;
      case SEND_RESIGN:
        this.writeNetworkMessageln("resign " + arg1);
        break;
      case SEND_MOVE:
        this.writeNetworkMessageln("move "+arg1);
        break;
      case SEND_COMMAND:
        this.writeNetworkMessageln(arg1); 
        break;
      default:
        System.out.println("Dispatcher: Unhandled command " + command);
        throw new RuntimeException("Unhandled command " + command);
    }
  }

  /** Primary entry point to the command dispatcher
   * what takes two strings as an argument
   * @param command The Command to Execute
   * @param arg1 A string that the dispatched function wants
   * @param arg2 Another string that the dispatched function wants
   */
  public void dispatch(Command command, String arg1, String arg2) {
    System.out.println("COMMAND: "+command+" ARG1: "+arg1+" ARG2: "+arg2);
    switch (command) {
    
      case INVITED:
        onInvited(arg1, arg2);
        break;
      case INVITE:
        onInvite(arg1, arg2);
        break;
      case INVITE_WARNING:
        onInviteWarning(arg1, arg2);
        break;
      case MATCH_OVER:
        break;
      default:
        System.out.println("Dispatcher: Unhandled command " + command);
        throw new RuntimeException("Unhandled command" + command);
    }
  }

  /** Primary entry point to the command dispatcher
   * what takes an Object as an argument
   * @param command The Command to Execute
   * @param obj An Object that the dispatched function wants
   */
  public void dispatch(Command command, Object obj) {
    System.out.println("COMMAND: "+command+" OBJ: "+obj);
    boolean b;
    switch (command) {
      case GAME_MOVE:
        break;
      case READY_TO_PLAY:
        b = (Boolean)obj;
        if (!b)
          onYouInvited(null);
        break;
      case TOGGLE_DOUBLE:
        b = (Boolean)obj;
        break;
      case PLAY_CUE:
        break;
      case SET_RATING_GRAPH_CONFIG:
        break;
      default:
        System.out.println("Dispatcher: Unhandled command " + command);
        throw new RuntimeException("Unhandled command" + command);
    }
  }

  
  /** Write a message to the status widget in the registerUser dialog
   * @param s The message to write
   */
  public void writeRegisterUserMessage(String s) {
    System.out.println(s);
  }

  /** Write a message to the System message pane in the specified color.
   * @param type The style of the text
   * @param s The message to write
   */
  public void writeSystemMessage(int type, String s) {
    System.out.println("TYPE:" + type+" - "+s);
  }

  /** Write a message to the System message pane in the specified color.
   * Terminate the message with a crlf.
   * @param type The style of the text
   * @param s The message to write
   */
  public void writeSystemMessageln(int type, String s) {
    System.out.println("TYPE:" + type+" - "+s);
  }

  /** Write a message to the chat message pane in the normal color.
   * Terminate the message with a crlf.
   * @param name The user what sent the message
   * @param cookie The mode used to send the message (shout, whisper, kibitz, etc)
   * @param text The message to write
   */
  public void writeChatMessageln(String name, int cookie, String text) {
    final String mode[] = {"says", "shouts", "whispers", "kibitzes", "say", "shout", "whisper", "kibitz"};
    int cookieMode = cookie - CLIP_SAYS;
    if (cookie == CLIP_YOU_SAY) {
      String[] ss = text.split(" ", 2);
      System.out.println("You tell " + ss[0] + ": " + ss[1] + eol);
      return;
    }
    System.out.println(name + " " + mode[cookieMode] + ": " + text + eol);
  }

  public void writeGameMessageln(String s) {
    System.out.println(s);
  }

  /** Send a message to the fibs server
   * @param s The message to send
   * @return success
   */
  public boolean writeNetworkMessage(String s) {
    if (this.clientConnection != null) {
      this.clientConnection.sendMessage(s);
      return(true);
    } else 
      return(false);
  }

  /** Send a message to the fibs server appending a crlf to the end
   * @param s The message to send
   * @return success
   */
  public boolean writeNetworkMessageln(String s) {
    return(this.writeNetworkMessage(s + eol));
  }


  private void onConnectToServer() {
    startClientConnection(Mode.Run);
  }


  public void startClientConnection(Mode mode) {
    if (this.clientConnection != null) {
      System.out.println("Tear down old ClientConnection");
      this.clientConnection.shutDown();
      this.clientConnection = null;
    }
    System.out.println("Create new ClientConnection");
    this.clientConnection = new ClientConnection();
    parser = new ClientReceiveParser();
    this.clientConnection.setClientAdapter(parser);
    parser.setClientConnection(this.clientConnection);
    parser.setCommandDispatcher(this);
    parser.setMode(mode);
    this.clientConnection.start();
  }

  
  private void onShutdown() {
    onNetworkDisconnect();
  }

  
  private void onNetworkConnected() {
    //INITIALIZE ENVIRONMENT
    writeNetworkMessageln("set boardstyle 3");
    writeNetworkMessageln("set autoboard 1");
    writeNetworkMessageln("set automove 0");
    writeNetworkMessageln("set autodouble 0");
    writeNetworkMessageln("set greedy 0");
    writeNetworkMessageln("set away 0");
    writeNetworkMessageln("set ready 1");
    writeNetworkMessageln("set moves 1");
    System.out.println("NOW WE ARE CONFIGURED!!");
    //writeNetworkMessageln("who");
  }

  private void onNetworkDisconnect() {
    if (this.clientConnection != null) {
      this.clientConnection.shutDown();
      this.clientConnection = null;
    }
  }

  private void onInvite(String playerName, String length) {
    this.writeNetworkMessageln("invite " + playerName + " " + length);
  }

  private void onSavedMatch(String s) {
    System.out.println("SAVED MATCH!");
  }

  private void onMissManners(String s) {
    System.out.println("MISSMANNER: "+s);
  }

  private void onGetPlayerReport(String playerName) {
  }

  private void onWatch(String playerName) {
    this.writeNetworkMessageln("watch " + playerName);
  }

  private void onWatching() {
    this.writeNetworkMessageln("board");
  }

  private void onAcceptInvitation(String playerName) {
    this.writeNetworkMessageln("join " + playerName);
  }

  private void onRollOrDouble() {
  }

  private void onAcceptOrDeclineDouble() {
  }

  private void onToggleReadyToPlay() {
    writeNetworkMessageln("toggle ready");
  }

  private void onToggleGreedyBearoff() {
    writeNetworkMessageln("toggle gready");
  }

  private void onInvited(String playerName, String matchLength) {
    //RICHIAMATO QUANDO MI ARRIVA UN INVITO
    System.out.println("INVITED: "+playerName+" TO: "+matchLength);
  }

  private void onInviteWarning(String playerName, String warning) {
  }

  private void onUninvited(String playerName) {
  }

  private void onYouInvited(String playerName) {
  }

  private void onStartGame() {
  }

  private void onYourMove(int diceToMove) {
  }

  private void parseFibsBoard(String board) {
    System.out.println("FIBSBOARD: "+board);
    //this.mainDialog.getBoard().parseFibsBoard(board);
  }

  private void parseOwnInfo(String s) {
    System.out.println("OWNINFO: "+s);
  }


  @Override
  public void ropChanged(boolean[] rop) {
    //TODO
    //this.properties.setROP(rop);
  }
  
  public void sendLogin(String username, String password) {
    clientConnection.sendLogin(username, password);
  }
  
  public void createAccount() {
    startClientConnection(Mode.Register);
  }
}
