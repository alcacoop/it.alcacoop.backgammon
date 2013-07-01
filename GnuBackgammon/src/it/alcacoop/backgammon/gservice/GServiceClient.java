package it.alcacoop.backgammon.gservice;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.io.*; 
import java.net.*;


public class GServiceClient implements GServiceMessages {
  
  private Socket clientSocket;
  private DataOutputStream outToServer;
  private BufferedReader inFromServer;
  private boolean active = false;
  public static GServiceClient instance;

  public GServiceNetHandler net;
  public GServiceCookieMonster coockieMonster;
  
  private GServiceClient() {
    net = new GServiceNetHandler();
    coockieMonster = new GServiceCookieMonster();
  }

  public static GServiceClient getInstance() {
    if (instance == null) instance = new GServiceClient();
    return instance;
  }
  
  public void connect() {
    try {
      clientSocket = new Socket("dmartella.homelinux.net", 4321);
      outToServer = new DataOutputStream(clientSocket.getOutputStream());
      inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (Exception e) {
      active = false;
      return;
    }
    
    Runnable r = new Runnable() {
      @Override
      public void run() {
        System.out.println("STARTING READER THREAD..");
        active = true;
        while (active) {
          try {
            String s = inFromServer.readLine();
            if (s==null) {
              active=false;
              clientSocket.close();
              GnuBackgammon.fsm.processEvent(Events.GSERVICE_ERROR, null);
              net.reset();
            }
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
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_FIRSTROLL, p);
                break;
              case GSERVICE_BYE:
                active = false;
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
                net.reset();
                break;
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        System.out.println("SHUTTING DOWN");
      }
    };

    Thread t = new Thread(r);
    t.start();
  }
  
  public void disconnect() {
    sendMessage("BYE\n");
  }
  
  public void sendMessage(String msg) {
    try {
      outToServer.writeBytes(msg+"\n");
    } catch (Exception e) {}
  }
}
