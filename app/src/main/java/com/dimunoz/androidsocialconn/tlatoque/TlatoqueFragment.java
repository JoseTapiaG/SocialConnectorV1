package com.dimunoz.androidsocialconn.tlatoque;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.photos.Photo;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.loopj.android.image.SmartImageView;
import com.loopj.android.image.WebImage;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/*
 * Created by Diego Munoz
 * Based on MainFragment of Tlatoque created by Eduardo Quintana
 * Date: 15/09/15
 */

public class TlatoqueFragment extends Fragment {

    private static final String TAG = "TlatoqueFragment";

    // Flipper
    public static int flipInterval = 20000;
    private static final int PICTURES_CACHE = 9;
    private static final int HALF_CACHE = (int) Math.floor(PICTURES_CACHE / 2);
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    public static long startedAt = 0;
    public static long elapsed = 0;

    // FB Query
    private int maxPictures = 100;

    private FrameLayout view;
    private static RelativeLayout lyPanel;

    private View dialogLoading;
    private TextView dialogMessage;

    // Carousel
    private TlatoqueViewFlipper flipper;
    private Animation inFromRightAnimation, outToLeftAnimation, inFromLeftAnimation, outToRightAnimation;
    public GestureDetector gestureDetector;

    private ArrayList<PhotoEntity> pictures = new ArrayList<>();
    private int currentIndex = 0;

    // Bar
    private SmartImageView profilePic;
    private TextView tvCaption;

    private boolean autoStart = false;

    public static boolean isLogEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        resetFlipper();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        resetFlipper();
        if (isFlipping()) {
            stopFlipping();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");

        view = (FrameLayout) inflater.inflate(R.layout.tlatoque_fragment, container, false);
        loadPreferences();
        init(inflater);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        Log.d(TAG, "onActivityCreated");
        setRetainInstance(true);

        Weather weather = new Weather(this.getActivity(), handlerWeather);
        weather.refreshWeather();
        autoStart = true;
        createAnimations();
        try {
            getPictures();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.d(TAG, "Main - onSaveInstanceState ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onBackPressed() {
        return false;
    }

    /**
     * Initiates the view components.
     */
    private void init(LayoutInflater inflater) {
        flipper = (TlatoqueViewFlipper) view.findViewById(R.id.view_flipper);
        flipper.setFlipInterval(flipInterval);
        flipper.setHandler(handlerFlipper);

        lyPanel = (RelativeLayout) inflater.inflate(R.layout.tlatoque, null, false);

        // Loading dialog
        dialogLoading = view.findViewById(R.id.dialog_loading);
        dialogMessage = (TextView) view.findViewById(R.id.dialog_message);

        // add views
        view.addView(lyPanel);

        // InstagramUser's profile picture custom view
        profilePic = (SmartImageView) lyPanel.findViewById(R.id.profile_pic);

        tvCaption = (TextView) lyPanel.findViewById(R.id.picture_caption);

        try {
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");

            if (font == null) {
                Log.v(TAG, "Null font");
            }

            TextView txtWeather = (TextView) lyPanel.findViewById(R.id.main_weather);
            txtWeather.setTypeface(font);

        } catch (Exception e) {
            e.printStackTrace();
        }

        gestureDetector = new GestureDetector(getActivity(), new TlatoqueGestureDetector());
    }

    /**
     * Creates the <code>ViewFlipper</code>'s animations.
     */
    private void createAnimations() {
        int inTime = 400;
        int outTime = 600;

        inFromRightAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRightAnimation.setDuration(inTime);
        inFromRightAnimation.setInterpolator(new AccelerateInterpolator());

        outToLeftAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToLeftAnimation.setDuration(outTime);
        outToLeftAnimation.setInterpolator(new DecelerateInterpolator());

        inFromLeftAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeftAnimation.setDuration(inTime);
        inFromLeftAnimation.setInterpolator(new AccelerateInterpolator());

        outToRightAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outToRightAnimation.setDuration(outTime);
        outToRightAnimation.setInterpolator(new DecelerateInterpolator());
    }

    private void setWeather() {
        TextView weather = (TextView) lyPanel.findViewById(R.id.main_weather);
        if (Weather.weatherSample.getTempF() != null && Weather.weatherSample.getTempC() != null) {
            if (Weather.weatherSample.getCountry().equals("US")) {
                weather.setText(Weather.weatherSample.getTempF() + "ºF");
            } else {
                weather.setText(Weather.weatherSample.getTempC() + "ºC");
            }
        } else {
            weather.setText("");
        }
        SmartImageView weatherCondition = (SmartImageView) lyPanel.findViewById(R.id.weather_condition);
        weatherCondition.setImageUrl("http://icons.wxug.com/i/c/k/" + Weather.weatherSample.getCondition() + ".gif");
    }

    private final Handler handlerWeather = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Weather.UPDATE_WEATHER) {
                setWeather();
            }
        }
    };

    private final Handler handlerFlipper = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TlatoqueViewFlipper.FLIP_MSG:
                    if (!pictures.isEmpty() && flipper.isRunning()) {
                        currentIndex = getPictureIndex(currentIndex + 1);

                        // set animations from-right-to-left (in case the user manually flipped images to the right)
                        flipper.setInAnimation(inFromRightAnimation);
                        flipper.setOutAnimation(outToLeftAnimation);

                        moveForward();
                        updateFrame();

                        elapsed = 0;
                        startedAt = System.currentTimeMillis();

                        msg = obtainMessage(TlatoqueViewFlipper.FLIP_MSG);
                        sendMessageDelayed(msg, flipInterval);
                    }
                    break;
                case TlatoqueViewFlipper.STOP_FLIP:
                    if (isFlipping()) {
                        stopFlipping();
                    }
                    break;
                case TlatoqueViewFlipper.CONTINUE_FLIP:
                    if (!isFlipping() && !Util.isPaused()) {
                        final long ADDED_TIME_AFTER_ZOOM = 2000;
                        extendPictureTime(ADDED_TIME_AFTER_ZOOM);
                        startFlipping();
                    }
                    break;
            }
        }
    };

    private final Handler handlerRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TlatoqueViewFlipper.REFRESH:
                    // refresh carousel
                    try {
                        getPictures();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    /**
     * Resets the refresh time for the carousel
     */
    public void resetRefresh() {
        Log.d(TAG, "CLEARING REFRESH QUEUE");

        handlerRefresh.removeCallbacksAndMessages(null);
    }

    /**
     * Schedules the next carousel refresh.
     */
    public void scheduleRefresh() {
        scheduleRefresh(getRefreshRateInMillis());
    }

    public void scheduleRefresh(long delay) {
        Log.d(TAG, "Next refresh in: " + delay + "ms.");

        handlerRefresh.removeCallbacksAndMessages(null);
        Message msg = handlerRefresh.obtainMessage(TlatoqueViewFlipper.REFRESH);
        handlerRefresh.sendMessageDelayed(msg, delay);
    }

    /**
     * Gets the next update time for the carousel
     * refreshing. This time is loaded from the settings.
     *
     * @return the next update time
     */
    private long getRefreshRateInMillis() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int nextHour = 3;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, nextHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (hour >= nextHour) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis() - System.currentTimeMillis();
    }

    // Flipper

    /**
     * Loads pictures in the flipper's cache.
     * The cache is centered in the first image or
     * (if resuming the app) the last viewed image.
     */
    private void initFlipper() {

        if (pictures.size() > PICTURES_CACHE) {
            // load index 0, next 4, and previous 4
            for (int i = currentIndex, c = 0; c < PICTURES_CACHE; i++, c++) {
                if (c == HALF_CACHE + (PICTURES_CACHE % 2)) {
                    i = getPictureIndex(currentIndex - HALF_CACHE);
                }

                PhotoEntity picture = getPicture(i);
                addPictureView(picture);
            }
        }
        // load all pictures
        else {
            for (PhotoEntity picture : pictures) {
                addPictureView(picture);
            }
        }
    }

    /**
     * Adds a <code>PictureView</code> to the carousel.
     */
    private void addPictureView(PhotoEntity picture) {
        PictureView pictureView = new PictureView(getActivity());
        pictureView.setDetector(gestureDetector);
        pictureView.loadPicture(MainActivity.photoService.getPhoto(picture.getPath()));
        pictureView.setHandler(handlerFlipper);
        flipper.addView(pictureView);

        // cache user's profile picture
        // TODO: 26-09-2016 Cargar profile picture
        new SmartImageView(getActivity()).setImageUrl("http://static.batanga.com/sites/default/files/styles/large/public/curiosidades.batanga.com/files/Por-qu%C3%A9-los-perros-mueven-la-cola.jpg?itok=I1MKTTQi");
    }

    /**
     * Starts a timer to cycle through child views.
     */
    public void startFlipping() {
        Log.d(TAG, "START flipping");
        startedAt = System.currentTimeMillis();
        showView(lyPanel);
        flipper.startFlipping();
    }

    /**
     * Stops flipping the carousel.
     */
    public void stopFlipping() {
        elapsed += System.currentTimeMillis() - startedAt;
        Log.d(TAG, "STOP flipping: " + (elapsed / 1000) + " secs elapsed");
        flipper.stopFlipping();
    }

    /**
     * Loads the next picture <code>(current + cache / 2)</code>
     * at the end of the <code>ViewFlipper</code>'s cache. Also removes
     * the additional picture at the beginning.
     */
    private void moveForward() {
        Log.d(TAG, "MOVING FW: (INDEX:" + currentIndex + ")");

        if (isFlipping()) {
            restartFlipper();
        }

        PictureView pictureView = ((PictureView) flipper.getChildAt(getFlipperIndex(flipper.getDisplayedChild() + 1)));
        pictureView.resetZoom();
        pictureView.loadPicture(MainActivity.photoService.getPhoto(pictures.get(getPictureIndex(currentIndex)).getPath()));
        flipper.showNext();
        updateCache(1);
    }

    /**
     * Loads the previous picture <code>(current - cache / 2)</code>
     * at the beginning of the <code>ViewFlipper</code>'s cache. Also
     * removes the additional picture at the end.
     */
    private void moveBackwards() {
        Log.d(TAG, "MOVING BW: (INDEX:" + currentIndex + ")");

        if (isFlipping()) {
            restartFlipper();
        }

        PictureView pictureView = ((PictureView) flipper.getChildAt(getFlipperIndex(flipper.getDisplayedChild() - 1)));
        pictureView.resetZoom();
        pictureView.loadPicture(MainActivity.photoService.getPhoto(pictures.get(getPictureIndex(currentIndex)).getPath()));
        flipper.showPrevious();
        updateCache(-1);
    }

    /**
     * Updates the flipper's cache and removes the unnecessary pictures
     * from the memory cache.
     *
     * @param direction the flipper's movement.
     *                  <code>1</code> is forward, <code>-1</code> is backwards.
     */
    private void updateCache(int direction) {
        if (pictures.size() > PICTURES_CACHE) {
            PhotoEntity picture = getPicture(currentIndex + (direction * HALF_CACHE));
            ((PictureView) flipper.getChildAt(getFlipperIndex(flipper.getDisplayedChild() + (direction * HALF_CACHE))))
                    .loadPicture(MainActivity.photoService.getPhoto(picture.getPath()));

            XmlContact contact = getUserFromMail(picture.getEmail());

            if(contact != null)
                new SmartImageView(getActivity()).setImageUrl(contact.getPhoto());
        }
    }

    /**
     * Returns <code>true</code> if the Flipper is flipping.
     */
    public boolean isFlipping() {
        return flipper.isFlipping();
    }

    /**
     * Restarts the flipper timer.
     */
    private void restartFlipper() {
        stopFlipping();
        elapsed = 0;
        startFlipping();
    }

    /**
     * Removes all pictures from the carousel.
     */
    private void resetFlipper() {
        flipper.removeAllViews();

        // reset caption
        tvCaption.setText("");

        // reset owner's picture
        profilePic.setVisibility(View.INVISIBLE);
    }

    private void extendPictureTime(long extendedTime) {
        if (elapsed > flipInterval - extendedTime) {
            elapsed -= extendedTime;
            Log.d(TAG, "Extended... Remaining: " + (flipInterval - elapsed));
        }
    }

    private PhotoEntity getPicture(int index) {
        return pictures.get(getPictureIndex(index));
    }

    /**
     * Gets an absolute index in the <code>Flipper</code>.
     *
     * @param index the index queried
     * @return the correct index in the <code>Flipper</code>.
     */
    private int getFlipperIndex(int index) {
        return getAbsoluteIndex(index, flipper.getChildCount());
    }

    /**
     * Gets an absolute index in the <code>Picture</code> list.
     *
     * @param index the index queried
     * @return the correct index in the <code>Picture</code> list.
     */
    private int getPictureIndex(int index) {
        return getAbsoluteIndex(index, pictures.size());
    }

    /**
     * Gets an absolute index from a collection.
     *
     * @param index the index queried
     * @param size  the collections size
     * @return the correct index from a collection.
     */
    private int getAbsoluteIndex(int index, int size) {
        if (index < 0) {
            return size + index;
        }
        return (index % size);
    }

    /**
     * Updates the GUI:
     */
    public void updateFrame() {
        PhotoEntity picture = pictures.get(currentIndex);

        // set caption
        String creationDate = picture.getDate();

        String pictureCaption = picture.getCaption();
        pictureCaption = !pictureCaption.equals("") ? "<br>" + pictureCaption : "";

        XmlContact contact = getUserFromMail(picture.getEmail());


        if (contact != null) {
            tvCaption.setText(Html.fromHtml("<font color='#ff203d94'><b>" +
                    contact.getNickname() + " " + creationDate +
                    "</b></font>" + pictureCaption));

            // set owner's picture
            setProfilePic(contact.getPhoto());
        }
    }

    public XmlContact getUserFromMail(String email) {
        for (XmlContact contact : MainActivity.xmlContacts) {
            if (contact.getEmail().equals(email))
                return contact;
        }

        return null;
    }

    private void setProfilePic(String url) {
        profilePic.setImageUrl(url);
    }

    // Pictures
    public void getPictures() {
        resetFlipper();
        pictures.clear();
        currentIndex = 0;
        showLoadingPicturesDialog();

        Util.setPaused(Util.PAUSED_LOADING, true);
        stopFlipping();
        elapsed = 0;

        resetRefresh();
        pictures = (ArrayList<PhotoEntity>) MainActivity.photoService.getLastTenPhotos();


        if (!pictures.isEmpty()) {
            Collections.shuffle(pictures);
            int maxPictures = pictures.size() > TlatoqueFragment.this.maxPictures ?
                    TlatoqueFragment.this.maxPictures : pictures.size();
            pictures = new ArrayList<>(pictures.subList(0, maxPictures));
            Log.d(TAG, "PICTURES " + pictures.size());

            initFlipper();
            flipper.setDisplayedChild(0);
            updateFrame();

            Util.setPaused(Util.PAUSED_LOADING, false);

            if (autoStart) {
                startFlipping();
                autoStart = false;
            } else if (!Util.isPaused()) {
                startFlipping();
            }
        }

        hideView(dialogLoading);

        // set next refresh
        scheduleRefresh();

    }

    private void showLoadingPicturesDialog() {
        dialogMessage.setText("Cargando fotos...");
        showView(dialogLoading);
    }

    private class TlatoqueGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isShowingPanel()) {
                Log.d(TAG, "OnSingleTapUp - isShowingPanel() = true");
                hideView(lyPanel);
                return true;
            } else {
                Log.d(TAG, "OnSingleTapUp");
                showView(lyPanel);
                onBackPressed();
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!pictures.isEmpty() && !((PictureView) flipper.getCurrentView()).isZoomed()) {
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                        return false;
                    }
                    // Right to left swipe
                    if ((e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
                        flipper.setInAnimation(inFromRightAnimation);
                        flipper.setOutAnimation(outToLeftAnimation);

                        currentIndex = getPictureIndex(currentIndex + 1);
                        moveForward();
                        updateFrame();

                        return true;
                    }
                    // Left to right swipe
                    else if ((e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) && (Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)) {
                        flipper.setInAnimation(inFromLeftAnimation);
                        flipper.setOutAnimation(outToRightAnimation);

                        currentIndex = getPictureIndex(currentIndex - 1);
                        moveBackwards();
                        updateFrame();

                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            return false;
        }
    }

    public void loadPreferences() {
        if (isAdded()) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

            maxPictures = settings.getInt(TlatoquePreferences.KEY_MAX_PICTURES, TlatoquePreferences.MAX_PICTURES_DEF) * 5; //step = 5, units = 1
            Log.d(TAG, "MAX PICTURES: " + maxPictures);

            flipInterval = settings.getInt(TlatoquePreferences.KEY_FLIP_INTERVAL, TlatoquePreferences.FLIP_INTERVAL_DEF) * 1000; //step = 1, units = 1000
            Log.d(TAG, "FLIP INTERVAL: " + flipInterval);

            isLogEnabled = settings.getBoolean(TlatoquePreferences.KEY_ENABLE_LOG, true);
            Log.d(TAG, "LOG ENABLED: " + isLogEnabled);
        }
    }

    public static boolean isShowingPanel() {
        return lyPanel.getVisibility() == View.VISIBLE;
    }

    private void hideView(View view) {
        setViewVisibility(view, View.GONE);
    }

    private void showView(View view) {
        setViewVisibility(view, View.VISIBLE);
    }

    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }
}