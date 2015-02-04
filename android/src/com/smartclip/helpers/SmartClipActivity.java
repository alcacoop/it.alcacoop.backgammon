package com.smartclip.helpers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.PrivateDataManager;
import it.alcacoop.backgammon.helpers.ADSHelpers;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.ois.android.OIS;
import com.ois.android.controller.OISinstreamController;


public class SmartClipActivity extends Activity {

  private OISinstreamController instreamSDK;
  private int displayWidth;
  private int displayHeight;
  private RelativeLayout rLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setScreenDimensions();
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    rLayout = new RelativeLayout(this);
    initSDK();
    setContentView(rLayout);
  }

  @Override
  public void onBackPressed() {}

  private void initSDK() {
    instreamSDK = new OIS(this).instreamController(rLayout);
    instreamSDK.config().addPreroll(PrivateDataManager.SMARTCLIP_URL);
    instreamSDK.config().setCountdownEnabled(true);
    instreamSDK.config().setCountdownText("Remaining time: [remaining]");
    instreamSDK.config().setCountdownBarPosition("bottom");
    int delay = FrequencyCapManager.getInstance().getSkipAfterSecsValueForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID);
    instreamSDK.config().setLinearCloseButtonDelay(delay);

    instreamSDK.setResponseListener(new OISinstreamController.ResponseListener() {

      public void didFailLoad() {
        GnuBackgammon.out.println("smartclip failed");
        ADSHelpers.getInstance().showImmediateInterstitial();
        finish();
      }

      public boolean handleClickThru(String url) {
        return false;
      }

      public void proceedStart() {
        FrequencyCapManager.getInstance().setDisplayConsumedForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID);
        GnuBackgammon.Instance.interstitialVisible = false;
        finish();
      }

      public int[] updateDimensions(int arg0, int arg1) {
        return new int[] { displayWidth, displayHeight };
      }

      public void updateProgress(int duration, int progress, int arg2) {}
      public void hideControls() {}
      public void linearPreparedToPlay() {}
      public void midrollIsActive(boolean active) {}
      public void pauseContent(boolean pause) {}
      public void proceedEnd() {}
      public void showControls() {}
      public void willShowAd() {}
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    instreamSDK.onBeforeContent();
  }


  private void setScreenDimensions() {
    if (Build.VERSION.SDK_INT >= 19) {
      Point size = new Point();
      try {
        getWindowManager().getDefaultDisplay().getRealSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
      } catch (NoSuchMethodError e) {}

    } else {
      DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
      displayWidth = metrics.widthPixels;
      displayHeight = metrics.heightPixels;
    }
  }

}
