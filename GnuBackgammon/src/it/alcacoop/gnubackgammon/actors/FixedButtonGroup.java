package it.alcacoop.gnubackgammon.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class FixedButtonGroup extends ButtonGroup {

  @Override
  public void setChecked(String text) {
    if (text == null) throw new IllegalArgumentException("text cannot be null.");
    for (int i = 0, n = getButtons().size; i < n; i++) {
      Button button = getButtons().get(i);
      if (button instanceof TextButton && text.equals((String)((TextButton)button).getText().toString())) {
        button.setChecked(true);
        return;
      }   
    }   
  }
  
}
