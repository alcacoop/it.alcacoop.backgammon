package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.AILevels;
import it.alcacoop.backgammon.logic.MatchState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

public class AchievementsManager {

  public static final Map<String, String> achievMap;
  static
  {
    achievMap = new HashMap<String, String>();
    achievMap.put("BEGINNER", "CgkI9ZWZjusDEAIQAw");
    achievMap.put("CASUAL", "CgkI9ZWZjusDEAIQCg");
    achievMap.put("INTERMEDIATE", "CgkI9ZWZjusDEAIQBA");
    achievMap.put("ADVANCED", "CgkI9ZWZjusDEAIQBQ");
    achievMap.put("EXPERT", "CgkI9ZWZjusDEAIQBg");
    achievMap.put("WORLDCLASS", "CgkI9ZWZjusDEAIQBw");
    achievMap.put("SUPREMO", "CgkI9ZWZjusDEAIQCA");
    achievMap.put("GRANDMASTER", "CgkI9ZWZjusDEAIQCQ");

    achievMap.put("TOURNAMENT_EXPERT", "CgkI9ZWZjusDEAIQCw");
    achievMap.put("TOURNAMENT_LEADER", "CgkI9ZWZjusDEAIQDA");
    achievMap.put("TOURNAMENT_STAR", "CgkI9ZWZjusDEAIQDQ");
    achievMap.put("BIG_BOSS_OF_TOURNAMENT", "CgkI9ZWZjusDEAIQDg");

    achievMap.put("SOCIAL_NEWBIE", "CgkI9ZWZjusDEAIQDw");
    achievMap.put("SOCIAL_PROUD", "CgkI9ZWZjusDEAIQEA");
    achievMap.put("SOCIAL_ADDICTED", "CgkI9ZWZjusDEAIQEQ");

    achievMap.put("MULTIPLAYER_TURTLE", "CgkI9ZWZjusDEAIQEg");
    achievMap.put("MULTIPLAYER_RABBIT", "CgkI9ZWZjusDEAIQEw");
    achievMap.put("MULTIPLAYER_DOBERMANN", "CgkI9ZWZjusDEAIQFA");
    achievMap.put("MULTIPLAYER_TIGER", "CgkI9ZWZjusDEAIQFQ");
  }

  public Preferences prefs;
  private static AchievementsManager instance;
  public static ArrayList<String> opponents_played;

  @SuppressWarnings("unchecked")
  private AchievementsManager() {
    prefs = Gdx.app.getPreferences("Achievemnts");
    String currentString = prefs.getString("OPPONENTS", "{}");
    Json json = new Json();
    opponents_played = json.fromJson(ArrayList.class, currentString);
  }

  public static synchronized AchievementsManager getInstance() {
    if (instance == null) instance = new AchievementsManager();
    return instance;
  }

  public void checkAchievements(boolean youWin) {
    switch (MatchState.matchType) {
    case 0:
      // Single player
      checkSinglePlayerAchievements(youWin);
      break;
    case 3:
      // Gservice
      checkMultiplayerAchievements(youWin);
    default:
      break;
    }
  }
  
  public void checkSocialAchievements(String opponent_player_id) {
    System.out.println("GSERVICE checkSocialAchievements" + opponent_player_id);
    if (!opponents_played.contains(opponent_player_id)) {
      opponents_played.add(opponent_player_id);
      
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_NEWBIE"), 1);
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_PROUD"), 1);
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("SOCIAL_ADDICTED"), 1);
      
      Json json = new Json();
      prefs.putString("OPPONENTS", json.toJson(opponents_played));
      prefs.flush();
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateState();
    }
  }

  /**
   * START PRIVATE METHODS
   */
  private void checkSinglePlayerAchievements(boolean youWin) {
    if (!youWin) return;

    switch (MatchState.nMatchTo) {
    case 7:
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(getSingleAchievementByGameLevel(), 1);
      if (MatchState.anScore[0] >= MatchState.nMatchTo) {
        GnuBackgammon.Instance.nativeFunctions.gserviceUnlockAchievement(getTournamentAchievementByGameLevel());
      }
      break;
    default:
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(getSingleAchievementByGameLevel(), 1);
      break;
    }
  }
  
  private void checkMultiplayerAchievements(boolean youWin) {
    if (!youWin) return;

    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_TURTLE"), 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_RABBIT"), 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_DOBERMANN"), 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(achievMap.get("MULTIPLAYER_TIGER"), 1);
  }

  private String getSingleAchievementByGameLevel() {
    String id = "";
    AILevels level = MatchState.currentLevel;
    switch (level) {
    case BEGINNER:
      id = achievMap.get("BEGINNER");
      break;
    case CASUAL:
      id = achievMap.get("CASUAL");
      break;
    case INTERMEDIATE:
      id = achievMap.get("INTERMEDIATE");
      break;
    case ADVANCED:
      id = achievMap.get("ADVANCED");
      break;
    case EXPERT:
      id = achievMap.get("EXPERT");
      break;
    case WORLDCLASS:
      id = achievMap.get("WORLDCLASS");
      break;
    case SUPREMO:
      id = achievMap.get("SUPREMO");
      break;
    case GRANDMASTER:
      id = achievMap.get("GRANDMASTER");
      break;
    default:
      break;
    }
    return id;
  }
  
  private String getTournamentAchievementByGameLevel() {
    String id = "";
    AILevels level = MatchState.currentLevel;
    switch (level) {
    case EXPERT:
      id = achievMap.get("TOURNAMENT_EXPERT");
      break;
    case WORLDCLASS:
      id = achievMap.get("TOURNAMENT_LEADER");
      break;
    case SUPREMO:
      id = achievMap.get("TOURNAMENT_STAR");
      break;
    case GRANDMASTER:
      id = achievMap.get("BIG_BOSS_OF_TOURNAMENT");
      break;
    default:
      break;
    }
    return id;
  }

}
