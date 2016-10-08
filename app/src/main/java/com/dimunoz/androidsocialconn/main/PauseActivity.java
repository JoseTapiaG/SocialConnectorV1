package com.dimunoz.androidsocialconn.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.dimunoz.androidsocialconn.utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 09-01-14
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class PauseActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        MainActivity.pauseHandlerActive = true;
        super.onCreate(savedInstanceState);

        // keep screen on
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (MainActivity.nightMode)
            Utils.changeScreenBrightness(0.1F,this);
        MainActivity.pauseActivity = this;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        MainActivity.pauseHandlerActive = false;
        finish();
        return true;
    }

    public void finishActivity() {
        MainActivity.pauseHandlerActive = false;
        Utils.changeScreenBrightness(1F,this);
        finish();
    }
}