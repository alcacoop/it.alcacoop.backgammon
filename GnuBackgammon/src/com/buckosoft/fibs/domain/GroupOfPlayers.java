/******************************************************************************
 * GroupOfPlayers.java - A group of Players
 * $Id: GroupOfPlayers.java,v 1.6 2011/01/01 20:32:05 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2010 - Dick Balaska - BuckoSoft, Corp.
 * 
 * $Log: GroupOfPlayers.java,v $
 * Revision 1.6 2011/01/01 20:32:05 dick
 * Add some debug.
 *
 * Revision 1.5 2011/01/01 06:24:59 dick
 * Javadoc.
 *
 * Revision 1.4 2011/01/01 02:30:19 dick
 * We need to keep the whole list of PlayerGroups. Hibernate goes bonkers recreating the rows.
 * So, the master is the playerGroups list and we duplicate that to the players<Integer> list.
 *
 * Revision 1.3 2011/01/01 00:17:12 dick
 * PlayerGroup had to move to a top level domain object so that the hibernate mapping would work.
 * It doesn't like mapping to subclasses.
 *
 * Revision 1.2 2010/12/31 05:35:31 dick
 * A group of Players.
 *
 * Revision 1.1 2010/12/30 17:41:08 dick
 * Define a Group of Players.
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

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/** A group of Players
 * @author Dick Balaska
 * @since 2010/12/30
 * @version $Revision: 1.6 $ <br> $Date: 2011/01/01 20:32:05 $
 * @see PlayerGroup
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/main/java/com/buckosoft/fibs/domain/GroupOfPlayers.java">cvs GroupOfPlayers.java</a>
 */
public class GroupOfPlayers {
  private final static boolean DEBUG = false;

  private int id;
  private String groupName = "";
  private Color color = Color.black;
  private List<PlayerGroup> playerGroups = new LinkedList<PlayerGroup>();
  private LinkedList<Integer> players = new LinkedList<Integer>();
  private boolean ignore = false;
  private boolean active = false;
  private boolean dirty = true;

  /** Default empty constructor
  */
  public GroupOfPlayers() {}

  /** Convienence constructor to set some fields
   * @param groupName The name of this new GroupOfPlayers
   * @param color The color to display this group in.
   */
  public GroupOfPlayers(String groupName, Color color) {
    this.groupName = groupName;
    this.color = color;
  }


  /** Return the unique id for this GroupOfPlayers
   * @return the id
   */
  public int getId() {
    return id;
  }

  /** Set the unique id for this GroupOfPlayers
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

  /** Get the name of this group.
   * @return the name
   */
  public String getGroupName() {
    return groupName;
  }
  /** Set the name of this group.
   * @param groupName the name to set
   */
  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }
  /** Get the color to display group members in.
   * @return the color
   */
  public Color getColor() {
    return color;
  }
  /** Set the color to display group members in.
   * @param color the color to set
   */
  public void setColor(Color color) {
    this.color = color;
  }

  /** Get the color in a database friendly format.
   * @return the RGB triple in an int format
   */
  public int getColorStore() {
    return(this.color.getRGB());
  }

  /** Set the color from a database friendly format.
   * @param rgb
   */
  public void setColorStore(int rgb) {
    this.color = new Color(rgb);
  }

  /** Get the list of Players in this group.
   * @return the players
   */
  public LinkedList<Integer> getPlayers() {
    return players;
  }

  /** Get the PlayserGroup list, the database likes to do this for saving
   * @return A reference to the PlayerGroup list.
   */
  public List<PlayerGroup> getPlayerGroups() {
    return(this.playerGroups);
  }

  /** Set the PlayerGroup list, usually after loading it from the database.
   * Upon setting this, update the handly players list. 
   * @param playerGroups The PlayerGroups for this group
   */
  public void setPlayerGroups(List<PlayerGroup> playerGroups) {
    this.playerGroups = playerGroups;
    this.players.clear();
    for (PlayerGroup pg : playerGroups)
      this.players.add(pg.getPlayerId());
  }

  /** Add this player to this group.
   * @param playerId The id of the player to add to this group.
   */
  public void addPlayer(int playerId) {
    if (DEBUG)
      System.out.println("GroupOfPlayers: addPlayer " + playerId);
    this.playerGroups.add(new PlayerGroup(id, playerId));
    this.players.add(playerId);
    this.dirty = true;
  }

  /** Is this a list of players to ignore?
   * @return the ignore
   */
  public boolean isIgnore() {
    return ignore;
  }

  /** Are ignoring this list of players
   * @param ignore the ignore to set
   */
  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /** Did the user select this Group to be active?
   * @return the active
   */
  public boolean isActive() {
    return active;
  }

  /** The user changed this Group's active state.
   * @param active the active to set
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /** Does this Group need to be saved to disk?
   * @return the dirty
   */
  public boolean isDirty() {
    return dirty;
  }

  /** Set whether this Group needs to be saved or not.
   * @param dirty the dirty to set
   */
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }
}
