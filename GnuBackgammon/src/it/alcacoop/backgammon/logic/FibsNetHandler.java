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
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;


public class FibsNetHandler {

    private ExecutorService dispatchExecutor;
    private static MessageQueue queue;
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
        e = Events.NOOP;
        o = null;
      }
    }
    
    
    private class MessageQueue {
      private LinkedList<Evt> list;
      public MessageQueue() {
        list = new LinkedList<Evt>();
      }
      public synchronized Evt pop() {
        try {
          while(list.isEmpty())
            wait();

        } catch(InterruptedException ex) {}
        return list.poll();
      }
      public synchronized void push(Evt e) {
        list.add(e);
        notify();
      }
      public synchronized boolean isEempty(){
        if (list.size()==0) return true;
        else return false;
      }
      public synchronized void empty() {
        if (!this.isEempty()) list.clear();
      }
      public synchronized int getSize() {
        return list.size();
      }
    }
    
    
    private class Dispatcher implements Runnable {
      private MessageQueue q;
      private FibsNetHandler i;
      public Dispatcher(MessageQueue _q, FibsNetHandler _i) {
        q = _q;
        i = _i;
        i.incReq();
      }
      @Override
      public void run() {
        Evt e = q.pop();
        i.decReq();
        if (e.e!=Events.NOOP) {
          GnuBackgammon.fsm.processEvent(e.e, e.o);
        } else { //RESET REQ
          if (i.getReq()>0) queue.push(evtPool.obtain()); 
        }
        evtPool.free(e);
      }
    }
    
    public FibsNetHandler() {
      queue = new MessageQueue();
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
    
    
    public void pull() {
      if (queue.isEempty()) { //CODA MESSAGGI VUOTA.. HO BISOGNO DI UN THREAD CHE ASPETTI..
        dispatchExecutor.execute(new Dispatcher(queue, this));
      } else { //ELEMENTO DISPONIBILE.. DISPATCH IMMEDIATO
        Evt e = queue.pop();
        GnuBackgammon.fsm.processEvent(e.e, e.o);
        evtPool.free(e);
      }
    }
    
    
    public void post(Events _e, Object _o) {
      Evt e = evtPool.obtain();
      e.init(_e, _o);
      queue.push(e);
    }
    public void post() {
      Evt e = evtPool.obtain();
      e.init(Events.CONTINUE, null);
      queue.push(e);
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
      queue.empty();
      if (getReq()>0) queue.push(evtPool.obtain()); //RESET
    }
    public void debug() {
      System.out.println("RICHIESTE IN CODA: "+nreq);
      System.out.println("MESSAGGI IN CODA: "+queue.getSize());
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
