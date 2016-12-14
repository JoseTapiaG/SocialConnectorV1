package com.dimunoz.androidsocialconn.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.main.PauseActivity;
import com.dimunoz.androidsocialconn.photos.AlbumPhotoFragment;
import com.dimunoz.androidsocialconn.photos.NewPhotosFragment;
import com.dimunoz.androidsocialconn.tlatoque.TlatoqueFragment;

import java.util.Calendar;
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
public class TlatoquePauseHandler {

    private String TAG = "TlatoquePauseHandler";

    private final static int SECONDS_TO_SLEEP_AFTER_PAUSE = 240;

    private ScheduledExecutorService pauseTimer;
    private MainActivity activity;

    public TlatoquePauseHandler(MainActivity activity) {
        this.activity = activity;
        startPauseTimer();
    }

    /* Timer methods for handling pause after certain time */

    public void startPauseTimer() {
        whatToDoAfterPauseTime();
    }

    public void stopPauseTimer() {
        pauseTimer.shutdownNow();
    }

    public void restartPauseTimer() {
        Log.d(TAG, "restartPauseTimer");
        stopPauseTimer();
        startPauseTimer();
    }

    /* Pause time is when there are no events on the screen after a certain amount of time */

    public void whatToDoAfterPauseTime() {
        pauseTimer = Executors.newSingleThreadScheduledExecutor();
        PauseTimeTask myTask = new PauseTimeTask();
        pauseTimer.schedule(myTask.getRunnable(), SECONDS_TO_SLEEP_AFTER_PAUSE, TimeUnit.SECONDS);
    }

    /*****
     * BEGIN TASKS
     *****/

    /* Task that runs after pause time is accomplished */
    public class PauseTimeTask {

        private void logAlbumPhoto(Fragment fragment) {
            AlbumPhotoFragment photoFragment = (AlbumPhotoFragment) fragment;
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(activity.getApplicationContext());
            Long photo_begin_see = settings.getLong(
                    activity.getApplicationContext().getString(R.string.album_photo_begin_see),
                    -1);
            Calendar cal = Calendar.getInstance();
            Utils.logSeeAlbumPhotoEvent(photoFragment.currentPhoto.getId() + "", photo_begin_see, cal.getTimeInMillis());
        }

        private void logNewPhoto(Fragment fragment) {
            NewPhotosFragment photosFragment = (NewPhotosFragment) fragment;
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(activity.getApplicationContext());
            Long photo_begin_see = settings.getLong(
                    activity.getApplicationContext().getString(R.string.new_photo_begin_see),
                    -1);
            Calendar cal = Calendar.getInstance();
            //TODO cambiar fecha
            Utils.logSeeNewPhotoEvent(photosFragment.currentPhoto.getId() + "", 0L,
                    photo_begin_see, cal.getTimeInMillis());
        }

        private void logNewPhotosEmpty() {
            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(activity.getApplicationContext());
            Long photo_begin_see = settings.getLong(
                    activity.getApplicationContext().getString(R.string.new_photos_empty_begin_see),
                    -1);
            Calendar cal = Calendar.getInstance();
            Utils.logNewPhotosEmpty(photo_begin_see, cal.getTimeInMillis());
        }

        public Runnable getRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    Utils.checkIfNightTime();
                    if (!MainActivity.pauseHandlerActive || MainActivity.hasChangedNightMode) {
                        Log.d(TAG, "getRunnable");
                        if (MainActivity.nightMode) {
                            Intent intent = new Intent(activity.getApplicationContext(),
                                    PauseActivity.class);
                            activity.startActivity(intent);
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        MainActivity.pauseActivity.finishActivity();
                                    } catch (Exception e) {
                                        Log.d(TAG, "no PauseActivity");
                                    }
                                    MainActivity.pauseHandlerActive = true;
                                    FragmentManager fragmentManager = activity.getFragmentManager();
                                    Fragment currentFragment = fragmentManager.findFragmentByTag(
                                            MainActivity.FRAGMENT_TAG);
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    TlatoqueFragment newFragment = new TlatoqueFragment();
                                    transaction.replace(R.id.fragment_container, newFragment,
                                            MainActivity.FRAGMENT_TAG);
                                    transaction.commit();
                                    if (currentFragment instanceof NewPhotosFragment) {
                                        if (!MainActivity.newPhotosList.isEmpty())
                                            logNewPhoto(currentFragment);
                                        else
                                            logNewPhotosEmpty();
                                    } else if (currentFragment instanceof AlbumPhotoFragment) {
                                        logAlbumPhoto(currentFragment);
                                    }
                                }
                            });
                        }
                    }
                    MainActivity.tlatoquePauseHandler.restartPauseTimer();
                }
            };
        }
    }
}
