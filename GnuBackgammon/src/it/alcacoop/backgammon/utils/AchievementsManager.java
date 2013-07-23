package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.logic.AILevels;
import it.alcacoop.backgammon.logic.MatchState;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

public class AchievementsManager {

  private static final int UNHANDLED_LEVEL = -1;
  private Preferences prefs;
  private static AchievementsManager instance;
  private static ArrayList<String> opponents_played;

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

  public void checkAchievements() {
    switch (MatchState.matchType) {
    case 0:
      // Single player
      checkSinglePlayerAchievements();
      break;
    case 3:
      // Gservice
      checkMultiplayerAchievements();
    default:
      break;
    }
  }
  
  public void checkSocialAchievements(String opponent_player_id) {
    System.out.println("GSERVICE checkSocialAchievements" + opponent_player_id);
    if (!opponents_played.contains(opponent_player_id)) {
      opponents_played.add(opponent_player_id);
      
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.SOCIAL_NEWBIE, 1);
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.SOCIAL_PROUD, 1);
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.SOCIAL_ADDICTED, 1);
      
      Json json = new Json();
      prefs.putString("OPPONENTS", json.toJson(opponents_played));
      prefs.flush();
    }
  }

  private void checkSinglePlayerAchievements() {
    /*
     * If fMove != 0 the opponent won the game,
     * so we have not achievements to increment 
     */
    if (MatchState.fMove != 0) return;

    switch (MatchState.nMatchTo) {
    case 1:
      int single_achiev_id = getSingleAchievementByGameLevel();
      GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(single_achiev_id, 1);
      break;
    case 3:
      if (MatchState.anScore[0] >= MatchState.nMatchTo) {
        int tournament_achiev_id = getTournamentAchievementByGameLevel();
        GnuBackgammon.Instance.nativeFunctions.gserviceUnlockAchievement(tournament_achiev_id);
      }
      break;
    default:
      break;
    }
  }
  
  private void checkMultiplayerAchievements() {
    /*
     * If fMove != 0 the opponent won the game,
     * so we have not achievements to increment 
     */
    if (MatchState.fMove != 0) return;

    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.MULTIPLAYER_TURTLE, 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.MULTIPLAYER_RABBIT, 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.MULTIPLAYER_DOBERMANN, 1);
    GnuBackgammon.Instance.nativeFunctions.gserviceUpdateAchievement(Achievements.MULTIPLAYER_TIGER, 1);
  }

  private int getSingleAchievementByGameLevel() {
    int id = 0;
    AILevels level = MatchState.currentLevel;
    switch (level) {
    case BEGINNER:
      id = Achievements.BEGINNER;
      break;
    case CASUAL:
      id = Achievements.CASUAL;
      break;
    case INTERMEDIATE:
      id = Achievements.INTERMEDIATE;
      break;
    case ADVANCED:
      id = Achievements.ADVANCED;
      break;
    case EXPERT:
      id = Achievements.EXPERT;
      break;
    case WORLDCLASS:
      id = Achievements.WORLDCLASS;
      break;
    case SUPREMO:
      id = Achievements.SUPREMO;
      break;
    case GRANDMASTER:
      id = Achievements.GRANDMASTER;
      break;
    default:
      id = UNHANDLED_LEVEL;
      break;
    }
    
    return id;
  }
  
  private int getTournamentAchievementByGameLevel() {
    int id = 0;
    AILevels level = MatchState.currentLevel;
    switch (level) {
    case EXPERT:
      id = Achievements.TOURNAMENT_EXPERT;
      break;
    case WORLDCLASS:
      id = Achievements.TOURNAMENT_LEADER;
      break;
    case SUPREMO:
      id = Achievements.TOURNAMENT_STAR;
      break;
    case GRANDMASTER:
      id = Achievements.BIG_BOSS_OF_TOURNAMENT;
      break;
    default:
      id = UNHANDLED_LEVEL;
      break;
    }
    
    return id;
  }

}
