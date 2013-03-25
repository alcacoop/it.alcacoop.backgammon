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

package it.alcacoop.backgammon.logic;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;


public class FibsNetHandler {

    private ExecutorService dispatchExecutor;
    private static LinkedBlockingQueue<Evt> queue;
    private int nreq;
    private Pool<Evt> evtPool;
    private Pool<FibsBoard> boardPool;
    
    private class Evt implements Poolable {
      public Events e;
      public Object o;
      public Evt() {
        reset();
      }
      public void init(Events _e, Object _o) {
        e = _e;
        o = _o;
      }
      @Override
      public void reset() {
        e = null;
        o = null;
      }
    }
        
    
    private class Dispatcher implements Runnable {
      private LinkedBlockingQueue<Evt> q;
      private FibsNetHandler i;
      private Events evt;
      private boolean found;
      public Dispatcher(LinkedBlockingQueue<Evt> _q, FibsNetHandler _i, Events _evt) {
        evt = _evt;
        q = _q;
        i = _i;
        found = false;
        i.incReq();
      }
      @Override
      public void run() {
        i.decReq();
        Evt e = null;
        while (!found) {
          try {
            e = q.take();
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
          if ((evt == null)&&(e.e!=null)) { //PASSO IL PRIMO DISPONIBILE
            GnuBackgammon.fsm.processEvent(e.e, e.o);
            found = true;
            evtPool.free(e);
          } else {
            System.out.println("SCARTO: "+e.e);
            if (evt==e.e) {
              GnuBackgammon.fsm.processEvent(e.e, e.o);
              found = true;
            }
            evtPool.free(e);
          }
        }
      }
    }

    
    
    public FibsNetHandler() {
      queue = new LinkedBlockingQueue<Evt>();
      dispatchExecutor = Executors.newSingleThreadExecutor();
      nreq=0;
      
      evtPool = new Pool<Evt>(5, 5) {
        protected Evt newObject() {
          return new Evt();
        };
      };
      boardPool = new Pool<FibsBoard>(5, 5) {
        protected FibsBoard newObject() {
          return new FibsBoard();
        };
      };
    }
    

    public void pull(Events evt) {
      dispatchExecutor.submit(new Dispatcher(queue, this, evt));
    }
    
    public void post(Events _e, Object _o) {
      if (_e==null) return;
      synchronized (queue) {
        Evt e = evtPool.obtain();
        e.init(_e, _o);
        try {
          queue.put(e);
          System.out.println("ACCODAMENTO MESSAGGIO: "+e.e);
          debug();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }        
      }
    }
    public void post() { //DEBUG PURPOSE..
      synchronized (queue) {
        Evt e = evtPool.obtain();
        e.init(Events.CONTINUE, null);
        try {
          queue.put(e);
          System.out.println("ACCODAMENTO MESSAGGIO: "+e.e);
          debug();
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }
    public synchronized int getReq() {
      return nreq;
    }
    public synchronized void incReq() {
      nreq++;
    }
    public synchronized void decReq() {
      nreq--;
    }
    public void reset() {
      synchronized (queue) {
        queue.clear();
        dispatchExecutor.shutdownNow();
        nreq = 0;
        dispatchExecutor = Executors.newSingleThreadExecutor();
      }
    }
    public void debug() {
      synchronized (queue) {
        System.out.println("RICHIESTE IN CODA: "+nreq);
        System.out.println("MESSAGGI IN CODA: "+queue.size());
        Iterator<Evt> itr = queue.iterator();
        while(itr.hasNext()) {
          Evt element = itr.next();
          System.out.println("  "+element.e);
       }
      }
    }
    
    public void releaseBoard(FibsBoard b) {
      boardPool.free(b);
    }
    public FibsBoard obtainBoard(String s) {
      FibsBoard b = boardPool.obtain();
      b.parseBoard(s);
      return b;
    }
}
