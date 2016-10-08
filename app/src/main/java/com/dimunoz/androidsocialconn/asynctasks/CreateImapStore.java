package com.dimunoz.androidsocialconn.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.google.code.oauth2.OAuth2Authenticator;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 30-06-14
 * Time: 03:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateImapStore extends AsyncTask<Void, Void, Void> {

    private String TAG = "CreateImapStore";
    private MainActivity activity;
    private String email;
    private String token;

    public CreateImapStore(MainActivity activity, String email, String token) {
        this.activity = activity;
        this.email = email;
        this.token = token;
    }

    @Override
    protected void onPostExecute(Void unused) {
        Log.d(TAG, "ImapStore creado");
        new GetEmailsFromGmail(activity).execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");
        try {
            MainActivity.imapStore = OAuth2Authenticator.connectToImap(
                    "imap.gmail.com",
                    993,
                    email,
                    token,
                    true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}