package com.dimunoz.androidsocialconn.sendmessage;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.basefragments.BaseContactListFragment;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.xml.XmlContact;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 */
public class SendMessageFragment extends BaseContactListFragment {

    private static final String TAG = "SendMessageFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.SendMessage));
        TextView topMessage = (TextView) contentLayout.findViewById(R.id.contact_list_top_message);
        topMessage.setText(getResources().getText(R.string.top_message_send_message));

        // Get a list only with common contacts between both previous lists
        filterContacts();

        // Display contacts from filtered list
        initialItemIndex = 0;
        displayContacts(initialItemIndex);

        return contentLayout;
    }

    protected void filterContacts() {
        filteredContacts.clear();
        for (XmlContact contact : MainActivity.xmlContacts) {
            if (contact.getEmail().compareTo("") != 0) {
                filteredContacts.add(contact);
            }
        }
    }

    public void handleContactTapEvent(View view) {
        CreateMessageFragment fragment = new CreateMessageFragment();
        // add xml contact to arguments
        XmlContact contact = new XmlContact();
        long userId = (long) view.getTag();
        for (XmlContact c: MainActivity.xmlContacts) {
            if (c.getId() == userId) {
                contact = c;
                break;
            }
        }
        fragment.contact = contact;
        // display fragment
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, MainActivity.FRAGMENT_TAG);
        transaction.commit();
    }
}
