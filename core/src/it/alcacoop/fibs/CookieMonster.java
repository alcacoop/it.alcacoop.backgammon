/******************************************************************************
 * CookieMonster.java - Parse messages received from the network.
 * $Id: CookieMonster.java,v 1.5 2010/12/30 20:56:30 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */

package it.alcacoop.fibs;

import it.alcacoop.backgammon.GnuBackgammon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;


/**
 * Implementation of the CookieMonster API from fibs
 * 
 * @author cppc
 * @author Dick Balaska
 * @author Original author of C version says not to use his name
 * @since 2008/04/08
 * @version $Revision: 1.5 $ <br>
 *          $Date: 2010/12/30 20:56:30 $
 * @see <a href="http://www.fibs.com/fcm/">http://www.fibs.com/fcm/</a> <br>
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/main/java/com/buckosoft/fibs/net/CookieMonster.java">cvs CookieMonster.java</a>
 */
public class CookieMonster implements FIBSMessages {
  private final static boolean DEBUG = false;
  private ClientConnection clientConnection; // needed to pushback any runon strings

  private class CookieDough {
    public Pattern regex = null;
    public int message = 0;
  }

  private final int FIBS_UNINITIALIZED = 1,
      FIBS_LOGIN = 2,
      FIBS_MOTD = 3,
      FIBS_RUN = 4,
      FIBS_LOGOUT = 5,
      FIBS_REGISTER = 6;

  private int messageState = FIBS_UNINITIALIZED;

  private LinkedList<CookieDough> alphaBatch;
  private LinkedList<CookieDough> starsBatch;
  private LinkedList<CookieDough> numericBatch;
  private LinkedList<CookieDough> loginBatch;
  private LinkedList<CookieDough> registerBatch;
  private LinkedList<CookieDough> motdBatch;

  public void setClientConnection(ClientConnection clientConnection) {
    this.clientConnection = clientConnection;
  }

  /**
   * Parse this message into a fibs cookie
   * 
   * @param message
   *          A single line from fibs
   * @return The cookie that matches this string
   */
  public int fIBSCookie(String message) {
    if (messageState == FIBS_UNINITIALIZED) {
      prepareBatches();
    }
    int result = FIBS_Unknown;
    Iterator<CookieMonster.CookieDough> iter;
    CookieDough ptr = null;

    switch (messageState) {
      case FIBS_UNINITIALIZED:
        return (FIBS_BAD_COOKIE);
      case FIBS_RUN:
        if (message.length() == 0)
          return (FIBS_Empty);
        char ch = message.charAt(0);
        if (ch >= '0' && ch <= '9') {
          iter = numericBatch.iterator();
          while (iter.hasNext()) {
            ptr = iter.next();
            if (ptr.regex.matcher(message).find()) {
              result = ptr.message;
              break;
            }
          }
        } else if (ch == '*') {
          iter = starsBatch.iterator();
          while (iter.hasNext()) {
            ptr = iter.next();
            if (ptr.regex.matcher(message).find()) {
              result = ptr.message;
              break;
            }
          }
        } else {
          iter = alphaBatch.iterator();
          while (iter.hasNext()) {
            ptr = iter.next();
            if (ptr.regex.matcher(message).find()) {
              result = ptr.message;
              break;
            }
          }
        }
        if (result == FIBS_Goodbye)
          messageState = FIBS_LOGOUT;
        break;
      case FIBS_LOGIN:
        result = FIBS_PreLogin;
        iter = loginBatch.iterator();
        while (iter.hasNext()) {
          ptr = iter.next();
          if (ptr.regex.matcher(message).find()) {
            result = ptr.message;
            break;
          }
        }
        if (result == CLIP_MOTD_BEGIN)
          messageState = FIBS_MOTD;
        if (result == FIBS_WelcomeToFibs)
          messageState = FIBS_REGISTER;
        break;
      case FIBS_MOTD:
        result = FIBS_MOTD;
        iter = motdBatch.iterator();
        while (iter.hasNext()) {
          ptr = iter.next();
          if (ptr.regex.matcher(message).find()) {
            result = ptr.message;
            break;
          }
        }
        if (result == CLIP_MOTD_END)
          messageState = FIBS_RUN;
        break;
      case FIBS_REGISTER:
        result = FIBS_MOTD;
        iter = registerBatch.iterator();
        while (iter.hasNext()) {
          ptr = iter.next();
          if (ptr.regex.matcher(message).find()) {
            result = ptr.message;
            break;
          }
        }
        break;
      case FIBS_LOGOUT:
        return (FIBS_PostGoodbye);
    }
    if (result == FIBS_Unknown)
      return (FIBS_Unknown);
    String[] ss = ptr.regex.split(message, 2);
    if (ss.length > 1 && ss[1].length() > 0) {
      if (DEBUG) {
        GnuBackgammon.out.println("cookie = " + result);
        GnuBackgammon.out.println("message = '" + message + "'");
        GnuBackgammon.out.println("Leftover = '" + ss[1] + "'");
      }
      clientConnection.pushBack(ss[1]);
    }
    return (result);
  }

  /**
   * big reset, Not really needed.
   */
  public void reset() {
    messageState = FIBS_UNINITIALIZED;
  }

  /**
   * Call this function to reset before reconnecting to FIBS.
   * If the batches have already been initialized, just reset the<br>
   * message state, else do the initialization.<br>
   * <br>
   * Note that it is not necessary to call this function before calling<br>
   * fIBSCookie(), which calls prepareBatches() on first use if needed.<br>
   * You can call this function first however, to make sure things<br>
   * are ready to go when you connect.<br>
   */
  public void resetFIBSCookieMonster()
  {
    if (messageState == FIBS_UNINITIALIZED)
      prepareBatches();
    else
      messageState = FIBS_LOGIN;
  }


  LinkedList<CookieDough> currentBatchBuild;

  private void addDough(int msg, String re) {
    CookieDough newDough = new CookieDough();

    newDough.regex = Pattern.compile(re);
    newDough.message = msg;
    currentBatchBuild.add(newDough);
    // return newDough;
  }

  private static String PL = "[a-zA-Z0-9_<>]+";

  private void prepareBatches() {
    currentBatchBuild = new LinkedList<CookieDough>();

    // addDough(FIBS_Board, "^board:" + PL + ":" + PL + ":[0-9:\\-]+$"); // 52 colons
    addDough(FIBS_Board, "^board:" + PL + ":" + PL + "(:\\-*[0-9]+){49}:[0-9]"); // 52 colons
    // addDough(FIBS_BAD_Board, "^board:");
    addDough(FIBS_YouRoll, "^You roll [1-6] and [1-6]\\.");
    addDough(FIBS_PlayerRolls, "^" + PL + " rolls [1-6] and [1-6]\\.");

    addDough(FIBS_RollOrDouble, "^It's your turn to roll or double\\.?");
    addDough(FIBS_RollOrDouble, "^It's your turn\\. Please roll or double\\.?");
    addDough(FIBS_AcceptRejectDouble, "doubles\\. Type 'accept' or 'reject'\\.");
    addDough(FIBS_Doubles, "^" + PL + " doubles\\.");
    addDough(FIBS_PlayerAcceptsDouble, "accepts the double\\. The cube shows [0-9]+\\.");
    addDough(FIBS_PleaseMove, "^Please move [1-4] pieces?\\.");
    // addDough(FIBS_PlayerMoves, "^" + PL + " moves .*-off ");
    addDough(FIBS_PlayerMoves, "^" + PL + " moves .*\\. ?");
    addDough(FIBS_PlayerMoves, "^" + PL + " moves .*$"); // XXX: run to the end of line, doesn't handle runons.
    addDough(FIBS_BearingOff, "^Bearing off: [0-9]+ o .*$"); // XXX: run to the end of line, doesn't handle runons.
    addDough(FIBS_BearingOff, "^Bearing off: [0-9]+ o [0-9]+ o ");
    addDough(FIBS_YouReject, "^You reject\\. The game continues\\.");
    addDough(FIBS_YouStopWatching, "You're not watching anymore\\."); // overloaded //PLAYER logs out.. You're not watching anymore.
    addDough(FIBS_OpponentLogsOut, "The game was saved\\."); // PLAYER logs out. The game was saved. || PLAYER drops connection. The game was saved.
    addDough(FIBS_GameWasSaved, "Your game with " + PL + " was saved\\."); // PLAYER logs out. The game was saved. || PLAYER drops connection. The game was saved.
    addDough(FIBS_OnlyPossibleMove, "^The only possible move is .* \\.");
    addDough(FIBS_FirstRoll, "" + PL + " rolled [1-6].+rolled [1-6]");
    addDough(FIBS_MakesFirstMove, " makes the first move\\.");
    addDough(FIBS_YouDouble, "^You double\\. Please wait for " + PL + " to accept or reject\\."); // You double. Please wait for PLAYER to accept or reject.
    addDough(FIBS_PlayerWantsToResign, "^" + PL + " wants to resign\\. You will win [0-9]+ points?\\. Type 'accept' or 'reject'\\.");
    addDough(FIBS_WatchResign, "^" + PL + " wants to resign\\. " + PL + " will win [0-9]+ points?\\."); // PLAYER wants to resign. PLAYER2 will win 2 points. (ORDER MATTERS HERE)
    addDough(FIBS_YouResign, "^You want to resign\\. " + PL + " will win [0-9]+ points?\\."); // You want to resign. PLAYER will win 1 .
    addDough(FIBS_ResumeMatchAck5, "^You are now playing with " + PL + "\\. Your running match was loaded\\.");
    addDough(FIBS_JoinNextGame, "^Type 'join' if you want to play the next game, type 'leave' if you don't\\.");
    addDough(FIBS_NewMatchRequest, "^" + PL + " wants to play a [0-9]+ point match with you\\.");
    addDough(FIBS_WARNINGSavedMatch, "^WARNING: Don't accept if you want to continue .* match\\!");
    addDough(FIBS_ResignRefused, "rejects\\. The game continues\\.");
    addDough(FIBS_MatchLength, "^match length: [0-9]+");
    addDough(FIBS_TypeJoin, "^Type 'join " + PL + "' to accept\\.");
    addDough(FIBS_YouAreWatching, "^You're now watching " + PL + "\\.");
    addDough(FIBS_YouStopWatching, "^You stop watching " + PL + "\\."); // overloaded
    addDough(FIBS_PlayerStartsWatching, "" + PL + " starts watching " + PL + "\\.");
    addDough(FIBS_PlayerStartsWatching, "" + PL + " is watching you\\.");
    addDough(FIBS_PlayerStopsWatching, "" + PL + " stops watching " + PL + "\\.");
    addDough(FIBS_PlayerIsWatching, "" + PL + " is watching ");
    addDough(FIBS_NotInteresting, "" + PL + " is not doing anything interesting\\.");

    addDough(FIBS_ResignWins, "^" + PL + " gives up\\. " + PL + " wins [0-9]+ points?\\."); // PLAYER1 gives up. PLAYER2 wins 1 point.
    addDough(FIBS_ResignYouWin, "^" + PL + " gives up\\. You win [0-9]+ points?\\.");
    addDough(FIBS_YouAcceptAndWin, "^You accept and win [0-9]+ points?\\.");
    addDough(FIBS_AcceptWins, "^" + PL + " accepts and wins [0-9]+ points?\\."); // PLAYER accepts and wins N points.
    addDough(FIBS_PlayersStartingMatch, "^" + PL + " and " + PL + " start a [0-9]+ point match\\."); // PLAYER and PLAYER start a <n> point match.
    addDough(FIBS_StartingNewGame, "^Starting a new game with " + PL + "\\.");
    addDough(FIBS_YouGiveUp, "^You give up\\. " + PL + " wins [0-9]+ points?\\.");
    addDough(FIBS_YouWinMatch, "^You win the [0-9]+ point match [0-9]+-[0-9]+ \\.");
    addDough(FIBS_PlayerWinsMatch, "^" + PL + " wins the [0-9]+ point match [0-9]+-[0-9]+ \\."); // PLAYER wins the 3 point match 3-0 .
    addDough(FIBS_ResumingUnlimitedMatch, "^" + PL + " and " + PL + " are resuming their unlimited match\\.");
    addDough(FIBS_ResumingLimitedMatch, "^" + PL + " and " + PL + " are resuming their [0-9]+-point match\\.");
    addDough(FIBS_MatchResult, "^" + PL + " wins a [0-9]+ point match against " + PL + " [0-9]+-[0-9]+ \\."); // PLAYER wins a 9 point match against PLAYER 11-6 .
    addDough(FIBS_PlayerWantsToResign, "wants to resign\\."); // Same as a longline in an actual game This is just for watching.

    // addDough(FIBS_BAD_AcceptDouble, "^" + PL + " accepts? the double\\. The cube shows [0-9]+\\..+");
    addDough(FIBS_YouAcceptDouble, "^You accept the double\\. The cube shows [0-9]+\\.");
    addDough(FIBS_PlayerAcceptsDouble, "^" + PL + " accepts the double\\. The cube shows ");
    addDough(FIBS_PlayerAcceptsDouble, "^" + PL + " accepts the double\\."); // while watching
    addDough(FIBS_ResumeMatchRequest, "^" + PL + " wants to resume a saved match with you\\.");
    addDough(FIBS_ResumeMatchAck0, "has joined you\\. Your running match was loaded\\.");
    addDough(FIBS_YouWinGame, "^You win the game and get [0-9]+ points?\\. Congratulations?\\!"); // Jibs spells congratulations wrong.
    addDough(FIBS_UnlimitedInvite, "^" + PL + " wants to play an unlimted match with you\\.");
    addDough(FIBS_PlayerWinsGame, "^" + PL + " wins the game and gets [0-9]+ points?. Sorry.");
    addDough(FIBS_PlayerWinsGame, "^" + PL + " wins the game and gets [0-9]+ points?. "); // (when watching)
    addDough(FIBS_WatchGameWins, "wins the game and gets");
    addDough(FIBS_PlayersStartingUnlimitedMatch, "start an unlimited match\\."); // PLAYER_A and PLAYER_B start an unlimited match.
    addDough(FIBS_ReportLimitedMatch, "^" + PL + " +- +" + PL + " .+ point match"); // PLAYER_A - PLAYER_B (5 point match 2-2)
    addDough(FIBS_ReportUnlimitedMatch, "^" + PL + " +- +" + PL + " \\(unlimited");
    addDough(FIBS_ShowMovesStart, "^" + PL + " is X - " + PL + " is O");
    addDough(FIBS_ShowMovesRoll, "^[XO]: \\([1-6]"); // ORDER MATTERS HERE
    addDough(FIBS_ShowMovesWins, "^[XO]: wins");
    addDough(FIBS_ShowMovesDoubles, "^[XO]: doubles");
    addDough(FIBS_ShowMovesAccepts, "^[XO]: accepts");
    addDough(FIBS_ShowMovesRejects, "^[XO]: rejects");
    addDough(FIBS_ShowMovesOther, "^[XO]:"); // AND HERE
    addDough(FIBS_ScoreUpdate, "^score in [0-9]+ point match: " + PL + "-[0-9]+ " + PL + "-[0-9]+");
    addDough(FIBS_MatchStart, "^Score is [0-9]+-[0-9]+ in a [0-9]+ point match\\.");
    addDough(FIBS_Settings, "^Settings of variables:");
    addDough(FIBS_Turn, "^turn: " + PL + "\\.");
    addDough(FIBS_Boardstyle, "^boardstyle:");
    addDough(FIBS_Linelength, "^linelength:");
    addDough(FIBS_Pagelength, "^pagelength:");
    addDough(FIBS_Redoubles, "^redoubles:");
    addDough(FIBS_Sortwho, "^sortwho:");
    addDough(FIBS_Timezone, "^timezone:");
    addDough(FIBS_YouCantMove, "^You can't move\\.");
    addDough(FIBS_CantMove, "^" + PL + " can't move\\."); // PLAYER can't move
    addDough(FIBS_ListOfGames, "^List of games:");
    addDough(FIBS_PlayerInfoStart, "^Information about " + PL + ":");
    addDough(FIBS_EmailAddress, "^ Email address:.*");
    addDough(FIBS_NoEmail, "^ No email address\\.");
    addDough(FIBS_WavesAgain, "^" + PL + " waves goodbye again\\.");
    addDough(FIBS_Waves, "waves goodbye\\.");
    addDough(FIBS_Waves, "^You wave goodbye\\.");
    addDough(FIBS_WavesAgain, "^You wave goodbye again and log out\\.");
    addDough(FIBS_NoSavedGames, "^no saved games\\.");
    addDough(FIBS_HasSavedGames, "^" + PL + " has [0-9]+ saved games?\\.");
    addDough(FIBS_HasNoSavedGames, "^" + PL + " has no saved games\\.");
    addDough(FIBS_TypeBack, "^You're away\\. Please type 'back'");
    addDough(FIBS_SavedMatch, "^ " + PL + " +[0-9]+ +[0-9]+ +- +[0-9]+");
    addDough(FIBS_SavedMatchPlaying, "^ \\*" + PL + " +[0-9]+ +[0-9]+ +- +[0-9]+");
    // NOTE: for FIBS_SavedMatchReady, see the Stars message, because it will appear to be one of those (has asterisk at index 0).
    addDough(FIBS_PlayerIsWaitingForYou, "^" + PL + " is waiting for you to log in\\.");
    addDough(FIBS_IsAway, "^" + PL + " is away: ");
    addDough(FIBS_AllowpipTrue, "^allowpip +YES");
    addDough(FIBS_AllowpipFalse, "^allowpip +NO");
    addDough(FIBS_AutoboardTrue, "^autoboard +YES");
    addDough(FIBS_AutoboardFalse, "^autoboard +NO");
    addDough(FIBS_AutodoubleTrue, "^autodouble +YES");
    addDough(FIBS_AutodoubleFalse, "^autodouble +NO");
    addDough(FIBS_AutomoveTrue, "^automove +YES");
    addDough(FIBS_AutomoveFalse, "^automove +NO");
    addDough(FIBS_BellTrue, "^bell +YES");
    addDough(FIBS_BellFalse, "^bell +NO");
    addDough(FIBS_CrawfordTrue, "^crawford +YES");
    addDough(FIBS_CrawfordFalse, "^crawford +NO");
    addDough(FIBS_DoubleTrue, "^double +YES");
    addDough(FIBS_DoubleFalse, "^double +NO");
    addDough(FIBS_MoreboardsTrue, "^moreboards +YES");
    addDough(FIBS_MoreboardsFalse, "^moreboards +NO");
    addDough(FIBS_MovesTrue, "^moves +YES");
    addDough(FIBS_MovesFalse, "^moves +NO");
    addDough(FIBS_GreedyTrue, "^greedy +YES");
    addDough(FIBS_GreedyFalse, "^greedy +NO");
    addDough(FIBS_NotifyTrue, "^notify +YES");
    addDough(FIBS_NotifyFalse, "^notify +NO");
    addDough(FIBS_RatingsTrue, "^ratings +YES");
    addDough(FIBS_RatingsFalse, "^ratings +NO");
    addDough(FIBS_ReadyTrue, "^ready +YES");
    addDough(FIBS_ReadyFalse, "^ready +NO");
    addDough(FIBS_ReportTrue, "^report +YES");
    addDough(FIBS_ReportFalse, "^report +NO");
    addDough(FIBS_SilentTrue, "^silent +YES");
    addDough(FIBS_SilentFalse, "^silent +NO");
    addDough(FIBS_TelnetTrue, "^telnet +YES");
    addDough(FIBS_TelnetFalse, "^telnet +NO");
    addDough(FIBS_WrapTrue, "^wrap +YES");
    addDough(FIBS_WrapFalse, "^wrap +NO");
    addDough(FIBS_Junk, "^Closed old connection with user");
    addDough(FIBS_Done, "^Done\\.");
    addDough(FIBS_YourTurnToMove, "^It's your turn to move\\.");
    addDough(FIBS_SavedMatchesHeader, "^ opponent matchlength score \\(your points first\\)");
    addDough(FIBS_MessagesForYou, "^There are messages for you:");
    addDough(FIBS_RedoublesSetTo, "^Value of 'redoubles' set to [0-9]+\\.");
    addDough(FIBS_BoardstyleSetTo, "^Value of 'boardstyle' set to [0-9]+\\.");
    addDough(FIBS_DoublingCubeNow, "^The number on the doubling cube is now [0-9]+");
    addDough(FIBS_FailedLogin, "^> [0-9]+"); // bogus CLIP messages sent after a failed login
    addDough(FIBS_Average, "^Time (UTC) average min max");
    addDough(FIBS_DiceTest, "^[nST]: ");
    addDough(FIBS_LastLogout, "^ Last logout:.*");
    addDough(FIBS_RatingCalcStart, "^rating calculation:");
    addDough(FIBS_RatingCalcInfo, "^Probability that underdog wins:");
    addDough(FIBS_RatingCalcInfo, "is 1-Pu if underdog wins"); // P=0.505861 is 1-Pu if underdog wins and Pu if favorite wins
    addDough(FIBS_RatingCalcInfo, "^Experience: "); // Experience: fergy 500 - jfk 5832
    addDough(FIBS_RatingCalcInfo, "^K=max\\(1"); // K=max(1 , -Experience/100+5) for fergy: 1.000000
    addDough(FIBS_RatingCalcInfo, "^rating difference");
    addDough(FIBS_RatingCalcInfo, "^change for"); // change for fergy: 4*K*sqrt(N)*P=2.023443
    addDough(FIBS_RatingCalcInfo, "^match length ");
    addDough(FIBS_WatchingHeader, "^Watching players:");
    addDough(FIBS_SettingsHeader, "^The current settings are:");
    addDough(FIBS_AwayListHeader, "^The following users are away:");
    addDough(FIBS_RatingExperience, "^ Rating: +[0-9]+\\..*Experience: [0-9]+"); // Rating: 1693.11 Experience: 5781
    addDough(FIBS_ReadyWatchingPlaying, "^ " + PL + ".* playing\\."); // dickbalaska is ready to play, not watching, not playing.

    addDough(FIBS_NotLoggedIn, "^ Not logged in right now\\.");
    addDough(FIBS_IsPlayingWith, "is playing with " + PL + "\\.");
    addDough(FIBS_SavedScoreHeader, "^opponent +matchlength"); // opponent matchlength score (your points first)
    addDough(FIBS_StillLoggedIn, "^ Still logged in\\. .* idle\\."); // Still logged in. 2:12 minutes idle.
    addDough(FIBS_NoOneIsAway, "^None of the users is away\\.");
    addDough(FIBS_PlayerListHeader, "^No S username rating exp login idle from");
    addDough(FIBS_RatingsHeader, "^ rank name rating Experience");
    addDough(FIBS_ClearScreen, "^.\\[;H.\\[2J"); // ANSI clear screen sequence
    addDough(FIBS_Timeout, "^Connection timed out\\.");
    addDough(FIBS_Goodbye, " Goodbye\\.");
    addDough(FIBS_LastLogin, "^ Last login: .*");
    addDough(FIBS_NoInfo, "^No information found on user");
    addDough(FIBS_PointsFor, "^points for " + PL + ": [0-9]+");
    addDough(FIBS_PlayerBannedWatch, "^" + PL + " bans you from watching\\.");

    this.alphaBatch = this.currentBatchBuild;

    // --- Numeric messages ---------------------------------------------------
    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(CLIP_WHO_INFO, "^5 [^ ]+ - - [01].*");
    addDough(CLIP_WHO_INFO, "^5 [^ ]+ [^ ]+ - [01].*");
    addDough(CLIP_WHO_INFO, "^5 [^ ]+ - [^ ]+ [01].*");

    addDough(FIBS_Average, "^[0-9][0-9]:[0-9][0-9]-"); // output of average command
    addDough(FIBS_DiceTest, "^[1-6]-1 [0-9]"); // output of dicetest command
    addDough(FIBS_DiceTest, "^[1-6]: [0-9]");
    addDough(FIBS_Stat, "^[0-9]+ bytes"); // output from stat command
    addDough(FIBS_Stat, "^[0-9]+ accounts");
    addDough(FIBS_Stat, "^[0-9]+ ratings saved. reset log");
    addDough(FIBS_Stat, "^[0-9]+ registered users.");
    addDough(FIBS_Stat, "^[0-9]+\\([0-9]+\\) saved games check by cron");

    addDough(CLIP_WHO_END, "^6.*$");
    addDough(CLIP_SHOUTS, "^13 " + PL + " .*");
    addDough(CLIP_SAYS, "^12 " + PL + " .*");
    addDough(CLIP_WHISPERS, "^14 " + PL + " .*");
    addDough(CLIP_KIBITZES, "^15 " + PL + " .*");
    addDough(CLIP_YOU_SAY, "^16 " + PL + " .*");
    addDough(CLIP_YOU_SHOUT, "^17 .*");
    addDough(CLIP_YOU_WHISPER, "^18 .*");
    addDough(CLIP_YOU_KIBITZ, "^19 .*");
    addDough(CLIP_LOGIN, "^7 " + PL + " " + PL + " logs in\\.");
    addDough(CLIP_LOGIN, "^7 " + PL + " " + PL + " logs in again\\.");
    addDough(CLIP_LOGIN, "^7 " + PL + " " + PL + " just registered and logs in\\.");
    addDough(CLIP_LOGOUT, "^8 " + PL + " .*");
    addDough(CLIP_LOGOUT, "^8 " + PL + " .*");
    addDough(CLIP_MESSAGE, "^9 " + PL + " [0-9]+ .*");
    addDough(CLIP_MESSAGE_DELIVERED, "^10 " + PL + "$");
    addDough(CLIP_MESSAGE_SAVED, "^11 " + PL + "$");
    this.numericBatch = this.currentBatchBuild;

    // --- '**' messages ------------------------------------------------------
    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(FIBS_Username, "^\\*\\* User");
    addDough(FIBS_Junk, "^\\*\\* You tell "); // "** You tell PLAYER: xxxxx"
    addDough(FIBS_YouGag, "^\\*\\* You gag");
    addDough(FIBS_YouUngag, "^\\*\\* You ungag");
    addDough(FIBS_YouBlind, "^\\*\\* You blind");
    addDough(FIBS_YouUnblind, "^\\*\\* You unblind");
    addDough(FIBS_UseToggleReady, "^\\*\\* Use 'toggle ready' first");
    addDough(FIBS_NewMatchAck9, "^\\*\\* You are now playing an unlimited match with ");
    addDough(FIBS_NewMatchAck10, "^\\*\\* You are now playing a [0-9]+ point match with " + PL); // ** You are now playing a 5 point match with PLAYER
    addDough(FIBS_NewMatchAck2, "^\\*\\* Player " + PL + " has joined you for a [0-9]+ point match\\."); // ** Player PLAYER has joined you for a 2 point match.
    addDough(FIBS_YouTerminated, "^\\*\\* You terminated the game");
    addDough(FIBS_OpponentLeftGame, "^\\*\\* Player " + PL + " has left the game. The game was saved\\.");
    addDough(FIBS_PlayerLeftGame, "has left the game\\."); // overloaded
    addDough(FIBS_YouInvited, "^\\*\\* You invited .* match\\.");
    addDough(FIBS_YourLastLogin, "^\\*\\* Last login:");
    addDough(FIBS_NoOne, "^\\*\\* There is no one called " + PL + "\\.");
    addDough(FIBS_AllowpipFalse, "^\\*\\* You don't allow the use of the server's 'pip' command\\.");
    addDough(FIBS_AllowpipTrue, "^\\*\\* You allow the use the server's 'pip' command\\.");
    addDough(FIBS_AutoboardFalse, "^\\*\\* The board won't be refreshed");
    addDough(FIBS_AutoboardTrue, "^\\*\\* The board will be refreshed");
    addDough(FIBS_AutodoubleTrue, "^\\*\\* You agree that doublets");
    addDough(FIBS_AutodoubleFalse, "^\\*\\* You don't agree that doublets");
    addDough(FIBS_AutomoveFalse, "^\\*\\* Forced moves won't");
    addDough(FIBS_AutomoveTrue, "^\\*\\* Forced moves will");
    addDough(FIBS_BellFalse, "^\\*\\* Your terminal won't ring");
    addDough(FIBS_BellTrue, "^\\*\\* Your terminal will ring");
    addDough(FIBS_CrawfordFalse, "^\\*\\* You would like to play without using the Crawford rule\\.");
    addDough(FIBS_CrawfordTrue, "^\\*\\* You insist on playing with the Crawford rule\\.");
    addDough(FIBS_DoubleFalse, "^\\*\\* You won't be asked if you want to double\\.");
    addDough(FIBS_DoubleTrue, "^\\*\\* You will be asked if you want to double\\.");
    addDough(FIBS_GreedyTrue, "^\\*\\* Will use automatic greedy bearoffs\\.");
    addDough(FIBS_GreedyFalse, "^\\*\\* Won't use automatic greedy bearoffs\\.");
    addDough(FIBS_MoreboardsTrue, "^\\*\\* Will send rawboards after rolling\\.");
    addDough(FIBS_MoreboardsFalse, "^\\*\\* Won't send rawboards after rolling\\.");
    addDough(FIBS_MovesTrue, "^\\*\\* You want a list of moves after this game\\.");
    addDough(FIBS_MovesFalse, "^\\*\\* You won't see a list of moves after this game\\.");
    addDough(FIBS_NotifyFalse, "^\\*\\* You won't be notified");
    addDough(FIBS_NotifyTrue, "^\\*\\* You'll be notified");
    addDough(FIBS_RatingsTrue, "^\\*\\* You'll see how the rating changes are calculated\\.");
    addDough(FIBS_RatingsFalse, "^\\*\\* You won't see how the rating changes are calculated\\.");
    addDough(FIBS_ReadyTrue, "^\\*\\* You're now ready to invite or join someone\\.");
    addDough(FIBS_ReadyFalse, "^\\*\\* You're now refusing to play with someone\\.");
    addDough(FIBS_ReportFalse, "^\\*\\* You won't be informed");
    addDough(FIBS_ReportTrue, "^\\*\\* You will be informed");
    addDough(FIBS_SilentTrue, "^\\*\\* You won't hear what other players shout\\.");
    addDough(FIBS_SilentFalse, "^\\*\\* You will hear what other players shout\\.");
    addDough(FIBS_TelnetFalse, "^\\*\\* You use a client program");
    addDough(FIBS_TelnetTrue, "^\\*\\* You use telnet");
    addDough(FIBS_WrapFalse, "^\\*\\* The server will wrap");
    addDough(FIBS_WrapTrue, "^\\*\\* Your terminal knows how to wrap");
    addDough(FIBS_PlayerRefusingGames, "^\\*\\* " + PL + " is refusing games\\.");
    addDough(FIBS_NotWatching, "^\\*\\* You're not watching\\.");
    addDough(FIBS_NotWatchingPlaying, "^\\*\\* You're not watching or playing\\.");
    addDough(FIBS_NotPlaying, "^\\*\\* You're not playing\\.");
    addDough(FIBS_EchoJunk, "^\\*\\* You're not playing, so you can't give up\\.");
    addDough(FIBS_EchoJunk, "^\\*\\* You can't say nothing\\.");

    addDough(FIBS_NoUser, "^\\*\\* There is no one called " + PL);
    addDough(FIBS_AlreadyPlaying, "is already playing with .*\\.");
    addDough(FIBS_BadMove, "^\\*\\* You can't remove this piece");
    addDough(FIBS_YouAlreadyPlaying, "^\\*\\* You are already playing\\.");
    addDough(FIBS_YouAlreadyRolled, "^\\*\\* You did already roll the dice\\.");
    addDough(FIBS_DidntInvite, "^\\*\\* " + PL + " didn't invite you\\.");
    addDough(FIBS_BadMove, "^\\*\\* You can't remove this piece");
    addDough(FIBS_CantMoveFirstMove, "^\\*\\* You can't move .*move\\."); // ** You can't move to 14 in your second move.'
    addDough(FIBS_CantShout, "^\\*\\* Please type 'toggle silent' again before you shout\\.");
    addDough(FIBS_MustMove, "^\\*\\* You must give [1-4] moves\\.");
    addDough(FIBS_MustComeIn, "^\\*\\* You have to remove pieces from the bar in your first move\\.");
    addDough(FIBS_UsersHeardYou, "^\\*\\* [0-9]+ users? heard you\\.");
    addDough(FIBS_PleaseWaitForJoin, "^\\*\\* Please wait for " + PL + " to join too\\.");
    addDough(FIBS_SavedMatchReady, "^\\*\\*" + PL + " +[0-9]+ +[0-9]+ +- +[0-9]+"); // double star before a name indicates you have a saved game with this player
    addDough(FIBS_NotYourTurnToRoll, "^\\*\\* It's not your turn to roll the dice\\.");
    addDough(FIBS_NotYourTurnToMove, "^\\*\\* It's not your turn to move\\.");
    addDough(FIBS_YouStopWatching, "^\\*\\* You stop watching " + PL + "\\.");
    addDough(FIBS_UnknownCommand, "^\\*\\* Unknown command: .*");
    addDough(FIBS_CantWatch, "^\\*\\* You can't watch another game while you're playing\\.");
    addDough(FIBS_CantInviteSelf, "^\\*\\* You can't invite yourself\\.");
    addDough(FIBS_DontKnowUser, "^\\*\\* Don't know user");
    addDough(FIBS_MessageUsage, "^\\*\\* usage: message <user> <text>");
    addDough(FIBS_PlayerNotPlaying, "^\\*\\* " + PL + " is not playing\\.");
    addDough(FIBS_CantTalk, "^\\*\\* You can't talk if you won't listen\\.");
    addDough(FIBS_WontListen, "^\\*\\* " + PL + " won't listen to you\\.");
    addDough(FIBS_Why, "Why would you want to do that"); // (not sure about ** vs *** at front of line.)
    addDough(FIBS_Ratings, "^\\* *[0-9]+ +" + PL + " +[0-9]+\\.[0-9]+ +[0-9]+");
    addDough(FIBS_NoSavedMatch, "^\\*\\* There's no saved match with .* length\\.");
    addDough(FIBS_WARNINGSavedMatch, "^\\*\\* WARNING: Don't accept if you want to continue");
    addDough(FIBS_CantGagYourself, "^\\*\\* You talk too much, don't you\\?");
    addDough(FIBS_CantBlindYourself, "^\\*\\* You can't read this message now, can you\\?");
    addDough(FIBS_RollBeforeMove, "^\\*\\* You have to roll the dice before moving\\.");
    addDough(FIBS_ATTENTION, "^\007\\*\\*\\* ATTENTION \\!.*");
    addDough(FIBS_ShuttingDown, "^\\*\\* System administrator causes shutdown\\. FIBS is terminating\\.");
    addDough(FIBS_Rebooting, "^\\*\\* It will be back when the computer is rebooted.");
    addDough(FIBS_GamesWillBeSaved, "^\\*\\* All running games will be saved\\.");
    addDough(FIBS_DoesntWantYouToWatch, "^\\*\\* " + PL + " doesn't want you to watch\\.");
    addDough(FIBS_WaitForLastInvitation, "^\\*\\* Wait until " + PL + " accepted or rejected your resign\\.");
    addDough(FIBS_WaitForAcceptResign, "^\\*\\* Please wait for your last invitation to be accepted\\.");


    this.starsBatch = this.currentBatchBuild;

    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(FIBS_LoginPrompt, "^login:.*");
    addDough(CLIP_WELCOME, "^1 " + PL + " [0-9]+ .*"); // better EOS could be had
    addDough(CLIP_OWN_INFO, "^2 " + PL + " [01] [01] .*"); // better EOS could be had
    addDough(CLIP_MOTD_BEGIN, "^3$");
    addDough(FIBS_FailedLogin, "^> [0-9]+"); // bogus CLIP messages sent after a failed login
    addDough(FIBS_WelcomeToFibs, "^Welcome to \\w+\\.\\s*You just logged in as guest\\.");
    this.loginBatch = this.currentBatchBuild;

    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(FIBS_OneUserPerPerson, "^\\s*ONE USERNAME PER PERSON ONLY\\!\\!\\!");
    addDough(FIBS_GivePassword, "Please give your password: ");
    addDough(FIBS_RetypePassword, "Please retype your password: ");
    addDough(FIBS_YouAreRegistered, "You are registered.");
    addDough(FIBS_UseAnotherName, "Please use another name");
    addDough(FIBS_TooMuchAccounts, "Your site is not allowed to create new accounts\\.");
    addDough(FIBS_GiveUsername, "^> ");
    this.registerBatch = this.currentBatchBuild;


    // Only interested in one message here, but we still use a message list for simplicity and consistency.
    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(CLIP_MOTD_END, "^4$");
    this.motdBatch = this.currentBatchBuild;

    messageState = FIBS_LOGIN;

  }
}
