package com.dimunoz.androidsocialconn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.dimunoz.androidsocialconn.database.SocialConnectorContract.Photo;

/**
 * Created by JoseManuel on 27-09-2016.
 */

public class PhotoDB {

    private SocialConnectorDBHelper mDbHelper;

    public PhotoDB(Context context) {
        this.mDbHelper = new SocialConnectorDBHelper(context);
    }

    public long insertPhoto(PhotoEntity photo) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Photo.CONTACT_NAME, photo.getContactName());
        values.put(Photo.CAPTION, photo.getCaption());
        values.put(Photo.EMAIL, photo.getEmail());
        values.put(Photo.PATH, photo.getPath());
        values.put(Photo.SEEN, photo.isSeen());
        values.put(Photo.DATE, String.valueOf(photo.getDate()));
        long row = db.insert(Photo.TABLE_NAME, null, values);
        db.close();
        return row;
    }

    public List<PhotoEntity> getPhotosByEmail(String email) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                Photo._ID,
                Photo.CONTACT_NAME,
                Photo.CAPTION,
                Photo.EMAIL,
                Photo.PATH,
                Photo.SEEN,
                Photo.DATE
        };

// Filter results WHERE "title" = 'My Title'
        String selection = Photo.EMAIL + " = ?";
        String[] selectionArgs = {email};

        String sortOrder =
                Photo.DATE + " DESC";

        Cursor c = db.query(
                Photo.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        ArrayList<PhotoEntity> photos = new ArrayList<>();
        while (!c.isAfterLast()){
            PhotoEntity photoEntity = new PhotoEntity(
                    c.getString(c.getColumnIndexOrThrow(Photo.CONTACT_NAME)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)),
                    c.getString(c.getColumnIndexOrThrow(Photo.EMAIL)),
                    c.getString(c.getColumnIndexOrThrow(Photo.PATH)),
                    c.getInt(c.getColumnIndexOrThrow(Photo.SEEN)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)));
            photoEntity.setId(c.getColumnIndexOrThrow(Photo._ID));
            photos.add(photoEntity);
            c.moveToNext();
        }
        db.close();
        return photos;
    }

    public List<PhotoEntity> getNewPhotos() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = Photo.SEEN + " = ?";
        String[] selectionArgs = {"0"};

        String sortOrder =
                Photo.DATE + " DESC";

        Cursor c = db.query(
                Photo.TABLE_NAME,                     // The table to query
                null,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        c.moveToFirst();
        ArrayList<PhotoEntity> photos = new ArrayList<>();
        while (!c.isAfterLast()){
            PhotoEntity photoEntity = new PhotoEntity(
                    c.getString(c.getColumnIndexOrThrow(Photo.CONTACT_NAME)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)),
                    c.getString(c.getColumnIndexOrThrow(Photo.EMAIL)),
                    c.getString(c.getColumnIndexOrThrow(Photo.PATH)),
                    c.getInt(c.getColumnIndexOrThrow(Photo.SEEN)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)));
            photoEntity.setId(c.getInt(c.getColumnIndexOrThrow(Photo._ID)));
            photos.add(photoEntity);
            c.moveToNext();
        }
        db.close();
        return photos;
    }

    public void updatePhoto(PhotoEntity photo) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Photo.CONTACT_NAME, photo.getContactName());
        values.put(Photo.CAPTION, photo.getCaption());
        values.put(Photo.EMAIL, photo.getEmail());
        values.put(Photo.PATH, photo.getPath());
        values.put(Photo.SEEN, 1);
        values.put(Photo.DATE, String.valueOf(photo.getDate()));

        String where = Photo._ID + " = ?";
        String[] whereArgs = {photo.getId() + ""};

        db.update(Photo.TABLE_NAME, values, where, whereArgs);
        db.close();
    }

    public List<PhotoEntity> getLastTenPhotos() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("Select * from " + Photo.TABLE_NAME + " DESC limit 10", null);

        c.moveToFirst();
        ArrayList<PhotoEntity> photos = new ArrayList<>();
        while (!c.isAfterLast()){
            PhotoEntity photoEntity = new PhotoEntity(
                    c.getString(c.getColumnIndexOrThrow(Photo.CONTACT_NAME)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)),
                    c.getString(c.getColumnIndexOrThrow(Photo.EMAIL)),
                    c.getString(c.getColumnIndexOrThrow(Photo.PATH)),
                    c.getInt(c.getColumnIndexOrThrow(Photo.SEEN)),
                    c.getString(c.getColumnIndexOrThrow(Photo.CAPTION)));
            photoEntity.setId(c.getInt(c.getColumnIndexOrThrow(Photo._ID)));
            photos.add(photoEntity);
            c.moveToNext();
        }
        db.close();
        return photos;
    }
}
