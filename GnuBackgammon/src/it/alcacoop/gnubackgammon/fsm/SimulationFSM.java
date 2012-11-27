package it.alcacoop.gnubackgammon.fsm;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.AILevels;
import it.alcacoop.gnubackgammon.logic.MatchState;


// MAIN FSM
public class SimulationFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;

  public enum States implements State {
    STARTING_SIMULATION {
      public void enterState(Context ctx) {
        ctx.setMoves(0);
        ctx.board().initBoard(2);
        MatchState.fMove = 0;
        MatchState.fTurn = 0;

        final Context c = ctx;
        
        ctx.board().addAction(Actions.sequence(
            Actions.delay(0.8f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                GnuBackgammon.Instance.board.animate(0.6f);
              }
            }), 
            Actions.delay(0.8f),
            Actions.run(new Runnable() {
              @Override
              public void run() {
                c.state(States.SIMULATED_TURN);
                c.board().switchTurn();  
              }
            })
        ));
      };
    },
    
    SIMULATED_TURN {
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            ctx.setMoves(ctx.getMoves()+1);
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
            AICalls.EvaluateBestMove(ctx.board().dices.get());
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
        if (ctx.board().gameFinished()||ctx.getMoves()==10) {
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
    AICalls.SetAILevel(AILevels.BEGINNER);
    state(States.STARTING_SIMULATION);
  }

  
  public Board board() {
    return board;
  }
  
}