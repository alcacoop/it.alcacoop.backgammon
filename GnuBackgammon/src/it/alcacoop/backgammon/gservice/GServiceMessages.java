package it.alcacoop.backgammon.gservice;

public interface GServiceMessages {
  final static int GSERVICE_CONNECTED = 1;
  final static int GSERVICE_READY = 2;
  final static int GSERVICE_HANDSHAKE = 3;
  final static int GSERVICE_OPENING_ROLL = 4;
  final static int GSERVICE_BYE = 99;
  final static int GSERVICE_ERROR = 98;
}
