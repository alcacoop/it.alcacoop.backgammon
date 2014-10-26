package it.alcacoop.backgammon.gservice;

import it.alcacoop.backgammon.GnuBackgammon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;


public class GServiceCookieMonster implements GServiceMessages {
  private final static boolean DEBUG = false;

  private class CookieDough {
    public Pattern regex = null;
    public int message = 0;
  }

  private LinkedList<CookieDough> numericBatch;

  public GServiceCookieMonster() {
    prepareBatches();
  }

  public int gserviceCookie(String message) {
    int result = 0;
    Iterator<GServiceCookieMonster.CookieDough> iter;
    CookieDough ptr = null;

    iter = numericBatch.iterator();
    while (iter.hasNext()) {
      ptr = iter.next();
      if (ptr.regex.matcher(message).find()) {
        result = ptr.message;
        break;
      }
    }

    if (result == 0)
      return (0);

    String[] ss = ptr.regex.split(message, 2);
    if (ss.length > 1 && ss[1].length() > 0) {
      if (DEBUG) {
        GnuBackgammon.out.println("cookie = " + result);
        GnuBackgammon.out.println("message = '" + message + "'");
        GnuBackgammon.out.println("Leftover = '" + ss[1] + "'");
      }
    }
    return (result);
  }


  LinkedList<CookieDough> currentBatchBuild;

  private void addDough(int msg, String re) {
    CookieDough newDough = new CookieDough();
    newDough.regex = Pattern.compile(re);
    newDough.message = msg;
    currentBatchBuild.add(newDough);
  }


  private void prepareBatches() {
    currentBatchBuild = new LinkedList<CookieDough>();
    addDough(GSERVICE_CONNECTED, "^" + GSERVICE_CONNECTED + "$");
    addDough(GSERVICE_READY, "^" + GSERVICE_READY + "$");
    addDough(GSERVICE_INIT_RATING, "^" + GSERVICE_INIT_RATING + " ");
    addDough(GSERVICE_HANDSHAKE, "^" + GSERVICE_HANDSHAKE + " ");
    addDough(GSERVICE_OPENING_ROLL, "^" + GSERVICE_OPENING_ROLL + " ");
    addDough(GSERVICE_PLAY_AGAIN, "^" + GSERVICE_PLAY_AGAIN + " ");
    addDough(GSERVICE_ROLL, "^" + GSERVICE_ROLL + " ");
    addDough(GSERVICE_DOUBLE, "^" + GSERVICE_DOUBLE + " ");
    addDough(GSERVICE_ACCEPT, "^" + GSERVICE_ACCEPT + " ");
    addDough(GSERVICE_MOVE, "^" + GSERVICE_MOVE + " ");
    addDough(GSERVICE_BOARD, "^" + GSERVICE_BOARD + " ");
    addDough(GSERVICE_PING, "^" + GSERVICE_PING + " ");
    addDough(GSERVICE_CHATMSG, "^" + GSERVICE_CHATMSG + " ");
    addDough(GSERVICE_ABANDON, "^" + GSERVICE_ABANDON + " ");
    addDough(GSERVICE_ERROR, "^" + GSERVICE_ERROR + "$");
    addDough(GSERVICE_BYE, "^" + GSERVICE_BYE + "$");
    this.numericBatch = this.currentBatchBuild;
  }
}
