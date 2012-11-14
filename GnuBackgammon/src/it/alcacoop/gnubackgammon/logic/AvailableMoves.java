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
    _board = _b._board;
    b = _b;
  }


  public void setMoves(int _moves[][], int _dices[]) {
    moves = _moves;
    dices.clear();
    if(moves!=null) {
      for(int i =0; i<moves.length;i++) {
        for(int j=0;j<8;j++) {
          System.out.print(moves[i][j] + " ");
        }
        System.out.print("\n");
      }
    }
    if((moves!=null) && (moves.length==2) && (moves[0][2]==-1)) {  //ONLY ONE MOVE
      dices.add(moves[0][0] - moves[0][1]);
      //int t[] = new int[1];
      int t = moves[0][0] - moves[0][1];
      if(b.dices.get()[0]==t)
        b.dices.remove(b.dices.get()[1]);
      //b.dices.disable(b.dices.last[1]);
      else 
        b.dices.remove(b.dices.get()[0]);
      //b.dices.disable(b.dices.last[0]);
      //b.dices.last = t;
      System.out.println(dices.get(0));
    } else {
      for (int i=0;i<_dices.length;i++)
        dices.add(_dices[i]);
    }
  }


  public int[] getPoints(int nPoint) {
    int nMove = b.dices.get().length - dices.size();
    ArrayList<Integer> ret = new ArrayList<Integer>();
    System.out.println("NMOVE: " + nMove);
    
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
      System.out.print("DADO ESAMINATO: " + j);
      for (int i=0; i<moves.length; i++) { //ALL GENERATED MOVES
        System.out.println("PUNTA RICHIESTA: "+moves[i][nMove*2]+"/"+nPoint);
        System.out.println("DISTANZA MOSSA ESAMINATA: "+(moves[i][nMove*2]-moves[i][nMove*2+1]));
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
      System.out.print("\t"+r[i]);
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

      if (x-d<0) return null; //TODO: BEAR OFF
      
      
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
