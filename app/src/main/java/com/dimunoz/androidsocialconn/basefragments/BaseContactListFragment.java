package com.dimunoz.androidsocialconn.basefragments;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.gesture.ContactListGestureDetector;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 16/02/15
 * Time: 18:37
 */
public class BaseContactListFragment extends Fragment {

    private final String TAG = "BaseContactListFragment";

    protected ArrayList<XmlContact> filteredContacts = new ArrayList<>();

    protected static PercentRelativeLayout contentLayout;

    public ArrayList<View[]> fragmentItems = new ArrayList<>();
    public ArrayList<ImageView> fragmentArrows = new ArrayList<>();

    protected int initialItemIndex;

    protected GestureDetector gestureDetector;
    protected View.OnTouchListener gestureListener;
    public View touchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        contentLayout = (PercentRelativeLayout) inflater.inflate(R.layout.contact_list, container, false);

        // fill list of items
        fragmentItems.clear();
        fragmentItems.add(new View[]{
                (SmartImageView) contentLayout.findViewById(R.id.contact_1_image),
                (TextView) contentLayout.findViewById(R.id.contact_1_text)
        });
        fragmentItems.add(new View[]{
                (SmartImageView) contentLayout.findViewById(R.id.contact_2_image),
                (TextView) contentLayout.findViewById(R.id.contact_2_text)
        });
        fragmentItems.add(new View[]{
                (SmartImageView) contentLayout.findViewById(R.id.contact_3_image),
                (TextView) contentLayout.findViewById(R.id.contact_3_text)
        });

        // fill list of arrows
        fragmentArrows.clear();
        fragmentArrows.add((ImageView) contentLayout.findViewById(R.id.contact_list_left_arrow_image));
        fragmentArrows.add((ImageView) contentLayout.findViewById(R.id.contact_list_right_arrow_image));

        // Gesture detection for every view
        gestureDetector = new GestureDetector(getActivity(), new ContactListGestureDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                touchView = v;
                return gestureDetector.onTouchEvent(event);
            }
        };
        addGestureListenerToAllViews();

        return contentLayout;
    }

    protected void addGestureListenerToAllViews() {
        Log.d(TAG, "addGestureListenerToAllViews");
        PercentRelativeLayout topLayout = (PercentRelativeLayout) contentLayout.findViewById(R.id.contact_list_top_layout);
        addListenerToPercentRelativeLayoutChildren(topLayout);
    }

    private void addListenerToPercentRelativeLayoutChildren(PercentRelativeLayout layout) {
        int count = layout.getChildCount();
        View view;
        for (int i = 0; i < count; i++) {
            view = layout.getChildAt(i);
            if (view instanceof PercentRelativeLayout) {
                addListenerToPercentRelativeLayoutChildren((PercentRelativeLayout) view);
            } else {
                view.setOnTouchListener(gestureListener);
            }
        }
    }

    public void displayContacts(int index) {
        // set photos and nicknames from xml contacts
        for (int i = 0; i < 3; i++) {
            try {
                XmlContact contact = filteredContacts.get(index + i);
                View[] views = fragmentItems.get(i);
                SmartImageView contactPhoto = (SmartImageView) views[0];
                TextView contactNickname = (TextView) views[1];
                ColorDrawable cd = new ColorDrawable(getResources().getColor(
                        android.R.color.transparent));
                contactPhoto.setImageDrawable(cd);
                contactPhoto.setVisibility(View.VISIBLE);
                contactNickname.setText(contact.getNickname());
                contactPhoto.setTag(contact.getId());
                contactNickname.setTag(contact.getId());
                setAvatar(contact, contactPhoto);
            } catch (Exception e) {
                View[] views = fragmentItems.get(i);
                SmartImageView contactPhoto = (SmartImageView) views[0];
                TextView contactNickname = (TextView) views[1];
                contactPhoto.setVisibility(View.INVISIBLE);
                contactPhoto.setImage(null);
                contactNickname.setText("");
            }
        }
        // check if left arrow must be visible
        ImageView leftArrowImage = (ImageView) contentLayout.findViewById(R.id.contact_list_left_arrow_image);
        TextView leftArrowText = (TextView) contentLayout.findViewById(R.id.contact_list_left_arrow_text);
        if (initialItemIndex == 0) {
            leftArrowImage.setVisibility(View.INVISIBLE);
            leftArrowText.setVisibility(View.INVISIBLE);
        } else {
            leftArrowImage.setVisibility(View.VISIBLE);
            leftArrowText.setVisibility(View.VISIBLE);
        }
        // check if right arrow must be visible
        ImageView rightArrowImage = (ImageView) contentLayout.findViewById(R.id.contact_list_right_arrow_image);
        TextView rightArrowText = (TextView) contentLayout.findViewById(R.id.contact_list_right_arrow_text);
        try {
            filteredContacts.get(initialItemIndex + 3);
            rightArrowImage.setVisibility(View.VISIBLE);
            rightArrowText.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            rightArrowImage.setVisibility(View.INVISIBLE);
            rightArrowText.setVisibility(View.INVISIBLE);
        }
    }

    public void displayNextContacts() {
        try {
            filteredContacts.get(initialItemIndex + 3);
            initialItemIndex += 3;
            displayContacts(initialItemIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayPreviousContacts() {
        try {
            filteredContacts.get(initialItemIndex - 3);
            initialItemIndex -= 3;
            displayContacts(initialItemIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void filterContacts() {
    }

    public void handleArrowTapEvent(View view) {
        if (view.getId() == R.id.contact_list_left_arrow_image) {
            displayPreviousContacts();
        } else {
            displayNextContacts();
        }
    }

    public void handleContactTapEvent(View view) {
    }

    private void setAvatar(XmlContact contact, SmartImageView view) {
        if (contact.getPhoto() != null) {
            String folder = Environment.getExternalStorageDirectory().getPath()
                    + "/EmailImages/";
            String path = folder + contact.getPhoto();
            view.setImageBitmap(MainActivity.photoService.getPhoto(path));
        }
    }
}
