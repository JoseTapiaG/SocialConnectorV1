package com.dimunoz.androidsocialconn.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dimunoz.androidsocialconn.database.PhotoDB;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class PhotoService {

    private PhotoDB photoDB;

    public PhotoService(Context context) {
        photoDB = new PhotoDB(context);
    }

    public List<PhotoEntity> getPhotos(String email){
        return photoDB.getPhotosByEmail(email);
    }

    public List<PhotoEntity> getNewPhotos(){
        return photoDB.getNewPhotos();
    }

    public List<PhotoEntity> getLastTenPhotos(){
        return photoDB.getLastTenPhotos();
    }

    public long savePhoto(PersonalMessage pm, MimeBodyPart part, String localFileName){
        try {
            File localFile = new File(localFileName);
            part.saveFile(localFile);

            PhotoEntity photoEntity = new PhotoEntity(pm.getAuthor().getNickname(), pm.getContent(), pm.getAuthor().getEmail(), localFileName, 0, new Date().toString());
            return photoDB.insertPhoto(photoEntity);

        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public Bitmap getPhoto(String path){
        File imgFile = new  File(path);
        if(imgFile.exists())
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        return null;
    }

    public void updatePhoto(PhotoEntity photo) {
        photoDB.updatePhoto(photo);
    }
}
