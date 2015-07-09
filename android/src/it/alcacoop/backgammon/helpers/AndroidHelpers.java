package it.alcacoop.backgammon.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AndroidHelpers {
  private String data_dir;
  private Context context;
  private Activity activity;
  private DisplayMetrics metrics;

  public AndroidHelpers(Activity activity) {
    this.activity = activity;
    context = activity.getBaseContext();
    data_dir = context.getApplicationInfo().dataDir + "/gnubg/";
    metrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
  }


  public int getAppVersionCode() {
    PackageInfo pInfo;
    try {
      pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return pInfo.versionCode;
    } catch (NameNotFoundException e) {
      return -1;
    }
  }

  public String getDataDir() {
    return data_dir;
  }

  private void _copyAsset(String filename) {
    InputStream in = null;
    OutputStream out = null;
    AssetManager assetManager = context.getAssets();
    try {
      in = assetManager.open("gnubg/" + filename);
      out = new FileOutputStream(data_dir + filename);
      copyFile(in, out);
      in.close();
      in = null;
      out.flush();
      out.close();
      out = null;
    } catch (IOException e) {}
  }

  public void copyAssetsIfNotExists() {
    String[] files = { "gnubg.weights", "gnubg.wd", "g11.xml", "gnubg_os0.bd", "gnubg_ts0.bd" };

    File assetDir = new File(data_dir);
    if (!assetDir.exists())
      assetDir.mkdirs();

    _copyAsset(files[0]);
    _copyAsset(files[1]);
    for (int i = 2; i < files.length; i++) {
      File f = new File(data_dir + files[i]);
      if (!f.exists())
        _copyAsset(files[i]);
    }
  }


  private void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
  }


  public boolean isNetworkUp() {
    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }


  public boolean isTablet() {
    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
    return (xlarge || large);
  }


  @SuppressLint("Assert")
  public void openURL(String... urls) {
    assert urls.length <= 1;
    try { // Native APP URL
      Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[0]));
      activity.startActivityForResult(myIntent, 1000);
    } catch (Exception e) { // Fallback URL
      if (urls.length > 0) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls[1]));
        activity.startActivityForResult(myIntent, 1000);
      }
    }
  }


  public int getScreenWidth() {
    return metrics.widthPixels;
  }


  public int getScreenHeight() {
    return metrics.heightPixels;
  }


  private String now() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.getDefault());
    return dateFormat.format(new Date());
  }


  public void sendFile(byte[] data) {
    String now = now();
    final Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.setType("message/rfc822");

    intent.putExtra(Intent.EXTRA_SUBJECT, "Backgammon Mobile exported Match (Played on " + now + ")");
    intent.putExtra(Intent.EXTRA_TEXT,
        "You can analize attached file with desktop version of GNU Backgammon\nNOTE:"
            + " GNU Backgammon is available for Windows, MacOS and Linux\n\nIf you enjoyed "
            + "Backgammon Mobile please help us rating it on the market");

    try {
      File dir = new File(Environment.getExternalStorageDirectory(), "gnubg-sgf");
      dir.mkdir();
      final File file = new File(dir, "match-" + now + ".sgf");

      FileOutputStream out = new FileOutputStream(file);
      out.write(data);
      out.flush();
      out.close();

      Uri uri = Uri.fromFile(file);
      intent.putExtra(Intent.EXTRA_STREAM, uri);

      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          activity.startActivityForResult(Intent.createChooser(intent, "Send email..."), 1001);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void saveBitmap(final Bitmap bmp, final String filename) {
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        // PREPARE CANVAS
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 0, 0));
        paint.setStrokeWidth(4);

        Bitmap b = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        Rect dest = new Rect(2, 2, 253, 253);
        c.drawRect(0, 0, 255, 255, paint);
        c.drawBitmap(bmp, src, dest, null);

        FileOutputStream out = null;
        String fname = data_dir + filename + ".png";
        File old = new File(fname);
        old.delete();
        try {
          out = new FileOutputStream(fname);
          b.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          try {
            if (out != null) {
              out.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

}
