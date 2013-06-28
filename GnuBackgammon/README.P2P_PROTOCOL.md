# P2P protocol logic

This file describes the logic behind p2p connection through a TCP channel between 2 clients.

## Protocol codes

These are the allowed messages to be sent:

* 1: Connected, Wait for opponent
* 2: Ready to play
* 99: Bye
