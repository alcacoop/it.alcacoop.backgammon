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
          AICalls.AskForDoubling();
          break;
        case ASK_FOR_DOUBLING:
          System.out.println("I'D LIKE TO DOUBLE... "+params);
          if(Integer.parseInt(params.toString())==1) { // OPEN DOUBLING DIALOG
            ctx.board().doubleDialog.show(ctx.board().getStage());
          } else
            AICalls.RollDice();
          break;
        case DOUBLING_RESPONSE:
          if(Integer.parseInt(params.toString())==1) { //DOUBLING ACCEPTED
            System.out.println("DOUBLE ACCEPTED");
            MatchState.fCubeOwner = 0;
            MatchState.nCube = MatchState.nCube*2;
            GnubgAPI.UpdateMSCubeInfo(MatchState.nCube, MatchState.fCubeOwner);
            AICalls.RollDice();
          } else { //double not accepted
            System.out.println("DOUBLE NOT ACCEPTED");
            ctx.state(CHECK_END_MATCH);
          }
          break;
        case ROLL_DICE:
          int dices[] = (int[])params;
          ctx.board().setDices(dices[0], dices[1]);
          ctx.board().thinking(true);
          AICalls.EvaluateBestMove(dices);
          break;
        case EVALUATE_BEST_MOVE:
          ctx.board().thinking(false);
          int moves[] = (int[])params;
          moves = (int[])params;
          ctx.board().setMoves(moves);
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
          ctx.board().addActor(ctx.board().rollBtn);
          if(MatchState.fCubeOwner != 1) {
            ctx.board().addActor(ctx.board().doubleBtn);
          }
          break;
        case CPU_DOUBLING_RESPONSE:
          MatchState.fMove = 0;
          MatchState.fTurn = 1;
          GnubgAPI.SetGameTurn(MatchState.fTurn, MatchState.fMove);
          if(GnubgAPI.AcceptDouble() == 1) { //CPU ACCEPTED MY DOUBLE
            ctx.board().resultLabel.setText("CPU accepted double");
            MatchState.fCubeOwner = 1;
            MatchState.nCube = MatchState.nCube*2;
            GnubgAPI.UpdateMSCubeInfo(MatchState.nCube, MatchState.fCubeOwner);
            ctx.board().removeActor(ctx.board().doubleBtn);
          } else { //CPU HAS NOT ACCEPTED MY DOUBLE
            ctx.board().resultLabel.setText("CPU has not accepted double");
            ctx.state(CHECK_END_MATCH);
          }
          ctx.board().cpuDoubleDialog.show(ctx.board().getStage());
          break;
        case ROLL_DICE:
          ctx.board().removeActor(ctx.board().rollBtn);
          ctx.board().removeActor(ctx.board().doubleBtn);
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
          ctx.board().updatePInfo();
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
          ctx.state(CHECK_END_MATCH);
        } else {
          if (MatchState.fMove==1)
            ctx.state(States.HUMAN_TURN);
          else
            ctx.state(States.CPU_TURN);

          ctx.board().switchTurn();
        }
      }
    },
    
    
    
    CHECK_END_MATCH {
      @Override
      public void enterState(Context ctx) {
        int game_score = MatchState.nCube*ctx.board().gameScore();
        MatchState.anScore[MatchState.fMove]+=game_score;
        System.out.println("DENTRO: "+MatchState.anScore[MatchState.fMove]);
        if(MatchState.fMove==1)
          ctx.board().winLabel.setText("CPU WON!");
        else
          ctx.board().winLabel.setText("HUMAN WON!");
        
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
        MatchState.fCubeOwner = -1;
        MatchState.nCube = 1;
        ctx.board().initBoard();
        ctx.board().updatePInfo();
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
    //state(States.OPENING_ROLL);
    GnuBackgammon.Instance.goToScreen(4);
  }

  public void stop() {
    state(States.STOPPED);
  }

  public Board board() {
    return board;
  }

}