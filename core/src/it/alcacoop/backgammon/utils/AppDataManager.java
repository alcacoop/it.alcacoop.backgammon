package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.utils.legacy.Json;

import java.util.ArrayList;
import java.util.HashMap;

public class AppDataManager {

  private HashMap<String, String> app_data;
  private static AppDataManager instance;

  public static AppDataManager getInstance() {
    if (instance == null)
      instance = new AppDataManager();
    return instance;
  }


  private AppDataManager() {
    app_data = new HashMap<String, String>();
  }

  public byte[] getBytes() {
    app_data.put("singleboard", GnuBackgammon.Instance.optionPrefs.getString("SINGLEBOARD", "0"));
    app_data.put("multiboard", GnuBackgammon.Instance.optionPrefs.getString("MULTIBOARD", "0"));
    app_data.put("fibsboard2", GnuBackgammon.Instance.optionPrefs.getString("FIBSBOARD2", "0"));
    app_data.put("tigaboard", GnuBackgammon.Instance.optionPrefs.getString("TIGABOARD", "0"));

    app_data.put("sound", GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes"));
    app_data.put("speed", GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast"));
    app_data.put("amoves", GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap"));
    app_data.put("lmoves", GnuBackgammon.Instance.optionPrefs.getString("LMOVES", "Yes"));
    app_data.put("dicesg", GnuBackgammon.Instance.optionPrefs.getString("DICESG", "MER-TWS"));
    app_data.put("greedy", GnuBackgammon.Instance.optionPrefs.getString("GREEDY", "No"));

    app_data.put("board", GnuBackgammon.Instance.appearancePrefs.getString("BOARD", "B1"));
    app_data.put("checkers", GnuBackgammon.Instance.appearancePrefs.getString("CHECKERS", "CS1"));
    app_data.put("direction", GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise"));
    app_data.put("numberedp", GnuBackgammon.Instance.appearancePrefs.getString("NPOINTS", "Yes"));

    app_data.put("fusername", GnuBackgammon.Instance.fibsPrefs.getString("fusername", ""));
    app_data.put("fpassword", GnuBackgammon.Instance.fibsPrefs.getString("fpassword", ""));
    app_data.put("tusername", GnuBackgammon.Instance.fibsPrefs.getString("tusername", ""));
    app_data.put("tpassword", GnuBackgammon.Instance.fibsPrefs.getString("tpassword", ""));

    app_data.put("opponents", AchievementsManager.getInstance().prefs.getString("OPPONENTS", "{}"));

    for (int i = 0; i < 9; i++) { // STATS
      app_data.put("stat_" + i, GnuBackgammon.Instance.optionPrefs.getString("STAT_" + i, ""));
    }

    Json json = new Json();
    return json.toJson(app_data).getBytes();
  }


  @SuppressWarnings("unchecked")
  public void loadState(byte[] bytes) {
    Json json = new Json();
    if (bytes.length == 0)
      return;
    app_data = json.fromJson(HashMap.class, new String(bytes));
    if (app_data == null)
      return;
    savePrefs();
  }


  @SuppressWarnings("unchecked")
  public byte[] resolveConflict(byte[] local, byte[] remote) {
    Json jLocal = new Json();
    HashMap<String, String> hLocal = jLocal.fromJson(HashMap.class, new String(local));
    Json jRemote = new Json();
    HashMap<String, String> hRemote = jRemote.fromJson(HashMap.class, new String(remote));

    double single = Math.max(Double.parseDouble(hLocal.get("singleboard")), Double.parseDouble(hRemote.get("singleboard")));
    double multi = Math.max(Double.parseDouble(hLocal.get("multiboard")), Double.parseDouble(hRemote.get("multiboard")));
    double fibs = Math.max(Double.parseDouble(hLocal.get("fibsboard2")), Double.parseDouble(hRemote.get("fibsboard2")));
    double tiga = Math.max(Double.parseDouble(hLocal.get("tigaboard")), Double.parseDouble(hRemote.get("tigaboard")));

    app_data.put("singleboard", single + "");
    app_data.put("multiboard", multi + "");
    app_data.put("fibsboard2", fibs + "");
    app_data.put("tigaboard", tiga + "");

    app_data.put("sound", hRemote.get("sound"));
    app_data.put("speed", hRemote.get("speed"));
    app_data.put("amoves", hRemote.get("amoves"));
    app_data.put("lmoves", hRemote.get("lmoves"));
    app_data.put("dicesg", hRemote.get("dicesg"));
    app_data.put("greedy", hRemote.get("greedy"));

    app_data.put("board", hRemote.get("board"));
    app_data.put("checkers", hRemote.get("checkers"));
    app_data.put("direction", hRemote.get("direction"));
    app_data.put("numberedp", hRemote.get("numberedp"));

    app_data.put("fusername", hRemote.get("fusername"));
    app_data.put("fpassword", hRemote.get("fpassword"));
    app_data.put("tusername", hRemote.get("tusername"));
    app_data.put("tpassword", hRemote.get("tpassword"));

    for (int i = 0; i < 9; i++) { // STATS
      GnuBackgammon.out.println(" ===> CONFLICT REMOTE STATS: " + hRemote.get("stat_" + i));
      if (hRemote.get("stat_" + i) != "")
        app_data.put("stat_" + i, hRemote.get("stat_" + i));
      else
        app_data.put("stat_" + i, hLocal.get("stat_" + i));
    }

    ArrayList<String> local_played_list = jLocal.fromJson(ArrayList.class, hLocal.get("opponents"));
    ArrayList<String> remote_played_list = jRemote.fromJson(ArrayList.class, hRemote.get("opponents"));
    if (local_played_list.size() >= remote_played_list.size())
      app_data.put("opponents", hLocal.get("opponents"));
    else
      app_data.put("opponents", hRemote.get("opponents"));

    savePrefs();
    return new Json().toJson(app_data).getBytes();
  }


  private void savePrefs() {
    GnuBackgammon.Instance.optionPrefs.putString("SINGLEBOARD", app_data.get("singleboard"));
    GnuBackgammon.Instance.optionPrefs.putString("MULTIBOARD", app_data.get("multiboard"));
    GnuBackgammon.Instance.optionPrefs.putString("TIGABOARD", app_data.get("tigaboard"));
    GnuBackgammon.Instance.optionPrefs.putString("FIBSBOARD2", app_data.get("fibsboard2"));

    GnuBackgammon.Instance.optionPrefs.putString("SOUND", app_data.get("sound"));
    GnuBackgammon.Instance.optionPrefs.putString("SPEED", app_data.get("speed"));
    GnuBackgammon.Instance.optionPrefs.putString("AMOVES", app_data.get("amoves"));
    GnuBackgammon.Instance.optionPrefs.putString("LMOVES", app_data.get("lmoves"));
    GnuBackgammon.Instance.optionPrefs.putString("DICESG", app_data.get("dicesg"));
    GnuBackgammon.Instance.optionPrefs.putString("GREEDY", app_data.get("greedy"));

    GnuBackgammon.Instance.appearancePrefs.putString("BOARD", app_data.get("board"));
    GnuBackgammon.Instance.appearancePrefs.putString("CHECKERS", app_data.get("checkers"));
    GnuBackgammon.Instance.appearancePrefs.putString("DIRECTION", app_data.get("direction"));
    GnuBackgammon.Instance.appearancePrefs.putString("NPOINTS", app_data.get("numberedp"));

    GnuBackgammon.Instance.fibsPrefs.putString("fusername", app_data.get("fusername"));
    GnuBackgammon.Instance.fibsPrefs.putString("fpassword", app_data.get("fpassword"));
    GnuBackgammon.Instance.fibsPrefs.putString("tusername", app_data.get("tusername"));
    GnuBackgammon.Instance.fibsPrefs.putString("tpassword", app_data.get("tpassword"));

    AchievementsManager.getInstance().prefs.putString("OPPONENTS", app_data.get("opponents"));

    for (int i = 0; i < 9; i++) { // STATS
      if (app_data.get("stat_" + i) != "")
        GnuBackgammon.Instance.optionPrefs.putString("STAT_" + i, app_data.get("stat_" + i));
    }

    GnuBackgammon.Instance.optionPrefs.flush();
    GnuBackgammon.Instance.appearancePrefs.flush();
    GnuBackgammon.Instance.fibsPrefs.flush();
    AchievementsManager.getInstance().prefs.flush();
  }
}
