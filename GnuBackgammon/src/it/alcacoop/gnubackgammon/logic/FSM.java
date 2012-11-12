package it.alcacoop.gnubackgammon.logic;

import com.badlogic.gdx.Gdx;
import it.alcacoop.gnubackgammon.layers.Board;


// SIMULATED GAME STATE MACHINE
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
    START,
    PERFORMED_MOVE,
    NO_MORE_MOVES,
    POINT_TOUCHED,
    GENERATE_MOVES
  }

  public enum States implements State {
    STARTING {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        if (evt == Events.START) {
          ctx.state(States.SIMULATED_TURN);
          return true;
        }
        return false;
      }
    },
    
    SIMULATED_TURN {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            AICalls.RollDice();
            break;
          case ROLL_DICE:
            int dices[] = (int[])params;
            ctx.board().setDices(dices[0], dices[1]);
            AICalls.EvaluateBestMove(dices);
            break;
          case EVALUATE_BEST_MOVE:
            int moves[] = (int[])params;
            ctx.board().setMoves(moves);
            break;
          case PERFORMED_MOVE:
            ctx.board().performNextMove();
            break;
          case NO_MORE_MOVES:
            ctx.state(States.CHECK_WIN);
            break;
          default:
            return false;
        }
        return true;
      }
      public void enterState(Context ctx) {
        int m = 1;
        if (MatchState.fMove == 1) m = 0;
        MatchState.fMove = m;
        MatchState.fTurn = m;
        AICalls.SetGameTurn(m, m);
      }
    },

    
    HUMAN_TURN {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            AICalls.RollDice();
            break;
          case ROLL_DICE:
            int dices[] = (int[])params;
            AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
            ctx.board().setDices(dices[0], dices[1]);
            break;
          case POINT_TOUCHED:
            Gdx.app.log("TOUCHED", ""+params);
            if (ctx.board().points[(Integer)params].isTarget) {
              int origin = ctx.board().selected.boardX;
              int dest = (Integer)params;
              if (MatchState.fMove==0) dest = 23-dest;
              int moves[] = {origin, dest, -1, -1, -1, -1, -1, -1}; 
              ctx.board().setMoves(moves);
            } else {
              ctx.board().selectChecker((Integer)params);
            }
            break;
          case PERFORMED_MOVE:
            if (!ctx.board().hasMoves())
              processEvent(ctx, Events.NO_MORE_MOVES, null);
            break;
          case NO_MORE_MOVES:
            ctx.state(SIMULATED_TURN);
            break;
          default:
            return false;
        }
        return true;
      }
      public void enterState(Context ctx) {
        MatchState.fMove = 0;
        MatchState.fTurn = 0;
        AICalls.SetGameTurn(0, 0);
      }
    },
    
    CHECK_WIN {
      public void enterState(Context ctx) {
        int m = MatchState.fMove;
        if (ctx.board().bearedOff[m] == 15) {
          ctx.state(States.SIMULATION_FINISHED);
        } else {
          ctx.state(States.SIMULATED_TURN);
          //ctx.state(States.HUMAN_TURN);
        }
      }
    },
    
    SIMULATION_FINISHED {
      public void enterState(Context ctx) {
        ctx.board().initBoard();
        ctx.state(States.SIMULATED_TURN);
      }
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
    //System.out.println("PROCESS EVENT: "+evt);
    //System.out.println("\tSRC STATE: "+state());
    boolean res = state().processEvent(this, evt, params);
    //System.out.println("\tDST STATE: "+state());
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