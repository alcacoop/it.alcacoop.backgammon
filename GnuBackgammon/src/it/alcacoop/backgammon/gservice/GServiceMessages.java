package it.alcacoop.backgammon.gservice;

public interface GServiceMessages {
  final static int GSERVICE_CONNECTED = 1;
  final static int GSERVICE_READY = 2;
  final static int GSERVICE_HANDSHAKE = 3;
  final static int GSERVICE_OPENING_ROLL = 4;
  final static int GSERVICE_ROLL = 5;
  final static int GSERVICE_MOVE = 6;
  final static int GSERVICE_BOARD = 7;
  final static int GSERVICE_INIT_RATING = 8;
  final static int GSERVICE_DOUBLE = 9;
  final static int GSERVICE_ACCEPT = 10;

  final static int GSERVICE_PLAY_AGAIN = 20;

  final static int GSERVICE_PING = 70;
  final static int GSERVICE_CHATMSG = 90;
  final static int GSERVICE_ABANDON = 97;
  final static int GSERVICE_ERROR = 98;
  final static int GSERVICE_BYE = 99;
}
