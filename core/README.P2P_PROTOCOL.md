# P2P protocol logic

This file describes the logic behind p2p connection through a TCP channel between 2 clients.

## Protocol codes

These are the allowed messages to be sent:

* 1: Connected, Wait for opponent
* 2: Ready to play
* 99: Bye

## Test scenarios

### Single player mode:
1. Win matchTo = 1
  - update single achievement by gamelevel
  - update singleplayer leaderboard
2. Win matchTo > 1, matchTo != 7
  - update single game achievement by gamelevel
  - update singleplayer leaderboard if match finished
3. Win matchTo = 7
  - update single game achievement by gamelevel
  - update tournament achievement if match finished
  - update singleplayer leaderboard if match finished

### Multi player mode:
1. Invite friends
  - update social achievements
2. Win against friend
  - update multiplayer achievements
  - update multiplayer leaderboard
