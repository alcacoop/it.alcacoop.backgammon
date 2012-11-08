package it.alcacoop.gnubackgammon.logic;

import com.badlogic.gdx.Gdx;

// GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html


interface Context {
  //TemplateGame game();
  State state();
  void state(State state);
}

interface State {
  // true: processed, false: not processed (invalid event)
  boolean processEvent(Context ctx, FSM.Events evt, Object params);
  void enterState(Context ctx);
  void exitState(Context ctx);
}


// MAIN FSM
public class FSM implements Context {
  public enum Events {
    ACCEPT_DOUBLE,
    ACCEPT_RESIGN,
    ASK_FOR_DOUBLING,
    ASK_FOR_RESIGNATION,
    EVALUATE_BEST_MOVE,
    INITIALIZE_ENVIRONMENT,
    ROLL_DICE,
    SET_AI_LEVEL,
    SET_BOARD,
    SET_GAME_TURN,
    SET_MATCH_SCORE,
    SET_MATCH_TO,
    UPDATE_MS_CUBEINFO
  }

  public enum States implements State {
    STARTING {
      public void enterState(Context ctx) {
        Gdx.app.log("STATE","STARTING");
      }
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        switch (evt) {
          case ROLL_DICE:
            ctx.state(P1_ROLL_DICE);
          break;
          default:
            return false;
        }
        return true;
      }
      @Override
      public void exitState(Context ctx) {
      }
    },
    P1_ROLL_DICE {
      public void enterState(Context ctx) {
        Gdx.app.log("STATE","P1 ROLL DICE");
      }
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        return false;
      }
      @Override
      public void exitState(Context ctx) {
      }
    },
  };

  public boolean processEvent(Context ctx, FSM.Events evt, Object params) 
  { /* DO NOTHING */ return false; }
  public void enterState(Context ctx) { /* DO NOTHING */ }
  public void exitState(Context ctx) { /* DO NOTHING */ }


  public State currentState;


  public FSM() {
  }

  public void start() {
    state(States.STARTING);
  }

  public boolean processEvent(Events evt, Object params) {
    System.out.println("PROCESS EVENT: "+evt);
    System.out.println("\tSRC STATE: "+state());
    boolean res = state().processEvent(this, evt, params);
    System.out.println("\tDST STATE: "+state());
    return res;
  }


  public State state() {
    return currentState;
  }

  public void state(State state) {
    if(currentState != null)
      currentState.exitState(this);
    currentState = state;
    if(currentState != null)
      currentState.enterState(this);        
  }
}
