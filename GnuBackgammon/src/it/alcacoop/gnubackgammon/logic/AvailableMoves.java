package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.actors.Board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class AvailableMoves {

  public ArrayList<Integer> dices;
  private ArrayList<int[]> moves;
  private Board b;


  public AvailableMoves(Board _b) {
    dices = new ArrayList<Integer>();
    moves = new ArrayList<int[]>();
    b = _b;
  }

  public void setMoves(int _moves[][]) {
    int _dices[] = b.dices.get();
    moves.clear();
    dices.clear();

    for (int i=0;i<_moves.length;i++)
      moves.add(_moves[i]);

    evaluatePlayableDices(_dices);
  }


  private void evaluatePlayableDices(int d[]) {
    
    //AVAILABLE MOVES DICES ON BEARING OFF..
    int max_point = b.bearingOff();
    int dice = 0;
    if(max_point>=0) { //BEARING OFF!
      
      int max_moves = 0;
      for (int i=0;i<moves.size();i++) {
        for (int j=0;j<4;j++) {
          if (moves.get(i)[j*2]!=-1) {
            max_moves=(j+1);
            dice = moves.get(i)[j*2]-moves.get(i)[j*2+1];
          }
        }
        if (max_moves==b.dices.get().length) {
          break;
        }
      }
      
      if ((d[0]!=d[1])&&(max_moves==1)) {
        dices.add(dice);
        if (dice==d[0])
          b.dices.disable(d[1]);
        else
          b.dices.disable(d[0]);
        return;
      }
      
      for (int i=0;i<max_moves;i++)
        dices.add(d[i]);
      
      if (d[0]==d[1])
        for (int i=0;i<4-max_moves;i++)
          b.dices.disable(d[0]);
      
      return;
    }
    //END BEARING OFF DICES
    

    //DISABLING UNPLAYABLE DICES ON RACE GAME
    boolean all_presents = false;
    int max_moves = 0;
    List<Integer> presents = new ArrayList<Integer>();

    if (d.length == 2) { //STANDARD ROLL
      for (int i=0;i<moves.size();i++) {
        for (int j=0;j<4;j++) {
          for (int k=0;k<d.length;k++) {
            if ((moves.get(i)[j*2]-moves.get(i)[j*2+1])==d[k])
              presents.add(d[k]);
          }
        }
      }
    } else { //DOUBLE!
      for (int i=0;i<moves.size();i++) {
        for (int j=0;j<4;j++) {
          if (moves.get(i)[j*2]!=-1) max_moves=(j+1);
        }
        if (max_moves==4) {
          all_presents = true;
          break;
        }
      }
    }

    List<Integer> unique = new ArrayList<Integer>(new HashSet<Integer>(presents));
    if (unique.size()==2) all_presents = true;

    if (all_presents) { 
      for (int i=0;i<d.length;i++)
        dices.add(d[i]);
    } else { //NOT ALL DICES ARE PLAYABLE
      if (d.length==4) { //DOUBLING
        for (int i=0;i<max_moves;i++)
          dices.add(d[0]);
        for (int i=0;i<4-max_moves;i++)
          b.dices.disable(d[0]);
      } else { //NON DOUBLING ROLL
        int t = unique.get(0); //THE ONLY PLAYABLE DICE
        dices.add(t);
        if(b.dices.get()[0]==t)
          b.dices.disable(b.dices.get()[1]);
        else 
          b.dices.disable(b.dices.get()[0]);
      }  
    }
  }


  public int[] getPoints(int nPoint) {
    
    ArrayList<Integer> ret = new ArrayList<Integer>();
    
    if (moves.size()==0) return null; //NO MOVES AVAILABLE
    if ((b._board[MatchState.fMove][24]>0)&&(nPoint!=24)) //CHECKERS ON BAR
      return null;

    for (int i=0;i<moves.size();i++) {
      for (int j=0;j<4;j++) {
        for (int k=0;k<dices.size();k++) {
          if (moves.get(i)[2*j]==nPoint) {
            int max_point = b.bearingOff();
            if (max_point==-1) { //STANDARD MOVE...
              if ((moves.get(i)[2*j]-moves.get(i)[2*j+1]==dices.get(k))&&(moves.get(i)[2*j+1]!=-1)) {
                ret.add(moves.get(i)[2*j+1]);//TODO
              }
            } else { //BOFF
              if (moves.get(i)[2*j]-moves.get(i)[2*j+1]==dices.get(k)) {
                //STANDARD BEAROFF
                ret.add(moves.get(i)[2*j+1]);
              }
              else if ((moves.get(i)[2*j]-moves.get(i)[2*j+1]<=dices.get(k))&&(nPoint==max_point)&&(moves.get(i)[2*j+1]<0)) {
                //BEARFOFF WITH BIGGER DICE
                ret.add(moves.get(i)[2*j+1]); //TODO
              }
                
            }
          }
        }
      }
    }

    List<Integer> unique = new ArrayList<Integer>(new HashSet<Integer>(ret));
    //RETURN unique AS STANDARD ARRAY
    int[] r = new int[unique.size()];
    for (int i=0;i<unique.size();i++) {
      r[i] = unique.get(i);
    }
    return r;
  }


  public void dropDice(int d) {
    int idx = dices.indexOf(d);
    if (idx==-1) {//BEARING OFF WITH GREATER DICE
      Iterator<Integer> itr = dices.iterator();
      while (itr.hasNext()) {
        if (itr.next()>d) {
          itr.remove();
          break;
        }
      }
    } else //REMOVE PLAYED DICE
      dices.remove(idx);
  }


  public boolean hasMoves() {
    return (!dices.isEmpty()&&(!b.gameFinished()));
  }
  
  
  public ArrayList<int[]> removeMoves(int orig, int dest) {
    ArrayList<int[]> removed = new ArrayList<int[]>();
    Iterator<int[]> itr = moves.iterator();
    while (itr.hasNext()) { 
      boolean matched = false;
      int mv[] = itr.next();
      for (int i=0;i<4;i++) {
        if ((mv[2*i]==orig)&&(mv[2*i+1]==dest)) {
          matched=true;
          break;
        }
      }
      if (!matched) {
        itr.remove();
        removed.add(mv);
      }
    }
    return removed;
  }
  
  
  public void restoreMoves(ArrayList<int[]> rm) {
    for (int i=0;i<rm.size();i++)
      moves.add(rm.get(i));
  }
}