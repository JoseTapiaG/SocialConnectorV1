package com.dimunoz.androidsocialconn.photos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.basefragments.BaseDisplayPhotoFragment;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewPhotosFragment extends BaseDisplayPhotoFragment {

    private static final String TAG = "NewPhotosFragment";
    private final int LEFT = 0;
    private final int RIGHT = 1;
    private int BUTTON;

    // SharedPreferences
    private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        this.currentPhoto = MainActivity.newPhotosList.get(0);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.NewPhotos));

        // set current photo image
        changePhoto();

        // set arrow visibility
        this.leftArrow.setVisibility(View.INVISIBLE);
        if (MainActivity.newPhotosList.size() > 1)
            this.rightArrow.setVisibility(View.VISIBLE);
        else
            this.rightArrow.setVisibility(View.INVISIBLE);
        this.photoCount.setText("Foto 1 de " + MainActivity.newPhotosList.size());

        markCurrentPhotoAsSeen();

        return contentLayout;
    }

    public void handleLeftArrowTapEvent(View view) {
        Log.d(TAG, "handleLeftArrowTapEvent");
        BUTTON = LEFT;
        new LoadingPhoto().execute();
    }

    public void handleRightArrowTapEvent(View view) {
        Log.d(TAG, "handleRightArrowTapEvent");
        BUTTON = RIGHT;
        new LoadingPhoto().execute();
    }

    private void changePhoto() {
        // set initial timestamp for seeing photo (for log)
        Calendar cal = Calendar.getInstance();
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putLong(getActivity().getApplicationContext().getString(R.string.new_photo_begin_see),
                cal.getTimeInMillis());
        editor.apply();

        // change photo
        //this.photo.setImageUrl(currentPhoto.getLowResUrl());
        // TODO: 26-09-2016 Recuperar usuario
        this.photoAuthor.setText("Test");
        this.photoCaption.setText(currentPhoto.getCaption());
    }

    private void markCurrentPhotoAsSeen() {
        if (!currentPhoto.isSeen()) {
            currentPhoto.setSeen(true);
            SharedPreferences.Editor editor;
            editor = settings.edit();
            editor.putString(getActivity().getApplicationContext()
                            .getString(R.string.instagram_last_seen_photo),
                    currentPhoto.getId() + "");
            editor.apply();
            Utils.changeBadgeNewPhotosText(getActivity());
        }
    }

    public void handlePhotoTapEvent(View view) {
        Log.d(TAG, "handlePhotoTapEvent");
        Intent intent = new Intent(getActivity(), FullPhotoActivity.class);
        //intent.putExtra("url", currentPhoto.getHighResUrl());
        startActivity(intent);
    }

    private class LoadingPhoto extends AsyncTask<Void, Void, Void> {

        protected void onPostExecute(Void dResult) {
            MainActivity.progressDialog.dismiss();
            leftArrow.setClickable(true);
            rightArrow.setClickable(true);
        }

        protected void onPreExecute() {
            Long photo_begin_see = settings.getLong(
                    getActivity().getApplicationContext().getString(R.string.new_photo_begin_see),
                    -1);
            Calendar cal = Calendar.getInstance();
            //TODO cambiar fecha
            Utils.logSeeNewPhotoEvent(currentPhoto.getId() + "", 0L,
                    photo_begin_see, cal.getTimeInMillis());
            MainActivity.progressDialog.setMessage("Cargando foto...");
            MainActivity.progressDialog.show();
            leftArrow.setClickable(false);
            rightArrow.setClickable(false);
        }

        protected Void doInBackground(Void... params) {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    ArrayList<PhotoEntity> list = MainActivity.newPhotosList;
                    switch (BUTTON) {
                        case LEFT:
                            // change current photo
                            currentPhoto = list.get(list.indexOf(currentPhoto) - 1);
                            // set current photo image
                            changePhoto();
                            // set visibility
                            rightArrow.setVisibility(View.VISIBLE);
                            if (list.indexOf(currentPhoto) == 0)
                                leftArrow.setVisibility(View.INVISIBLE);
                            photoCount.setText("Foto " + (list.indexOf(currentPhoto) + 1)
                                    + " de " + list.size());
                            break;
                        case RIGHT:
                            // change current photo
                            currentPhoto = list.get(list.indexOf(currentPhoto) + 1);
                            // set current photo image
                            changePhoto();

                            markCurrentPhotoAsSeen();
                            // set visibility
                            leftArrow.setVisibility(View.VISIBLE);
                            if (list.indexOf(currentPhoto) == (list.size() - 1))
                                rightArrow.setVisibility(View.INVISIBLE);
                            photoCount.setText("Foto " + (list.indexOf(currentPhoto) + 1)
                                    + " de " + list.size());
                            break;
                    }
                }
            });
            return null;
        }
    }
}
