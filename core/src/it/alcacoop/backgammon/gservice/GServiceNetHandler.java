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
  private LinkedBlockingQueue<Evt> queue;

  private class Evt {
    public Events e;
    public Object o;

    public Evt(Events e, Object o) {
      this.e = e;
      this.o = o;
    }
  }


  private class Dispatcher implements Runnable {
    private Events[] evt;

    public Dispatcher(Events... _evt) {
      evt = _evt;
    }

    @Override
    public void run() {
      Evt e = null;
      while (true) {
        try {
          e = queue.take();
        } catch (InterruptedException e1) {
          return;
        }

        if (evt == null) { // PASSO IL PRIMO DISPONIBILE
          GnuBackgammon.fsm.processEvent(e.e, e.o);
          return;
        }

        for (int i = 0; i < evt.length; i++) {
          if (evt[i] == e.e) { // PASSO IL PRIMO DISPONIBILE TRA QUELLI RICHIESTI
            GnuBackgammon.fsm.processEvent(e.e, e.o);
            return;
          }
        }
      }
    }
  }


  public GServiceNetHandler() {
    queue = new LinkedBlockingQueue<Evt>();
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public synchronized void pull(final Events... evt) {
    GnuBackgammon.out.println("===> PULL REQUEST: " + evt[0]);
    dispatchExecutor.submit(new Dispatcher(evt));
  }

  public synchronized void post(final Events _e, final Object _o) {
    GnuBackgammon.out.println("===> POSTED EVENT: " + _e);
    if (_e == null)
      return;
    Evt e = new Evt(_e, _o);
    try {
      queue.put(e);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
  }


  public synchronized void dispose() {
    GnuBackgammon.out.println("===> QUEUE DISPOSED");
    dispatchExecutor.shutdownNow();
    queue.clear();
  }

  public synchronized void reset() {
    GnuBackgammon.out.println("===> QUEUE RESETTED");
    dispatchExecutor.shutdownNow();
    dispatchExecutor = Executors.newSingleThreadExecutor();
  }


  public synchronized void debug() {
    GnuBackgammon.out.println("===> EVENTI IN CODA: " + queue.size());
    GnuBackgammon.out.print("   ===> ");
    Iterator<Evt> itr = queue.iterator();
    while (itr.hasNext()) {
      Evt element = itr.next();
      GnuBackgammon.out.print("  " + element.e);
    }
    GnuBackgammon.out.println(" ");
  }
}
