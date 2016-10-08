package com.dimunoz.androidsocialconn.photos;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dmunoz on 02-09-15.
 *
 */
public class Photo {


    private String id;
    private String caption = "";
    private Long creationDate;
    private String creationDateParsed;
    private String highResUrl;
    private String lowResUrl;
    private Boolean seen;

    public Photo(JSONObject element) {
        id = element.optString("id");
        JSONObject captionJSON = element.optJSONObject("caption");
        long creationDate = element.optLong("created") * 1000;
        this.creationDate = creationDate;
        creationDateParsed = parseDate(creationDate, true);
        JSONObject images = element.optJSONObject("images");
        JSONObject lowRes = images.optJSONObject("low_resolution");
        JSONObject highRes = images.optJSONObject("standard_resolution");
        highResUrl = highRes.optString("url");
        lowResUrl = lowRes.optString("url");
        seen = false;
    }

    public static Boolean isInList(Photo photo, ArrayList<Photo> list) {
        for (Photo p: list) {
            if (p.id.compareTo(photo.getId()) == 0)
                return true;
        }
        return false;
    }

    private String parseDate(long creationDate, boolean addParenthesis) {
        final String CAPTION_DATE_FORMAT = "MMMM d, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(CAPTION_DATE_FORMAT, Locale.getDefault());

        if (creationDate != 0) {
            if (addParenthesis) {
                return "(" + sdf.format(new Date(creationDate)) + ")";
            }
            return sdf.format(new Date(creationDate));
        }
        return "";
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public String getCreationDateParsed() {
        return creationDateParsed;
    }

    public String getHighResUrl() {
        return highResUrl;
    }

    public String getLowResUrl() {
        return lowResUrl;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
