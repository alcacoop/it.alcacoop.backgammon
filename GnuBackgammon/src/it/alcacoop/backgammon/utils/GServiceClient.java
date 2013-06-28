package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.fsm.BaseFSM.Events;

import java.io.*; 
import java.net.*;


public class GServiceClient {
  
  private Socket clientSocket;
  private DataOutputStream outToServer;
  private BufferedReader inFromServer;
  private boolean active = false;
  public static GServiceClient instance;
  
  
  private GServiceClient() {
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
            System.out.println("RECEIVED: "+s);
            s = s.replace("\n", "");
            int coockie = Integer.parseInt(s);
            switch (coockie) {
              case 1:
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_CONNECTED, null);
                break;
              case 2:
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_READY, null);
                break;
              case 99:
                active = false;
                GnuBackgammon.fsm.processEvent(Events.GSERVICE_BYE, null);
                break;
            }
          } catch (Exception e) {}
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
      outToServer.writeBytes(msg);
    } catch (Exception e) {}
  }
}
