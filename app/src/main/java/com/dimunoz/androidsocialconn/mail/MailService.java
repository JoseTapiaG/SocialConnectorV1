package com.dimunoz.androidsocialconn.mail;

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
import com.dimunoz.androidsocialconn.utils.Utils;
import com.dimunoz.androidsocialconn.views.GifView;
import com.dimunoz.androidsocialconn.xml.XmlContact;
import com.google.code.oauth2.OAuth2Authenticator;
import com.sun.mail.imap.IMAPStore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;

import static com.dimunoz.androidsocialconn.main.MainActivity.scheduledFuture;

public class MailService {

    static IMAPStore imapStore;
    static Folder inbox;
    private String TAG = "Mail Service";
    private ArrayList<String> mailList = new ArrayList<>();
    private int notSeenMessagesCount = 0;

    MainActivity activity;

    public MailService(MainActivity activity) {
        this.activity = activity;
    }

    private ArrayList<PersonalMessage> personalMessages = new ArrayList<>();

    public ArrayList<PersonalMessage> getEmails() {
        synchronized (this) {
            return personalMessages;
        }
    }

    public void connect(String emailAccount, String oauthToken) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    imapStore = OAuth2Authenticator.connectToImap(
                            "imap.gmail.com",
                            993,
                            strings[0],
                            strings[1],
                            true);
                    inbox = imapStore.getFolder("INBOX");
                    cargarCorreos();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(emailAccount, oauthToken);
    }

    public void cargarCorreos() {
        Log.d(TAG, "cargar correos prev sync");
        synchronized (this) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Log.d(TAG, "cargar correos");
                        mailList.clear();
                        notSeenMessagesCount = 0;
                        for (XmlContact contact : MainActivity.xmlContacts) {
                            mailList.add(contact.getEmail());
                        }
                        MailService.this.personalMessages = new ArrayList<>();

                        inbox.open(Folder.READ_ONLY);
                        if (MailService.this.activity.xmlContacts.isEmpty())
                            return null;

                        FromStringTerm[] stringTerms = new FromStringTerm[MailService.this.activity.xmlContacts.size()];

                        for (int i = 0; i < MailService.this.activity.xmlContacts.size(); i++) {
                            stringTerms[i] = new FromStringTerm(MailService.this.activity.xmlContacts.get(i).getEmail());
                        }
                        OrTerm orTerm = new OrTerm(stringTerms);

                        Message[] folderMessages = inbox.search(orTerm);
                        int count = 0;
                        for (int j = folderMessages.length - 1; j >= 0; j--) {
                            Message message = folderMessages[j];
                            if (count > 10) {
                                break;
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
                                pm.setSeen(message.isSet(Flags.Flag.SEEN));
                                checkImageAttachments(pm, message);
                                personalMessages.add(pm);
                            }
                            count++;
                        }

                        updateCountTopBar();

                    } catch (Exception mex) {
                        mex.printStackTrace();
                    } finally {
                        try {
                            if (inbox != null)
                                inbox.close(true);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }.execute();
        }
    }

    private void updateCountTopBar() {
        Log.d(TAG, "update top bar");
        FragmentManager fragmentManager = activity.getFragmentManager();
        final Fragment currentFragment = fragmentManager.findFragmentByTag(MainActivity.FRAGMENT_TAG);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentFragment instanceof NewMessagesTransitionFragment) {
                    if (!personalMessages.isEmpty()) {
                        FragmentManager fragmentManager = activity.getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        NewMessagesFragment newMessagesFragment = new NewMessagesFragment();
                        transaction.replace(R.id.fragment_container, newMessagesFragment,
                                MainActivity.FRAGMENT_TAG);
                        transaction.commit();
                        Utils.changeBadgeNewMessagesText(activity);
                        Utils.changeBadgeNewPhotosText(activity);
                    } else {
                        TextView defaultText = (TextView) currentFragment.getActivity().findViewById(R.id.default_text);
                        defaultText.setVisibility(View.GONE);
                        TextView responseText = (TextView) currentFragment.getActivity().findViewById(R.id.response_text);
                        responseText.setText("No tienes nuevos mensajes.");
                        responseText.setVisibility(View.VISIBLE);
                        GifView gv = (GifView) currentFragment.getActivity().findViewById(R.id.default_gif);
                        gv.setVisibility(View.GONE);
                    }
                    MainActivity.messagesDownloaded = true;
                    MainActivity.checkingNewEmails = false;
                } else if (!(currentFragment instanceof NewMessagesFragment)) {
                    Utils.changeBadgeNewMessagesText(activity);
                    MainActivity.messagesDownloaded = true;
                    MainActivity.checkingNewEmails = false;
                }
            }
        });
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

    public int getNotSeenMessagesCount() {
        int count = 0;
        for (PersonalMessage personalMessage : personalMessages)
            if (!personalMessage.getSeen())
                count++;

        return count;
    }

    public void markMessageAsRead(PersonalMessage currentMessage) {
        for (PersonalMessage message : personalMessages) {

            if (currentMessage.equals(message)) {
                message.setSeen(true);
                break;
            }
        }

    }

    public static IMAPStore getImapStore() {
        return imapStore;
    }

    public static Folder getInbox() {
        return inbox;
    }
}
