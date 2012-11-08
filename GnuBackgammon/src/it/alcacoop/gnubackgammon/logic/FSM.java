package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.layers.Board;


// GAME STATE MACHINE
// From: http://vanillajava.blogspot.com/2011/06/java-secret-using-enum-as-state-machine.html

interface Context {
  Board board();
  State state();
  void state(State state);
}

interface State {
  boolean processEvent(Context ctx, FSM.Events evt, Object params);
  void enterState(Context ctx);
  void exitState(Context ctx);
}


// MAIN FSM
public class FSM implements Context {
  
  Board board;
  
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
    UPDATE_MS_CUBEINFO,
    NO_MORE_MOVES
  }

  public enum States implements State {
    STARTING {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        if (evt == Events.SET_AI_LEVEL) {
          ctx.state(SIMULATED_TURN);
          return true;
        }
        return false;
      }
    },
    
    SIMULATED_TURN {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            break;
          case SET_BOARD:
            AICalls.RollDice();
            break;
          case ROLL_DICE:
            int dices[] = (int[])params;
            AICalls.EvaluateBestMove(dices);
            break;
          case EVALUATE_BEST_MOVE:
            int moves[] = (int[])params;
            ctx.board().setMoves(moves);
            break;
          case NO_MORE_MOVES:
            ctx.state(CHECK_WIN);
            break;
          default:
            return false;
        }
        return true;
      }
      public void enterState(Context ctx) {
        int m = 0;
        if (MatchState.fMove == 0) m = 1;
        AICalls.SetGameTurn(m, m);
        MatchState.fMove = m;
        MatchState.fTurn = m;
      }
    },
    
    
    CHECK_WIN {
      public void enterState(Context ctx) {
        int m = MatchState.fMove;
        if (ctx.board().bearedOff[m] == 15)
          ctx.state(SIMULATION_FINISHED);
        else
          ctx.state(States.SIMULATED_TURN);
      }
    },
    
    SIMULATION_FINISHED {
      
    };
    
    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, FSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}

  };

  public boolean processEvent(Context ctx, FSM.Events evt, Object params) { return false;}
  public void enterState(Context ctx) {}
  public void exitState(Context ctx) {}
  public State currentState;


  public FSM(Board _board) {
    board = _board;
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

  public Board board() {
    return board;
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