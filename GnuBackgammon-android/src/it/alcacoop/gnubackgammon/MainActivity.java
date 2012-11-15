package it.alcacoop.gnubackgammon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import it.alcacoop.gnubackgammon.logic.GnubgAPI;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
    cfg.useGL20 = false;
    
    copyAssetsIfNotExists();
    
    initialize(new GnuBackgammon(), cfg);
    GnubgAPI.InitializeEnvironment(getString(R.string.asset_path));
    //TODO: spostare gli asset in data-lib
  }
  
  
  //Load library
  static {
    System.loadLibrary("glib-2.0");
    System.loadLibrary("gthread-2.0");
    System.loadLibrary("gnubg");
  }   

  private void copyAssetsIfNotExists() {
    File a1 = new File(getString(R.string.asset_path)+"g11.xml");
    File a2 = new File(getString(R.string.asset_path)+"gnubg_os0.bd");
    File a3 = new File(getString(R.string.asset_path)+"gnubg_ts0.bd");
    File a4 = new File(getString(R.string.asset_path)+"gnubg.weights");
    File a5 = new File(getString(R.string.asset_path)+"gnubg.wd");
    
    //Asset already presents
    if (a1.exists()&&a2.exists()&&a3.exists()&&a4.exists()&&a5.exists()) return;
    
    File assetDir = new File(getString(R.string.asset_path));
    assetDir.mkdirs();
    
    AssetManager assetManager = getAssets();
    String[] files = null;
    try {
      files = assetManager.list("gnubg");
    } catch (IOException e) {
      Log.e("tag", "Failed to get asset file list.", e);
    }
    for(String filename : files) {
      InputStream in = null;
      OutputStream out = null;
      try {
        Log.e("MINE", filename);
        in = assetManager.open("gnubg/"+filename);
        out = new FileOutputStream(getString(R.string.asset_path) + filename);
        copyFile(in, out);
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
      } catch(IOException e) {
        Log.e("tag", "Failed to copy asset file: " + filename, e);
      }       
    }
  }
  
  private void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
      out.write(buffer, 0, read);
    }
  }
  
}
