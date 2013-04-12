package it.alcacoop.backgammon.ui;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.layers.GameScreen;
import it.alcacoop.backgammon.logic.MatchState;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class SwipeDetector implements GestureListener {
  private float lastX = 0;
  private float lastY = 0;
  
  @Override
  public boolean touchDown(float x, float y, int pointer, int button) {
    lastX = x;
    lastY = y;
    return false;
  }

  @Override
  public boolean tap(float x, float y, int count, int button) {
    return false;
  }

  @Override
  public boolean longPress(float x, float y) {
    return false;
  }

  @Override
  public boolean fling(float velocityX, float velocityY, int button) {
    System.out.println(lastX+":"+lastY);
    
    if (MatchState.matchType!=2) return false;
    GameScreen g = (GameScreen)GnuBackgammon.Instance.currentScreen;
    System.out.println(g.chatBox.chatHit(lastX, lastY));
    if (velocityY>0) {
      GnuBackgammon.Instance.showChatBox();
      return true;
    } else if ((velocityY<0)&&(!g.chatBox.chatHit(lastX, lastY))) {
      GnuBackgammon.Instance.hideChatBox();
      return true;
    }
    return false;
  }

  @Override
  public boolean pan(float x, float y, float deltaX, float deltaY) {
    /*
    System.out.println(x+":"+y+" - Dy: "+deltaY);
    if (MatchState.matchType!=2) return false;
    GameScreen g = (GameScreen)GnuBackgammon.Instance.currentScreen;
    
    if (deltaY>30) {
      if (g.chatHit(x, y)) return false;
      GnuBackgammon.Instance.showChatBox();
      return true;
    } else if (deltaY<-30) {
      if (g.chatHit(x, y)) return false;
      GnuBackgammon.Instance.hideChatBox();
      return true;
    }
    */
    return false;
  }

  @Override
  public boolean zoom(float initialDistance, float distance) {
    return false;
  }

  @Override
  public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
    return false;
  }
}
