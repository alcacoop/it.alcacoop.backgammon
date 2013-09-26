package it.alcacoop.backgammon;

import android.content.Context;
import android.util.Log;
import it.alcacoop.backgammon.util.IabHelper;
import it.alcacoop.backgammon.util.IabResult;
import it.alcacoop.backgammon.util.Inventory;
import it.alcacoop.backgammon.util.Purchase;

public class PrivateDataManager {

  public static String ads_id = "";
  public static String int_id = "";
  public static String base64EncodedPublicKey = "";
  public static String verifyCode = "";

  public static IabHelper mHelper = null;
  public static boolean msIsPremium = false;
  public static String TAG = "BILLING";
  public static final String SKU_NOADS = "";
  public static final String SKU_DONATE = "";
  public static int RC_REQUEST = 1000001;
  
  
  public static IabHelper getHelper() {
    return mHelper;
  }

  public static void initData() {}

  public static void createBillingData(Context ctx) {}

  public static void destroyBillingData() {
  }

  public static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {}
  };

  public static boolean verifyDeveloperPayload(Purchase p) {
    return true;
  }
}
