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

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.NativeFunctions;
import it.alcacoop.gnubackgammon.logic.GnubgAPI;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.SharedLibraryLoader;



public class Main implements NativeFunctions {
  private static Main instance;
  
  
  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "GnuBackgammon";
    cfg.width = 800;
    cfg.height = 480;
    instance = new Main();
    
    new LwjglApplication(new GnuBackgammon(instance), cfg);
    
    new SharedLibraryLoader("libs/gnubg.jar").load("gnubg");
    String s = System.getProperty("user.dir");
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
}
