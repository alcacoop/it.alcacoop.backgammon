package it.alcacoop.gnubackgammon.fsm;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.MatchState;

public class GameFSM extends BaseFSM implements Context {
  
  private Board board;
  public State currentState;

  public enum States implements State {
    
    CPU_TURN {
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
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
            System.out.println("I'D LIKE TO DOUBLE... "+params);
            if(params=="1") {
              //ask human for double
              processEvent(ctx, Events.DOUBLING_RESPONSE, null);
            } else
              AICalls.RollDice();
            break;
          case DOUBLING_RESPONSE:
            //open dialog and wait for human accepting or not
            ctx.board().addActor(ctx.board().doubleDialog);
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

    
    HUMAN_TURN {
      public boolean processEvent(Context ctx, GameFSM.Events evt, Object params) {
        switch (evt) {
          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            ctx.board().dices.clear();
            ctx.board().addActor(ctx.board().rollBtn);
            break;
          case ROLL_DICE:
            ctx.board().removeActor(ctx.board().rollBtn);
            int dices[] = (int[])params;
            AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
            ctx.board().setDices(dices[0], dices[1]);
            break;
          case GENERATE_MOVES:
            int moves[][] = (int[][])params;
            if(moves != null) {
              ctx.board().availableMoves.setMoves((int[][])params);
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
      public boolean processEvent(Context ctx, GameFSM.Events evt, Object params) {
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
        case DICE_CLICKED:
          ctx.board().dices.clear();
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
        if (ctx.board().gameFinished()) {
          ctx.state(States.GAME_FINISHED);
        } else {
          if (MatchState.fMove==1)
            ctx.state(States.HUMAN_TURN);
          else
            ctx.state(States.CPU_TURN);
          
          ctx.board().switchTurn();
        }
      }
    },
    
    GAME_FINISHED {
      public void enterState(Context ctx) {
        //OPEN DIALOG
        if(MatchState.fMove==1)
          ctx.board().winDialog.text("CPU WON!");
        else
          ctx.board().winDialog.text("HUMAN WON!");
        ctx.board().addActor(ctx.board().winDialog);
//        MatchState.fMove = 0;
//        MatchState.fTurn = 0;
//        ctx.board().initBoard(0);
//        ctx.state(States.CPU_TURN);
//        ctx.board().switchTurn();
      }
    },
    
    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        AICalls.RollDice();
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case ROLL_DICE:
            int dices[] =(int[])params;
            if (dices[0]==dices[1]) AICalls.RollDice();
            else if (dices[0]>dices[1]) {//START HUMAN
              MatchState.fMove = 0;
              MatchState.fTurn = 0;
              AICalls.SetGameTurn(0, 0);
              ctx.board().setDices(dices[0], dices[1]);
            } else if (dices[0]<dices[1]) {//START CPU
              MatchState.fMove = 1;
              MatchState.fTurn = 1;
              AICalls.SetGameTurn(1, 1);
              ctx.board().setDices(dices[0], dices[1]);
            }
            break;
          case SET_GAME_TURN:
            if (MatchState.fMove == 0)
              AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
            else 
              AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
            break;
          case SET_BOARD:
            if (MatchState.fMove == 0) {
              ctx.state(HUMAN_TURN);
              int d[] = ctx.board().dices.get();
              AICalls.GenerateMoves(ctx.board(), d[0], d[1]);
            } else {
              ctx.state(CPU_TURN);
              AICalls.EvaluateBestMove(ctx.board().dices.get());
            }
            break;
          default:
            return false;
        }
        return false;
      }
    };
    
    
    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}
    
  };


  public GameFSM(Board _board) {
    board = _board;
  }

  public void start() {
    state(States.OPENING_ROLL);
  }

  
  public Board board() {
    return board;
  }
  
}