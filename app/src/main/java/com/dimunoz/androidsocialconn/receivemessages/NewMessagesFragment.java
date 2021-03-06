package com.dimunoz.androidsocialconn.receivemessages;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.asynctasks.MarkEmailAsRead;
import com.dimunoz.androidsocialconn.basefragments.BaseDisplayMessageFragment;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.main.TopbarFragment;
import com.dimunoz.androidsocialconn.sendmessage.CreateMessageFragment;
import com.dimunoz.androidsocialconn.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import static com.dimunoz.androidsocialconn.main.MainActivity.mailService;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewMessagesFragment extends BaseDisplayMessageFragment {

    private static final String TAG = "NewMessagesFragment";
    private ArrayList<PersonalMessage> personalMessages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setHasOptionsMenu(true);
        personalMessages = mailService.getEmails();
        PersonalMessage message = personalMessages.get(0);
        contact = message.getAuthor();
        currentMessage = message;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "OnCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.NewMessages));

        // change contact info
        changeContactInfo();

        // set message
        changeMessage();
        this.mainMessageDetails.setTypeface(null, Typeface.BOLD_ITALIC);

        // set mic image and text
        Drawable microphoneDrawable = getResources().getDrawable(R.drawable.send_flying_message);
        this.answerMessageImage.setImageDrawable(microphoneDrawable);
        this.answerMessageText.setText("Responder mensaje");

        // set answer message button invisible
        this.microphoneImage.setVisibility(View.INVISIBLE);
        this.microphoneText.setVisibility(View.INVISIBLE);

        // set arrow visibility
        this.leftArrowImage.setVisibility(View.INVISIBLE);
        this.leftArrowText.setVisibility(View.INVISIBLE);
        if (personalMessages.size() > 1) {
            this.rightArrowImage.setVisibility(View.VISIBLE);
            this.rightArrowText.setVisibility(View.VISIBLE);
        } else {
            this.rightArrowImage.setVisibility(View.INVISIBLE);
            this.rightArrowText.setVisibility(View.INVISIBLE);
        }

        // set messages count
        this.messageCount.setText("1 de " + personalMessages.size());

        markCurrentMessageAsSeen();
        Utils.changeBadgeNewMessagesText(getActivity());

        return contentLayout;
    }

    // answer message button
    public void handleDownButtonTapEvent(View view) {
        CreateMessageFragment fragment = new CreateMessageFragment();
        // add xml contact to arguments
        fragment.contact = contact;
        // display fragment
        changeTopbarColors(R.id.topbar_layout_send_message);
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, MainActivity.FRAGMENT_TAG);
        transaction.commit();
    }

    // left arrow button
    public void handleLeftArrowTapEvent(View view) {
        int index = personalMessages.indexOf(currentMessage) - 1;
        this.currentMessage = personalMessages.get(index);
        this.contact = currentMessage.getAuthor();
        changeContactInfo();
        changeMessage();
        this.messageCount.setText((index + 1) + " de " + personalMessages.size());

        // set arrow visibility
        this.rightArrowImage.setVisibility(View.VISIBLE);
        this.rightArrowText.setVisibility(View.VISIBLE);
        if (index == 0) {
            this.leftArrowImage.setVisibility(View.INVISIBLE);
            this.leftArrowText.setVisibility(View.INVISIBLE);
        } else {
            this.leftArrowImage.setVisibility(View.VISIBLE);
            this.leftArrowText.setVisibility(View.VISIBLE);
        }
    }

    // right arrow button
    public void handleRightArrowTapEvent(View view) {
        Log.d(TAG, "handleRightArrowTapEvent");
        int index = personalMessages.indexOf(currentMessage) + 1;
        this.currentMessage = personalMessages.get(index);
        this.contact = currentMessage.getAuthor();
        changeContactInfo();
        changeMessage();
        markCurrentMessageAsSeen();
        this.messageCount.setText((index + 1) + " de " + personalMessages.size());

        // set arrow visibility
        this.leftArrowImage.setVisibility(View.VISIBLE);
        this.leftArrowText.setVisibility(View.VISIBLE);
        if (index == personalMessages.size() - 1) {
            this.rightArrowImage.setVisibility(View.INVISIBLE);
            this.rightArrowText.setVisibility(View.INVISIBLE);
        } else {
            this.rightArrowImage.setVisibility(View.VISIBLE);
            this.rightArrowText.setVisibility(View.VISIBLE);
        }
    }

    private void changeContactInfo() {
        this.leftProfilePictureText.setText(this.contact.getNickname() + " dice:");
    }

    private void changeMessage() {
        String logType = "onlyText";
        this.mainMessage.setText(this.currentMessage.getContent());
        this.mainMessageDetails.setText(Utils.createTimeStringFromTimestampMillisecondsLong(
                this.currentMessage.getDatetime().getTime()));
        if (this.currentMessage.getHasAttachedAudio()) {
            logType = "messageWithAudio";
        } else if (this.currentMessage.getHasAttachedImage()) {
            logType = "messageWithImage";
        } else if (this.currentMessage.getHasAttachedVideo()) {
            logType = "messageWithVideo";
        }
        if (this.currentMessage.getHasAttachedImage()) {
            setLeftProfilePictureImage(this.currentMessage.getImageFile());
        } else {
            setAvatar(this.contact, this.leftProfilePictureImage);
        }
        Calendar cal = Calendar.getInstance();
        Utils.logReadNewMessageEvent(logType, this.currentMessage.getAuthor().getEmail(),
                MainActivity.userContact.getEmail(), this.currentMessage.getDatetime().getTime(),
                cal.getTimeInMillis());
    }

    private void changeTopbarColors(int layout) {
        // set gray icons
        PercentRelativeLayout topbarLayout = TopbarFragment.topbarLayout;
        ImageView imageCall = (ImageView) topbarLayout.findViewById(R.id.topbar_image_call);
        Drawable callDrawable = getResources().getDrawable(R.drawable.topbar_call_gray);
        ImageView imageSendMessage = (ImageView) topbarLayout.findViewById(R.id.topbar_image_send_message);
        Drawable sendMessageDrawable = getResources().getDrawable(R.drawable.topbar_send_message_gray);
        ImageView imageNewMessages = (ImageView) topbarLayout.findViewById(R.id.topbar_image_new_messages);
        Drawable newMessagesDrawable = getResources().getDrawable(R.drawable.topbar_new_messages_gray);
        ImageView imageNewPhotos = (ImageView) topbarLayout.findViewById(R.id.topbar_image_new_photos);
        Drawable newPhotosDrawable = getResources().getDrawable(R.drawable.topbar_new_photos_gray);
        ImageView imageAlbum = (ImageView) topbarLayout.findViewById(R.id.topbar_image_album);
        Drawable albumDrawable = getResources().getDrawable(R.drawable.topbar_album_gray);
        // set gray fonts
        TextView textCall = (TextView) topbarLayout.findViewById(R.id.topbar_text_call);
        textCall.setTextColor(getResources().getColor(R.color.grayFromIcons));
        TextView textSendMessage = (TextView) topbarLayout.findViewById(R.id.topbar_text_send_message);
        textSendMessage.setTextColor(getResources().getColor(R.color.grayFromIcons));
        TextView textNewMessages = (TextView) topbarLayout.findViewById(R.id.topbar_text_new_messages);
        textNewMessages.setTextColor(getResources().getColor(R.color.grayFromIcons));
        TextView textNewPhotos = (TextView) topbarLayout.findViewById(R.id.topbar_text_new_photos);
        textNewPhotos.setTextColor(getResources().getColor(R.color.grayFromIcons));
        TextView textAlbum = (TextView) topbarLayout.findViewById(R.id.topbar_text_album);
        textAlbum.setTextColor(getResources().getColor(R.color.grayFromIcons));
        // make the clicked one black
        switch (layout) {
            case R.id.topbar_layout_call:
                callDrawable = getResources().getDrawable(R.drawable.topbar_call);
                textCall.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.topbar_layout_send_message:
                sendMessageDrawable = getResources().getDrawable(R.drawable.topbar_send_message);
                textSendMessage.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.topbar_layout_new_messages:
                newMessagesDrawable = getResources().getDrawable(R.drawable.topbar_new_messages);
                textNewMessages.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.topbar_layout_new_photos:
                newPhotosDrawable = getResources().getDrawable(R.drawable.topbar_new_photos);
                textNewPhotos.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.topbar_layout_album:
                albumDrawable = getResources().getDrawable(R.drawable.topbar_album);
                textAlbum.setTextColor(getResources().getColor(R.color.black));
                break;
        }
        imageCall.setImageDrawable(callDrawable);
        imageSendMessage.setImageDrawable(sendMessageDrawable);
        imageNewMessages.setImageDrawable(newMessagesDrawable);
        imageNewPhotos.setImageDrawable(newPhotosDrawable);
        imageAlbum.setImageDrawable(albumDrawable);
    }

    private void markCurrentMessageAsSeen() {
        if (!currentMessage.getSeen()) {
            currentMessage.setSeen(true);
            MainActivity.mailService.markMessageAsRead(currentMessage);
            new MarkEmailAsRead(getActivity(), currentMessage).execute();
        }
    }
}
