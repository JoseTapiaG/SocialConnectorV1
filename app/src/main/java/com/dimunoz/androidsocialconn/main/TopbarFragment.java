package com.dimunoz.androidsocialconn.main;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimunoz.androidsocialconn.R;

import static com.dimunoz.androidsocialconn.main.MainActivity.photoService;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class TopbarFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TopbarFragment";

    public static PercentRelativeLayout topbarLayout;
    private boolean savedState = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Menu - OnCreate");

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Menu - onResume " + savedState);

        if (savedState) {
            Log.d(TAG, "onResume Resuming Saved State");

            topbarLayout.setVisibility(View.VISIBLE);
            savedState = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "Menu - OnCreateView " + savedState);
        topbarLayout = (PercentRelativeLayout) inflater.inflate(R.layout.topbar_fragment, container, false);

        // set click listeners
        PercentRelativeLayout callLayout = (PercentRelativeLayout) topbarLayout.findViewById(R.id.topbar_layout_call);
        callLayout.setOnClickListener(this);
        PercentRelativeLayout sendMessageLayout = (PercentRelativeLayout) topbarLayout.findViewById(R.id.topbar_layout_send_message);
        sendMessageLayout.setOnClickListener(this);
        PercentRelativeLayout newMessagesLayout = (PercentRelativeLayout) topbarLayout.findViewById(R.id.topbar_layout_new_messages);
        newMessagesLayout.setOnClickListener(this);
        PercentRelativeLayout newPhotosLayout = (PercentRelativeLayout) topbarLayout.findViewById(R.id.topbar_layout_new_photos);
        newPhotosLayout.setOnClickListener(this);
        PercentRelativeLayout albumLayout = (PercentRelativeLayout) topbarLayout.findViewById(R.id.topbar_layout_album);
        albumLayout.setOnClickListener(this);
        FloatingActionButton resetButton = (FloatingActionButton) topbarLayout.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);


        // set badges initially invisible
        TextView badgeNewMessages = (TextView) topbarLayout.findViewById(R.id.badge_new_messages);
        badgeNewMessages.setVisibility(View.INVISIBLE);
        TextView badgeNewPhotos = (TextView) topbarLayout.findViewById(R.id.badge_new_photos);
        badgeNewPhotos.setVisibility(View.INVISIBLE);
        return topbarLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbar_layout_call:
                //changeTopbarColorsAndLimits(R.id.topbar_layout_call);
                //((MainActivity) getActivity()).displayCallFragment();
                break;
            case R.id.topbar_layout_send_message:
                changeTopbarColorsAndLimits(R.id.topbar_layout_send_message);
                ((MainActivity) getActivity()).displaySendMessageFragment();
                break;
            case R.id.topbar_layout_new_messages:
                changeTopbarColorsAndLimits(R.id.topbar_layout_new_messages);
                ((MainActivity) getActivity()).displayNewMessagesFragment();
                break;
            case R.id.topbar_layout_new_photos:
                if (!photoService.getNewPhotos().isEmpty()) {
                    changeTopbarColorsAndLimits(R.id.topbar_layout_new_photos);
                    ((MainActivity) getActivity()).displayNewPhotosFragment();
                } else
                    Toast.makeText(getActivity(), "No hay fotos nuevas para mostrar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.topbar_layout_album:
                changeTopbarColorsAndLimits(R.id.topbar_layout_album);
                ((MainActivity) getActivity()).displayAlbumFragment();
                break;
            case R.id.resetButton:
                MainActivity.photoService.resetDatabase(getActivity());
                break;
        }
    }

    private void changeTopbarColorsAndLimits(int layout) {
        // set all limits visible
        View callLimit = topbarLayout.findViewById(R.id.topbar_limit_call);
        View sendMessageLimit = topbarLayout.findViewById(R.id.topbar_limit_send_message);
        View newMessagesLimit = topbarLayout.findViewById(R.id.topbar_limit_new_messages);
        View newPhotosLimit = topbarLayout.findViewById(R.id.topbar_limit_new_photos);
        View albumLimit = topbarLayout.findViewById(R.id.topbar_limit_album);
        callLimit.setVisibility(View.VISIBLE);
        sendMessageLimit.setVisibility(View.VISIBLE);
        newMessagesLimit.setVisibility(View.VISIBLE);
        newPhotosLimit.setVisibility(View.VISIBLE);
        albumLimit.setVisibility(View.VISIBLE);
        // set gray icons
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
        // make the clicked icon black and the corresponding limit invisible
        switch (layout) {
            case R.id.topbar_layout_call:
                callDrawable = getResources().getDrawable(R.drawable.topbar_call);
                textCall.setTextColor(getResources().getColor(R.color.black));
                callLimit.setVisibility(View.INVISIBLE);
                break;
            case R.id.topbar_layout_send_message:
                sendMessageDrawable = getResources().getDrawable(R.drawable.topbar_send_message);
                textSendMessage.setTextColor(getResources().getColor(R.color.black));
                sendMessageLimit.setVisibility(View.INVISIBLE);
                break;
            case R.id.topbar_layout_new_messages:
                newMessagesDrawable = getResources().getDrawable(R.drawable.topbar_new_messages);
                textNewMessages.setTextColor(getResources().getColor(R.color.black));
                newMessagesLimit.setVisibility(View.INVISIBLE);
                break;
            case R.id.topbar_layout_new_photos:
                newPhotosDrawable = getResources().getDrawable(R.drawable.topbar_new_photos);
                textNewPhotos.setTextColor(getResources().getColor(R.color.black));
                newPhotosLimit.setVisibility(View.INVISIBLE);
                break;
            case R.id.topbar_layout_album:
                albumDrawable = getResources().getDrawable(R.drawable.topbar_album);
                textAlbum.setTextColor(getResources().getColor(R.color.black));
                albumLimit.setVisibility(View.INVISIBLE);
                break;
        }
        imageCall.setImageDrawable(callDrawable);
        imageSendMessage.setImageDrawable(sendMessageDrawable);
        imageNewMessages.setImageDrawable(newMessagesDrawable);
        imageNewPhotos.setImageDrawable(newPhotosDrawable);
        imageAlbum.setImageDrawable(albumDrawable);
    }

}
