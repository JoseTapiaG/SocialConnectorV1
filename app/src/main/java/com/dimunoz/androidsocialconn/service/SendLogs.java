package com.dimunoz.androidsocialconn.service;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.asynctasks.SendLogEmail;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.utils.Utils;

import java.io.File;
import java.util.Calendar;

/**
 * Created by dmunoz on 01-10-15.
 *
 */
public class SendLogs {

    private final String TAG = "SendLogs";
    private MainActivity activity;
    private SharedPreferences settings;

    public SendLogs(MainActivity activity) {
        Log.d(TAG, "Init service");
        this.activity = activity;
        settings = PreferenceManager
                .getDefaultSharedPreferences(this.activity);
    }

    public Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "run");
                sendServicesLog();
                sendUseLog();
            }
        };
    }

    private void sendServicesLog() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        //if (cal.get(Calendar.HOUR_OF_DAY) == 3) {
            Long lastSentServicesLog = settings.getLong(
                    activity.getString(R.string.last_sent_services_log), -1);
            if (lastSentServicesLog == -1) {
                File externalStorageDir = Environment.getExternalStorageDirectory();
                File logFile = new File(externalStorageDir, Utils.getLogServiceFilename(cal));
                if (logFile.exists()) {
                    new SendLogEmail(activity, logFile).execute();
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong(activity.getString(R.string.last_sent_services_log), cal.getTimeInMillis());
                    editor.apply();
                }
            } else {
                cal.setTimeInMillis(lastSentServicesLog);
                cal.add(Calendar.DATE, 1);
                Calendar today = Calendar.getInstance();
                while (!(cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                        && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))) {
                    File externalStorageDir = Environment.getExternalStorageDirectory();
                    File logFile = new File(externalStorageDir, Utils.getLogServiceFilename(cal));
                    if (logFile.exists()) {
                        new SendLogEmail(activity, logFile).execute();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putLong(activity.getString(R.string.last_sent_services_log), cal.getTimeInMillis());
                        editor.apply();
                    }
                    cal.add(Calendar.DATE, 1);
                }
            }
        //}
    }

    private void sendUseLog() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        //if (cal.get(Calendar.HOUR_OF_DAY) == 2) {
            Long lastSentUseLog = settings.getLong(
                    activity.getString(R.string.last_sent_use_log), -1);
            if (lastSentUseLog == -1) {
                File externalStorageDir = Environment.getExternalStorageDirectory();
                File logFile = new File(externalStorageDir, Utils.getLogUseFilename(cal));
                if (logFile.exists()) {
                    new SendLogEmail(activity, logFile).execute();
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putLong(activity.getString(R.string.last_sent_use_log), cal.getTimeInMillis());
                    editor.apply();
                }
            } else {
                cal.setTimeInMillis(lastSentUseLog);
                cal.add(Calendar.DATE, 1);
                Calendar today = Calendar.getInstance();
                while (!(cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                        && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH))) {
                    File externalStorageDir = Environment.getExternalStorageDirectory();
                    File logFile = new File(externalStorageDir, Utils.getLogUseFilename(cal));
                    if (logFile.exists()) {
                        new SendLogEmail(activity, logFile).execute();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putLong(activity.getString(R.string.last_sent_use_log), cal.getTimeInMillis());
                        editor.apply();
                    }
                    cal.add(Calendar.DATE, 1);
                }
            }
        //}
    }
}
