/******************************************************************************
 * ClientConnection.java - Manage a connection to the FIBS server
 * $Id: ClientConnection.java,v 1.3 2010/03/03 13:12:21 inim Exp $
 * 
 * BuckoFIBS - Backgammon by BuckoSoft
 * Copyright© 2009,2010 - Dick Balaska - BuckoSoft, Corp.
 */
 
package com.buckosoft.fibs.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;


/** Manage a connection to the FIBS server.
 * This object runs in it's own Thread.
 * The only synchronized object is the outbound message queue.
 * @author Dick Balaska
 * @since 2008/03/29
 * @version $Revision: 1.3 $ <br> $Date: 2010/03/03 13:12:21 $
 * @see <a href="http://cvs.buckosoft.com/Projects/BuckoFIBS/BuckoFIBS/src/com/buckosoft/fibs/net/ClientConnection.java">cvs ClientConnection.java</a>
 */
public class ClientConnection extends Thread {

  final static String eol = "\r\n";

  private Socket sock = null;
  private InputStream is = null;
  private OutputStream os = null;
  boolean loggedIn = false;

  private ClientAdapter parser;
  private CookieMonster cookieMonster;
  private boolean shuttingDown = false;
  private LinkedList<String> outMessages = new LinkedList<String>();
  private Boolean listLock = new Boolean(false);
  private String leftover = null;
  private String pushbackString = null;

  public ClientConnection() {
    cookieMonster = new CookieMonster();
    cookieMonster.setClientConnection(this);
  }


  public void setClientAdapter(ClientAdapter clientAdapter) {
    parser = clientAdapter;
  }


  public void resetFIBSCookieMonster() {
    this.cookieMonster.resetFIBSCookieMonster();
  }

  public void pushBack(String s) {
    pushbackString = s;
  }

  
  public void sendMessage(String s) {
    if (s == null)
      throw new RuntimeException("Can't send a null message");
    accessOutMessages(s);
  }

  
  public void shutDown() {
    shuttingDown = true;
    try {
      if (os != null)
        os.close();
      os = null;
      if (is != null)
        is.close();
      is = null;
      if (sock != null) {
        sock.shutdownInput();
        sock.shutdownOutput();
        sock.close();
      }
      sock = null;
    } catch (SocketException sex) { 
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  
  private void accessOutMessages(String s) {
    synchronized (listLock) {
      if (s == null)
        outMessages.remove();
      else
        outMessages.add(s);
    }
  }

  
  @Override
  public synchronized void run() {
    sock = null;
    String server = "localhost";
    //server = "fibs.com";
    int port = 4321;
    try {
      sock = new Socket(server, port);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      return;
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    try {
      is = sock.getInputStream();
      os = sock.getOutputStream();

      while (!shuttingDown) {
        if (pushbackString != null) {
          String s = pushbackString;
          pushbackString = null;
          handleMessage(s);
          continue;
        }
        if (is.available() > 0) {
          readMessage();
          continue;
        }
        if (!outMessages.isEmpty()) {
          writeMessage();
          continue;
        }
        Thread.sleep(100);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (sock != null) {
      try {
        sock.shutdownInput();
        sock.shutdownOutput();
        sock.close();
      } catch (SocketException e) {
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void readMessage() {
    byte[] b;
    try {
      int r = is.available();
      b = new byte[r];
      is.read(b);
    } catch (IOException e) {
      e.printStackTrace();
      shuttingDown = true;
      return;
    }
    String s = new String(b);
    boolean hasLeftOver = false;
    
    String[] ss = s.split("\r\n");
    if (ss.length == 0)
      return;
    if (leftover != null) {
      ss[0] = leftover + ss[0];
      leftover = null;
    }
    for (int i=0; i<ss.length; i++) {
      if (i == ss.length-1 && hasLeftOver && !ss[i].startsWith("login:"))
        leftover = ss[i];
      else
        handleMessage(ss[i]);
      while (pushbackString != null) {
        String t = pushbackString;
        pushbackString = null;
        handleMessage(t);
      }
    }
  }

  /** Send the login message to FIBS.
  */
  public void sendLogin() {
    String user = "pippo";
    String pwd = "pippo";
    StringBuffer sb = new StringBuffer();
    sb.append("login ");
    sb.append("BackGammonMobile-0.1");
    sb.append(" 1008 ");
    sb.append(user);
    sb.append(" ");
    sb.append(pwd);
    sb.append(eol);
    this.sendMessage(sb.toString());
  }

  private void writeMessage() {
    String s = outMessages.getFirst();
    
    
    try {
      if (os == null) {
        shuttingDown = true;
        return;
      }
      os.write(s.getBytes());
    } catch (Exception e) {
      parser.connectionAborted();
      e.printStackTrace();
      shuttingDown = true;
    }
    this.accessOutMessages(null);
  }

  private void handleMessage(String s) {
    if (s.length() > 1 && s.charAt(0) == 13) // I have seen two '5's runon with just a cr between them,
      s = s.substring(1); // not a crlf. Check for that here.
    if (s.length() > 1 && s.charAt(0) == 10) // I have seen two '5's runon with just a lf between them,
      s = s.substring(1); // not a crlf. Check for that here.
    int cookie = this.cookieMonster.fIBSCookie(s);
    
    if (this.pushbackString != null) {
      s = s.substring(0, s.length()-this.pushbackString.length());
    }
    parser.dispatch(cookie, s);
  }
}
