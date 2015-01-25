/******************************************************************************
 * Player.java - Describe one FIBS player
 * $Id: Player.java,v 1.5 2011/01/01 00:17:39 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright� 2008 - Dick Balaska - BuckoSoft, Corp.
 */ 
 
package it.alcacoop.fibs;

import it.alcacoop.backgammon.GnuBackgammon;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Pool.Poolable;


/** Describe one FIBS player. <br>
 * Most of these fields don't have setters because this object is mostly filled out from FIBS.
 * See {@link #parsePlayer(String)}.
 * @author Dick Balaska
 * @since 2008/03/31
 * @version $Revision: 1.5 $ <br> $Date: 2011/01/01 00:17:39 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/main/java/com/buckosoft/fibs/domain/Player.java">cvs Player.java</a>
 */
public class Player implements Comparable<Player>, Poolable {
  private int id = 0;
  private String name;
  private String opponent = "";
  private String watching = "";
  private boolean ready;
  private boolean away;
  private double rating;
  private int experience;
  private int idleTime;
  private long loginTime;
  private String hostName;
  private String client;
  private String email;
  private WinLoss winLoss = null;

  private int bfFlag = 0;
  private String bfStatus;

  private boolean invited = false;

  private String savedMatch;
  private String missManners;
  
  public String fibsPlayer;
  
  private TextureRegionDrawable readyDrawable, busyDrawable, playingDrawable;
  private Label label;
  private Image status;
  
  

  /** Create a default/empty Player
  */
  public Player() {
    readyDrawable = new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("ready"));
    busyDrawable = new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("busy"));
    playingDrawable = new TextureRegionDrawable(GnuBackgammon.atlas.findRegion("playing"));
    label = new Label("", GnuBackgammon.skin);
    status = new Image();
    status.setDrawable(readyDrawable);
  }

  /** Convienence constructor to set the player's name.
   * @param playerName The name of this new player.
   */
  public Player(String playerName) {
    this.name = playerName;
  }

  /** Get the local player id.
   * This is a key to our local database, and is not a FIBS number at all.
   * @return the id
   */
  public int getId() {
    return id;
  }

  
  public Image getStatusImage() {
    return status;
  }
  
  public Label getLabel() {
    return label;
  }
  /** Set the local player id.
   * This is a key to our local database, and is not a FIBS number at all.
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  /** Get the username of this player
   * @return The username
   */
  public String getName() {
    return(name);
  }

  /** Set the username of this player.
   * This is useful when we get an invite from an unknown player.
   * @param name The player's name
   */
  public void setName(String name) {
    this.name = name;
  }

  /** Return true if this player is playing someone.
   * This field is derived from the opponent field.
   * @return true if playing.
   */
  public boolean isPlaying() {
    if (opponent.length() > 0)
      return(true);
    return(false);
  }

  /** If this player is playing someone, then return the opponent's name.
   * @return The opponent's name or the empty string if not playing anyone.
   */
  public String getOpponent() {
    return(opponent);
  }

  /** If this player is watching someone, then return the name of the player we are watching.
   * @return The player being watched or the empty string if not watching anyone.
   */
  public String getWatching() {
    return(watching);
  }

  /** Is this player ready to play?
   * @return true if ready to play.
   */
  public boolean isReady() {
    return(ready && !isPlaying());
  }

  /** Has this player marked himself as away?
   * @return true = yes
   */
  public boolean isAway() {
    return(away);
  }

  /** Get the rating for this player
   * @return The rating
   */
  public double getRating() {
    return(rating);
  }

  /** Get the number of games this player has played
   * @return The Experience
   */
  public int getExperience() {
    return(experience);
  }

  /** How long has this player been idle?
   * @return The number of seconds he has not done anything.
   */
  public int getIdleTime() {
    return(idleTime);
  }

  /** When did this player log in?
   * @return The number of seconds since Jan. 1, 1970
   */
  public long getLoginTime() {
    return(loginTime);
  }

  /** Get the hostname of the machine this player is playing on.
   * @return The hostname
   */
  public String getHostName() {
    return(hostName);
  }

  /** What client is this player using?
   * @return The name of the client.
   */
  public String getClient() {
    return(client);
  }

  /** Set the name of the client that this player is using.
   * @param client
   */
  public void setClient(String client) {
    this.client = client;
  }

  /** What is this player's email address?
   * @return The email address that the player gave to FIBS (probably not a real email address).
   */
  public String getEmail() {
    return(email);
  }

  /** Return a string of your won/loss record against this player.
   * @return the winLoss
   */
  public WinLoss getWinLoss() {
    return(winLoss);
    // if (winLoss == null)
    // return("");
    // return winLoss.toString();
  }

  /** Set the string of your won/loss record against this player.
   * @param wins The number of times you have beaten this opponent.
   * @param losses The number of times you have lost to this opponent.
   */
  public void setWinLoss(int wins, int losses) {
    this.winLoss = new WinLoss(wins, losses);
  }

  /** Get the BuckoFIBS flag (not used yet)
   * @return the bfFlag
   */
  public int getBfFlag() {
    return bfFlag;
  }

  /** Set the BuckoFIBS flag (not used yet)
   * @param bfFlag the bfFlag to set
   */
  public void setBfFlag(int bfFlag) {
    this.bfFlag = bfFlag;
  }

  /** If this player has a special status string, like an invite warning, it goes here.
   * @return the bfStatus The status string.
   */
  public String getBfStatus() {
    return bfStatus;
  }

  /** If this player has a special status string, like an invite warning, it goes here.
   * @param bfStatus the bfStatus to set
   */
  public void setBfStatus(String bfStatus) {
    this.bfStatus = bfStatus;
  }

  /** Have we invited this player?
   * @return the invited
   */
  public boolean isInvited() {
    return invited;
  }

  /**
   * @param invited the invited to set
   */
  public void setInvited(boolean invited) {
    this.invited = invited;
  }

  /** If you have a saved match with this player, then this string describes it. 
   * @return the savedMatch or null if none
   */
  public String getSavedMatch() {
    return savedMatch;
  }

  /** Set the savedMatch (tooltip) text
   * @param savedMatch the savedMatch to set
   */
  public void setSavedMatch(String savedMatch) {
    this.savedMatch = savedMatch;
  }

  /** If this player has a MissManners warning, this is it.
   * @return the missManners warning, or null if this player has none.
   */
  public String getMissManners() {
    return missManners;
  }

  /** Set the MissManners warning issued about this player
   * @param missManners the missManners to set
   */
  public void setMissManners(String missManners) {
    this.missManners = missManners;
  }

  /** Parse this player from a FIBS string.
   * Fills in most of this object's fields from the FIBS 
   * <a href="http://www.fibs.com/fibs_interface.html#clip_who_info">who info</a> line.
   * @param s The who info line from FIBS
   * @return success
   */
  public boolean parsePlayer(String s) {
    fibsPlayer = s;
    String[] ss = s.split(" ");
    if (ss.length != 13) {
      return(false);
    }
    name = ss[1];
    opponent = ss[2];
    if ("-".equals(ss[2]) || opponent == null)
      opponent = "";
    watching = ss[3];
    if ("-".equals(watching))
      watching = "";
    ready = "1".equals(ss[4]);
    away = "1".equals(ss[5]);
    rating = Double.parseDouble(ss[6]);
    experience = Integer.parseInt(ss[7]);
    idleTime = Integer.parseInt(ss[8]);
    loginTime = Long.parseLong(ss[9]);
    hostName = ss[10];
    client = ss[11];
    email = ss[12];
    
    if (isPlaying())  status.setDrawable(playingDrawable);
    else if (!isReady()) status.setDrawable(busyDrawable);
    else status.setDrawable(readyDrawable);
    
    LabelStyle ls = label.getStyle();
    label.setText(" "+name+" ("+rating+")");
    label.setStyle(ls);
    
    return(true);
  }
  
  public void toggleReady() {
    if (isReady()) status.setDrawable(busyDrawable);
    else status.setDrawable(readyDrawable);
  }

  /** String compare the name of this player with that one.
   * @param o The other player to compare
   */
  @Override
  public int compareTo(Player o) {
    return(this.name.compareToIgnoreCase(o.getName()));
  }

  @Override
  public void reset() {
    id = 0;
    name = "";
    opponent = "";
    watching = "";
    ready = false;
    away = false;
    rating = 0;
    experience = 0;
    idleTime = 0;
    loginTime = 0;
    hostName = "";
    client = "";
    email = "";
    winLoss = null;
    bfFlag = 0;
    bfStatus = "";
    invited = false;
    savedMatch = "";
    missManners = "";
    label.setText("");
    status.setDrawable(readyDrawable);
  }
}
