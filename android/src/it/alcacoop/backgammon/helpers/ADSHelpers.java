package it.alcacoop.backgammon.helpers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.NativeFunctions;
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

  private static ADSHelpers Instance;

  public ADSHelpers(Activity activity, boolean isTablet) {
    this.activity = activity;
    if (((NativeFunctions)activity).isProVersion())
      return;

    adView = new AdView(activity);
    adView.setAdUnitId(PrivateDataManager.ads_id);

    if (isTablet)
      adView.setAdSize(AdSize.FULL_BANNER);
    else
      adView.setAdSize(AdSize.BANNER);
    adView.setVisibility(View.GONE);


    if (!((NativeFunctions)activity).isProVersion())
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

    Instance = this;
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

    if (!((NativeFunctions)activity).isProVersion()) {
      adsTimer = new Timer();
      adsTask = new TimerTask() {
        @Override
        public void run() {
          activity.runOnUiThread(new Runnable() {
            public void run() {
              if ((!((NativeFunctions)activity).isProVersion()) && (!interstitial.isLoaded())) {
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


  public View getAdView() {
    return adView;
  }


  public void showAds(final boolean show) {
    if (((NativeFunctions)activity).isProVersion())
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


  public void showImmediateInterstitial() {
    if (((NativeFunctions)activity).isProVersion())
      return;
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (interstitial.isLoaded()) {
          GnuBackgammon.Instance.currentScreen.pause();
          GnuBackgammon.Instance.interstitialVisible = true;
          interstitial.show();
        }
      }
    });
  }


  public void showInterstitial() {
    if (((NativeFunctions)activity).isProVersion())
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
    if (((NativeFunctions)activity).isProVersion()) {
      adView.setVisibility(View.GONE);
      if (adsTimer != null) {
        adsTimer.cancel();
        adsTimer.purge();
        PrivateDataManager.destroyBillingData(); // Memory Optimization!
      }
    }
  }

  public static ADSHelpers getInstance() {
    return Instance;
  }
}
