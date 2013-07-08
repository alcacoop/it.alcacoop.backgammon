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

package it.alcacoop.backgammon.gservice;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class GServiceNetHandler {

    private ExecutorService dispatchExecutor;
    private static LinkedBlockingQueue<Evt> queue;
    private int eventRequest;
    
    private class Evt {
      public Events e;
      public Object o;
      public Evt(Events e, Object o) {
        this.e = e;
        this.o = o;
      }
    }
        
    
    private class Dispatcher implements Runnable {
      private Events evt;
      private boolean found;
      public Dispatcher(Events _evt) {
        evt = _evt;
        found = false;
        eventRequest++;
      }
      @Override
      public void run() {
        Evt e = null;
        while (!found) {
          try {
            while (true) {
              e = queue.take();
              if (e.e!=null) break;
            }
          } catch (InterruptedException e1) {}
          
          if (evt == Events.CONTINUE) {
            found = true; //FOR CLEAR PURPOSE
          } else if ((evt==Events.FIBS_BOARD)&&(e.o==null)) {
            found = true; //FOR CLEAR PURPOSE
          } else if (evt == null) { //PASSO IL PRIMO DISPONIBILE
            GnuBackgammon.fsm.processEvent(e.e, e.o);
            found = true;
          } else if (evt==e.e) { //PASSO IL PRIMO RICHIESTO DISPONIBILE
            GnuBackgammon.fsm.processEvent(e.e, e.o);
            found = true;
          }
        }
        eventRequest--;
      }
    }

    
    
    public GServiceNetHandler() {
      queue = new LinkedBlockingQueue<Evt>();
      dispatchExecutor = Executors.newSingleThreadExecutor();
      eventRequest=0;
    }

    
    //VORREI UN EVT DI TIPO evt...
    public synchronized void pull() {
      pull(null);
    }
    public synchronized void pull(Events evt) {
      System.out.println("GSERVICE: pull "+evt);
      dispatchExecutor.submit(new Dispatcher(evt));
    }
    
    public synchronized void post(Events _e, Object _o) {
      System.out.println("GSERVICE: post "+_e);
      if (_e==null) return;
      Evt e = new Evt(_e, _o);
      try {
        queue.put(e);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }        
      //debug();
    }

    public synchronized void reset() {
      queue.clear();
      dispatchExecutor.shutdownNow();
      dispatchExecutor = Executors.newSingleThreadExecutor();
      eventRequest = 0;
    }
    
    public synchronized void debug() {
      System.out.println("CODA EVENTI...");
      System.out.println("RICHIESTE IN CODA: "+eventRequest);
      System.out.println("MESSAGGI IN CODA: "+queue.size());
      Iterator<Evt> itr = queue.iterator();
      while(itr.hasNext()) {
        Evt element = itr.next();
        System.out.print("  "+element.e);
      }
    }
}
