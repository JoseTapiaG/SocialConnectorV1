package com.dimunoz.androidsocialconn.photos;

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
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.service.PhotoService;
import com.dimunoz.androidsocialconn.xml.XmlContact;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlbumContactsFragment extends BaseContactListFragment {

    private static final String TAG = "AlbumContactsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.Album));
        TextView topMessage = (TextView) contentLayout.findViewById(R.id.contact_list_top_message);
        topMessage.setText(getResources().getText(R.string.top_message_album));

        // filter instagram contacts
        filterContacts();

        // Display contacts from filtered list
        initialItemIndex = 0;
        displayContacts(initialItemIndex);

        return contentLayout;
    }

    @Override
    protected void filterContacts() {
        filteredContacts.clear();
        for (XmlContact contact: MainActivity.xmlContacts) {
                filteredContacts.add(contact);
        }
    }

    public void handleContactTapEvent(View view) {
        long userId = (long) view.getTag();
        MainActivity.progressDialog.setMessage("Cargando fotos...");
        MainActivity.progressDialog.show();
        for (XmlContact contact: MainActivity.xmlContacts) {
            if (contact.getId() == userId) {
                // TODO: 26-09-2016 Recuperar fotos con fotos service
                MainActivity.currentAlbumUser = contact;
                MainActivity.albumPhotosList = (ArrayList<PhotoEntity>) MainActivity.photoService.getPhotos(contact.getEmail());
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AlbumPhotoFragment albumPhotoFragment = new AlbumPhotoFragment();
                transaction.replace(R.id.fragment_container, albumPhotoFragment, MainActivity.FRAGMENT_TAG);
                transaction.commit();
                MainActivity.progressDialog.dismiss();
                break;
            }
        }
    }
}
