package me.dylam.flashlight;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    private static Camera mCam;
    private static String mTag = "MainActivity";
    private static int mNotifyId = 0;
    private NotificationReceiver mReceiver;
    private static String notifyOn = "me.dylam.flashlight.ON";
    private static String notifyOff= "me.dylam.flashlight.OFF";
    private static String notifyExit= "me.dylam.flashlight.EXIT";
    private NotificationManager mNotificationManager;
    private ToggleButton mToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "No light. :(",Toast.LENGTH_LONG).show();
            finish();
        }

        mToggleButton = (ToggleButton)findViewById(R.id.toggleButton);

        toggleOn();

        // Create receiver and register it
        mReceiver = new NotificationReceiver();
        IntentFilter i = new IntentFilter();
        i.addAction(notifyOn);
        i.addAction(notifyOff);
        i.addAction(notifyExit);
        registerReceiver(mReceiver, i);

        // Create Notification and display it
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Flashlight")
                .setContentText("todo")
                .setOngoing(true);

        Intent onReceive = new Intent(notifyOn);
        PendingIntent pendingIntentOn = PendingIntent.getBroadcast(this, 12345, onReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_launcher, "On", pendingIntentOn);

        Intent offReceive = new Intent(notifyOff);
        PendingIntent pendingIntentOff = PendingIntent.getBroadcast(this, 12345, offReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_launcher, "Off", pendingIntentOff);

        Intent exitReceive = new Intent(notifyExit);
        PendingIntent pendingIntentExit = PendingIntent.getBroadcast(this, 12345, exitReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_launcher, "Quit", pendingIntentExit);

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotifyId, mBuilder.build());
    }

    @Override
    protected void onDestroy() {
        toggleOff();

        // Clean up
        unregisterReceiver(mReceiver);
        mNotificationManager.cancelAll();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

     public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(mTag, "Received notification with action:" + action);

            if (action.equals(notifyOn)) {
                toggleOn();
            } else if (action.equals(notifyOff)) {
                toggleOff();
            } else if (action.equals(notifyExit)) {
                finish();
            } else {
                Log.d(mTag, "NotificationReceiver received invalid action");
            }
        }
    }

    public void toggleLightBtn(View v) {
        // Get text
        // Toast.makeText(this, ((TextView)v).getText().toString(),Toast.LENGTH_SHORT).show();

        // React appropiately
        if (((TextView)v).getText().toString().equals("ON")) {
            toggleOn();
        } else {
            toggleOff();
        }
    }

    public void toggleOn() {
        //Toast.makeText(this, "toggle on!", Toast.LENGTH_SHORT).show();
        if (mCam != null) {
            Log.d(mTag, "Camera already on!");
            return;
        }

        try {
            mToggleButton.setChecked(true);
            mCam = Camera.open();
            Camera.Parameters p = mCam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCam.setParameters(p);
            mCam.startPreview();
        } catch (Exception e) {
            Log.e(mTag, "Exception in toggling cam light on.");
            e.printStackTrace();
        }
    }

    public void toggleOff() {
        //Toast.makeText(this, "toggle off!", Toast.LENGTH_SHORT).show();
        if (mCam != null) {
            mToggleButton.setChecked(false);
            mCam.stopPreview();
            mCam.release();
            mCam = null;
        }
    }
}