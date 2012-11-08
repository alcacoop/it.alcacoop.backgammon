package it.alcacoop.gnubackgammon.logic;

import java.util.Stack;

public class AIThread extends Thread {

    private Stack<Runnable> queue;
    public AIThread() {
      super();
      queue = new Stack<Runnable>();
      this.start();
    }
    
    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(250);
          synchronized (queue) {
            if (!queue.empty()) queue.pop().run();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    
    public void post(Runnable r) {
      queue.push(r);
    }
}
