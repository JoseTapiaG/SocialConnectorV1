package com.dimunoz.androidsocialconn.receivemessages;

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
public class NewMessagesTransitionFragment extends Fragment {

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
        contentLayout.setBackgroundColor(getResources().getColor(R.color.NewMessages));
        TextView tv = (TextView) contentLayout.findViewById(R.id.default_text);
        tv.setText("Cargando mensajes, espere un momento");
        tv.setTextSize(50);
        tv.setTextColor(getResources().getColor(R.color.black));
        GifView gv = (GifView) contentLayout.findViewById(R.id.default_gif);
        gv.setMyDrawable(R.drawable.page_loader);
        gv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        gv.init(getActivity());
        ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
        scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
        CheckIfHasMessages myTask = new CheckIfHasMessages();
        scheduledFuture = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                myTask.getRunnable(), 0, 3, TimeUnit.SECONDS);

        return contentLayout;
    }

    private class CheckIfHasMessages {

        private final String TAG = "CheckIfHasMessages";

        public CheckIfHasMessages() {
            Log.d(TAG, "Init service");
        }

        public Runnable getRunnable() {
            return new Runnable() {

                @Override
                public void run() {
                    if (!MainActivity.newMessagesList.isEmpty()) {
                        Log.d(TAG, "!MainActivity.newMessagesList.isEmpty()");
                        FragmentManager fragmentManager = getActivity().getFragmentManager();
                        Fragment currentFragment = fragmentManager.findFragmentByTag(
                                MainActivity.FRAGMENT_TAG);

                        Log.d(TAG, currentFragment.getClass().toString());
                        if (currentFragment instanceof NewMessagesTransitionFragment) {
                            Log.d(TAG, "currentFragment instanceof NewMessagesTransitionFragment");
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            NewMessagesFragment newMessagesFragment = new NewMessagesFragment();
                            PersonalMessage message = MainActivity.newMessagesList.get(0);
                            newMessagesFragment.contact = message.getAuthor();
                            newMessagesFragment.currentMessage = message;
                            transaction.replace(R.id.fragment_container, newMessagesFragment,
                                    MainActivity.FRAGMENT_TAG);
                            transaction.commit();
                            scheduledFuture.cancel(false);
                        }
                    } else {
                        if (!MainActivity.checkingNewEmails) {
                            TextView defaultText = (TextView) contentLayout.findViewById(R.id.default_text);
                            defaultText.setVisibility(View.GONE);
                            TextView responseText = (TextView) contentLayout.findViewById(R.id.response_text);
                            responseText.setText("No tienes nuevos mensajes.");
                            responseText.setVisibility(View.VISIBLE);
                            GifView gv = (GifView) contentLayout.findViewById(R.id.default_gif);
                            gv.setVisibility(View.GONE);
                            scheduledFuture.cancel(false);
                        }
                    }
                }
            };
        }
    }
}
