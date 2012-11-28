package it.alcacoop.gnubackgammon.logic;

import java.util.ArrayList;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.actors.Checker;

public class Move {
  
  public int from, to;
  public boolean hitted = false;
  int dice = 0;
  Board b;
  int color;
  public ArrayList<int[]> removedMoves;

  public Move(Board _b, int _from, int _to) {
    removedMoves = new ArrayList<int[]>();
    b = _b;
    from = _from;
    to = _to;
    color = MatchState.fMove;
  }

  public void setDice(int d) {
    dice = d;
  }
  
  public void setRemovedMoves(ArrayList<int[]> rm) {
    removedMoves = rm;
  }
  
  public void undo() {
    b.availableMoves.restoreMoves(removedMoves);
    
    b.points.reset();
    if (b.selected!=null) {
      b.selected.highlight(false);
      b.selected = null;
    }
      
    Checker c = b.getChecker(color, to);
    
    if (hitted) {
      int _c = c.getSpecularColor();
      int _x = c.getSpecularPosition();
      Checker ch = b.getChecker(_c, 24); //PICK HITTED FROM THE BAR
      b._board[_c][24]--;
      int y = b._board[_c][_x]++;
      ch.reset(_x, y, 0.2f);
    }
    
    if (to!=-1) b._board[color][to]--;
    else b.bearedOff[color]--;
    int y = b._board[color][from]++;
    
    c.reset(from, y, 0.2f);
    b.dices.enable(dice);
    b.availableMoves.dices.add(dice);
  }
}
