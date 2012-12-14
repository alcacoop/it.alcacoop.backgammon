package it.alcacoop.gnubackgammon.fsm;


import it.alcacoop.gnubackgammon.GnuBackgammon;
import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.layers.MatchOptionsScreen;
import it.alcacoop.gnubackgammon.logic.MatchState;
import it.alcacoop.gnubackgammon.ui.UIDialog;


// MENU FSM
public class MenuFSM extends BaseFSM implements Context {

  private Board board;
  public State currentState;

  public enum States implements State {

    MAIN_MENU {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(2);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          System.out.println(params);
          if (params.toString().equals("SINGLE PLAYER")) {
            MatchState.matchType = 0;
            ctx.state(States.MATCH_OPTIONS);
          }
          if (params.toString().equals("TWO PLAYERS")) {
            MatchState.matchType = 1;
            ctx.state(States.MATCH_OPTIONS);
          }
          if (params.toString().equals("STATISTICS")) {
          }
          if (params.toString().equals("OPTIONS")) {
            ctx.state(States.GAME_OPTIONS);
          }
          if (params.toString().equals("ABOUT")) {
            ctx.state(States.ABOUT);
          }
          return true;
        }
        return false;
      }
    },
    
    GAME_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(1);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          System.out.println(params);
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        }
        return false;
      }
    },
    
    MATCH_OPTIONS {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(3);
      }
      
      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          if (params.toString().equals("PLAY")) {
            if((MatchState.currentLevel.ordinal() == 5) || (MatchState.currentLevel.ordinal() == 6) || (MatchState.currentLevel.ordinal() == 7)) {
              UIDialog.getYesNoDialog(
                Events.LEVEL_ALERT, 
                "AI Level choosed is very CPU intensive. \nAre you sure to proceed?", 
                ((MatchOptionsScreen)GnuBackgammon.Instance.currentScreen).stage);
            } else
              GnuBackgammon.Instance.setFSM("GAME_FSM");
          }
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        } else if (evt == Events.LEVEL_ALERT) {
          if ((Boolean)params)
            GnuBackgammon.Instance.setFSM("GAME_FSM");
        }
        return false;
      }
    },
    
    OPTIONS {
    },
    
    STOPPED {
      @Override
      public void enterState(Context ctx) {
        System.out.println("MENU FSM STOPPED");
      }
    }, 
    
    ABOUT {
      @Override
      public void enterState(Context ctx) {
        GnuBackgammon.Instance.goToScreen(5);
      }

      @Override
      public boolean processEvent(Context ctx, Events evt, Object params) {
        if (evt==Events.BUTTON_CLICKED) {
          System.out.println(params);
          if (params.toString().equals("BACK")) {
            ctx.state(States.MAIN_MENU);
          }
          return true;
        }
        return false;
      }      
    };
    
    //DEFAULT IMPLEMENTATION
    public boolean processEvent(Context ctx, BaseFSM.Events evt, Object params) {return false;}
    public void enterState(Context ctx) {}
    public void exitState(Context ctx) {}
    
  };


  public MenuFSM(Board _board) {
    board = _board;
  }

  public void start() {
    state(States.MAIN_MENU);
  }

  public void stop() {
    state(States.STOPPED);
  }
  
  public Board board() {
    return board;
  }
  
}