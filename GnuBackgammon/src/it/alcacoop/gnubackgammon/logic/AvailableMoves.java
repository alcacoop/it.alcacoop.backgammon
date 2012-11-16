package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.layers.Board;

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

    //FIX BIGGER OF AVAILABLE MOVES DICES ON BEARING OFF.. 
    int max_point = b.bearingOff();
    if(max_point>=0) { //BEARING OFF!
      for (int i=0;i<d.length;i++)
        dices.add(d[i]);
      for(int j=0;j<dices.size();j++) {
        if (dices.get(j)>max_point+1) {
          dices.set(j, max_point+1);
        }
      }
      return;
    }
    //END FIXING BEARING OFF DICES

    //NOW WE ARE DISABLING UNPLAYABLE DICES
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
          b.dices.remove(d[0]);
      } else { //NON DOUBLING ROLL
        int t = unique.get(0); //THE ONLY PLAYABLE DICE
        dices.add(t);
        if(b.dices.get()[0]==t)
          b.dices.remove(b.dices.get()[1]);
        else 
          b.dices.remove(b.dices.get()[0]);
      }  
    }
  }


  public int[] getPoints(int nPoint) {
    
    //BEARING OFF
    int max_point = b.bearingOff();
    if (max_point>0) {
      ArrayList<Integer> ret = new ArrayList<Integer>();
      for (int i=0;i<dices.size();i++)
        if (nPoint-dices.get(i)>=0) 
          ret.add(nPoint-dices.get(i));
        else if (nPoint-dices.get(i)==-1)
          ret.add(-1);
        else if ((nPoint-dices.get(i)<-1)&&(nPoint==max_point))
          ret.add(-1);
      
      int r[] = new int[ret.size()];
      for (int i=0;i<ret.size();i++)
        r[i] = ret.get(i);
      return r;
    }

    
    //RACE GAME
    ArrayList<Integer> ret = new ArrayList<Integer>();
    if (moves.size()==0) return null;
    if ((b._board[MatchState.fMove][24]>0)&&(nPoint!=24))
      return null;

    for (int i=0;i<moves.size();i++) {
      for (int j=0;j<4;j++) {
        for (int k=0;k<dices.size();k++) {
          if (moves.get(i)[2*j]==nPoint) {
            if (moves.get(i)[2*j]-moves.get(i)[2*j+1]==dices.get(k)) ret.add(moves.get(i)[2*j+1]);
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
    int i = dices.indexOf(d);
    if (i==-1) //BEARING OFF WITH GREATER DICE
      dices.remove(dices.size()-1);
    else
      dices.remove(i);
  }


  public boolean hasMoves() {
    return !dices.isEmpty();
  }


  public void removeMoves(int orig, int dest) {
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
      if (!matched) itr.remove();
    }
  }
}