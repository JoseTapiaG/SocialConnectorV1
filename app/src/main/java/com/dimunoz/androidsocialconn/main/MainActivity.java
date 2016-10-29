package com.dimunoz.androidsocialconn.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.asynctasks.CreateImapStore;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.photos.AlbumContactsFragment;
import com.dimunoz.androidsocialconn.photos.AlbumPhotoFragment;
import com.dimunoz.androidsocialconn.photos.NewPhotosFragment;
import com.dimunoz.androidsocialconn.receivemessages.NewMessagesFragment;
import com.dimunoz.androidsocialconn.receivemessages.NewMessagesTransitionFragment;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.sendmessage.SendMessageFragment;
import com.dimunoz.androidsocialconn.service.CheckNewData;
import com.dimunoz.androidsocialconn.service.PhotoService;
import com.dimunoz.androidsocialconn.service.SendLogs;
import com.dimunoz.androidsocialconn.utils.TlatoquePauseHandler;
import com.dimunoz.androidsocialconn.utils.Utils;
import com.dimunoz.androidsocialconn.videocall.CallFragment;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.dimunoz.androidsocialconn.xml.XmlParser;
import com.persistance.SocialconnApplication;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


//import org.opencv.android.OpenCVLoader;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // Fragment Tag
    public final static String FRAGMENT_TAG = "MyContentFragment";

    // Contacts
    public static ArrayList<XmlContact> xmlContacts;
    public static XmlContact userContact;

    // calls
    public static XmlContact calledContact;

    // email settings
    public static String oauthToken;
    public static String emailAccount;

    // private messages
    public static IMAPStore imapStore;
    public static ArrayList<PersonalMessage> newMessagesList;
    public static boolean messagesDownloaded = false;
    public static Integer newMessagesCounter = 0;
    private static AccountManager accountManager;

    // SharedPreferences
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    // new photos
    public static ArrayList<PhotoEntity> newPhotosList;
    public static boolean isCheckingNewPhotos = false;

    // album photos
    public static ArrayList<PhotoEntity> albumPhotosList;
    public static XmlContact currentAlbumUser = new XmlContact();

    // booleans for inactivity and night time
    public static boolean nightMode = false;
    public static boolean pauseHandlerActive = false;
    public static boolean hasChangedNightMode = false;

    // service for checking new contents
    public static TlatoquePauseHandler tlatoquePauseHandler;
    public static ScheduledFuture scheduledFuture;
    public static boolean checkingNewEmails = false;

    // progress dialog
    public static ProgressDialog progressDialog;

    // pause activity
    public static PauseActivity pauseActivity;

    // Photo Service
    public static PhotoService photoService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main_menu);

        //OpenCV Loader
        /*if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }*/

        // set lists
        newPhotosList = new ArrayList<>();
        albumPhotosList = new ArrayList<>();

        // set progress dialog settings
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        // start inactivity and night mode handler
        tlatoquePauseHandler = new TlatoquePauseHandler(this);

        // initial check if night time
        Utils.checkIfNightTime();

        // keep screen on
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.changeScreenBrightness(1F, this);

        writeTempFile();
        System.out.println(Environment.getExternalStorageDirectory());
        // set contacts from xml file
        xmlContacts = XmlParser.parseContactsXml();
        userContact = XmlParser.parseOwnerXml();
        calledContact = userContact;

        // initial variable of private messages
        newMessagesList = new ArrayList<>();

        settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        photoService = new PhotoService(this);

        // connect to email
        accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        onAccountSelected(accounts[0]);
        //cuando inicia la aplicacion tambien debe iniciar el servicio que toma imagenes
        SocialconnApplication app = (SocialconnApplication) getApplicationContext();
        //app.startRecordingService();

        // AudioManager by Victor
        // edited by Diego Munoz to log Skype Calls
        AudioManager myAudioManager;
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    Log.d(TAG, "comienza a sonar llamada o se ejecuta");
                    Calendar cal = Calendar.getInstance();
                    editor = settings.edit();
                    editor.putLong(getApplicationContext().getString(R.string.skype_begin_call),
                            cal.getTimeInMillis());
                    editor.apply();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    Log.d(TAG, "se termina llamada");
                    Calendar cal = Calendar.getInstance();
                    Long skype_end_call = cal.getTimeInMillis();
                    Long skype_begin_call = settings.getLong(
                            getApplicationContext().getString(R.string.skype_begin_call),
                            -1);
                    if (calledContact == userContact) {
                        Utils.logCallEvent("someone", userContact.getEmail(),
                                skype_begin_call, skype_end_call);
                    } else {
                        Utils.logCallEvent(userContact.getEmail(), calledContact.getEmail(),
                                skype_begin_call, skype_end_call);
                    }
                    calledContact = userContact;
                    //moveAppToFront();

                    SocialconnApplication app = (SocialconnApplication) getApplicationContext();
                    if (app.isTalkingBySkype()) {
                        app.startRecordingService();
                        app.setTalkingBySkype(false);
                        Log.d(TAG, "Skype call has stopped, recording service stopped too");
                    }
                    // Resume playback
                } else {
                    //moveAppToFront();
                    Log.d(TAG, "cualquier otro evento");
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        // first start check new emails
        startCheckNewDataService();
        checkingNewEmails = true;

        // service for sending logs
        startSendDailyLogsService();

        // default fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ContentFragment contentFragment = new ContentFragment();
        transaction.replace(R.id.fragment_container, contentFragment, FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tlatoquePauseHandler.stopPauseTimer();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
        Utils.changeScreenBrightness(1F, this);
        tlatoquePauseHandler.restartPauseTimer();
        if (!checkingNewEmails) {
            startCheckNewDataService();
            checkingNewEmails = true;
        }
    }

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Utils.changeScreenBrightness(1F, this);
        tlatoquePauseHandler.restartPauseTimer();
    }

    private void logAlbumPhoto(Fragment fragment) {
        AlbumPhotoFragment photoFragment = (AlbumPhotoFragment) fragment;
        Long photo_begin_see = settings.getLong(
                getApplicationContext().getString(R.string.album_photo_begin_see),
                -1);
        Calendar cal = Calendar.getInstance();
        Utils.logSeeAlbumPhotoEvent(photoFragment.currentPhoto.getId() + "", photo_begin_see, cal.getTimeInMillis());
    }

    private void logNewPhoto(Fragment fragment) {
        NewPhotosFragment photosFragment = (NewPhotosFragment) fragment;
        Long photo_begin_see = settings.getLong(
                getApplicationContext().getString(R.string.new_photo_begin_see),
                -1);
        Calendar cal = Calendar.getInstance();
        //TODO cambiar fecha
        Utils.logSeeNewPhotoEvent(photosFragment.currentPhoto.getId() + "", 0L,
                photo_begin_see, cal.getTimeInMillis());
    }

    private void logNewPhotosEmpty() {
        Long photo_begin_see = settings.getLong(
                getApplicationContext().getString(R.string.new_photos_empty_begin_see),
                -1);
        Calendar cal = Calendar.getInstance();
        Utils.logNewPhotosEmpty(photo_begin_see, cal.getTimeInMillis());
    }

    public void displayCallFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (!(currentFragment instanceof CallFragment)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            CallFragment callFragment = new CallFragment();
            transaction.replace(R.id.fragment_container, callFragment, FRAGMENT_TAG);
            transaction.commit();
            if (currentFragment instanceof NewPhotosFragment) {
                if (!newPhotosList.isEmpty()) {
                    logNewPhoto(currentFragment);
                    checkSeenNewPhotos();
                } else
                    logNewPhotosEmpty();
            } else if (currentFragment instanceof AlbumPhotoFragment) {
                logAlbumPhoto(currentFragment);
            }
        } else {
            ((CallFragment) currentFragment).displayContacts(0);
        }
    }

    public void displaySendMessageFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (!(currentFragment instanceof SendMessageFragment)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            SendMessageFragment sendMessageFragment = new SendMessageFragment();
            transaction.replace(R.id.fragment_container, sendMessageFragment, FRAGMENT_TAG);
            transaction.commit();
            if (currentFragment instanceof NewPhotosFragment) {
                if (!newPhotosList.isEmpty()) {
                    logNewPhoto(currentFragment);
                    checkSeenNewPhotos();
                } else
                    logNewPhotosEmpty();
            } else if (currentFragment instanceof AlbumPhotoFragment) {
                logAlbumPhoto(currentFragment);
            }
        }
    }

    public void displayNewMessagesFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (!(currentFragment instanceof NewMessagesFragment)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            NewMessagesTransitionFragment newMessagesFragment = new NewMessagesTransitionFragment();
            transaction.replace(R.id.fragment_container, newMessagesFragment, FRAGMENT_TAG);
            transaction.commit();
            if (currentFragment instanceof NewPhotosFragment) {
                if (!newPhotosList.isEmpty()) {
                    logNewPhoto(currentFragment);
                    checkSeenNewPhotos();
                } else
                    logNewPhotosEmpty();
            } else if (currentFragment instanceof AlbumPhotoFragment) {
                logAlbumPhoto(currentFragment);
            }
        }
    }

    public void displayNewPhotosFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (!(currentFragment instanceof NewPhotosFragment)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            NewPhotosFragment newPhotosFragment = new NewPhotosFragment();
            transaction.replace(R.id.fragment_container, newPhotosFragment, FRAGMENT_TAG);
            transaction.commit();
            if (currentFragment instanceof AlbumPhotoFragment) {
                logAlbumPhoto(currentFragment);
            }
        }

    }

    public void displayAlbumFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (!(currentFragment instanceof AlbumContactsFragment)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            AlbumContactsFragment albumContactsFragment = new AlbumContactsFragment();
            transaction.replace(R.id.fragment_container, albumContactsFragment, FRAGMENT_TAG);
            transaction.commit();
            if (currentFragment instanceof NewPhotosFragment) {
                if (!newPhotosList.isEmpty()) {
                    logNewPhoto(currentFragment);
                    checkSeenNewPhotos();
                } else
                    logNewPhotosEmpty();
            } else if (currentFragment instanceof AlbumPhotoFragment) {
                logAlbumPhoto(currentFragment);
            }
        }
    }

    private void checkSeenNewPhotos() {
        MainActivity.newPhotosList = (ArrayList<PhotoEntity>) photoService.getNewPhotos();
        Utils.changeBadgeNewPhotosText(this);
    }

    public static void setNewMessagesCounter(Integer newMessages) {
        newMessagesCounter = newMessages;

    }

    private void onAccountSelected(final Account account) {
        MainActivity.userContact.setEmail(account.name);
        SocialconnApplication app = (SocialconnApplication) getApplicationContext();
        app.setUserEmail(account.name);
        accountManager.getAuthToken(account, "oauth2:https://mail.google.com/", null, this,
                new OnTokenAcquired(account), null);
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        private Account account;

        public OnTokenAcquired(Account account) {
            this.account = account;
        }

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();
                oauthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                emailAccount = account.name;
                new CreateImapStore(MainActivity.this, emailAccount, oauthToken).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startSendDailyLogsService() {
        ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
        scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
        SendLogs myTask = new SendLogs(this);
        scheduledFuture = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                myTask.getRunnable(), 0, 1, TimeUnit.HOURS);
    }

    public void startCheckNewDataService() {
        ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
        scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
        CheckNewData myTask = new CheckNewData(this);
        scheduledFuture = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                myTask.getRunnable(), 0, 5, TimeUnit.MINUTES);
    }

    public void writeTempFile() {
        File myFile = new File(Environment.getExternalStorageDirectory(), "SocialConnContacts.xml");
        if (!myFile.exists()) {
            try {
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            /*myOutWriter.append("<?xml version=\"1.0\"?>" +
                    "<contacts>" +
                    "   <contact>" +
                    "       <id>1</id>" +
                    "       <nickname>Camila</nickname>" +
                    "       <photo></photo>" +
                    "       <email>dmunozs86@gmail.com</email>" +
                    "       <skype>diego.munoz.saez</skype>" +
                    "       <instagram>hitgirlhit</instagram>" +
                    "   </contact>" +
                    "   <contact>" +
                    "       <id>2</id>" +
                    "       <nickname>Chileanskies</nickname>" +
                    "       <photo></photo>" +
                    "       <email>acmo42@yahoo.com</email>" +
                    "       <skype>diego.munoz.saez</skype>" +
                    "       <instagram>chileanskies</instagram>" +
                    "   </contact>" +
                    "   <contact>" +
                    "       <id>3</id>" +
                    "       <nickname>Erick</nickname>" +
                    "       <photo></photo>" +
                    "       <skype>diego.munoz.saez</skype>" +
                    "       <email>tbarrazaa@gmail.com</email>" +
                    "       <instagram>sanguxe</instagram>" +
                    "   </contact>" +
                    "</contacts>");*/
                myOutWriter.append("<?xml version=\"1.0\"?>" +
                        "<owners>" +
                        "   <owner>" +
                        "       <id>0</id>" +
                        "       <nickname>Jose Manuel</nickname>" +
                        "       <photo></photo>" +
                        "       <email>jose.wt@gmail.com</email>" +
                        "       <skype></skype>" +
                        "       <instagram></instagram>" +
                        "   </owner>" +
                        "</owners>" +
                        "<contacts>" +
                        "   <contact><id>0</id><nickname>Jose</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>jose.wt@gmail.com</email><skype></skype><instagram>sochoa2525</instagram></contact>" +
                        "   <contact><id>1</id><nickname>Sergio</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>sochoa@dcc.uchile.cl</email><skype>sergio.ochoa51</skype><instagram>sochoa2525</instagram></contact>" +
                        "   <contact><id>2</id><nickname>Carla</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>test@gmail.com</email><skype>carla.sambrizzi</skype><instagram></instagram></contact>" +
                        "   <contact><id>3</id><nickname>Juanma</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>test@ug.uchile.cl</email><skype>juanma8a</skype><instagram></instagram></contact>" +
//                    "   <contact><id>4</id><nickname>Rocío</nickname><photo></photo><email>rocio8aastorga@hotmail.com</email><skype>rocio.ochoa.astorga</skype><instagram>ro.ochoaastorga</instagram></contact>" +
//                    "   <contact><id>5</id><nickname>Daniel</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>daniel-ochoa@hotmail.com</email><skype>danielochoa66</skype><instagram></instagram></contact>" +
//                    "   <contact><id>6</id><nickname>Pablo</nickname><photo>http://a5.mzstatic.com/us/r30/Purple5/v4/5a/2e/e9/5a2ee9b3-8f0e-4f8b-4043-dd3e3ea29766/icon128-2x.png</photo><email>pablo_8a@live.com</email><skype>pabloj.8a</skype><instagram></instagram></contact>" +
                        "</contacts>");
                myOutWriter.close();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void moveAppToFront() {
        Log.d(TAG, "moveAppToFront");
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo recentTask : recentTasks) {
            Log.d("Executed app", "Application executed : "
                    + recentTask.baseActivity.toShortString()
                    + "\t\t ID: " + recentTask.id + "");
            // bring to front
            if (recentTask.baseActivity.toShortString().contains("com.dimunoz.androidsocialconn.main.MainActivity")) {
                activityManager.moveTaskToFront(recentTask.id, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }
}
