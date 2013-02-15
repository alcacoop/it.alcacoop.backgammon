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


public class FibsQueue {

    private static MessageQueue queue;
    
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
      public synchronized boolean empty(){
        if (list.size()==0) return true;
        else return false;
      }
    }
    
    
    
    private class Dispatcher extends Thread {
      private MessageQueue q;
      public Dispatcher(MessageQueue _q) {
        q = _q;
      }
      @Override
      public void run() {
        Evt e = q.pop();
        System.out.println("DELAYED DISPATCH...");
        GnuBackgammon.fsm.processEvent(e.e, e.o);
      }
    }
    
    
    public FibsQueue() {
      queue = new MessageQueue();
    }
    
    
    public void pull() {
      if (queue.empty()) { //CODA MESSAGGI VUOTA.. HO BISOGNO DI UN THREAD CHE ASPETTI..
        System.out.println("THREAD CREATION...");
        Dispatcher d = new Dispatcher(queue);
        d.start();
      } else { //ELEMENTO DISPONIBILE.. DISPATCH IMMEDIATO
        System.out.println("IMMEDIATE DISPATCH...");
        Evt e = queue.pop();
        GnuBackgammon.fsm.processEvent(e.e, e.o);
      }
    }
    
    public void post(Events e, Object o) {
      queue.push(new Evt(e,o));
    }

}
