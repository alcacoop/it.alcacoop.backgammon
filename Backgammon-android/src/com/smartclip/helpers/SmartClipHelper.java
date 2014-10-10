package com.smartclip.helpers;

import it.alcacoop.backgammon.NativeFunctions;
import it.alcacoop.backgammon.PrivateDataManager;
import android.app.Activity;
import android.content.Intent;

public class SmartClipHelper {

  private Activity activity;


  public SmartClipHelper(Activity activity) {
    this.activity = activity;
    if (((NativeFunctions)activity).isProVersion())
      return;
    FrequencyCapManager.initializeWithAppKey(PrivateDataManager.SMARTCLIP_APPKEY, activity);
  }


  public boolean hasClipAvailable() {
    if (((NativeFunctions)activity).isProVersion())
      return false;
    return FrequencyCapManager.getInstance().canDisplayAdForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID);
  }

  public void playClip() {
    if (((NativeFunctions)activity).isProVersion())
      return;
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Intent myIntent = new Intent(activity, SmartClipActivity.class);
        activity.startActivity(myIntent);
      }
    });
  }

}
