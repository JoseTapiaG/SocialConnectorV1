package com.dimunoz.androidsocialconn.asynctasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.utils.Utils;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 08-01-14
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkEmailAsRead extends AsyncTask<Void, Void, Void> {

    private String TAG = "MarkEmailAsRead";
    private Activity activity;
    private PersonalMessage personalMessage;

    public MarkEmailAsRead(Activity activity, PersonalMessage personalMessage) {
        Log.d(TAG, "constructor");
        this.activity = activity;
        this.personalMessage = personalMessage;
    }

    @Override
    protected void onPostExecute(Void unused) {
        Utils.changeBadgeNewMessagesText(activity);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground");
        try {
            Folder received = MainActivity.mailService.getImapStore().getFolder("INBOX");
            received.open(Folder.READ_WRITE);

            SearchTerm term = new ReceivedDateTerm(ComparisonTerm.EQ, personalMessage.getDatetime());

            MimeMessage[] messages = (MimeMessage[]) received.search(term);
            for (MimeMessage m : messages) {
                if (m.getSentDate().equals(personalMessage.getDatetime())) {
                    received.setFlags(new Message[] {m}, new Flags(Flags.Flag.SEEN), true);
                }
            }

            received.close(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}