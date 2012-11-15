package it.alcacoop.gnubackgammon.logic;

import it.alcacoop.gnubackgammon.layers.Board;

import java.util.ArrayList;
import java.util.Iterator;


public class AvailableMoves {

  private int moves[][];
  public ArrayList<Integer> dices;
  
  int _board[][];
  Board b;

  public AvailableMoves(Board _b) {
    dices = new ArrayList<Integer>();
    b = _b;
  }


  public void setMoves(int _moves[][]) {
    int _dices[] = b.dices.get();
    moves = _moves;
    dices.clear();

    evaluatePlayableDices(_dices);
  }

  
  private int[] evaluatePlayableDices(int d[]) {
    
    System.out.println("\n MAX ITER: "+moves.length);
    int occurs[] = {0,0,0,0,0,0,0};
    boolean all_presents = true;
    int max_moves = 0;
    
    if (d.length == 2) { //STANDARD ROLL
      for (int i=0;i<moves.length;i++) {
        System.out.print(" "+i);
        all_presents = true;
        for (int j=0;j<4;j++) {
          for (int k=0;k<d.length;k++) {
            if ((moves[i][j*2]!=-1)&& ((moves[i][j*2]-moves[i][j*2+1])==d[k])) 
              occurs[d[k]]++;
            all_presents = all_presents && (occurs[d[k]]>0);
          }
        }
        if (all_presents) break;
      }
    } else { //DOUBLE!
      System.out.println("DOUBLING TURN");
      all_presents = false;
      for (int i=0;i<moves.length;i++) {
        for (int j=0;j<4;j++) {
          if (moves[i][j*2]!=-1) max_moves=(j+1);
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
     
    
    return new int[2];
  }

  
  public int[] getPoints(int nPoint) {
    //TODO: doesn't work for doubles
    int nMove = b.dices.get().length - dices.size();
    ArrayList<Integer> ret = new ArrayList<Integer>();
    
    int values[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    int bo=0; //BOFF

    if (moves==null) return null;
    
    int max_point = b.bearingOff();
    if(max_point>=0) {
      for(int j=0;j<dices.size();j++) {
        if (dices.get(j)>max_point+1) {
          dices.set(j, max_point+1);
        }
      }
    }
  
    
    Iterator<Integer> itr = dices.iterator();
    while (itr.hasNext()) {
      int j = itr.next(); //REMAINING DICES
      for (int i=0; i<moves.length; i++) { //ALL GENERATED MOVES
        if ((moves[i][nMove*2]==nPoint) && (moves[i][nMove*2]-moves[i][nMove*2+1]==j))
          if (moves[i][nMove*2+1]!=-1)
            values[moves[i][nMove*2+1]]++;
          else //BOFF
            bo++;
      }
    }

    for (int i=0;i<25;i++)
      if (values[i]>0)
        ret.add(i);
    if (bo>0) ret.add(-1); //BOFF

    int[] r = new int[ret.size()];
    for (int i=0;i<ret.size();i++) {
      r[i] = ret.get(i);
    }
    return r;
  }


  public void dropDice(int d) {
    dices.remove(dices.indexOf(d));
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

}