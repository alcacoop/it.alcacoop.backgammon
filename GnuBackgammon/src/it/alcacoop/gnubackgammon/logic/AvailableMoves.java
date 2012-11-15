package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.layers.Board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


public class AvailableMoves {

  //private int moves[][];
  public ArrayList<Integer> dices;
  
  int _board[][];
  Board b;
  ArrayList<int[]> moves;

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
    
    //PRINT PLAYABLE DICES
    System.out.print("PLAYABLE DICES: ");
    for (int i=0;i<dices.size();i++)
      System.out.print(" "+dices.get(i));
    System.out.print("\n");
    
    for (int i=0;i<moves.size();i++) {
      for (int j=0;j<4;j++)
        System.out.print(moves.get(i)[2*j]+"/"+ moves.get(i)[2*j+1]+" ");
      System.out.print("\n");
    }
    //END PRINTING
    
        
  }

  
  private void evaluatePlayableDices(int d[]) {
    
    System.out.println("\n\nPIPS: "+b.getPIPS());
    
    //FIX BIGGER OF AVAILABLE MOVES DICES ON BEARING OFF.. 
    int max_point = b.bearingOff();
    if(max_point>=0) { //BEARING OFF!
      for (int i=0;i<d.length;i++)
        dices.add(d[i]);
      
      int rcs = 15-b.bearedOff[MatchState.fMove];
      System.out.println("REMAINING: "+rcs);
        
      for(int j=0;j<dices.size();j++) {
        if (dices.get(j)>max_point+1) {
          dices.set(j, max_point+1);
        }
      }
      return;
    }
    //END FIXING BEARING OFF DICES
    
    
    //NOW WE ARE DISABLING UNPLAYABLE DICES
    System.out.println("\n MAX ITER: "+moves.size());
    int occurs[] = {0,0,0,0,0,0,0};
    boolean all_presents = true;
    int max_moves = 0;
    
    if (d.length == 2) { //STANDARD ROLL
      for (int i=0;i<moves.size();i++) {
        System.out.print(" "+i);
        all_presents = true;
        for (int j=0;j<4;j++) {
          for (int k=0;k<d.length;k++) {
            if ((moves.get(i)[j*2]!=-1)&& ((moves.get(i)[j*2]-moves.get(i)[j*2+1])==d[k])) 
              occurs[d[k]]++;
            all_presents = all_presents && (occurs[d[k]]>0);
          }
        }
        if (all_presents) break;
      }
    } else { //DOUBLE!
      System.out.println("DOUBLING TURN");
      all_presents = false;
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

    System.out.println("MAX MOVES: "+max_moves);
    
    /*
     * HERE WE HAVE:
     *  all_presents (CAN PLAY ALL DICES)
     *  max_moves FOR DOUBLE ROLL
     *  occurs FOR NON BOUBLE ROLL
     */
    if (all_presents) { 
      for (int i=0;i<d.length;i++)
        dices.add(d[i]);
    } else { //NON ALL DICES ARE PLAYABLE
      if (d.length==4) { //DOUBLING
        for (int i=0;i<max_moves;i++)
          dices.add(d[0]);
        for (int i=0;i<4-max_moves;i++)
          b.dices.remove(d[0]);
      } else { //NON DOUBLING ROLL
        int t = 0; //THE ONLY PLAYABLE DICE
        for (int i=0;i<occurs.length;i++)
          if (occurs[i]!=0) t=occurs[i];
        
        if(b.dices.get()[0]==t)
          b.dices.remove(b.dices.get()[1]);
        else 
          b.dices.remove(b.dices.get()[0]);
      }  
    }
     
  }

  
  
  
  
  public int[] getPoints(int nPoint) {

    ArrayList<Integer> ret = new ArrayList<Integer>();
    if (moves.size()==0) return null;
    
    for (int i=0;i<moves.size();i++) {
      for (int j=0;j<4;j++) {
        for (int k=0;k<dices.size();k++) {
          if (moves.get(i)[2*j]==nPoint) {
//            System.out.println("EVALUATING COUPLE: "+moves.get(i)[2*j]+"/"+moves.get(i)[2*j+1]);
            if (moves.get(i)[2*j]-moves.get(i)[2*j+1]==dices.get(k)) ret.add(moves.get(i)[2*j+1]);
          }
        }
      }
    }

    System.out.print("OLDLIST:");
    for (int i=0;i<ret.size();i++)
      System.out.print(" "+ret.get(i));
    
    List<Integer> unique = new ArrayList<Integer>(new HashSet<Integer>(ret));
    System.out.println("\nNEWLIST SIZE: "+unique.size());
    
    //RETURN unique AS STANDARD ARRAY
    int[] r = new int[unique.size()];
    for (int i=0;i<unique.size();i++) {
      r[i] = unique.get(i);
    }
    return r;
  }


  public void dropDice(int d) {
    System.out.println("REMOVING DICE: "+d);
    int i = dices.indexOf(d);
    System.out.println("INDEX: "+i);
    if (i>=0)
      dices.remove(i);
    else 
      System.out.println("PROBLEMA!!");
  }


  public boolean hasMoves() {
    return !dices.isEmpty();
  }


  public int[] _getPoints(int nMove, int x) { 

    ArrayList<Integer> ret = new ArrayList<Integer>();
    
    Iterator<Integer> itr = dices.iterator();
    while (itr.hasNext()) {
      int d = itr.next();

      if (x-d<0) return null;
      
      if (MatchState.fMove==1) {
        if (_board[1][(x-d)]>=1) {
          ret.add(x-d);
        }
        if (_board[0][(23-x+d)]<2) {
          ret.add(x-d);
        }
      } else {
        if (_board[0][(x-d)]>=1) {
          ret.add(x-d);
        }
        if (_board[1][(23-x+d)]<2) {
          ret.add(x-d);
        }
      }
    }
    int r[] = new int[ret.size()];
    for (int i=0;i<ret.size();i++)
      r[i] = ret.get(i);
    return r;    
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
    System.out.println("REMAINIG MOVES: "+moves.size());
  }
  
}