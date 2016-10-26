package com.dimunoz.androidsocialconn.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.dimunoz.androidsocialconn.database.PhotoDB;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.utils.Utils;
import com.dimunoz.androidsocialconn.xml.XmlContact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import static com.dimunoz.androidsocialconn.main.MainActivity.photoService;

public class PhotoService {

    private PhotoDB photoDB;

    public PhotoService(Context context) {
        photoDB = new PhotoDB(context);
    }

    public List<PhotoEntity> getPhotos(String email) {
        return photoDB.getPhotosByEmail(email);
    }

    public List<PhotoEntity> getNewPhotos() {
        return photoDB.getNewPhotos();
    }

    public List<PhotoEntity> getLastTenPhotos() {
        return photoDB.getLastTenPhotos();
    }

    public long savePhoto(PersonalMessage pm, MimeBodyPart part, String localFileName) {
        try {
            File localFile = new File(localFileName);
            part.saveFile(localFile);

            PhotoEntity photoEntity = new PhotoEntity(pm.getAuthor().getNickname(), pm.getContent(), pm.getAuthor().getEmail(), localFileName, 0, new Date().toString());
            return photoDB.insertPhoto(photoEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long savePhoto(PhotoEntity photoEntity) {
        return photoDB.insertPhoto(photoEntity);
    }

    public Bitmap getPhoto(String path) {
        File imgFile = new File(path);
        if (imgFile.exists())
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        return null;
    }

    public void updatePhoto(PhotoEntity photo) {
        photoDB.updatePhoto(photo);
    }

    public void resetDatabase(final Activity activity){
        photoDB.resetDB();
        MainActivity.loadBD();
        MainActivity.newPhotosList = (ArrayList<PhotoEntity>) getNewPhotos();
        Utils.changeBadgeNewPhotosText(activity);


        MainActivity.newMessagesList.clear();

        PersonalMessage pm = new PersonalMessage();
        pm.setAuthor(new XmlContact(1, "test5@demo.com", "asd", "Andrés", null, "asd"));
        pm.setContent("Mira lo que te perdiste");
        pm.setDatetime(new Date());
        pm.setSeen(false);


        String filepath = Environment.getExternalStorageDirectory().getPath()
                + "/EmailImages/14.jpg";
        File image = new File(filepath);

        pm.setImageFile(image);
        pm.setHasAttachedImage(true);

        PersonalMessage pm1 = new PersonalMessage();
        pm1.setAuthor(new XmlContact(1, "test6@demo.com", "asd", "Pamela", null, "asd"));
        pm1.setContent("Saludos desde Roma");
        pm1.setDatetime(new Date());
        pm1.setSeen(false);

        filepath = Environment.getExternalStorageDirectory().getPath()
                + "/EmailImages/15.jpg";
        File image1 = new File(filepath);

        pm1.setImageFile(image1);
        pm1.setHasAttachedImage(true);

        MainActivity.newMessagesList.add(pm);
        MainActivity.newMessagesList.add(pm1);
        MainActivity.setNewMessagesCounter(MainActivity.newMessagesList.size());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.changeBadgeNewMessagesText(activity);
            }
        });
    }
}
