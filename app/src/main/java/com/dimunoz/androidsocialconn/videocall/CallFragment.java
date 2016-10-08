package com.dimunoz.androidsocialconn.videocall;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.basefragments.BaseContactListFragment;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.persistance.SocialconnApplication;

import java.util.ArrayList;

public class CallFragment extends BaseContactListFragment {

    private static final String TAG = "CallFragment";
    private ArrayList<String> androidSkypeContacts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.Call));
        TextView topMessage = (TextView) contentLayout.findViewById(R.id.contact_list_top_message);
        topMessage.setText(getResources().getText(R.string.top_message_call));

        // Get Skype contacts synced with Android device
        getAndroidSkypeContacts();

        // Get a list only with common contacts between both previous lists
        filterContacts();

        // Display contacts from that common list
        initialItemIndex = 0;
        displayContacts(initialItemIndex);

        return contentLayout;
    }

    protected void filterContacts() {
        filteredContacts.clear();
        for (XmlContact contact : MainActivity.xmlContacts) {
            if (androidSkypeContacts.contains(contact.getSkype())) {
                filteredContacts.add(contact);
            }
        }
    }

    private void getAndroidSkypeContacts() {
        androidSkypeContacts.clear();
        ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
        String[] projection =  { ContactsContract.RawContacts.SOURCE_ID };
        Cursor skype = cr.query(ContactsContract.Data.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Im.MIMETYPE
                        + " = 'vnd.android.cursor.item/com.skype.android.videocall.action'",
                null, null);

        while (skype.moveToNext()) {
            String result = skype.getString(0);
            if (result.compareTo("echo123") != 0) {
                androidSkypeContacts.add(skype.getString(0));
            }
        }
        skype.close();
    }

    public void handleContactTapEvent(View view) {
        long userId = (long) view.getTag();
        String skype = "";
        for (XmlContact contact: MainActivity.xmlContacts) {
            if (contact.getId() == userId) {
                MainActivity.calledContact = contact;
                skype = contact.getSkype();
                break;
            }
        }

        String uri = "skype:" + skype + "?call&video=true";
        Uri skypeUri = Uri.parse(uri);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);

        myIntent.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));

        SocialconnApplication app = (SocialconnApplication) getActivity().getApplicationContext();

        if(app.getFacebookUsername() != null) {
            app.setTalkingBySkype(true);
            app.stopRecordingService();
            Log.d("skype","Skype call started");
            Log.d("skype","Facebook username is "+app.getFacebookUsername());
        }

        startActivity(myIntent);
    }
}
