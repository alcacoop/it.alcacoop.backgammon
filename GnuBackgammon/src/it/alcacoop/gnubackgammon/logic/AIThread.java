package it.alcacoop.gnubackgammon.logic;

import java.util.Stack;

public class AIThread extends Thread {

    private static Stack<Runnable> queue;
    
    public AIThread() {
      super();
      queue = new Stack<Runnable>();
      this.start();
    }
    
    @Override
    public void run() {
        while (true) {
            pull().run();
        }
    }
    
    public synchronized void post(Runnable r) {
      queue.push(r);
      notify();
    }
    
    public synchronized Runnable pull() {
      if(queue.empty())
        try {
          wait();
        } catch(InterruptedException e) {} 
      return queue.pop();
    }
    
    public synchronized static void reset() {
      queue.clear();
    }
}
