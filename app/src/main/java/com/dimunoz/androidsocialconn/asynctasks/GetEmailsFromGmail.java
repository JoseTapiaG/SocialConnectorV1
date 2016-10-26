package com.dimunoz.androidsocialconn.asynctasks;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.receivemessages.NewMessagesFragment;
import com.dimunoz.androidsocialconn.receivemessages.NewMessagesTransitionFragment;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.utils.LongComparator;
import com.dimunoz.androidsocialconn.utils.Utils;
import com.dimunoz.androidsocialconn.views.GifView;
import com.dimunoz.androidsocialconn.xml.XmlContact;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 08-01-14
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetEmailsFromGmail extends AsyncTask<Void, Void, Void> {

    private String TAG = "GetEmailsFromGmail";
    private MainActivity activity;
    private ArrayList<PersonalMessage> localMessagesArray;
    private ArrayList<String> mailList;
    private Date dateBefore;
    private Integer newMessagesCounter;
    private ScheduledFuture scheduledFuture;

    public GetEmailsFromGmail(MainActivity activity, Date... date) {
        this.activity = activity;
        this.mailList = new ArrayList<>();
        Date d = new Date();
        this.dateBefore = date.length > 0 ? date[0] : new Date(d.getTime() - 7 * 24 * 3600 * 1000);
        this.newMessagesCounter = 0;
    }

    protected void onPreExecute() {
        for (XmlContact contact : MainActivity.xmlContacts) {
            mailList.add(contact.getEmail());
        }
        MainActivity.checkingNewEmails = true;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        /*try {
            Log.d(TAG, "Inicio " + DateFormat.getDateTimeInstance().format(new Date()));
            localMessagesArray = new ArrayList<>();
            Folder received = MainActivity.imapStore.getFolder("INBOX");
            addMessagesToLocalArray(received);

            ScheduledExecutorService scheduleCheckMessagesTaskExecutor;
            scheduleCheckMessagesTaskExecutor = Executors.newScheduledThreadPool(5);
            scheduledFuture = scheduleCheckMessagesTaskExecutor.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run");
                            FragmentManager fragmentManager = activity.getFragmentManager();
                            final Fragment currentFragment = fragmentManager.findFragmentByTag(MainActivity.FRAGMENT_TAG);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "runOnUiThread");
                                    Collections.sort(localMessagesArray, new LongComparator());
                                    MainActivity.newMessagesList = localMessagesArray;
                                    if (currentFragment instanceof NewMessagesTransitionFragment) {
                                        if (!MainActivity.newMessagesList.isEmpty()) {
                                            FragmentManager fragmentManager = activity.getFragmentManager();
                                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                                            NewMessagesFragment newMessagesFragment = new NewMessagesFragment();
                                            PersonalMessage message = MainActivity.newMessagesList.get(0);
                                            newMessagesFragment.contact = message.getAuthor();
                                            newMessagesFragment.currentMessage = message;
                                            transaction.replace(R.id.fragment_container, newMessagesFragment,
                                                    MainActivity.FRAGMENT_TAG);
                                            transaction.commit();
                                            Utils.changeBadgeNewMessagesText(activity);
                                        } else {
                                            TextView tv = (TextView) currentFragment.getActivity().findViewById(R.id.default_text);
                                            tv.setText("No tienes nuevos mensajes.");
                                            GifView gv = (GifView) currentFragment.getActivity().findViewById(R.id.default_gif);
                                            gv.setVisibility(View.INVISIBLE);
                                        }
                                        MainActivity.messagesDownloaded = true;
                                        MainActivity.checkingNewEmails = false;
                                        cancelScheduledFuture();
                                    } else if (!(currentFragment instanceof NewMessagesFragment)) {
                                        Utils.changeBadgeNewMessagesText(activity);
                                        MainActivity.messagesDownloaded = true;
                                        MainActivity.checkingNewEmails = false;
                                        cancelScheduledFuture();
                                    }
                                }
                            });
                        }
                    }, 0, 10, TimeUnit.SECONDS);
            Log.d(TAG, "Fin " + DateFormat.getDateTimeInstance().format(new Date()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;*/

        if (localMessagesArray == null)
            localMessagesArray = new ArrayList<>();

        localMessagesArray.clear();

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

        localMessagesArray.add(pm);
        localMessagesArray.add(pm1);



        MainActivity.newMessagesList = localMessagesArray;

        MainActivity.setNewMessagesCounter(localMessagesArray.size());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.changeBadgeNewMessagesText(activity);
            }
        });
        return null;
    }

    private void cancelScheduledFuture() {
        scheduledFuture.cancel(false);
        Log.d(TAG, "scheduledFuture.cancel(false);");
    }

    private void addMessagesToLocalArray(Folder folder) {
        try {
            // get messages between now and one week before
            folder.open(Folder.READ_ONLY);
            Date d = new Date();
            this.dateBefore = new Date(d.getTime() - 7 * 24 * 3600 * 1000);
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, dateBefore);
            SearchTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            SearchTerm dateSeenTerm = new AndTerm(newerThan, flagTerm);

            ArrayList<String> listaDirecciones = new ArrayList();
            for (XmlContact contact : MainActivity.xmlContacts) {
                listaDirecciones.add(contact.getEmail());
            }

            SearchTerm orTerm = new OrTerm(new FromStringTerm(listaDirecciones.get(0)), new FromStringTerm(listaDirecciones.get(1)));
            for (int i = 2; i < listaDirecciones.size(); i++) {
                orTerm = new OrTerm(orTerm, new FromStringTerm(listaDirecciones.get(i)));
            }

            SearchTerm andTerm = new AndTerm(dateSeenTerm, orTerm);

            Message[] folderMessages = folder.search(andTerm);

            Collections.reverse(Arrays.asList(folderMessages));
            boolean readNewestInboxMessage = false;

            FetchProfile fetchProfile = new FetchProfile();
            fetchProfile.add(FetchProfile.Item.ENVELOPE);
            fetchProfile.add(FetchProfile.Item.FLAGS);
            fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
            fetchProfile.add("X-mailer");
            folder.fetch(folderMessages, fetchProfile);

            for (Message message : folderMessages) {
                if (message.getSentDate().before(this.dateBefore)) {
                    continue;
                }
                int i;
                XmlContact contact;
                Address[] theOtherContact;
                theOtherContact = message.getFrom();

                if (theOtherContact != null && theOtherContact.length > 0) {
                    InternetAddress contactInternetAddress = (InternetAddress) theOtherContact[0];
                    String theOtherEmail = contactInternetAddress.getAddress();
                    i = mailList.indexOf(theOtherEmail);
                    contact = MainActivity.xmlContacts.get(i);
                    PersonalMessage pm = new PersonalMessage();
                    pm.setAuthor(contact);
                    pm.setContent(getText(message));
                    pm.setDatetime(message.getSentDate());
                    pm.setSeen(false);
                    checkImageAttachments(pm, message);
                    if (!readNewestInboxMessage) {
                        readNewestInboxMessage = true;
                    }
                    localMessagesArray.add(pm);
                    newMessagesCounter++;
                }
            }
            folder.close(false);
            MainActivity.setNewMessagesCounter(newMessagesCounter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkImageAttachments(PersonalMessage pm, Message message) throws MessagingException {
        try {
            pm.setHasAttachedAudio(false);
            pm.setHasAttachedImage(false);
            pm.setHasAttachedVideo(false);
            if (message.isMimeType("multipart/mixed")) {
                Multipart multipart = (Multipart) message.getContent();
                if (multipart.getCount() > 1) {
                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        Log.d(TAG, "disposition " + part.getDisposition());
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String filepath = Environment.getExternalStorageDirectory().getPath()
                                    + "/EmailImages";
                            File folder = new File(filepath);

                            // create folder if does not exist
                            if (!folder.exists()) {
                                Boolean b = folder.mkdirs();
                                Log.d(TAG, "email images folder created " + b);
                            }

                            String localFileName = folder.getAbsolutePath() + "/"
                                    + pm.getDatetime().getTime() + part.getFileName();
                            File localFile = new File(localFileName);
                            Log.d(TAG, localFileName);
                            if (!localFile.exists())
                                activity.photoService.savePhoto(pm, part, localFileName);
                            if (Utils.isImage(localFile)) {
                                pm.setHasAttachedImage(true);
                                pm.setImageFile(localFile);
                            } else if (Utils.isVideo(localFile)) {
                                pm.setHasAttachedVideo(true);
                                pm.setVideoFile(localFile);
                            } else if (Utils.isAudio(localFile)) {
                                pm.setHasAttachedAudio(true);
                                pm.setAudioFile(localFile);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String) p.getContent();
            if (p.isMimeType("text/html")) {
                Document doc = Jsoup.parseBodyFragment(s);
                for (Element element : doc.select("div.extra"))
                    element.remove();
                for (Element element : doc.select("div.gmail_extra"))
                    element.remove();
                for (Element element : doc.select("div.gmail_quote"))
                    element.remove();
                s = doc.outerHtml();
                s = s.replaceAll("\\n ", "<asdfasdf>");
                s = s.replaceAll("&aacute;", "á");
                s = s.replaceAll("&eacute;", "é");
                s = s.replaceAll("&iacute;", "í");
                s = s.replaceAll("&oacute;", "ó");
                s = s.replaceAll("&uacute;", "ú");
                s = s.replaceAll("&ntilde;", "ñ");
                s = s.replaceAll("&Aacute;", "Á");
                s = s.replaceAll("&Eacute;", "É");
                s = s.replaceAll("&Iacute;", "Í");
                s = s.replaceAll("&Oacute;", "Ó");
                s = s.replaceAll("&Uacute;", "Ú");
                s = s.replaceAll("&Ntilde;", "Ñ");
                s = s.replaceAll("&iquest;", "¿");
                s = s.replaceAll("&nbsp;", " ");
                s = s.replaceAll("\\s\\s+", " ");
                s = s.replaceAll("\\<.*?>", "");
                s = s.replace('\t', '\0');
            }
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }
}