package com.persistance;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.services.RecordingService;

import java.util.Calendar;
import java.util.List;

/**
 * Created by vito on 17-03-14.
 */
public class SocialconnApplication extends Application {
    private Context bsc;
    private boolean talkingBySkype;
    private String facebookUsername = null;
    private String userEmail="";

    public SocialconnApplication() {
        super();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        bsc = getBaseContext();
        setTalkingBySkype(false);
    }

    public boolean isTalkingBySkype() {
        return talkingBySkype;
    }

    public void setTalkingBySkype(boolean talkingBySkype) {
        this.talkingBySkype = talkingBySkype;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public String getFacebookUsername() {
        return facebookUsername;
    }

    public void setFacebookUsername(String facebookUsername){
        this.facebookUsername = facebookUsername;
    }

    public void startRecordingService() {
        Calendar cal = Calendar.getInstance();

        Intent intent = new Intent(this, RecordingService.class);
        intent.putExtra("userEmail", userEmail);
        //intent.putExtra("id", monitoredUserId);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager)getSystemService(this.ALARM_SERVICE);
        // Start every 30 seconds
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pintent);
        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 300*1000, pintent);
    }

    public void stopRecordingService() {
        Intent service = new Intent(this, RecordingService.class);
        stopService(service);

        Intent intentstop = new Intent(this, RecordingService.class);
        PendingIntent recordstop = PendingIntent.getService(this, 0, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager) this.getSystemService(ALARM_SERVICE);

        alarmManagerstop.cancel(recordstop);
    }

    public boolean isSkypeCalling(){
        ActivityManager activityManager = (ActivityManager)
                this.getSystemService( ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo appproc : procInfos){
            Log.d("process", appproc.processName);
            if(appproc.processName.equals("com.skype.raider")){
                return true;
            }
        }

        return false;
    }
}
