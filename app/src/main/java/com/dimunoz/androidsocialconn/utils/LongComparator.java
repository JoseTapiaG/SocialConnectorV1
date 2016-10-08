package com.dimunoz.androidsocialconn.utils;

import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 18-01-14
 * Time: 09:50 PM
 * To change this template use File | Settings | File Templates.
 */

public class LongComparator implements Comparator<PersonalMessage> {
    @Override
    public int compare(PersonalMessage pm1, PersonalMessage pm2) {
        return pm2.getDatetime().compareTo(pm1.getDatetime());
    }
}
