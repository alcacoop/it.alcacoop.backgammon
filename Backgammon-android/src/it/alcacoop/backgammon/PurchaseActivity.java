package it.alcacoop.backgammon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;



public class PurchaseActivity extends Activity {

  public static String ads_id = "";
  public static String int_id = "";
  public static String base64EncodedPublicKey = "";
  public static String verifyCode = "";
  public static boolean msIsPremium = false;
  
  public static final String SKU_PREMIUM = "";
  public static int RC_REQUEST = 0;
  
  
  
  public static void createBillingData(Context ctx) {}
  
  
  public static void destroyBillingData() {}
  
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
  
}
