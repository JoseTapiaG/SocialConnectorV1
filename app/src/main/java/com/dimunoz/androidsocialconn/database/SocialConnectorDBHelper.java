package com.dimunoz.androidsocialconn.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.dimunoz.androidsocialconn.database.SocialConnectorContract.Photo;
/**
 * Created by JoseManuel on 27-09-2016.
 */

public class SocialConnectorDBHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATE";
    private static final String BOOLEAN_TYPE = " BOOLEAN";
    private static final String DATABASE_NAME = " SocialConnector.db";
    private static final int DATABASE_VERSION = 11;
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Photo.TABLE_NAME + " (" +
                    Photo._ID + " INTEGER PRIMARY KEY, " +
                    Photo.CONTACT_NAME + TEXT_TYPE + COMMA_SEP +
                    Photo.CAPTION + TEXT_TYPE + COMMA_SEP +
                    Photo.EMAIL + TEXT_TYPE + COMMA_SEP +
                    Photo.PATH + TEXT_TYPE + COMMA_SEP +
                    Photo.SEEN + BOOLEAN_TYPE + COMMA_SEP +
                    Photo.DATE + DATE_TYPE + " )";

    private static final String SQL_DELETE_PHOTOS =
            "DROP TABLE IF EXISTS " + Photo.TABLE_NAME;

    public SocialConnectorDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PHOTOS);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
