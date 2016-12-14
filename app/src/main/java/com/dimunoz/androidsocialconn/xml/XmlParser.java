package com.dimunoz.androidsocialconn.xml;

import android.os.Environment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class XmlParser  {

    public static ArrayList<XmlContact> parseContactsXml() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            File myFile = new File(Environment.getExternalStorageDirectory(), "SocialConnContacts.xml");

            InputStream in_s = new FileInputStream(myFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            return parseXML(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XmlContact parseOwnerXml() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            File myFile = new File(Environment.getExternalStorageDirectory(), "SocialConnContacts.xml");
            InputStream in_s = new FileInputStream(myFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            return getOwnerXML(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static XmlContact getOwnerXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        int eventType = parser.getEventType();
        XmlContact owner = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.compareTo("owner") == 0){
                        owner = new XmlContact();
                    } else if (owner != null){
                        if (name.compareTo("id") == 0) {
                            owner.setId(Integer.parseInt(parser.nextText()));
                        } else {
                            String text = parser.nextText();
                            if (name.compareTo("nickname") == 0){
                                owner.setNickname(text);
                            } else if (name.compareTo("photo") == 0){
                                owner.setPhoto(text);
                            } else if (name.compareTo("email") == 0){
                                owner.setEmail(text);
                            } else if (name.compareTo("instagram") == 0){
                                owner.setInstagram(text);
                            } else if (name.compareTo("skype") == 0){
                                owner.setSkype(text);
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    return owner;
            }
            eventType = parser.next();
        }
        return owner;
    }

    private static ArrayList<XmlContact> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        ArrayList<XmlContact> xmlContacts = new ArrayList<>();
        int eventType = parser.getEventType();
        XmlContact currentContact = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.compareTo("contact") == 0){
                        currentContact = new XmlContact();
                    } else if (currentContact != null){
                        if (name.compareTo("id") == 0) {
                            currentContact.setId(Integer.parseInt(parser.nextText()));
                        } else {
                            String text = parser.nextText();
                            if (name.compareTo("nickname") == 0){
                                currentContact.setNickname(text);
                            } else if (name.compareTo("photo") == 0){
                                currentContact.setPhoto(text);
                            } else if (name.compareTo("email") == 0){
                                currentContact.setEmail(text);
                            } else if (name.compareTo("instagram") == 0){
                                currentContact.setInstagram(text);
                            } else if (name.compareTo("skype") == 0){
                                currentContact.setSkype(text);
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("contact") && currentContact != null){
                        xmlContacts.add(currentContact);
                    }
            }
            eventType = parser.next();
        }
        return xmlContacts;
    }
}
