/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
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

package it.alcacoop.backgammon;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.NativeFunctions;
import it.alcacoop.backgammon.utils.MatchRecorder;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;



public class Main implements NativeFunctions {
  private static Main instance;
  private static String data_dir;
  
  
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "GnuBackgammon";
    cfg.width = 800;
    cfg.height = 480;
    instance = new Main();
    new LwjglApplication(new GnuBackgammon(instance), cfg);
    
    new SharedLibraryLoader("libs/gnubg.jar").load("gnubg");
    String s = System.getProperty("user.dir");
    data_dir = s;
    s+="/libs/";
    GnubgAPI.InitializeEnvironment(s);
  }

  @Override
  public void showAds(boolean show) {
  }
  
  protected Object handler = new Object() {
  };


  @Override
  public void openURL(String url) {
  }

  @Override
  public String getDataDir() {
    return data_dir;
  }

  @Override
  public void shareMatch(MatchRecorder rec) {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
    Date date = new Date();
    String d = dateFormat.format(date);
    
    String path = Gdx.files.external("data/gnubg-sgf/match-"+d+".sgf").path();
    
    FileHandle fh = Gdx.files.absolute(path);
    Writer writer = fh.writer(false);
    try {
      writer.write(rec.saveSGF());
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}
