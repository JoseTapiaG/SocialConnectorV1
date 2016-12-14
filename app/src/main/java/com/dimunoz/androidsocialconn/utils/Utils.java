package com.dimunoz.androidsocialconn.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.database.PhotoEntity;
import com.dimunoz.androidsocialconn.main.MainActivity;
import com.dimunoz.androidsocialconn.photos.Photo;
import com.dimunoz.androidsocialconn.receivemessages.PersonalMessage;
import com.dimunoz.androidsocialconn.xml.XmlContact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    private final static String TAG = "Utils";
    private final static int NIGHT_TIME_BEGINNING = 23;
    private final static int NIGHT_TIME_END = 7;

    public static XmlContact getXmlContactFromEmail(String email) {
        for (XmlContact c : MainActivity.xmlContacts) {
            if (c.getEmail().compareTo(email) == 0) {
                return c;
            }
        }
        return null;
    }
	
	public static boolean checkJSONAnswer(double sendCheck, double receiveCheck) {
		return (sendCheck + 0.1) == receiveCheck;
	}

    public static String createTimeStringFromTimestampLong(long timestamp) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy\n" +
                    "HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getDefault());
            return sdf.format(new Timestamp(timestamp*1000));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getMonthName(Integer month) {
        switch (month) {
            case 0:
                return "Enero";
            case 1:
                return "Febrero";
            case 2:
                return "Marzo";
            case 3:
                return "Abril";
            case 4:
                return "Mayo";
            case 5:
                return "Junio";
            case 6:
                return "Julio";
            case 7:
                return "Agosto";
            case 8:
                return "Septiembre";
            case 9:
                return "Octubre";
            case 10:
                return "Noviembre";
            case 11:
                return "Diciembre";
            default:
                return "Diciembre";
        }
    }

    public static String createTimeStringFromTimestampMillisecondsLong(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
            String month = getMonthName(calendar.get(Calendar.MONTH));
            return "Mensaje escrito el " + day + " de " + month;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
	
	public static String createTimeStringFromDatetimeReceived(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getDefault());
		sdf.applyPattern("dd-MM-yyyy HH:mm:ss z");
		try {
			Date parsedDate = sdf.parse(s);
			sdf.applyPattern("dd-MM-yyyy\nHH:mm:ss");
			return sdf.format(parsedDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String cropText(String s, int size) {
		if ( s.length() >= size) {
			return s.substring(0, size - 3) + "...";
		}
		else
			return s;
	}
	
	public static String generateCurrentTimestampString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z", Locale.getDefault());
		Date date = new Date();
		return sdf.format(date);
	}
	
	public static String makeMD5(String s) {
		String hashtext = "";
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(s.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			hashtext = bigInt.toString(16);
			while (hashtext.length() < 32 ) {
				hashtext = "0" + hashtext;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashtext;
	}

	public static Bitmap createBitmapFromBytes(byte[] blob) {
		return BitmapFactory.decodeByteArray(blob, 0, blob.length);
	}
	
	public static Drawable createDrawableFromBitmap(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}
	
	public static String readTildes(String s) {
		String result = s;
		try {
			result = new String((s.getBytes("ISO-8859-1")),"UTF-8");
		} catch (UnsupportedEncodingException e) {
            e.printStackTrace();
		}
		return result;
	}

    public static void changeBadgeNewMessagesText(Activity activity) {
        TextView badgeNewMessages = (TextView) activity.findViewById(R.id.badge_new_messages);
        int newMessagesNotSeen = getNewMessagesNotSeen();
        if (newMessagesNotSeen > 0) {
            badgeNewMessages.setText("" + newMessagesNotSeen);
            badgeNewMessages.setVisibility(View.VISIBLE);
        } else {
            badgeNewMessages.setVisibility(View.INVISIBLE);
        }
    }

    public static void changeBadgeNewPhotosText(Activity activity) {
        TextView badgeNewPhotos = (TextView) activity.findViewById(R.id.badge_new_photos);
        if (MainActivity.newPhotosList.size() > 0) {
            int notSeen = getNewPhotosNotSeen();
            badgeNewPhotos.setText("" + notSeen);
            badgeNewPhotos.setVisibility(View.VISIBLE);
        } else {
            badgeNewPhotos.setVisibility(View.INVISIBLE);
        }
    }

    public static void changeScreenBrightness(final float value, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
                layout.screenBrightness = value;
                activity.getWindow().setAttributes(layout);
            }
        });
    }

    public static void checkIfNightTime() {
        Time now = new Time();
        now.setToNow();
        if (now.hour >= NIGHT_TIME_BEGINNING || now.hour < NIGHT_TIME_END) {
        //if (now.minute == 35) {
            // if it is night
            MainActivity.hasChangedNightMode = !MainActivity.nightMode;
            MainActivity.nightMode = true;
        } else {
            // if it is not night
            MainActivity.hasChangedNightMode = MainActivity.nightMode;
            MainActivity.nightMode = false;
        }
    }

    private static String getLogFilename(String prefix, Calendar c) {
        return MainActivity.userContact.getEmail() + "-" + prefix + c.get(Calendar.YEAR) +
                "-" + (c.get(Calendar.MONTH) + 1) + "-" +
                c.get(Calendar.DAY_OF_MONTH) + ".csv";
    }

    public static String getLogServiceFilename(Calendar c) {
        return getLogFilename("serv-", c);
    }

    public static String getLogUseFilename(Calendar c) {
        return getLogFilename("use-", c);
    }

    private static int getNewMessagesNotSeen() {
        return MainActivity.mailService.getNotSeenMessagesCount();
    }

    private static int getNewPhotosNotSeen() {
        int count = 0;
        for (PhotoEntity photo: MainActivity.newPhotosList) {
            if (!photo.isSeen())
                count++;
        }
        return count;
    }

    public static boolean isAudio(File file) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(file.getAbsolutePath());
            mp.prepare();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    public static boolean isVideo(File file) {
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(),
                MediaStore.Images.Thumbnails.MINI_KIND);
        return thumbnail != null;
    }

    public static void logCallEvent(String caller, String receiver, Long start, Long end) {
        Log.d(TAG, "logCallEvent");
        String data = "call," + caller + "," + receiver + "," + start + "," + end;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logReadNewMessageEvent(String type, String sender, String receiver,
                                              Long sentDate, Long read) {
        Log.d(TAG, "logReadNewMessageEvent");
        String data = "readNewMessage," + type + "," + sender + "," + receiver + ","
                + sentDate + "," + read;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logSeeAlbumPhotoEvent(String photoId, Long readBegin, Long readEnd) {
        Log.d(TAG, "logSeeAlbumPhotoEvent");
        String data = "seeAlbumPhoto," + photoId + "," + readBegin + "," + readEnd;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logSeeNewPhotoEvent(String photoId, Long uploadTime,
                                           Long readBegin, Long readEnd) {
        Log.d(TAG, "logSeeNewPhotoEvent");
        String data = "seeNewPhoto," + photoId + "," + uploadTime +
                "," + readBegin + "," + readEnd;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logNewPhotosEmpty(Long readBegin, Long readEnd) {
        Log.d(TAG, "logNewPhotosEmptyEvent");
        String data = "newPhotosEmpty," + + readBegin + "," + readEnd;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logSendMessageEvent(String sender, String receiver, Long timestamp) {
        Log.d(TAG, "logSendMessageEvent");
        String data = "sendMessage," + sender + "," + receiver + "," + timestamp;
        Calendar c = Calendar.getInstance();
        String filename = getLogServiceFilename(c);
        writeToFile(data, filename);
    }

    public static void logUseEvent(MotionEvent event, Activity activity) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            logUseEvent(event, activity, "tap");
        }
    }

    private static void logUseEvent(MotionEvent event, Activity activity, String type) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        FragmentManager fragmentManager = activity.getFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(MainActivity.FRAGMENT_TAG);
        long timestamp = System.currentTimeMillis();
        String context = currentFragment.getClass().getSimpleName();
        String data = type + "," + x + "," + y + "," + context + "," + timestamp;
        Calendar c = Calendar.getInstance();
        String filename = getLogUseFilename(c);
        writeToFile(data, filename);
    }

    /**
     * Writes some data to a file using a BufferedWriter.
     */
    public static void writeToFile(String data, String filename) {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File myFile = new File(externalStorageDir, filename);
        try {
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            if (myFile.exists())
                myOutWriter.append('\n');
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeExceptionLog(StackTraceElement[] data){
        File externalStorageDir = Environment.getExternalStorageDirectory();
        File myFile = new File(externalStorageDir, "logSocialConnector.log");
        try {
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            if (myFile.exists()){
                myOutWriter.append('\n');
                myOutWriter.append(new Date().toString());
                myOutWriter.append('\n');
            }
            for(StackTraceElement stack : data){
                myOutWriter.append(stack.toString());
                myOutWriter.append('\n');
            }
            myOutWriter.close();
            fOut.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
