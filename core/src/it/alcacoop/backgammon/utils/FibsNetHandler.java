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

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class FibsNetHandler {

  private ExecutorService dispatchExecutor, boardDispatchExecutor;
  private static LinkedBlockingQueue<Evt> queue, boardQueue;
  private int eventRequest, boardRequest;

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

    public Dispatcher(Events _evt) {
      evt = _evt;
      if (_evt == Events.FIBS_BOARD)
        boardRequest++;
      else
        eventRequest++;
    }
    @Override
    public void run() {
      Evt e = null;
      while (true) {
        try {
          if (evt == Events.FIBS_BOARD)
            e = boardQueue.take();
          else
            e = queue.take();
        } catch (InterruptedException e1) {
          return;
        }

        if (evt == Events.FIBS_BOARD)
          boardRequest--;
        else
          eventRequest--;

        if (evt == null) { // PASSO IL PRIMO DISPONIBILE
          GnuBackgammon.fsm.processEvent(e.e, e.o);
          return;
        } else if (evt == e.e) { // PASSO IL PRIMO RICHIESTO DISPONIBILE
          GnuBackgammon.fsm.processEvent(e.e, e.o);
          return;
        }
      }
    }
  }


  public FibsNetHandler() {
    queue = new LinkedBlockingQueue<Evt>();
    boardQueue = new LinkedBlockingQueue<Evt>();
    dispatchExecutor = Executors.newSingleThreadExecutor();
    boardDispatchExecutor = Executors.newSingleThreadExecutor();
    eventRequest = 0;
    boardRequest = 0;
  }


  public synchronized void pull(Events evt) {
    if (evt == Events.FIBS_BOARD)
      boardDispatchExecutor.submit(new Dispatcher(evt));
    else
      dispatchExecutor.submit(new Dispatcher(evt));
  }

  public synchronized void post(Events _e, Object _o) {
    if (_e == null)
      return;
    Evt e = new Evt(_e, _o);
    try {
      if (_e == Events.FIBS_BOARD)
        boardQueue.put(e);
      else
        queue.put(e);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    // debug();
  }

  public synchronized void reset() {
    queue.clear();
    boardQueue.clear();
    dispatchExecutor.shutdownNow();
    boardDispatchExecutor.shutdownNow();
  }

  public synchronized void boardReset() {
    ExecutorService tmp = boardDispatchExecutor;
    tmp.shutdownNow();
    boardQueue.clear();
    boardDispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public synchronized void debug() {
    GnuBackgammon.out.println("CODA EVENTI...");
    GnuBackgammon.out.println("RICHIESTE IN CODA: " + eventRequest);
    GnuBackgammon.out.println("MESSAGGI IN CODA: " + queue.size());
    Iterator<Evt> itr = queue.iterator();
    while (itr.hasNext()) {
      Evt element = itr.next();
      GnuBackgammon.out.print("  " + element.e);
    }

    GnuBackgammon.out.println("CODA BOARDS...");
    GnuBackgammon.out.println("RICHIESTE IN CODA: " + boardRequest);
    GnuBackgammon.out.println("MESSAGGI IN CODA: " + boardQueue.size());
    GnuBackgammon.out.println("");
  }

}
