package com.dimunoz.androidsocialconn.database;

import java.util.Date;

/**
 * Created by JoseManuel on 27-09-2016.
 */

public class PhotoEntity {

    private long id;
    private String contactName;
    private String caption;
    private String email;
    private String path;
    private boolean seen;
    private String date;

    public PhotoEntity(String contactName, String caption, String email, String path, int seen, String date) {
        this.contactName = contactName;
        this.caption = caption;
        this.email = email;
        this.path = path;
        this.seen = seen != 0;
        this.date = date;
    }

    public String getContactName() {
        return contactName;
    }

    public String getCaption() {
        return caption;
    }

    public String getEmail() {
        return email;
    }

    public String getPath() {
        return path;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
