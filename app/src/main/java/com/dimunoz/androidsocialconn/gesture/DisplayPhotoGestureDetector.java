package com.dimunoz.androidsocialconn.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.basefragments.BaseDisplayPhotoFragment;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 10/03/15
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class DisplayPhotoGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private BaseDisplayPhotoFragment fragment;

    public DisplayPhotoGestureDetector(BaseDisplayPhotoFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        if (fragment.touchView.getId() == R.id.left_arrow) {
            fragment.handleLeftArrowTapEvent(fragment.touchView);
        } else if (fragment.touchView.getId() == R.id.right_arrow) {
            fragment.handleRightArrowTapEvent(fragment.touchView);
        } else if (fragment.touchView.getId() == R.id.display_photo) {
            fragment.handlePhotoTapEvent(fragment.touchView);
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
