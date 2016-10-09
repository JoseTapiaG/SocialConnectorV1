package com.dimunoz.androidsocialconn.database;

import android.provider.BaseColumns;

public final class SocialConnectorContract {
    private SocialConnectorContract() {}

    /* Inner class that defines the table contents */
    public static class Photo implements BaseColumns {
        public static final String TABLE_NAME = "photo";
        public static final String CONTACT_NAME = "contact_name";
        public static final String CAPTION = "caption";
        public static final String EMAIL = "email";
        public static final String PATH = "path";
        public static final String SEEN = "seen";
        public static final String DATE = "date";
    }
}
