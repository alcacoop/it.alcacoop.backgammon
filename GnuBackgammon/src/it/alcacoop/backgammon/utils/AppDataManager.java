package it.alcacoop.backgammon.utils;

import it.alcacoop.backgammon.GnuBackgammon;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;

public class AppDataManager {

  private HashMap<String, String> app_data;
  private static AppDataManager instance;
  
  public static AppDataManager getInstance() {
    if (instance==null)
      instance = new AppDataManager();
    return instance;
  }
  
  
  private AppDataManager() {
    app_data = new HashMap<String, String>();
  }
  
  public byte[] getBytes() {
    app_data.put("singleboard", GnuBackgammon.Instance.optionPrefs.getString("SINGLEBOARD", "0"));
    app_data.put("multiboard", GnuBackgammon.Instance.optionPrefs.getString("MULTIBOARD", "0"));
    
    app_data.put("sound", GnuBackgammon.Instance.optionPrefs.getString("SOUND", "Yes"));
    app_data.put("speed", GnuBackgammon.Instance.optionPrefs.getString("SPEED", "Fast"));
    app_data.put("amoves", GnuBackgammon.Instance.optionPrefs.getString("AMOVES", "Tap"));
    app_data.put("lmoves", GnuBackgammon.Instance.optionPrefs.getString("LMOVES", "Yes"));
    app_data.put("mdices", GnuBackgammon.Instance.optionPrefs.getString("MDICES", "No"));
    
    app_data.put("board", GnuBackgammon.Instance.appearancePrefs.getString("BOARD", "B1"));
    app_data.put("checkers", GnuBackgammon.Instance.appearancePrefs.getString("CHECKERS", "CS1"));
    app_data.put("direction", GnuBackgammon.Instance.appearancePrefs.getString("DIRECTION", "AntiClockwise"));
    app_data.put("numberedp", GnuBackgammon.Instance.appearancePrefs.getString("NPOINTS", "Yes"));
    Json json = new Json();
    return json.toJson(app_data).getBytes();
  }

  
  @SuppressWarnings("unchecked")
  public void loadState(byte[] bytes) {
    Json json = new Json();
    if (bytes.length==0) return;
    else System.out.println("GSERVICE: "+new String(bytes));
    app_data = json.fromJson(HashMap.class, new String(bytes));
    savePrefs();
  }
  
  
  @SuppressWarnings("unchecked")
  public byte[] resolveConflict(byte[] local, byte[] remote) {
    Json jLocal = new Json();
    HashMap<String, String> hLocal = jLocal.fromJson(HashMap.class, new String(local));
    Json jRemote = new Json();
    HashMap<String, String> hRemote = jRemote.fromJson(HashMap.class, new String(remote));
    
    int single = Math.max(Integer.parseInt(hLocal.get("singleboard")), Integer.parseInt(hRemote.get("singleboard")));
    int multi = Math.max(Integer.parseInt(hLocal.get("multiboard")), Integer.parseInt(hRemote.get("multiboard")));
    
    app_data.put("singleboard", single+"");
    app_data.put("multiboard", multi+"");
    
    app_data.put("sound", hRemote.get("sound"));
    app_data.put("speed", hRemote.get("speed"));
    app_data.put("amoves", hRemote.get("amoves"));
    app_data.put("lmoves", hRemote.get("lmoves"));
    app_data.put("mdices", hRemote.get("mdices"));
    
    app_data.put("board", hRemote.get("board"));
    app_data.put("checkers", hRemote.get("checkers"));
    app_data.put("direction", hRemote.get("direction"));
    app_data.put("numberedp", hRemote.get("numberedp"));
    
    savePrefs();
    return new Json().toJson(app_data).getBytes();
  }
  
  
  private void savePrefs() {
    GnuBackgammon.Instance.optionPrefs.putString("SINGLEBOARD", app_data.get("singleboard"));
    GnuBackgammon.Instance.optionPrefs.putString("MULTIBOARD", app_data.get("multiboard"));
    
    GnuBackgammon.Instance.optionPrefs.putString("SOUND", app_data.get("sound"));
    GnuBackgammon.Instance.optionPrefs.putString("SPEED", app_data.get("speed"));
    GnuBackgammon.Instance.optionPrefs.putString("AMOVES", app_data.get("amoves"));
    GnuBackgammon.Instance.optionPrefs.putString("LMOVES", app_data.get("lmoves"));
    GnuBackgammon.Instance.optionPrefs.putString("MDICES", app_data.get("mdices"));
    
    GnuBackgammon.Instance.appearancePrefs.putString("BOARD", app_data.get("board"));
    GnuBackgammon.Instance.appearancePrefs.putString("CHECKERS", app_data.get("checkers"));
    GnuBackgammon.Instance.appearancePrefs.putString("DIRECTION", app_data.get("direction"));
    GnuBackgammon.Instance.appearancePrefs.putString("NPOINTS", app_data.get("numberedp"));
    
    GnuBackgammon.Instance.optionPrefs.flush();
    GnuBackgammon.Instance.appearancePrefs.flush();
  }
}
