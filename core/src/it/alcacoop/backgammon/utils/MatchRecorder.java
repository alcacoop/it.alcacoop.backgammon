/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella                                    #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.MatchState;
import it.alcacoop.backgammon.utils.legacy.Json;
import it.alcacoop.backgammon.utils.legacy.JsonWriter;


public class MatchRecorder {
  
  private Array<Game> matchInfo;
  
  private class Game extends OrderedMap<String, Object> {
    public OrderedMap<String, Object> gameInfo;
    public Array<OrderedMap<String, Object>> moves;
    
    public Game(OrderedMap<String, Object> gameInfo) {
      this.gameInfo = gameInfo;
      put("gameinfo", gameInfo);
      moves = new Array<OrderedMap<String, Object>>();
      put("moves", moves);
    }
    
    public Game(int game) {
      gameInfo = new OrderedMap<String, Object>();
      gameInfo.put("ff", 4);
      gameInfo.put("gm", 6);
      gameInfo.put("ca", "UTF-8");
      gameInfo.put("ap", "BackgammonMobile:0.1");
      
      gameInfo.put("mi_length", MatchState.nMatchTo);
      gameInfo.put("mi_game", game);
      gameInfo.put("mi_ws", MatchState.anScore[1]);
      gameInfo.put("mi_bs", MatchState.anScore[0]);
      
      gameInfo.put("pb", MatchState.pl1);
      gameInfo.put("pw", MatchState.pl0);
      
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date date = new Date(GregorianCalendar.getInstance().getTimeInMillis());
      gameInfo.put("dt", dateFormat.format(date));
      
      gameInfo.put("_cu", MatchState.nMatchTo>1?MatchState.fCubeUse:0);
      gameInfo.put("_cr", MatchState.fCrawford);
      gameInfo.put("_cg", MatchState.fCrafwordGame);
      gameInfo.put("_df", MatchState.currentLevel.ordinal());
      gameInfo.put("_co", MatchState.fCubeOwner);
      gameInfo.put("_cv", MatchState.nCube);
      gameInfo.put("_bb", GnuBackgammon.Instance.board.getBoardAsString(0));
      gameInfo.put("_bw", GnuBackgammon.Instance.board.getBoardAsString(1));
      
      moves = new Array<OrderedMap<String, Object>>();
      
      put("gameinfo", gameInfo);
      put("moves", moves);
    }
    
    public void updateBoards() {
      gameInfo.put("_bb", GnuBackgammon.Instance.board.getBoardAsString(0));
      gameInfo.put("_bw", GnuBackgammon.Instance.board.getBoardAsString(1));
    }
    
    public void addMove(OrderedMap<String, Object> m) {
      moves.add(m);
    }
    
    public void addDices(int d1, int d2, boolean rolled) {
      gameInfo.put("_d1", d1);
      gameInfo.put("_d2", d2);
      gameInfo.put("_rl", rolled);
    }
    
    public void addResult(int color, int game_score, boolean resigned) {
      String RE = "B+";
      if (color==1) RE="W+";
      RE+=game_score;
      if (resigned) RE+="R";
      gameInfo.put("re", RE);
    }
  }

  private static class Move {
    public static  OrderedMap<String, Object> getM(int color, int d1, int d2, int[] ms) {
      OrderedMap<String, Object> o = new OrderedMap<String, Object>();
      o.put("type", 0);
      o.put("c", color);
      o.put("d1", d1);
      o.put("d2", d2);
      o.put("m", asString(ms, color));
      return o;
    }
    public static OrderedMap<String, Object> getDR(int color) {
      OrderedMap<String, Object> o = new OrderedMap<String, Object>();
      o.put("type", 1);
      o.put("c", color);
      return o;
    }
    public static OrderedMap<String, Object> getDT(int color) {
      OrderedMap<String, Object> o = new OrderedMap<String, Object>();
      o.put("type", 2);
      o.put("c", color);
      return o;
    }
    public static OrderedMap<String, Object> getDD(int color) {
      OrderedMap<String, Object> o = new OrderedMap<String, Object>();
      o.put("type", 3);
      o.put("c", color);
      return o;
    }
    public static OrderedMap<String, Object> getPL(int color, int d1, int d2) {
      OrderedMap<String, Object> o = new OrderedMap<String, Object>();
      o.put("type", 9);
      o.put("c", color);
      if (d1!=0) o.put("d1", d1);
      if (d2!=0) o.put("d2", d2);
      return o;
    }
    
    private static String asString(int ms[], int color){
      String conv[][] = {
        {"x","w","v","u","t","s","r","q","p","o","n","m","l","k","j","i","h","g","f","e","d","c","b","a","y"},
        {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y"}
      };
      String m = "";
      for (int i=0;i<4;i++) {
        if (ms[i*2]==-1) break;
        m += conv[color][ms[i*2]];
        if (ms[i*2+1]==-1) m+="z";
        else
          m += conv[color][ms[i*2+1]];
      }
      return m;
    }
  }
  
  public MatchRecorder() {
    matchInfo = new Array<Game>();
  }
  
  public void addGame() {
    Game g = new Game(matchInfo.size+1);
    matchInfo.add(g);
  }
  
  public void updateBoard() {
    matchInfo.get(matchInfo.size-1).updateBoards();
  }
  
  public void addMove(int color, int d1, int d2, int[] ms) {
    matchInfo.get(matchInfo.size-1).addMove(Move.getM(color, d1, d2, ms));
  }
  
  public void addDoubleRequest(int color) {
    matchInfo.get(matchInfo.size-1).addMove(Move.getDR(color));
  }
  
  public void addDoubleTake(int color) {
    matchInfo.get(matchInfo.size-1).addMove(Move.getDT(color));
  }
  
  public void addDoubleDrop(int color) {
    matchInfo.get(matchInfo.size-1).addMove(Move.getDD(color));
  }
  
  public void addPlayerState(int color, int d1, int d2) {
    matchInfo.get(matchInfo.size-1).addMove(Move.getPL(color, d1, d2));
  }
  
  public void addDices(int d1, int d2, boolean rolled) {
    matchInfo.get(matchInfo.size-1).addDices(d1, d2, rolled);
  }
  
  public void addResult(int color, int game_score, boolean resigned) {
    matchInfo.get(matchInfo.size-1).addResult(color, game_score, resigned);
  }
  
  public void setCube(int value, int owner) {
    matchInfo.get(matchInfo.size-1).gameInfo.put("_cv", value);
    matchInfo.get(matchInfo.size-1).gameInfo.put("_co", owner);
  }
  
  public OrderedMap<String, Object> getLastGameInfo() {
    return matchInfo.get(matchInfo.size-1).gameInfo;
  }
  public OrderedMap<String, Object> getLastMove() {
    Array<OrderedMap<String, Object>> ar = matchInfo.get(matchInfo.size-1).moves;
    if (ar.size==0) return null;
    else return ar.get(ar.size-1);
  }
  
  public void reset() {
    matchInfo.clear();
  }
  
  public void saveJson(String fname) {
    FileHandle fh = Gdx.files.absolute(fname);
    Writer writer = fh.writer(false);
    Json json = new Json(JsonWriter.OutputType.json);
    try {
      writer.write(json.prettyPrint(this));
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
  public String saveSGF() {
    String sgf = "";
    Game g;
    for (int i=0;i<matchInfo.size;i++) {
      g = matchInfo.get(i);
      
      sgf += "(\n";
      sgf += ";FF[4]GM[6]CA[UTF-8]AP[BackgammonMobile:0.1]MI[length:";
      sgf += (Integer)(g.gameInfo.get("mi_length"));
      sgf += "][game:"+((Integer)(g.gameInfo.get("mi_game"))-1);
      sgf += "][ws:"+(Integer)(g.gameInfo.get("mi_ws"));
      sgf += "][bs:"+(Integer)(g.gameInfo.get("mi_bs"));
      sgf += "]PW["+((String)(g.gameInfo.get("pw"))).replace("(", "_Level").replace(")", "");
      sgf += "]PB["+(String)(g.gameInfo.get("pb"));
      sgf += "]DT["+(String)(g.gameInfo.get("dt"));
      sgf += "]";
      
      if (((Integer)(g.gameInfo.get("_cr"))>0)
          &&(Integer)(g.gameInfo.get("mi_length"))!=1) {
        String ru = "RU[Crawford";
        if ((Boolean)(g.gameInfo.get("_cg")))
          ru += ":CrawfordGame]";
        else
          ru += "]";
        sgf += ru;
      }
      
      if ((String)(g.gameInfo.get("re"))!="")
        sgf += "RE["+(String)(g.gameInfo.get("re"))+"]";
      
      sgf += "\n"; //END GAME INFO
      
      for (int j=0;j<g.moves.size;j++) {
        sgf += ";";
        if ((Integer)(g.moves.get(j).get("c"))==0)
          sgf += "B[";
        else
          sgf += "W[";
        switch ((Integer)(g.moves.get(j).get("type"))) {
          case 0: //MOVE
            sgf += (Integer)(g.moves.get(j).get("d1"));
            sgf += (Integer)(g.moves.get(j).get("d2"));
            sgf += (String)(g.moves.get(j).get("m"));
            break;
          case 1: //DOUBLE REQ
            sgf += "double";
            break;
          case 2: //DOUBLE ACCEPT
            sgf += "take";
            break;
          case 3: //DOUBLE DROP
            sgf += "drop";
            break;
        }
        sgf += "]\n";
      }
      sgf += ")\n";//END GAME
    }

    return sgf;
  }
  
  
  public void loadFromFile(String fname) {
    FileHandle fh = Gdx.files.absolute(fname);
    matchInfo.clear();
    
    JSONProperties jp = new JSONProperties(fh);
    Array<Object> a = (Array<Object>)jp.asArray("matchInfo", null);
    
    for (int i=0;i<a.size;i++) {
      @SuppressWarnings("unchecked")
      OrderedMap<String, Object> m = (OrderedMap<String, Object>) a.get(i);
      @SuppressWarnings("unchecked")
      OrderedMap<String, Object> gi = (OrderedMap<String, Object>) m.get("gameinfo");
      
      gi.put("ff", ((Float)gi.get("ff")).intValue());
      gi.put("gm", ((Float)gi.get("gm")).intValue());
      gi.put("re", (gi.get("re")));
      gi.put("mi_length", ((Float)gi.get("mi_length")).intValue());
      gi.put("mi_game", ((Float)gi.get("mi_game")).intValue());
      gi.put("mi_ws", ((Float)gi.get("mi_ws")).intValue());
      gi.put("mi_bs", ((Float)gi.get("mi_bs")).intValue());
      gi.put("_cr", ((Float)gi.get("_cr")).intValue());
      gi.put("_df", ((Float)gi.get("_df")).intValue());
      gi.put("_co", ((Float)gi.get("_co")).intValue());
      gi.put("_cv", ((Float)gi.get("_cv")).intValue());
      
      Game g = new Game(gi);
      
      @SuppressWarnings("unchecked")
      Array<Object> ar = (Array<Object>) m.get("moves");
      for (int j=0;j<ar.size;j++) {
        @SuppressWarnings("unchecked")
        OrderedMap<String, Object> mv = (OrderedMap<String, Object>) ar.get(j);
        mv.put("type", ((Float)mv.get("type")).intValue());
        mv.put("c", ((Float)mv.get("c")).intValue());
        if (mv.containsKey("d1")) mv.put("d1", ((Float)mv.get("d1")).intValue());
        if (mv.containsKey("d2")) mv.put("d2", ((Float)mv.get("d2")).intValue());
        if (mv.containsKey("m")) mv.put("m", (mv.get("m")).toString());
        g.moves.add(mv);
      }
      
      matchInfo.add(g);
    }
  }
  
}
