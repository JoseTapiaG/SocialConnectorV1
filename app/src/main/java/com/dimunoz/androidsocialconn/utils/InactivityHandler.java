package com.dimunoz.androidsocialconn.utils;

import android.app.Activity;
import android.content.Intent;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.main.PauseActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 05-07-13
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class InactivityHandler {

    private String TAG = "InactivityHandler";

    private final static int SECONDS_TO_SLEEP_AFTER_INACTIVITY = 120;

    private ScheduledExecutorService inactivityTimer;
    private Activity activity;

    public InactivityHandler(Activity activity) {
        this.activity = activity;
        startInactivityTimer();
    }

    /* Timer methods for handling inactivity after certain time */

    public void startInactivityTimer() {
        whatToDoAfterInactivityTime();
    }

    public void stopInactivityTimer() {
        inactivityTimer.shutdownNow();
    }

    public void restartInactivityTimer() {
        stopInactivityTimer();
        startInactivityTimer();
    }

    /* Inactivity time is when there are no events on the screen after a certain amount of time */

    public void whatToDoAfterInactivityTime() {
        inactivityTimer = Executors.newSingleThreadScheduledExecutor();
        InactivityTimeTask myTask = new InactivityTimeTask();
        inactivityTimer.schedule(myTask.getRunnable(), SECONDS_TO_SLEEP_AFTER_INACTIVITY, TimeUnit.SECONDS);
    }

    /***** BEGIN TASKS *****/

    /* Task that runs after inactivity time is accomplished */
    public class InactivityTimeTask {

        public Runnable getRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            };
        }
    }
}
