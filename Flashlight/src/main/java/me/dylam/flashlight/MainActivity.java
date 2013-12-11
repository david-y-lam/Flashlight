package me.dylam.flashlight;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "No light. :(",Toast.LENGTH_SHORT).show();
            return;
        }

        toggleOn();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        mCam.stopPreview();
        mCam.release();
    }
}
