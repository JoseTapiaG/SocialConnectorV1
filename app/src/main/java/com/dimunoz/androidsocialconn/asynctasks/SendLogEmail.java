package com.dimunoz.androidsocialconn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.mail.OAuth2Authenticator;
import com.dimunoz.androidsocialconn.main.MainActivity;

import java.io.File;

/**
 * Created by dmunoz on 01-10-15.
 *
 */
public class SendLogEmail extends AsyncTask<Void, Void, Void> {

    private final String TAG = "SendLogEmail";
    private MainActivity activity;
    private File file;

    public SendLogEmail(MainActivity activity, File file) {
        this.activity = activity;
        this.file = file;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");
        try {
            OAuth2Authenticator sender = new OAuth2Authenticator();
            try {
                sender.sendMail("[Social Connector] Logfile " + file.getName(),
                        "Logfile", MainActivity.emailAccount, MainActivity.oauthToken,
                        activity.getString(R.string.logs_email), file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.d(TAG, "onPostExecute");
        if (this.file.delete()) {
            Log.d(TAG, "Log deleted");
        }
    }
}
