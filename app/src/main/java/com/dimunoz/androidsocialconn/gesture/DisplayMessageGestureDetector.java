package com.dimunoz.androidsocialconn.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.basefragments.BaseDisplayMessageFragment;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 10/03/15
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class DisplayMessageGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private BaseDisplayMessageFragment fragment;

    public DisplayMessageGestureDetector(BaseDisplayMessageFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        if (fragment.touchView.getId() == R.id.display_message_right_button_up_image) {
            fragment.handleUpButtonTapEvent(fragment.touchView);
        } else if (fragment.touchView.getId() == R.id.display_message_right_button_down_image) {
            fragment.handleDownButtonTapEvent(fragment.touchView);
        } else if (fragment.touchView.getId() == R.id.display_message_left_arrow_image) {
            fragment.handleLeftArrowTapEvent(fragment.touchView);
        } else if (fragment.touchView.getId() == R.id.display_message_right_arrow_image) {
            fragment.handleRightArrowTapEvent(fragment.touchView);
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}
