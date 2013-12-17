package me.dylam.flashlight;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static Camera mCam;
    private static String mTag = "MainActivity";
    private static int mNotifyId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "No light. :(",Toast.LENGTH_SHORT).show();
            return;
        }

        toggleOn();

        // Create Notification and display it
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Flashlight")
                .setContentText("todo");

        Intent onReceive = new Intent(this, NotificationReceiver.class);
        onReceive.setAction("ON");
        PendingIntent pendingIntentOn = PendingIntent.getBroadcast(this,12345, onReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_launcher, "On", pendingIntentOn);

        Intent offReceive = new Intent(this, NotificationReceiver.class);
        offReceive.setAction("OFF");
        PendingIntent pendingIntentOff = PendingIntent.getBroadcast(this,12345, offReceive, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(R.drawable.ic_launcher, "Off", pendingIntentOff);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotifyId, mBuilder.build());
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
            Log.d(mTag, "Received notification with action:" + action );


            if (action.equals("ON")) {
                toggleOn();
            } else if (action.equals("OFF")) {
                toggleOff();
            } else {
                Log.d(mTag, "What happened here?");
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
        Toast.makeText(this, "toggle on!", Toast.LENGTH_SHORT).show();
        try {
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
        Toast.makeText(this, "toggle off!", Toast.LENGTH_SHORT).show();
        if (mCam) {
            mCam.stopPreview();
            mCam.release();
        }
    }
}
