package it.alcacoop.backgammon.helpers;

import it.alcacoop.backgammon.GnuBackgammon;
import it.alcacoop.backgammon.layers.FibsScreen;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerHelpers implements SensorEventListener {

  private final float NOISE = 0.5f;
  private boolean mSensorInitialized;
  private SensorManager mSensorManager;
  private Sensor mAccelerometer;
  private int rotation;

  public AccelerometerHelpers(Activity activity) {
    mSensorInitialized = false;
    mSensorManager = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);
    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
  }


  public void onResume() {
    mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
  }


  public void onPause() {
    mSensorManager.unregisterListener(this);
    if ((GnuBackgammon.Instance != null) && (GnuBackgammon.Instance.currentScreen instanceof FibsScreen) && (!GnuBackgammon.Instance.interstitialVisible)) {
      GnuBackgammon.Instance.commandDispatcher.send("BYE");
      GnuBackgammon.Instance.fibsScreen.fibsInvitations.clear();
      GnuBackgammon.Instance.fibsScreen.fibsPlayers.clear();
      GnuBackgammon.Instance.setFSM("MENU_FSM");
    }
  }


  @Override
  public void onAccuracyChanged(Sensor arg0, int arg1) {}


  @Override
  public void onSensorChanged(SensorEvent event) {
    float x = event.values[1];
    if (rotation == 3)
      x = -x;
    if (!mSensorInitialized) {
      mSensorInitialized = true;
    } else {
      if (Math.abs(x) < NOISE)
        return;
      if (GnuBackgammon.Instance != null)
        if (GnuBackgammon.Instance.currentScreen != null)
          GnuBackgammon.Instance.currentScreen.moveBG(x);
    }
  }

}
