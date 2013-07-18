package it.alcacoop.backgammon.gservice;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.util.concurrent.ArrayBlockingQueue;


public class GServiceClient implements GServiceMessages {
  
  public static GServiceClient instance;
  public GServiceNetHandler queue;
  public GServiceCookieMonster coockieMonster;
  public ArrayBlockingQueue<String> sendQueue;
  private Thread sendThread;

  
  private GServiceClient() {
    queue = new GServiceNetHandler();
    coockieMonster = new GServiceCookieMonster();
    sendQueue = new ArrayBlockingQueue<String>(20);
    
    sendThread = new Thread(){
      @Override
      public void run() {
        while (true) {
          try {
            System.out.println("GSERVICE: TAKING....");
            String msg = sendQueue.take();
            GnuBackgammon.Instance.nativeFunctions.gserviceSendReliableRealTimeMessage(msg);
            
            System.out.println("GSERVICE: WAITING....");
            synchronized (sendThread) {
              wait();
            }
            
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    sendThread.start();
  }

  public static GServiceClient getInstance() {
    if (instance == null) instance = new GServiceClient();
    return instance;
  }
  
  
  public void connect() {
    System.out.println("GSERVICE: CLIENT INITIALIZATION");
  }
 
  
  public void precessReceivedMessage(String s) {
	  System.out.println("RECEIVED: "+s);
      int coockie = coockieMonster.fIBSCookie(s);
      switch (coockie) {
        case GSERVICE_CONNECTED:
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_CONNECTED, null);
          break;
        case GSERVICE_READY:
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_READY, null);
          break;
        case GSERVICE_HANDSHAKE:
          String chunks[] = s.split(" ");
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_HANDSHAKE, Long.parseLong(chunks[1]));
          break;
        case GSERVICE_OPENING_ROLL:
          chunks = s.split(" ");
          int p[] = {Integer.parseInt(chunks[1]), Integer.parseInt(chunks[2]), Integer.parseInt(chunks[3])};
          queue.post(Events.GSERVICE_FIRSTROLL, p);
          break;
        case GSERVICE_ROLL:
          chunks = s.split(" ");
          int dices[] ={0, 0};
          for (int i=1;i<3;i++)
            dices[i-1] = Integer.parseInt(chunks[i]);
          queue.post(Events.GSERVICE_ROLL, dices);
          break;
        case GSERVICE_MOVE:
          chunks = s.split(" ");
          int moves[] ={-1, -1, -1, -1, -1, -1, -1, -1};
          for (int i=0;i<8;i++)
            moves[i] = Integer.parseInt(chunks[i+1]);
          queue.post(Events.GSERVICE_MOVES, moves);
          break;
        case GSERVICE_BOARD:
          chunks = s.split(" ");
          int[][] board = new int[2][25];
          for (int i=0;i<25;i++) board[0][i] = Integer.parseInt(chunks[i+1]);
          for (int i=25;i<50;i++) board[1][i-25] = Integer.parseInt(chunks[i+1]);
          queue.post(Events.GSERVICE_BOARD, board);
          break;
        case GSERVICE_CHATMSG:
          s = s.replace("90 ", "");
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_CHATMSG, s);
          break;
        case GSERVICE_ABANDON:
          chunks = s.split(" ");
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_ABANDON, Integer.parseInt(chunks[1]));
          break;
        case GSERVICE_PING:
        case GSERVICE_ERROR:
          break;
        case GSERVICE_BYE:
          GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
          break;
      }
  }
  
  
  public synchronized void sendMessage(String msg) {
    try {
      sendQueue.put(msg);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void notifyDispatched() {
    synchronized (sendThread) {
     sendThread.notify(); 
    }
  }
  
  private final static int STATUS_OK = 0;
  private final static int STATUS_NETWORK_ERROR_OPERATION_FAILED = 6;
  
  public void leaveRoom(int code) {
    GnuBackgammon.Instance.nativeFunctions.gserviceResetRoom();
    switch (code) {
    case STATUS_OK:
      // opponent disconnected
      GnuBackgammon.fsm.processEvent(Events.GSERVICE_ERROR, 0);
      break;
    case STATUS_NETWORK_ERROR_OPERATION_FAILED:
      // you disconnected
      GnuBackgammon.fsm.processEvent(Events.GSERVICE_ERROR, 1);
      break;
    case 10000:
      // activity stopped
      GnuBackgammon.fsm.processEvent(Events.GSERVICE_ERROR, 2);
      break;
    default:
      GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
      break;
    }
  }
}
