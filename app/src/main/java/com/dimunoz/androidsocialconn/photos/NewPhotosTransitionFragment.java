package com.dimunoz.androidsocialconn.photos;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.views.GifView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by dmunoz on 25-08-15.
 *
 */
public class NewPhotosTransitionFragment extends Fragment {

    private static final String TAG = "NewMessagesTransition";

    private static PercentRelativeLayout contentLayout;
    private boolean savedState = false;
    private ScheduledFuture scheduledFuture;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");

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

        contentLayout = (PercentRelativeLayout) inflater.inflate(R.layout.transition_fragment, container, false);
        contentLayout.setBackgroundColor(getResources().getColor(R.color.NewPhotos));
        TextView tv = (TextView) contentLayout.findViewById(R.id.default_text);
        tv.setText("Cargando fotos, espere un momento...");
        tv.setTextSize(50);
        tv.setTextColor(getResources().getColor(R.color.black));
        GifView gv = (GifView) contentLayout.findViewById(R.id.default_gif);
        gv.setMyDrawable(R.drawable.page_loader);
        gv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        gv.init(getActivity());
        ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
        scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
        CheckIfHasPhotos myTask = new CheckIfHasPhotos();
        scheduledFuture = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                myTask.getRunnable(), 0, 3, TimeUnit.SECONDS);

        return contentLayout;
    }

    private class CheckIfHasPhotos {

        private final String TAG = "CheckIfHasPhotos";

        public CheckIfHasPhotos() {
            Log.d(TAG, "Init service");
        }

        public Runnable getRunnable() {
            return new Runnable() {

                @Override
                public void run() {
                    if (!MainActivity.newPhotosList.isEmpty()) {
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        Fragment currentFragment = fragmentManager.findFragmentByTag(
                                MainActivity.FRAGMENT_TAG);

                        if (currentFragment instanceof NewPhotosTransitionFragment) {
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            NewPhotosFragment newPhotosFragment = new NewPhotosFragment();
                            transaction.replace(R.id.fragment_container, newPhotosFragment,
                                    MainActivity.FRAGMENT_TAG);
                            transaction.commit();
                        }
                        scheduledFuture.cancel(false);
                    } else {
                        if (!MainActivity.isCheckingNewPhotos) {
                            TextView tv = (TextView) contentLayout.findViewById(R.id.default_text);
                            tv.setText("No tienes nuevas fotos.");
                            GifView gv = (GifView) getActivity().findViewById(R.id.default_gif);
                            gv.setVisibility(View.INVISIBLE);
                            scheduledFuture.cancel(false);
                        }
                    }
                }
            };
        }
    }

}
