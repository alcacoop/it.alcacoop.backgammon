package it.alcacoop.gnubackgammon.fsm;

import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
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
          if(MatchState.fCubeUse == 1) {
            if((MatchState.fCrawford == 1) && ((MatchState.nMatchTo - MatchState.anScore[0] > 1) && (MatchState.nMatchTo - MatchState.anScore[1] > 1)))
              AICalls.AskForDoubling();
            else //CRAWFORD GAME //TODO: POST_CRAWFORD GAME
              ctx.board().rollDices();
          } else {
            ctx.board().rollDices();
          }
          break;
        case ASK_FOR_DOUBLING:
          System.out.println("I'D LIKE TO DOUBLE... "+params);
          if(Integer.parseInt(params.toString())==1) { // OPEN DOUBLING DIALOG
            ctx.board().doubleDialog.show(ctx.board().getStage());
          } else {
            ctx.board().rollDices();
          }
          break;
        case DOUBLING_RESPONSE:
          if(Integer.parseInt(params.toString())==1) { //DOUBLING ACCEPTED
            MatchState.UpdateMSCubeInfo(MatchState.nCube*2, 0);
            ctx.board().doubleCube();
            ctx.board().rollDices();
          } else { //double not accepted
            ctx.state(CHECK_END_MATCH);
          }
          break;
        case DICES_ROLLED:
          int dices[] = (int[])params;
          ctx.board().thinking(true);
          AICalls.EvaluateBestMove(dices);
          break;
        case EVALUATE_BEST_MOVE:
          ctx.board().thinking(false);
          int moves[] = (int[])params;
          if(moves[0] == -1) {
            ctx.board().noMovesLabel.setText("Your opponent has no more moves");
            ctx.board().noMovesDialog.show(ctx.board().getStage());
          } else {
            moves = (int[])params;
            ctx.board().setMoves(moves);
          }
          break;
        case PERFORMED_MOVE:
          ctx.board().updatePInfo();
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
          if(((MatchState.fCubeOwner == MatchState.fMove) || (MatchState.fCubeOwner == -1)) && (MatchState.fCubeUse == 1)) {
            System.out.println("IF GIUSTO PER IL CUBO");
            if((MatchState.fCrawford == 1) && ((MatchState.nMatchTo - MatchState.anScore[0] > 1) && (MatchState.nMatchTo - MatchState.anScore[1] > 1))) {
              ctx.board().addActor(ctx.board().rollBtn);
              ctx.board().addActor(ctx.board().doubleBtn);
            } else {
              ctx.board().rollDices();
            }
          } else {
            ctx.board().addActor(ctx.board().rollBtn);
          }
          break;
        case CPU_DOUBLING_RESPONSE:
          MatchState.SetGameTurn(1, 0);
          GnubgAPI.SetBoard(MatchState.board[1], MatchState.board[0]);
          AICalls.AcceptDouble();
          ctx.board().removeActor(ctx.board().doubleBtn);
          ctx.board().thinking(true);
          break;
        case SHOW_DOUBLE_DIALOG:
          ctx.board().humanDoubleDialog.show(ctx.board().getStage());
          break;
        case ACCEPT_DOUBLE:
          ctx.board().thinking(false);
          if((Integer)params == 1) { //CPU ACCEPTED MY DOUBLE
            ctx.board().resultLabel.setText("Your opponent accepted double");
            ctx.board().cpuDoubleDialog.show(ctx.board().getStage());
            MatchState.UpdateMSCubeInfo(MatchState.nCube*2, MatchState.fMove==0?1:0);
            ctx.board().doubleCube();
          } else { //OPPONENT HAS NOT ACCEPTED MY DOUBLE
            //ctx.board().resultLabel.setText("Your opponent didn't accept double");
            ctx.state(CHECK_END_MATCH);
          }
          break;
        case DICES_ROLLED:
          ctx.board().removeActor(ctx.board().rollBtn);
          if(MatchState.fCubeUse == 1)
            ctx.board().removeActor(ctx.board().doubleBtn);
          int dices[] = (int[])params;
          AICalls.GenerateMoves(ctx.board(), dices[0], dices[1]);
          break;
        case GENERATE_MOVES:
          int moves[][] = (int[][])params;
          if(moves != null) {
            ctx.board().availableMoves.setMoves((int[][])params);
            ctx.state(HUMAN_PERFORM_MOVES);
          } else { //player (human) has no more moves
            ctx.board().noMovesLabel.setText("No more moves available");
            ctx.board().noMovesDialog.show(ctx.board().getStage());
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
          if (ctx.board().points.get((Integer)params).isTarget) { //MOVE CHECKER
            int origin = ctx.board().selected.boardX;
            int dest = (Integer)params;
            int moves[] = {origin, dest, -1, -1, -1, -1, -1, -1};
            ctx.state(HUMAN_CHECKER_MOVING);
            ctx.board().setMoves(moves);
            ctx.board().availableMoves.dropDice(origin-dest);
          } else { //SELECT NEW CHECKER
            if ((Integer)params!=-1)
              ctx.board().selectChecker((Integer)params);
          }
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
    
    HUMAN_CHECKER_MOVING { //HERE ALL TOUCH EVENTS ARE IGNORED!
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case PERFORMED_MOVE:
            ctx.board().updatePInfo();
            ctx.state(HUMAN_PERFORM_MOVES);
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
          ctx.state(CHECK_END_MATCH);
        } else {
          if (MatchState.fMove==1)
            ctx.state(States.HUMAN_TURN);
          else {
            if (MatchState.matchType == 0)
              ctx.state(States.CPU_TURN);
            else
              ctx.state(States.HUMAN_TURN);
          }
            
          ctx.board().switchTurn();
        }
      }
    },
    
    
    
    CHECK_END_MATCH {
      @Override
      public void enterState(Context ctx) {
        int game_score = MatchState.nCube*ctx.board().gameScore(MatchState.fMove==1?0:1);
        if(MatchState.fMove == 0) 
          MatchState.SetMatchScore(MatchState.anScore[1], MatchState.anScore[MatchState.fMove]+game_score);
        else 
          MatchState.SetMatchScore(MatchState.anScore[MatchState.fMove]+game_score, MatchState.anScore[0]);
        

        if(MatchState.fMove==1)
          ctx.board().winLabel.setText("CPU WON "+game_score+" POINTS!");
        else
          ctx.board().winLabel.setText("HUMAN WON "+game_score+" POINTS!");
        
        ctx.board().winDialog.show(ctx.board().getStage());
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.CONTINUE) {
          
          if (MatchState.anScore[MatchState.fMove]>=MatchState.nMatchTo) { //MATCH FINISHED: GO TO MAIN MENU
            GnuBackgammon.Instance.setFSM("MENU_FSM");
          } else {
            ctx.state(OPENING_ROLL);
          }
        }
        return false;
      }
    },

    
    
    OPENING_ROLL {
      @Override
      public void enterState(Context ctx) {
        MatchState.UpdateMSCubeInfo(1, -1);
        ctx.board().initBoard();
        ctx.board().updatePInfo();
        
        ctx.board().rollDices();
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
        case DICES_ROLLED:
          int dices[] ={0,0};
          if (dices[0]==dices[1]) {
            while (dices[0]==dices[1]) {
              GnubgAPI.RollDice(dices);
            }
          }
          processEvent(ctx, Events.ROLL_DICE, dices);
          break;
          
        case ROLL_DICE:
          dices = (int[])params;
          if (dices[0]>dices[1]) {//START HUMAN
            MatchState.SetGameTurn(0, 0);
          } else if (dices[0]<dices[1]) {//START CPU
            MatchState.SetGameTurn(1, 1);
          }
          ctx.board().rollDices(dices[0], dices[1]);
          break;
        case SET_GAME_TURN:
          if (MatchState.fMove == 0)
            AICalls.SetBoard(ctx.board()._board[0], ctx.board()._board[1]);
          else 
            AICalls.SetBoard(ctx.board()._board[1], ctx.board()._board[0]);
          break;
        case SET_BOARD:
          if ((MatchState.fMove == 0)||(MatchState.matchType==1)) {
            ctx.state(HUMAN_TURN);
            int d[] = ctx.board().dices.get();
            AICalls.GenerateMoves(ctx.board(), d[0], d[1]);
          } else {
            ctx.state(CPU_TURN);
            ctx.board().thinking(true);
            AICalls.EvaluateBestMove(ctx.board().dices.get());
          }
          break;
        default:
          return false;
        }
        return false;
      }
    },

    STOPPED {
      @Override
      public void enterState(Context ctx) {
        System.out.println("GAME FSM STOPPED");
        ctx.board().initBoard();
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
    GnuBackgammon.Instance.goToScreen(4);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }

}