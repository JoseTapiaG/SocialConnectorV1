package com.dimunoz.androidsocialconn.main;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.dimunoz.androidsocialconn.R;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 23-10-13
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class ContentFragment extends Fragment {

    private static final String TAG = "ContentFragment";

    private static LinearLayout contentLayout;
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

            contentLayout.setVisibility(View.VISIBLE);
            savedState = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "Menu - OnCreateView " + savedState);

        contentLayout = (LinearLayout) inflater.inflate(R.layout.default_fragment, container, false);

        return contentLayout;
    }

}
