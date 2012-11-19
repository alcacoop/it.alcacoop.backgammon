package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.actors.Checker;

public class Move {
  
  public int from, to;
  public boolean hitted = false;
  int dice = 0;
  Board b;
  int color;

  public Move(Board _b, int _from, int _to) {
    b = _b;
    from = _from;
    to = _to;
    color = MatchState.fMove;
  }

  public void setDice(int d) {
    dice = d;
  }
  
  public void undo() {
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
