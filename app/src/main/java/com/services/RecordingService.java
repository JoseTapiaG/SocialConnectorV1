package com.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android_multipart_entity.multipart.FilePart;
import com.android_multipart_entity.multipart.MultipartEntity;
import com.audiotools.AudioRecordingTools;
import com.dimunoz.androidsocialconn.R;
import com.phototools.PhotoRecording;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vito on 18-04-14.
 */
public class RecordingService extends Service {

    private boolean RECORD = true;
    private String username;
    private int id;

    private int monitoringModeId = 2;
    public String path;

    private final String TAG = "RecordingService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //id = intent.getIntExtra("id",0);
        //username = intent.getStringExtra("username");
        //Log.i("mmmacer", "Recording voice");
        //if(RECORD){
        //recordVoice();
        //}
        username = intent.getStringExtra("userEmail");
        Log.i(TAG, "Checking maximum amplitude");
        int maximumAmplitudeAv = getMaximumAmplitude();
        Log.i(TAG, "average maximum amplitude: " + maximumAmplitudeAv);
        if (RECORD && isAmplitudeToRecord(maximumAmplitudeAv)) {
            Log.i("recordingservice", "Trying to Record");

            path = Long.toString(System.currentTimeMillis());
            Log.d(TAG,path);
            PhotoRecording photo = new PhotoRecording(path, username, monitoringModeId,getResources().getString(R.string.url_base));

            int camaraAvaible = photo.testCamera();
            if (camaraAvaible == 1) {
                Log.i(TAG, "Recording");

                photo.takeSnapShots();
                recordVoice();
            }



        }
        return Service.START_NOT_STICKY;
    }

    private boolean isAmplitudeToRecord(int maximumAmplitude) {
        if(isTablet(getApplicationContext()) && maximumAmplitude > 2000) {
            Log.d(TAG,"is tablet");
            return true;
        }
        else if(!isTablet(getApplicationContext()) && maximumAmplitude > 800) {
            Log.d(TAG,"is smartphone");
            return true;
        }
        return false;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private int getMaximumAmplitude(){
        MediaRecorder mRecorder;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile("/dev/null");

        List<Integer> maxs = new ArrayList<Integer>();

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
        Log.d(TAG, "recording");
        for (int i = 0; i < 10 ; i++) {

            int maxAmplitude = mRecorder.getMaxAmplitude();
            Log.d(TAG, maxAmplitude + "");
            maxs.add(maxAmplitude);

            /* Sleep 1 second */

            long endTime = System.currentTimeMillis() + 1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

        }

        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;

        return getAverageAmplitude(maxs);
    }

    private int getAverageAmplitude(List<Integer> maxs) {
        int sum = 0;
        int theMax = 0;
        for(Integer max: maxs){
            theMax = Math.max(theMax,max);
            sum += max;
        }

        sum -= theMax;

        return sum/(maxs.size()-1);
    }



    private void recordVoice() {
        AudioRecordingTools audio = new AudioRecordingTools("recording" + username + "_" + monitoringModeId,path);
        audio.startRecording();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audio.stopRecording();
        String audiofilePath = audio.getNombrewav();
        Log.d(TAG,audiofilePath);
        SendFilesTask task = new SendFilesTask();
        task.execute(new String[] { audiofilePath });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SendFilesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... audiofilePath) {
            String response = null;
            sendFileToServer(audiofilePath[0]);
            return response;
        }

        private void sendFileToServer(String path) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getResources().getString(R.string.url_base)+"emo_status/index.php?r=recording/upload");

            try {
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                MultipartEntity mpEntity = new MultipartEntity();
                File audio = new File(path);
                mpEntity.addPart(new FilePart("Recording", audio, path, "audio/x-wav"));
                httppost.setEntity(mpEntity);

                HttpResponse response = httpclient.execute(httppost);
                Log.i(TAG,inputStreamToString(response.getEntity().getContent()).toString());
                File file = new File(path);
                boolean deleted = file.delete();
                Log.i(TAG,"Deberia haberse borrado el archivo de audio");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG,"Task done");
        }

        public StringBuilder inputStreamToString(InputStream is) {
            String line;
            StringBuilder total = new StringBuilder();

            // Wrap a BufferedReader around the InputStream
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            // Read response until the end
            try {
                while ((line = rd.readLine()) != null) {
                    total.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Return full string
            return total;
        }
    }
}
