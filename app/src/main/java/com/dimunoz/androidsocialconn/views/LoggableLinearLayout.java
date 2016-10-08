package com.dimunoz.androidsocialconn.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.utils.Utils;

/**
 * Created by dmunoz on 22-09-15.
 *
 */
public class LoggableLinearLayout extends LinearLayout {

    private final String TAG = "LoggableLinearLayout";

    public LoggableLinearLayout(Context context) {
        super(context);
    }

    public LoggableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoggableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Utils.logUseEvent(event, (Activity) this.getContext());
        MainActivity.tlatoquePauseHandler.restartPauseTimer();
        MainActivity.pauseHandlerActive = false;
        return false;
    }
}
