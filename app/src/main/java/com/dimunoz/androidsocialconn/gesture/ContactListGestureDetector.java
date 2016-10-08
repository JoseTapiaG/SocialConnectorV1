package com.dimunoz.androidsocialconn.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dimunoz.androidsocialconn.basefragments.BaseContactListFragment;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 16/02/15
 * Time: 19:31
 */
public class ContactListGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private BaseContactListFragment fragment;

    public ContactListGestureDetector(BaseContactListFragment fragment) {
        super();
        this.fragment = fragment;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX,
                           float distanceY) {
        // x > 0 -> right to left
        // x < 0 -> left to right
        float x = e1.getX() - e2.getX();
        if (x > 0) {
            fragment.displayNextContacts();
        }
        else if (x < 0) {
            fragment.displayPreviousContacts();
        }
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        if (fragment.fragmentArrows.contains(fragment.touchView)) {
            fragment.handleArrowTapEvent(fragment.touchView);
        } else {
            for (View[] views : fragment.fragmentItems) {
                if (views[0] == fragment.touchView) {
                    fragment.handleContactTapEvent(fragment.touchView);
                } else if (views[1] == fragment.touchView)
                    fragment.handleContactTapEvent(fragment.touchView);
            }
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }
}