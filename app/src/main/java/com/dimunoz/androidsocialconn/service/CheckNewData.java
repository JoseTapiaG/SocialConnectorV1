package com.dimunoz.androidsocialconn.service;

import android.util.Log;

import com.dimunoz.androidsocialconn.asynctasks.GetEmailsFromGmail;
import com.dimunoz.androidsocialconn.main.MainActivity;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 10-04-14
 * Time: 12:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckNewData {

    private final String TAG = "CheckNewData";
    private MainActivity activity;

    public CheckNewData(MainActivity activity) {
        Log.d(TAG, "Init service");
        this.activity = activity;
    }

    public Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "run");
                // TODO: 26-09-2016 Recuperar fotos automaticamente
                if (!MainActivity.checkingNewEmails)
                    new GetEmailsFromGmail(activity).execute();
            }
        };
    }
}
