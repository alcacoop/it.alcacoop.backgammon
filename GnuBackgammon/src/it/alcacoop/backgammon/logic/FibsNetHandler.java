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

package it.alcacoop.backgammon.logic;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;
import java.util.LinkedList;

/*XXX
 * NEED REFACTORING VIA ThreadPoolExecutor 
 */

public class FibsNetHandler {

    private static MessageQueue queue;
    int nreq;
    
    private class Evt {
      public Events e;
      public Object o;
      public Evt(Events _e, Object _o) {
        e = _e;
        o = _o;
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
    
    
    private class Dispatcher extends Thread {
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
          if (i.getReq()>0) queue.push(new Evt(Events.NOOP, null)); 
        }
      }
    }
    
    public FibsNetHandler() {
      queue = new MessageQueue();
      nreq=0;
    }
    
    
    public void pull() {
      if (queue.isEempty()) { //CODA MESSAGGI VUOTA.. HO BISOGNO DI UN THREAD CHE ASPETTI..
        Dispatcher d = new Dispatcher(queue, this);
        d.start();
      } else { //ELEMENTO DISPONIBILE.. DISPATCH IMMEDIATO
        Evt e = queue.pop();
        GnuBackgammon.fsm.processEvent(e.e, e.o);
      }
    }
    
    public void post(Events e, Object o) {
      queue.push(new Evt(e,o));
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
      if (getReq()>0) queue.push(new Evt(Events.NOOP, null)); //RESET
    }
    public void debug() {
      System.out.println("RICHIESTE IN CODA: "+nreq);
      System.out.println("MESSAGGI IN CODA: "+queue.getSize());
    }
}
