package it.alcacoop.gnubackgammon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
  
  AudioDevice audio;
  Sound roll;
  Sound moving_start;
  Sound moving_stop;
  Sound click;

  public SoundManager() {
    roll = Gdx.audio.newSound(Gdx.files.internal("data/sounds/roll.wav"));
    moving_start = Gdx.audio.newSound(Gdx.files.internal("data/sounds/move1.wav"));
    moving_stop = Gdx.audio.newSound(Gdx.files.internal("data/sounds/move2.wav"));
  }
  
  public void playRoll() {
    if (GnuBackgammon.Instance.prefs.getString("SOUND", "Yes").equals("Yes"))
      roll.play();
  }

  public void playMoveStart() {
    if (GnuBackgammon.Instance.prefs.getString("SOUND", "Yes").equals("Yes"))
      moving_start.play();
  }
  
  public void playMoveStop() {
    if (GnuBackgammon.Instance.prefs.getString("SOUND", "Yes").equals("Yes"))
      moving_stop.play();
  }
}
