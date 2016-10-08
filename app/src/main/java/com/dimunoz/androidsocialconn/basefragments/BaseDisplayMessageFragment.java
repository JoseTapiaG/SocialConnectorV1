package com.dimunoz.androidsocialconn.basefragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.dimunoz.androidsocialconn.gesture.DisplayMessageGestureDetector;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.loopj.android.image.SmartImageView;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 19/02/15
 * Time: 16:48
 */
public class BaseDisplayMessageFragment extends Fragment {

    public XmlContact contact = new XmlContact();
    public PersonalMessage currentMessage = new PersonalMessage();

    protected GestureDetector gestureDetector;
    protected View.OnTouchListener gestureListener;
    public View touchView;

    // interface elements
    protected static PercentRelativeLayout contentLayout;
    protected SmartImageView leftProfilePictureImage;
    protected TextView leftProfilePictureText;
    protected TextView mainMessage;
    protected TextView mainMessageDetails;
    protected TextView messageCount;
    public TextView leftArrowText;
    public ImageView leftArrowImage;
    public TextView rightArrowText;
    public ImageView rightArrowImage;
    public ImageView microphoneImage;
    public TextView microphoneText;
    public ImageView answerMessageImage;
    public TextView answerMessageText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final String TAG = "BaseDisplayMessage";
        Log.d(TAG, "onCreateView");

        contentLayout = (PercentRelativeLayout) inflater.inflate(R.layout.display_message, container, false);

        // define interface elements
        this.leftProfilePictureImage = (SmartImageView) contentLayout.findViewById(R.id.display_message_contact_image);
        this.leftProfilePictureText = (TextView) contentLayout.findViewById(R.id.display_message_contact_text);
        this.mainMessage = (TextView) contentLayout.findViewById(R.id.display_message_text_content);
        this.mainMessageDetails = (TextView) contentLayout.findViewById(R.id.display_message_text_info);
        this.messageCount = (TextView) contentLayout.findViewById(R.id.display_message_count);
        this.leftArrowText = (TextView) contentLayout.findViewById(R.id.display_message_left_arrow_text);
        this.leftArrowImage = (ImageView) contentLayout.findViewById(R.id.display_message_left_arrow_image);
        this.rightArrowText = (TextView) contentLayout.findViewById(R.id.display_message_right_arrow_text);
        this.rightArrowImage = (ImageView) contentLayout.findViewById(R.id.display_message_right_arrow_image);
        this.microphoneImage = (ImageView) contentLayout.findViewById(R.id.display_message_right_button_up_image);
        this.microphoneText = (TextView) contentLayout.findViewById(R.id.display_message_right_button_up_text);
        this.answerMessageImage = (ImageView) contentLayout.findViewById(R.id.display_message_right_button_down_image);
        this.answerMessageText = (TextView) contentLayout.findViewById(R.id.display_message_right_button_down_text);

        // set gesture detector and listener to views
        gestureDetector = new GestureDetector(getActivity(), new DisplayMessageGestureDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                touchView = v;
                return gestureDetector.onTouchEvent(event);
            }
        };
        this.microphoneImage.setOnTouchListener(gestureListener);
        this.answerMessageImage.setOnTouchListener(gestureListener);
        this.leftArrowImage.setOnTouchListener(gestureListener);
        this.rightArrowImage.setOnTouchListener(gestureListener);

        return contentLayout;
    }

    public void handleDownButtonTapEvent(View view) {
    }

    public void handleUpButtonTapEvent(View view) {
    }

    public void handleLeftArrowTapEvent(View view) {
    }

    public void handleRightArrowTapEvent(View view) {
    }

    public void setAvatar(XmlContact contact, SmartImageView view) {
        view.setImageUrl(contact.getPhoto());
    }

    public void setLeftProfilePictureImage(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        leftProfilePictureImage.setImageBitmap(bitmap);
    }
}
