package it.alcacoop.backgammon;

import android.content.Context;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

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
  public static final int INAPP_BILLING_REQUEST = 1000001;

  public static final String SMARTCLIP_URL = "";
  public static final String SMARTCLIP_APPKEY = "";
  public static final String SMARTCLIP_ITEMID = "";

  public static Preferences billingPrefs;


  public static IabHelper getHelper() {
    return mHelper;
  }

  public static void initData() {
    billingPrefs = Gdx.app.getPreferences("billingPrefs");
  }

  public static void createBillingData(Context ctx) {
    initData();
  }

  public static void destroyBillingData() {}

  public static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {}
  };

  public static boolean verifyDeveloperPayload(Purchase p) {
    return true;
  }
}
