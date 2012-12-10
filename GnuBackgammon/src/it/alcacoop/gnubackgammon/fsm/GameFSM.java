package it.alcacoop.gnubackgammon.fsm;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.logic.AICalls;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import it.alcacoop.gnubackgammon.logic.MatchState;
import it.alcacoop.gnubackgammon.ui.UIDialog;

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
          if((Integer)params > 0) {
            MatchState.resignValue = (Integer)params;
            String s = "Your opponent resigned a game";
            if ((Integer)params==2) s = "Your opponent resigned a gammon game";
            if ((Integer)params==3) s = "Your opponent resigned a backgammon game";
            ctx.board().cpuResignLabel.setText(s);
            ctx.board().cpuResignDialog.show(ctx.board().getStage());
          
          } else { //ASKFORDOUBLING OR ROLL..
            if(MatchState.fCubeUse == 0) { //NO CUBE USE
              ctx.board().rollDices();
            } else {
              if (
                  ((MatchState.fCrawford==0)||(!MatchState.fCrafwordGame)) && //NOCR OR NO CRGAME
                  ((MatchState.fCubeOwner==-1)||(MatchState.fCubeOwner==1)) //AVAILABLE CUBE
                 ) {
                AICalls.AskForDoubling();
              } else {
                ctx.board().rollDices();
              }
            }
          }
          break;
          
          
        case ASK_FOR_DOUBLING:
          System.out.println("I'D LIKE TO DOUBLE... "+params);
          if(Integer.parseInt(params.toString())==1) { // OPEN DOUBLING DIALOG
            ctx.state(DIALOG_HANDLER);
            UIDialog.getYesNoDialog(
              Events.DOUBLING_RESPONSE, 
              "CPU is asking for double. Accept?", 
              ctx.board().getStage());
          } else {
            ctx.board().rollDices();
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
          if (MatchState.fCubeUse==0) {
            ctx.board().rollDices();
          } else {
            if (
                ((MatchState.fCrawford==0)||(!MatchState.fCrafwordGame)) && //NOCR OR NO CRGAME
                ((MatchState.fCubeOwner==-1)||(MatchState.fCubeOwner==MatchState.fMove)) //AVAILABLE CUBE
               ) {
              ctx.board().addActor(ctx.board().rollBtn);
              ctx.board().addActor(ctx.board().doubleBtn);
            } else {
              ctx.board().rollDices();
            }
          }
          break;
          
          
        case SHOW_DOUBLE_DIALOG:
          ctx.board().humanDoubleDialog.show(ctx.board().getStage());
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
        int game_score = 0;
        if(MatchState.resignValue == 0) {
          game_score = MatchState.nCube*ctx.board().gameScore(MatchState.fMove==1?0:1);
        } else {
          game_score = MatchState.resignValue * MatchState.nCube;
        }
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

    
    PLAYER_RESIGNED {
      @Override
      public void enterState(Context ctx) {
        System.out.println("Player wants to resign");
        GnubgAPI.SetBoard(MatchState.board[1], MatchState.board[0]); //TODO: check order
        AICalls.AcceptResign(MatchState.resignValue);
      }
      
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if(evt == Events.ACCEPT_RESIGN) {
          System.out.println("RESIGN ACCEPTED WITH VALUE: " + params);
          if((Integer)params == 0) { //resign not accepted
            ctx.board().resignNotDialog.show(ctx.board().getStage());
          } else {
            ctx.board().switchTurn();
            GnuBackgammon.fsm.state(States.CHECK_END_MATCH);
          }
          return true;
        }
        return false;
      }
    },
    
    
    DIALOG_HANDLER {
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        switch (evt) {
          case DOUBLING_RESPONSE: //RISPOSTA A CPU DOUBLING REQUEST
            if((Boolean)params) { //DOUBLING ACCEPTED
              MatchState.UpdateMSCubeInfo(MatchState.nCube*2, 0);
              ctx.board().doubleCube();
              ctx.state(CPU_TURN);
              ctx.board().rollDices();
            } else { //DOUBLING NOT ACCEPTED
              ctx.state(CHECK_END_MATCH);
            }
            break;
          
          case DOUBLE_REQUEST: //DOUBLE BUTTON CLICKED!
            if(MatchState.matchType == 0) { //CPU vs HUMAN
              MatchState.SetGameTurn(1, 0);
              GnubgAPI.SetBoard(MatchState.board[1], MatchState.board[0]);
              AICalls.AcceptDouble();
              ctx.board().removeActor(ctx.board().doubleBtn);
              ctx.board().thinking(true);              
            } else { //SHOW DOUBLE DIALOG!
              //TODO: GnuBackgammon.fsm.processEvent(Events.SHOW_DOUBLE_DIALOG, null);
            }
            break;
            
          case ACCEPT_DOUBLE: //CPU DOUBLING RESPONSE
            ctx.board().thinking(false);
            if((Integer)params == 1) { //CPU ACCEPTED MY DOUBLE
              UIDialog.getFlashDialog(
                Events.CPU_DOUBLE_ACCEPTED, 
                "Your opponent accepted double", 
                ctx.board().getStage());
            } else { //CPU DIDN'T ACCEPT MY DOUBLE
              UIDialog.getFlashDialog(
                  Events.CPU_DOUBLE_NOT_ACCEPTED, 
                  "Double not accepted", 
                  ctx.board().getStage());
            }
            break;
            
          case CPU_DOUBLE_ACCEPTED: //CPU ACCEPTED DOUBLE
            ctx.state(States.HUMAN_TURN);
            MatchState.UpdateMSCubeInfo(MatchState.nCube*2, MatchState.fMove==0?1:0);
            ctx.board().doubleCube();
            break;
            
          case CPU_DOUBLE_NOT_ACCEPTED: //CPU DIDN'T ACCEPT DOUBLE
            ctx.state(CHECK_END_MATCH);
            break;
            
          default: return false;
        }
        
        
        return true;
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