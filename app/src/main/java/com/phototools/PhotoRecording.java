package com.phototools;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.android_multipart_entity.multipart.FilePart;
import com.android_multipart_entity.multipart.MultipartEntity;
import com.dimunoz.androidsocialconn.R;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoRecording  {

    private  String TAG ="PhotoRecording";
    Camera camera;
    public String id;
    public  int monitoringModeId;
    public String path;
    private static final String PHOTO_RECORDER_FILE_EXT_WAV = ".jpg";
    private static final String PHOTO_RECORDER_FOLDER = "Emo_record";
    public String urlServer;
    public PhotoRecording(String pathA,String idA, int monitoringModeIdA, String urlBaseServer){

        Log.d(TAG, "idA: " +idA );
        id=idA;
        monitoringModeId=monitoringModeIdA;
        path=pathA;
        urlServer=urlBaseServer;
    }

    public int testCamera() {
        int result=1;
        int cameraId;
        Log.d(TAG, "Checking if camera is available" );
        cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            result=0;
            Log.d(TAG, "No front facing camera found." );
        } else {
            try {
                camera = Camera.open(cameraId);
                camera.stopPreview();
                camera.release();
                camera = null;
            }catch (Exception e)
            {
                Log.d(TAG, "The camera is being used" );

                result=0;
            }
        }

        return result;
    }
    public void takeSnapShots()
    {
        int cameraId;
        Log.d(TAG, "Image snapshot   Started" );


        // here below "this" is activity context.

        cameraId = findFrontFacingCamera();
        if (cameraId < 0) {
            Log.d(TAG, "No front facing camera found." );
        } else {
            camera = Camera.open(cameraId);

            try {
                SurfaceTexture surfaceTexture = new SurfaceTexture(10);
                camera.setPreviewTexture(surfaceTexture);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            camera.startPreview();
            Log.d(TAG, "going to take a picture" );
            camera.takePicture(null,null,jpegCallback);
        }

    }


    /** picture call back */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            FileOutputStream outStream = null;
            try {
                Log.d(TAG, "starting callback" );
                //File pictureFile = getOutputMediaFile();
                String photoFilePath=getFilename(""+id);
                outStream = new FileOutputStream(photoFilePath);
                outStream.write(data);
                outStream.close();
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
                //Send File to Server
                Log.d(TAG, "Sending picture to server");
                String filePath="recording"+id+"_"+monitoringModeId+"_"+path+".jpg" ;
                //sendFileToServer(pictureFile,filePath);
                //SendFilesTask task = new SendFilesTask();
                //task.execute(new String[] { photoFilePath ,filePath });

                //todo descomentar
                //new FaceCrop().crop(photoFilePath, urlServer, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                camera.stopPreview();
                camera.release();
                camera = null;
                Log.d(TAG, "Image snapshot Done");
            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    /*private void sendFileToServer(File photo,String path) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://10.0.115.49/emo_status/index.php?r=recording/upload");

        try {
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            MultipartEntity mpEntity = new MultipartEntity();

            mpEntity.addPart(new FilePart("Recording", photo, path, "image/jpg"));
            httppost.setEntity(mpEntity);
            Log.i("sending","Sending file+ "+path);
            HttpResponse response = httpclient.execute(httppost);
            Log.i("responsePost1",inputStreamToString(response.getEntity().getContent()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "RecordingPhotos");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("PruebaCamera", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
*/
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("MAIN", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    private String getFilename(String id){
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath,PHOTO_RECORDER_FOLDER);

        if(!file.exists()){
            file.mkdirs();
        }
        //path=Long.toString(System.currentTimeMillis());
        return (file.getAbsolutePath() + "/recording"+id+"_"+monitoringModeId+"_"+path + PHOTO_RECORDER_FILE_EXT_WAV);

    }



}