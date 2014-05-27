package it.alcacoop.backgammon.helpers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.PrivateDataManager;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class ADSHelpers {

  private Activity activity;

  private AdView adView;
  private InterstitialAd interstitial;
  private Timer adsTimer;
  private TimerTask adsTask;


  public ADSHelpers(Activity activity, boolean isTablet) {
    this.activity = activity;

    PrivateDataManager.initData();
    adView = new AdView(activity);
    adView.setAdUnitId(PrivateDataManager.ads_id);

    if (isTablet)
      adView.setAdSize(AdSize.FULL_BANNER);
    else
      adView.setAdSize(AdSize.BANNER);
    adView.setVisibility(View.GONE);

    if (!PrivateDataManager.msIsPremium)
      adView.loadAd(new AdRequest.Builder().build());

    interstitial = new InterstitialAd(activity);
    interstitial.setAdUnitId(PrivateDataManager.int_id);

    interstitial.setAdListener(new AdListener() {
      @Override
      public void onAdClosed() {
        GnuBackgammon.Instance.interstitialVisible = false;
        GnuBackgammon.Instance.currentScreen.resume();
        super.onAdClosed();
      }
    });
  }


  public void onPause() {
    if (adView != null)
      adView.pause();

    if (adsTimer != null) {
      adsTimer.cancel();
      adsTimer.purge();
    }
  }


  public void onResume() {
    if (adView != null)
      adView.resume();

    if (!PrivateDataManager.msIsPremium) {
      adsTimer = new Timer();
      adsTask = new TimerTask() {
        @Override
        public void run() {
          activity.runOnUiThread(new Runnable() {
            public void run() {
              if ((!PrivateDataManager.msIsPremium) && (!interstitial.isLoaded())) {
                interstitial.loadAd(new AdRequest.Builder().build());
              }
            }
          });
        }
      };
      adsTimer.schedule(adsTask, 0, 15000);
    }
  }


  public void onDestroy() {
    if (adView != null)
      adView.destroy();
  }


  public AdView getAdView() {
    return adView;
  }


  public void showAds(final boolean show) {
    if (PrivateDataManager.msIsPremium)
      return;
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (show) {
          adView.setVisibility(View.VISIBLE);
        } else {
          adView.setVisibility(View.GONE);
        }
      }
    });
  }


  public void showInterstitial() {
    if (PrivateDataManager.msIsPremium)
      return;
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (interstitial.isLoaded()) {
          GnuBackgammon.Instance.currentScreen.pause();
          GnuBackgammon.Instance.interstitialVisible = true;
          synchronized (this) {
            try {
              wait(700);
            } catch (InterruptedException e) {}
            interstitial.show();
          }
        }
      }
    });
  }


  public void disableAllAds() {
    if (PrivateDataManager.msIsPremium) {
      adView.setVisibility(View.GONE);
      if (adsTimer != null) {
        adsTimer.cancel();
        adsTimer.purge();
        PrivateDataManager.destroyBillingData(); // Memory Optimization!
      }
    }
  }
}
