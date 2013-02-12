package it.alcacoop.backgammon.logic;


public class FibsBoard {
  public int[][] board = {
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
  };
  public String p1, p2;
  public int color;
  public int turn;
  public int direction;
  public int dices[] = {0,0};
  
  public FibsBoard(String s) {

    String tmp[] = s.split(":");
    
    color = Integer.parseInt(tmp[41]);
    direction = Integer.parseInt(tmp[42]);
    turn = Integer.parseInt(tmp[32]);
    System.out.println("\nPARSE BOARD LENGTH: " + tmp.length);
    System.out.println("TURN: " + turn);
    System.out.println("COLOR: " + color);
    System.out.println("DIRECTION: " + direction);
    
    p1 = tmp[1];
    p2 = tmp[2];
    
    for (int i=6;i<=31;i++) {
      int p = Integer.parseInt(tmp[i]);
      if (direction==1) { //DA 1 A 24 (da 7 a 30) BAR = 6
        if (i==6) { //MY BAR
          board[0][24] = Math.abs(p);
        } else if (i==31) { //OPPONENT BAR
          board[1][24] = Math.abs(p);
        } else { //TABLE
          if (color*p>0) {
            board[0][(24-(i-6))] = Math.abs(p);
          } else {
            board[1][i-7] = Math.abs(p);
          }
        }
      } else { //DA 24 a 1 (da 30 a 7) BAR = 31
        if (i==31) { //MY BAR
          board[0][24] = Math.abs(p);
        } else if (i==6) { //OPPONENT BAR
          board[1][24] = Math.abs(p);
        } else { //TABLE
          if (color*p>0) {
            board[0][i-7] = Math.abs(p);
          } else {
            board[1][24-(i-6)] = Math.abs(p);
          }
        }
      }
    }
    
    
    if (Integer.parseInt(tmp[33])!=0) {
      System.out.println("YOU ROLL: "+tmp[33]+"-"+tmp[34]);
      dices[0] = Integer.parseInt(tmp[33]);
      dices[1] = Integer.parseInt(tmp[34]);
    }
    if (Integer.parseInt(tmp[35])!=0) {
      System.out.println("OPPONENT ROLLS: "+tmp[35]+"-"+tmp[36]);
      dices[0] = Integer.parseInt(tmp[35]);
      dices[1] = Integer.parseInt(tmp[36]);
    }
    
    String s1 = "", s2 = "";
    for (int i=0;i<25;i++) {
      s1 += board[0][i]+" ";
      s2 += board[1][i]+" ";
    }
    System.out.println(s1);
    System.out.println(s2);
    System.out.println();
  }

}
