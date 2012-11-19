package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.actors.Board;
import it.alcacoop.gnubackgammon.actors.Checker;

public class Move {
  
  public int from, to;
  public boolean hitted = false;
  int dice = 0;
  Board b;

  public Move(Board _b, int _from, int _to) {
    b = _b;
    from = _from;
    to = _to;
  }

  public void setDice(int d) {
    dice = d;
  }
  
  public void undo() {
    Checker c = b.getChecker(MatchState.fMove, to);
    /*
    c.moveTo(from);
    b.dices.enable(from-to);
    b.availableMoves.dices.add(from-to);
    */
  }
}
