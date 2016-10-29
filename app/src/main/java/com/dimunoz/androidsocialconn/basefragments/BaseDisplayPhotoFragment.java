package com.dimunoz.androidsocialconn.basefragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.gesture.DisplayPhotoGestureDetector;
import com.dimunoz.androidsocialconn.photos.Photo;
import com.loopj.android.image.SmartImageView;

/**
 * Created by dmunoz on 03-09-15.
 *
 */
public class BaseDisplayPhotoFragment extends Fragment {

    protected GestureDetector gestureDetector;
    protected View.OnTouchListener gestureListener;
    public View touchView;

    // interface elements
    protected static PercentRelativeLayout contentLayout;
    public PhotoEntity currentPhoto;
    public TextView leftArrow;
    public TextView rightArrow;
    public TextView emptyPhotoList;
    public SmartImageView photo;
    public TextView photoAuthor;
    public TextView photoCaption;
    public TextView photoCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final String TAG = "BaseDisplayPhoto";
        Log.d(TAG, "onCreateView");

        contentLayout = (PercentRelativeLayout) inflater.inflate(R.layout.display_photo, container, false);

        // define interface elements
        this.leftArrow = (TextView) contentLayout.findViewById(R.id.left_arrow);
        this.rightArrow = (TextView) contentLayout.findViewById(R.id.right_arrow);
        this.emptyPhotoList = (TextView) contentLayout.findViewById(R.id.empty_photo_list);
        this.photo = (SmartImageView) contentLayout.findViewById(R.id.display_photo);
        this.photoAuthor = (TextView) contentLayout.findViewById(R.id.display_photo_author);
        this.photoCaption = (TextView) contentLayout.findViewById(R.id.display_photo_caption);
        this.photoCount = (TextView) contentLayout.findViewById(R.id.display_photo_count);
        this.photoCaption.setMovementMethod(new ScrollingMovementMethod());

        // set arrow images and texts
        this.leftArrow.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.top_left_128, 0, 0);
        this.leftArrow.setText("Ver\nanterior");
        this.rightArrow.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.top_right_128, 0, 0);
        this.rightArrow.setText("Ver\nsiguiente");

        // set gesture detector and listener to views
        gestureDetector = new GestureDetector(getActivity(), new DisplayPhotoGestureDetector(this));
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                touchView = v;
                return gestureDetector.onTouchEvent(event);
            }
        };
        this.leftArrow.setOnTouchListener(gestureListener);
        this.rightArrow.setOnTouchListener(gestureListener);
        this.photo.setOnTouchListener(gestureListener);

        return contentLayout;
    }

    public void handleLeftArrowTapEvent(View view) { }
    public void handleRightArrowTapEvent(View view) { }
    public void handlePhotoTapEvent(View view) { }
}
