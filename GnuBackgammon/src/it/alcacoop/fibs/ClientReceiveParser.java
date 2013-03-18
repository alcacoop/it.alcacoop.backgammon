/******************************************************************************
 * ClientReceiveParser.java - Parse messages received from the network.
 * $Id: ClientReceiveParser.java,v 1.8 2010/12/29 07:46:35 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */ 
 
package it.alcacoop.fibs;
import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import it.alcacoop.backgammon.logic.FibsBoard;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.fibs.CommandDispatcher.Command;



/** Parse and dispatch messages received from the network.
 * @author Dick Balaska
 * @since 2008/03/31
 * @version $Revision: 1.8 $ <br> $Date: 2010/12/29 07:46:35 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/BuckoFIBS/ClientReceiveParser.java">cvs ClientReceiveParser.java</a>
 */
public class ClientReceiveParser implements FIBSMessages, ClientAdapter {

  private int login_attempt = 0;
  
  public enum Mode {
    Run,
    Register
  }

  private CommandDispatcher commandDispatcher = null;
  private ClientConnection clientConnection = null;
  private Mode mode = Mode.Run;

  private String lastInviter;
  

  public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
    this.commandDispatcher = commandDispatcher;
  }

  public void setClientConnection(ClientConnection clientConnection) {
    this.clientConnection = clientConnection;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
    clientConnection.resetFIBSCookieMonster();
  }


  private void dispatchMessage(int cookie, String s) {
    if (mode == Mode.Register) {
      parseMessageRegister(s, cookie);
      return;
    }
    
    switch (cookie) {
      // These do nothing here.
      case CLIP_MOTD_END:
      case FIBS_BoardstyleSetTo:
      case FIBS_SavedMatchesHeader:
      case FIBS_Empty:
      case FIBS_UsersHeardYou:
      case FIBS_YouAlreadyRolled:
        break;
      case CLIP_OWN_INFO:
        this.commandDispatcher.dispatch(CommandDispatcher.Command.OWN_INFO, s);
        break;
      case CLIP_WHO_INFO:
        parseWhoInfo(s);
        break;
      case CLIP_WHO_END:
        handleWhoEnd();
        break;
      case FIBS_SavedMatchPlaying:
      case FIBS_SavedMatchReady:
      case FIBS_SavedMatch:
        System.out.println(s);
        this.commandDispatcher.dispatch(CommandDispatcher.Command.SAVED_MATCH, s);
        break;
      case FIBS_LoginPrompt:
        System.out.println(s);
        if (login_attempt==0) {
          login_attempt = 1;
          GnuBackgammon.fsm.processEvent(Events.FIBS_CONNECTED, null);
        } else {
          GnuBackgammon.fsm.processEvent(Events.FIBS_LOGIN_ERROR, null);
          login_attempt = 0;
        }
        break;
      case CLIP_LOGIN:
        parsePlayerLoggedIn(s);
        break;
      case CLIP_LOGOUT:
        parsePlayerLoggedOut(s);
        break;
      case FIBS_OpponentLogsOut:
      case FIBS_OpponentLeftGame:
        parseAbortMatch(s);
        break;
      case FIBS_Goodbye:
        System.out.println("BYE BYE");
        disconnectFromServer();
        break;
      case FIBS_Timeout:
        System.out.println(s);
        disconnectFromServer();
        break;
      case CLIP_WELCOME:
        this.commandDispatcher.dispatch(CommandDispatcher.Command.NETWORK_CONNECTED);
        GnuBackgammon.fsm.processEvent(Events.FIBS_LOGIN_OK, null);
        break;
      case FIBS_YouAreWatching:
        System.out.println(s);
        this.commandDispatcher.dispatch(CommandDispatcher.Command.WATCHING);
        break;
      case CLIP_SAYS:
        if (s.startsWith("12 RepBotNG ")) {
          //this.mainDialog.getPlayerReportPane().receiveLine(s, cookie);
          return;
        }
        parseChatMessage(s, cookie);
        break;
      case CLIP_WHISPERS:
      case CLIP_KIBITZES:
        parseChatMessage(s, cookie);
        break;
      case CLIP_SHOUTS:
        if (s.startsWith("13 MissManners"))
          this.commandDispatcher.dispatch(CommandDispatcher.Command.MISS_MANNERS, s);
          parseChatMessage(s, cookie);
        break;
      case CLIP_YOU_SAY:
      case CLIP_YOU_SHOUT:
      case CLIP_YOU_WHISPER:
      case CLIP_YOU_KIBITZ:
        parseYouChatMessage(s, cookie);
        break;
      case FIBS_Waves:
      case FIBS_WavesAgain:
          parseWavesMessage(s);
        break;
      case FIBS_NewMatchRequest:
      case FIBS_ResumeMatchRequest:
        parseInvite(s);
        break;
      case FIBS_CantInviteSelf:
      case FIBS_NotInteresting:
      case FIBS_PlayerNotPlaying:
      case FIBS_PlayerRefusingGames:
      case FIBS_NotYourTurnToRoll:
      case FIBS_NotYourTurnToMove:
      case FIBS_PlayerBannedWatch:
      case FIBS_DoesntWantYouToWatch:
        showErrorMessage(s);
        break;
      case FIBS_StartingNewGame:
        startNewGame();
        break;
      case FIBS_ResumeMatchAck0:
      case FIBS_ResumeMatchAck5:
        parseGameMessage(s);
        startNewGame();
        break;
      case FIBS_Turn:
        parseGameMessage(s);
        parseResumeMatchTurn(s);
        break;
      case FIBS_MatchLength:
        parseGameMessage(s);
        parseResumeMatchLength(s);
        break;
      case FIBS_Board:
        System.out.println(s);
        parseBoard(s);
        break;
      case FIBS_YouWinMatch:
      case FIBS_PlayerWinsMatch:
        parseMatchOverMessage(s, cookie);
        break;
      case FIBS_ReadyTrue:
      case FIBS_ReadyFalse:
        parseReadyMessage(cookie);
        break;
      case FIBS_AcceptRejectDouble:
        handleAcceptRejectDouble();
        break;
      case FIBS_JoinNextGame: 
        this.commandDispatcher.writeNetworkMessageln("join");
        break;
      case FIBS_PlayerRolls:
        parseRoll(s);
        break;
        
      case FIBS_PlayerMoves:
        parseMove(s);
        break;
        
      case FIBS_FirstRoll:
        System.out.println("FIRST ROLL: "+s);
        break;
        
      case FIBS_CantMove:
        parseNoMoves();
        break;
      
      case FIBS_MakesFirstMove:
      case FIBS_OnlyPossibleMove:
      case FIBS_PlayerWinsGame:
      case FIBS_ScoreUpdate:
      case FIBS_ResignWins:
      case FIBS_PlayerStartsWatching:
      case FIBS_WatchResign:
      case FIBS_ResignRefused:
      case FIBS_AcceptWins:
      case FIBS_PointsFor:
      case FIBS_YouWinGame:
      case FIBS_CantMoveFirstMove:
      case FIBS_RollBeforeMove:
      case FIBS_Done:
      case FIBS_GreedyTrue:
      case FIBS_GreedyFalse:
      case FIBS_YouAcceptAndWin:
      case FIBS_YouResign:
      case FIBS_ResignYouWin:
      case FIBS_PleaseWaitForJoin:
      case FIBS_YouGiveUp:
      case FIBS_YouReject:
      case FIBS_MustMove:
        parseGameMessage(s);
        break;
      case FIBS_PlayerAcceptsDouble:
        parseGameMessage(s);
        break;
      case FIBS_YouCantMove:
        parseGameMessage(s);
        break;
      case FIBS_BearingOff:
        parseGameMessage(s);
        break;

      case FIBS_PlayersStartingMatch:
      case FIBS_MatchResult:
      case FIBS_PlayerStopsWatching:
      case FIBS_PlayersStartingUnlimitedMatch:
      case FIBS_ResumingLimitedMatch:
      case FIBS_ResumingUnlimitedMatch:
        parseOtherMatchMessage(s);
        break;
      case FIBS_PlayerInfoStart:
      case FIBS_LastLogin:
      case FIBS_StillLoggedIn:
      case FIBS_NotLoggedIn:
      case FIBS_ReadyWatchingPlaying:
      case FIBS_RatingExperience:
      case FIBS_NoEmail:
      case FIBS_EmailAddress:
      case FIBS_IsPlayingWith:
      case FIBS_HasSavedGames:
      case FIBS_HasNoSavedGames:
      case FIBS_NoInfo:
        break;
      case FIBS_PreLogin:
      case CLIP_MOTD_BEGIN:
      case FIBS_TypeJoin:
      case FIBS_YouStopWatching:
      case FIBS_NewMatchAck2:
      case FIBS_YouAlreadyPlaying:
      case FIBS_AlreadyPlaying:
      case FIBS_LastLogout:
      case FIBS_NotPlaying:
      case FIBS_NoUser:
      case FIBS_EchoJunk:
      case FIBS_WaitForLastInvitation:
      case FIBS_WaitForAcceptResign:
        System.out.println(s);
        break;
      case FIBS_DidntInvite:
      case FIBS_NotWatchingPlaying:
      case FIBS_NoSavedGames:
        System.out.println(s);
        break;
      case FIBS_NewMatchAck10:
        System.out.println(s);
        break;
      case FIBS_WARNINGSavedMatch:
        parseInviteWarning(s);
        break;
      case FIBS_DoubleTrue:
        parseDoublesOnOff(true);
        parseGameMessage(s);
        break;
      case FIBS_DoubleFalse:
        parseDoublesOnOff(false);
        parseGameMessage(s);
        break;
      case FIBS_Doubles:
        parseDoubles(s);
        parseGameMessage(s);
        break;
      case FIBS_YouDouble:
        break;
      case FIBS_YouAcceptDouble:
        break;
      case FIBS_RollOrDouble:
        this.commandDispatcher.dispatch(CommandDispatcher.Command.ROLL_OR_DOUBLE);
        break;
      case FIBS_YouRoll:
        parseRoll(s);
        break;
      case FIBS_YourTurnToMove: // First roll of the game, always 2 moves
        this.commandDispatcher.dispatch(CommandDispatcher.Command.YOUR_MOVE, "2");
        break;
      case FIBS_YouInvited:
        parseYouInvited(s);
        break;
      case FIBS_PleaseMove:
        parsePleaseMove(s);
        parseGameMessage(s);
        break;
      case FIBS_PlayerWantsToResign:
        parsePlayerWantsToResign(s);
        break;
      case FIBS_ATTENTION:
      case FIBS_ShuttingDown:
      case FIBS_Rebooting:
        //TODO
      case FIBS_GamesWillBeSaved:
      case FIBS_UnknownCommand:
      case FIBS_GameWasSaved:
        System.out.println(s);
        break;
      case FIBS_Unknown:
        System.out.println("Unknown message from FIBS: '" + s + "'");
        break;
      default:
        break;
    }
  }

  /** Replay a message from a saved game or a previous move in this game
   * @param s
   */
  public void parseReplayMessage(int cookie, String s) {
    switch (cookie) {
      case FIBS_Board:
        this.commandDispatcher.dispatch(CommandDispatcher.Command.FIBS_BOARD, s);
        break;
      default:
        System.out.println("Replay? " + cookie + " '" + s + "'");
        break;
    }
  }

  private enum RegState {
    name,
    password,
    password1,
    done,
    bye
  }
  
  private RegState regState = RegState.name;

  private void parseMessageRegister(String s, int fibsCookie) {
    System.out.println("MODE REGISTER: "+s);
    switch (fibsCookie) {
    
      case FIBS_LoginPrompt:
        commandDispatcher.writeNetworkMessageln("guest");
        break;
      
        
      case FIBS_GiveUsername:
        if (regState == RegState.name) {
          /*
          System.out.println("GIVE USERNAME!");
          String st = Math.round(Math.random()*1000000)+"";
          char c[] = {'a', 'b','c','d','e','f','g','h','i','j'};
          String sfx = "";
          for (int i=0;i<st.length();i++)
            sfx += c[Integer.parseInt(st.substring(i,i+1))];
          commandDispatcher.writeNetworkMessageln("name pluto_"+sfx);
          */
          commandDispatcher.writeNetworkMessageln("name "+GnuBackgammon.Instance.FibsUsername);
        } else {
          //ERROR.. END CONNECTION
          commandDispatcher.writeNetworkMessageln("bye");
          GnuBackgammon.fsm.processEvent(Events.FIBS_ACCOUNT_OK, null);
          //GnuBackgammon.fsm.processEvent(Events.FIBS_ERROR, null);
        }
        break;
        
      case FIBS_UseAnotherName:
        System.out.println("PRESENT!");
        commandDispatcher.writeNetworkMessageln("bye");
        GnuBackgammon.fsm.processEvent(Events.FIBS_ACCOUNT_PRESENT, null);
        regState = RegState.done;
        break;
        
      case FIBS_GivePassword:
        commandDispatcher.writeNetworkMessageln(GnuBackgammon.Instance.FibsPassword);
        break;
        
      case FIBS_RetypePassword:
        commandDispatcher.writeNetworkMessageln(GnuBackgammon.Instance.FibsPassword);
        break;

      case FIBS_YouAreRegistered:
        System.out.println("SUCCESS!");
        GnuBackgammon.fsm.processEvent(Events.FIBS_ACCOUNT_OK, null);
        regState = RegState.done;
        break;
      
      case FIBS_TooMuchAccounts:
        System.out.println("SPAM!");
        GnuBackgammon.fsm.processEvent(Events.FIBS_ACCOUNT_SPAM, null);
        regState = RegState.done;
        break;
    }
  }

  private void showErrorMessage(String s) {
    System.out.println("ERRORE: "+s);
  }

  private void disconnectFromServer() {
    System.out.println("DISCONNECT FROM SERVER NOW!");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.SHUTTING_DOWN);
    this.clientConnection.resetFIBSCookieMonster();
  }

  private void parseWhoInfo(String s) {
    Player p = new Player();
    p.parsePlayer(s);
    //TODO
    this.commandDispatcher.dispatch(CommandDispatcher.Command.PLAYER_CHANGED, p);
    System.out.println(s);
  }

  private void handleWhoEnd() {
  }

  private void parsePlayerLoggedOut(String s) {
    String[] ss = s.split(" ");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.PLAYER_GONE, ss[1]);
    System.out.println(ss[2] + " " + ss[3] + " " + ss[4]);
  }

  private void parseAbortMatch(String s) {
    System.out.println(s);
    this.commandDispatcher.writeNetworkMessageln("show saved");
  }

  /** Just log the player to the messages list.
   * Adding the player to the table comes with the CLIP_WHO_INFO message
   * @param s The Login message
   */
  private void parsePlayerLoggedIn(String s) {
    String[] ss = s.split(" ");
    System.out.println("LOGGED IN");
    System.out.println(ss[2] + " " + ss[3] + " " + ss[4]);
  }

  private void parseOtherMatchMessage(String s) {
    System.out.println(s);
  }

  private void parseInvite(String s) {
    String[] ss = s.split(" ");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.INVITED, 
        ss[0], 
        ss[3].equals("resume") ? ss[3] : ss[5]);
    lastInviter = ss[0];
    commandDispatcher.dispatch(Command.SEND_COMMAND, "join "+ss[0]);
  }

  private void parseInviteWarning(String s) {
    if (lastInviter != null) {
      this.commandDispatcher.dispatch(CommandDispatcher.Command.INVITE_WARNING, lastInviter, s);
    }
  }

  private void parseYouInvited(String s) {
    String[] ss = s.split(" ");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.YOU_INVITED, ss[3]);
    System.out.println(s);
  }

  private void parseDoubles(String s) {
    this.commandDispatcher.writeNetworkMessageln("board");
  }

  private void startNewGame() {
    this.commandDispatcher.dispatch(CommandDispatcher.Command.START_GAME);
  }

  private void parseGameMessage(String s) {
    this.commandDispatcher.writeGameMessageln(s);
  }

  private void parsePleaseMove(String s) {
    String[] ss = s.split(" ");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.YOUR_MOVE, ss[2]);
  }

  private void parsePlayerWantsToResign(String s) {
    String[] ss = s.split("\\.");
    ss[2] = "";
    ss[0] += ".";
    ss[1] += ".";
    this.commandDispatcher.writeGameMessageln(ss[0] + ss[1]);
  }

  private void parseDoublesOnOff(boolean onoff) {
    this.commandDispatcher.dispatch(CommandDispatcher.Command.TOGGLE_DOUBLE, new Boolean(onoff));

  }

  private void parseReadyMessage(int cookie) {
    boolean b = false;
    if (cookie == FIBS_ReadyTrue)
      b = true;
    this.commandDispatcher.dispatch(CommandDispatcher.Command.READY_TO_PLAY, new Boolean(b));
  }

  private void parseChatMessage(String s, int cookie) {
    int i = s.indexOf(' ', 3);
    if (i == -1)
      return;
    String name = s.substring(3, i);
    String text = s.substring(i+1);
    this.commandDispatcher.writeChatMessageln(name, cookie, text);
  }

  private void parseYouChatMessage(String s, int cookie) {
    this.commandDispatcher.writeChatMessageln("You", cookie, s.substring(3));
  }

  private void parseWavesMessage(String s) {
    System.out.println(s);
  }

  private void parseResumeMatchTurn(String s) {
  }

  private void parseResumeMatchLength(String s) {

  }

  private void handleAcceptRejectDouble() {
    this.commandDispatcher.dispatch(CommandDispatcher.Command.ACCEPT_OR_DECLINE_DOUBLE);
  }


  /* (non-Javadoc)
   * @see com.buckosoft.fibs.net.ClientAdapter#dispatch(java.lang.String)
   */
  @Override
  public void dispatch(int cookie, String s) {
    this.dispatchMessage(cookie, s);
  }


  @Override
  public void connectionAborted() {
    this.commandDispatcher.dispatch(CommandDispatcher.Command.DISCONNECT_FROM_NETWORK);
  }

  

  public void parseNoMoves() {
    GnuBackgammon.Instance.fibs.post(Events.FIBS_NOMOVES, null);
  }
  
  
  public void parseBoard(String s) {
    FibsBoard b = GnuBackgammon.Instance.fibs.obtainBoard(s); 
    GnuBackgammon.Instance.fibs.post(Events.FIBS_BOARD, b);
  }
  
  
  public void parseMove(String s) {
    s = s.replaceAll("-", " ");
    s = s.replaceAll("\\.", "");
    
    String tmp[] = s.split(" ");
    for (int k=2;k<tmp.length;k++) {
      if (tmp[k].contains("b")) 
        if (MatchState.FibsDirection==-1) tmp[k] = "0";
        else tmp[k] = "25";
      if (tmp[k].contains("o"))
        if (MatchState.FibsDirection==-1) tmp[k] = "25";
        else tmp[k] = "0";
    }
    
    int moves[] = {-1,-1,-1,-1,-1,-1,-1,-1};
    for (int i=0;i<tmp.length-2;i++)
      moves[i] = Integer.parseInt(tmp[2+i]);
      
    //NORMALIZE MOVES TO GNUBG CONVENTION...
    if (MatchState.FibsDirection==-1) { //LUI MUOVE DA 1 A 24
      for (int i=0;i<8;i++)
        if (moves[i]!=-1) moves[i]=24-moves[i];
    } else {
      for (int i=0;i<8;i++)
        if (moves[i]!=-1) moves[i]=moves[i]-1;
    }
    GnuBackgammon.Instance.fibs.post(Events.FIBS_MOVES, moves);
  }

  
  private void parseRoll(String s) {
    s = s.replace(".","");
    String t[] = s.split(" ");
    int d[] = {0,0};
    d[0] = Integer.parseInt(t[2]);
    d[1] = Integer.parseInt(t[4]);
    //GnuBackgammon.fbt.post(Events.FIBS_ROLLS, d);
  }
  
  private void parseMatchOverMessage(String s, int cookie) {
    String[] ss = s.split(" ");
    this.commandDispatcher.dispatch(CommandDispatcher.Command.MATCH_OVER, ss[0], ss[6]);
    GnuBackgammon.fsm.processEvent(Events.FIBS_MATCHOVER, s);
  }
}