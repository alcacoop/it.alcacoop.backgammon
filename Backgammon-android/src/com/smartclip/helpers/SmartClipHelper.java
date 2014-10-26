package com.smartclip.helpers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.NativeFunctions;
import it.alcacoop.backgammon.PrivateDataManager;

import java.util.Arrays;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class SmartClipHelper {

  private Activity activity;
  private TelephonyManager tm;
  private final String[] activeCountries = { "it" };


  public SmartClipHelper(Activity activity) {
    this.activity = activity;
    if (((NativeFunctions)activity).isProVersion())
      return;
    FrequencyCapManager.initializeWithAppKey(PrivateDataManager.SMARTCLIP_APPKEY, activity);
    tm = (TelephonyManager)activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
  }


  public boolean hasClipAvailable() {
    if (((NativeFunctions)activity).isProVersion() || (!((NativeFunctions)activity).isNetworkUp()))
      return false;

    boolean hasClip = FrequencyCapManager.getInstance().canDisplayAdForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID) &&
        (Arrays.binarySearch(activeCountries, getUserCountry()) >= 0);

    int remainingClips = FrequencyCapManager.getInstance().getNumberOfDisplaysStillAvailableForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID);
    GnuBackgammon.out.println("smartclip can display: " + FrequencyCapManager.getInstance().canDisplayAdForItemWithId(PrivateDataManager.SMARTCLIP_ITEMID));
    GnuBackgammon.out.println("smartclip left: " + remainingClips);
    GnuBackgammon.out.println("smartclip left max: " + (remainingClips != Integer.MAX_VALUE) + " -> " + Integer.MAX_VALUE);
    GnuBackgammon.out.println("smartclip country: " + getUserCountry());
    GnuBackgammon.out.println("smartclip active country: " + (Arrays.binarySearch(activeCountries, getUserCountry()) >= 0));
    GnuBackgammon.out.println("smartclip hasclip: " + hasClip);

    return hasClip;
  }

  public void playClip() {
    if (((NativeFunctions)activity).isProVersion())
      return;

    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        GnuBackgammon.Instance.currentScreen.pause();
        GnuBackgammon.Instance.interstitialVisible = true;
        synchronized (this) {
          try {
            wait(700);
          } catch (InterruptedException e) {}
          Intent myIntent = new Intent(activity, SmartClipActivity.class);
          activity.startActivity(myIntent);
        }
      }
    });

  }

  private String getUserCountry() {
    try {
      final String simCountry = tm.getSimCountryIso();
      if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
        return simCountry.toLowerCase(Locale.US);
      }
      else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
        String networkCountry = tm.getNetworkCountryIso();
        if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
          return networkCountry.toLowerCase(Locale.US);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return activity.getResources().getConfiguration().locale.getCountry().toLowerCase(Locale.US);
  }


}
