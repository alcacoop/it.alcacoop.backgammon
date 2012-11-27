package it.alcacoop.gnubackgammon.fsm;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.MatchState;


// MAIN FSM
public class SimulationFSM extends BaseFSM implements Context {
  
  private Board board;
  public State currentState;

  public enum States implements State {
    STARTING_SIMULATION {
      public void enterState(Context ctx) {
        ctx.board().initBoard(2);
        MatchState.fMove = 0;
        MatchState.fTurn = 0;
        ctx.board().initBoard(2);
        ctx.state(States.SIMULATED_TURN);
        ctx.board().switchTurn();  
      };
    },
    
    SIMULATED_TURN {
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            AICalls.AskForResignation();
            break;
          case ASK_FOR_RESIGNATION:
            System.out.println("I'M RESIGNING... "+params);
            AICalls.AskForDoubling();
            break;
          case ASK_FOR_DOUBLING:
            System.out.println("I'D LIKE TO DOUBLING... "+params);
            AICalls.RollDice();
            break;
          case ROLL_DICE:
            int dices[] = (int[])params;
            ctx.board().setDices(dices[0], dices[1]);
            final Board b = ctx.board();
            ctx.board().addAction(Actions.sequence(
                Actions.delay(0.1f),
                Actions.run(new Runnable() {
                  @Override
                  public void run() {
                    AICalls.EvaluateBestMove(b.dices.get());
                  }
                })
            ));
            break;
          case EVALUATE_BEST_MOVE:
            int moves[] = (int[])params;
            moves = (int[])params;
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

    
    CHECK_WIN {
      public void enterState(Context ctx) {
        if (ctx.board().gameFinished()) {
          ctx.state(States.STARTING_SIMULATION);
        } else {
          ctx.state(States.SIMULATED_TURN);
          ctx.board().switchTurn();
        }
      }
    };
    
    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}
    
  };


  public SimulationFSM(Board _board) {
    board = _board;
  }

  public void start() {
    state(States.STARTING_SIMULATION);
  }

  
  public Board board() {
    return board;
  }
  
}