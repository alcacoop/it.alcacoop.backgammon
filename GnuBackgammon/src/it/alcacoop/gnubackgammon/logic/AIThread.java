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
      while(true) {
        try {
          Runnable r = queue.pop();
          r.run();
        } catch(Exception e) {        
        }
      }
    }
    
    public void post(Runnable r) {
       queue.push(r);
    }
}
