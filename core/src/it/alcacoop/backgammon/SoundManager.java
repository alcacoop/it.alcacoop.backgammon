/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

  AudioDevice audio;
  Sound roll;
  Sound moving_start;
  Sound moving_stop;
  Sound click;
  Sound invite;
  Sound newmessage;

  public SoundManager() {
    roll = Gdx.audio.newSound(Gdx.files.internal("sounds/roll.wav"));
    moving_start = Gdx.audio.newSound(Gdx.files.internal("sounds/move1.wav"));
    moving_stop = Gdx.audio.newSound(Gdx.files.internal("sounds/move2.wav"));
    invite = Gdx.audio.newSound(Gdx.files.internal("sounds/invite.wav"));
    newmessage = Gdx.audio.newSound(Gdx.files.internal("sounds/newmessage.wav"));
  }

  public void playRoll() {
    if (GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      roll.play();
  }

  public void playMoveStart() {
    if (GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      moving_start.play();
  }

  public void playMoveStop() {
    if (GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      moving_stop.play();
  }

  public void playInvite() {
    if (GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      invite.play();
  }

  public void playMessage() {
    if (GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes").equals("Yes"))
      newmessage.play();
  }
}
