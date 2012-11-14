package it.alcacoop.gnubackgammon.logic;

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
          AICalls.SetGameTurn(0, 0);
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
            AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
            break;
          case GENERATE_MOVES:
            ctx.board().availableMoves.setMoves((int[][])params, ctx.board().dices.get());
            AICalls.EvaluateBestMove(ctx.board().dices.get());
            break;
          case EVALUATE_BEST_MOVE:
            int moves[] = (int[])params;
            moves = (int[])params;
//            int ps[] = ctx.board().availableMoves.getPoints(moves[0]);
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
          case GENERATE_MOVES:
            int moves[][] = (int[][])params;
            if(moves != null) {
              ctx.board().availableMoves.setMoves((int[][])params, ctx.board().dices.get());
              ctx.state(HUMAN_PERFORM_MOVES);
            } else {
              ctx.state(CHECK_WIN);
            }
            break;
          default:
            return false;
        }
        return true;
      }
    },
    
    
    HUMAN_PERFORM_MOVES {
      public boolean processEvent(Context ctx, FSM.Events evt, Object params) {
        switch (evt) {
        case POINT_TOUCHED:
          if (ctx.board().points.get((Integer)params).isTarget) {
            int origin = ctx.board().selected.boardX;
            int dest = (Integer)params;
            int moves[] = {origin, dest, -1, -1, -1, -1, -1, -1}; 
            ctx.board().setMoves(moves);
            ctx.board().availableMoves.dropDice(origin-dest);
          } else {
            if ((Integer)params!=-1)
              ctx.board().selectChecker((Integer)params);
          }
          break;
        case PERFORMED_MOVE:
          if (!ctx.board().availableMoves.hasMoves())
            processEvent(ctx, Events.NO_MORE_MOVES, null);
          break;
        case NO_MORE_MOVES:
          ctx.state(CHECK_WIN);
          break;
        default:
          return false;
        }
        return true;
      }
    },
    
    CHECK_WIN {
      public void enterState(Context ctx) {
        if (ctx.board().bearedOff[MatchState.fMove] == 15) {
          ctx.state(States.GAME_FINISHED);
        } else {
          if (MatchState.fMove==1)
            ctx.state(States.HUMAN_TURN);
          else
            ctx.state(States.SIMULATED_TURN);
//          ctx.state(States.SIMULATED_TURN);
//          ctx.state(States.HUMAN_TURN);
          MatchState.switchTurn();
        }
      }
    },
    
    GAME_FINISHED {
      public void enterState(Context ctx) {
        MatchState.fMove = 0;
        MatchState.fTurn = 0;
        ctx.board().initBoard();
        ctx.state(States.SIMULATED_TURN);
        MatchState.switchTurn();
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
//    System.out.println("PROCESS EVENT: "+evt);
//    System.out.println("\tSRC STATE: "+state());
    boolean res = state().processEvent(this, evt, params);
//    System.out.println("\tDST STATE: "+state());
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