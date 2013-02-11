/******************************************************************************
 * PlayerGroup.java - Associate Players with groups
 * $Id: PlayerGroup.java,v 1.2 2011/01/01 02:30:36 dick Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2010 - Dick Balaska - BuckoSoft, Corp.
 */ 

package com.buckosoft.fibs.domain;

/** Associate Players with groups
 * @author Dick Balaska
 * @since 2010/12/31
 * @version $Revision: 1.2 $ <br> $Date: 2011/01/01 02:30:36 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/main/java/com/buckosoft/fibs/domain/PlayerGroup.java">cvs PlayerGroup.java</a>
 */
public class PlayerGroup {
  private int id;
  private int groupId;
  private int playerId;
  private boolean dirty = true;
  private boolean tagForDelete = false;

  /** Construct an empty PlayerGroup
  */
  public PlayerGroup() {}

  /** Convienence constructor that initializes the fields.
   * @param groupId The groupId of this new PlayerGroup.
   * @param playerId The playerId of this new PlayerGroup.
   */
  public PlayerGroup(int groupId, int playerId) {
    this.groupId = groupId;
    this.playerId = playerId;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }
  /**
   * @return the groupId
   */
  public int getGroupId() {
    return groupId;
  }
  /**
   * @param groupId the groupId to set
   */
  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }
  /**
   * @return the playerId
   */
  public int getPlayerId() {
    return playerId;
  }
  /**
   * @param playerId the playerId to set
   */
  public void setPlayerId(int playerId) {
    this.playerId = playerId;
  }

  /** Does this PlayerGroup need saving?
   * @return the dirty
   */
  public boolean isDirty() {
    return dirty;
  }

  /** 
   * @param dirty the dirty to set
   */
  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * @return the tagForDelete
   */
  public boolean isTagForDelete() {
    return tagForDelete;
  }

  /**
   * @param tagForDelete the tagForDelete to set
   */
  public void setTagForDelete(boolean tagForDelete) {
    this.tagForDelete = tagForDelete;
  }


}

