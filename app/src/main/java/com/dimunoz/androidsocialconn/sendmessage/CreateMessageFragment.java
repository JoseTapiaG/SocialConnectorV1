package com.dimunoz.androidsocialconn.sendmessage;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.asynctasks.SendEmailThroughGmail;
import com.dimunoz.androidsocialconn.basefragments.BaseDisplayMessageFragment;
import com.dimunoz.androidsocialconn.main.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * InstagramUser: diego
 * Date: 19/02/15
 * Time: 17:10
 */
public class CreateMessageFragment extends BaseDisplayMessageFragment {

    private final String TAG = "CreateMessageFragment";
    private static final int RESULT_SPEECH = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView");
        contentLayout.setBackgroundColor(getResources().getColor(R.color.SendMessage));

        // set title to contact info
        this.leftProfilePictureText.setText("Mensaje a " + this.contact.getNickname() + ":");

        // set default info message
        this.mainMessage.setText("Toque el micrófono para dictar su mensaje...");
        this.mainMessage.setTypeface(null, Typeface.BOLD_ITALIC);

        // set mic image and text
        Drawable microphoneDrawable = getResources().getDrawable(R.drawable.microphone3);
        this.microphoneImage.setImageDrawable(microphoneDrawable);
        this.microphoneText.setText("Dictar mensaje");

        // set answer message button invisible
        this.answerMessageImage.setVisibility(View.INVISIBLE);
        this.answerMessageText.setVisibility(View.INVISIBLE);

        // set arrows invisible
        this.leftArrowImage.setVisibility(View.INVISIBLE);
        this.leftArrowText.setVisibility(View.INVISIBLE);
        this.rightArrowImage.setVisibility(View.INVISIBLE);
        this.rightArrowText.setVisibility(View.INVISIBLE);

        return contentLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // set message
                    this.mainMessage.setTypeface(null, Typeface.NORMAL);
                    this.mainMessage.setText(text.get(0));

                    // change microphone text
                    this.microphoneText.setText("Volver a dictar");

                    // set answer message image and text
                    Drawable answerMessageDrawable = getResources().getDrawable(R.drawable.send_flying_message);
                    this.answerMessageImage.setImageDrawable(answerMessageDrawable);
                    this.answerMessageText.setText("Enviar mensaje");

                    // set answer message button visible
                    this.answerMessageImage.setVisibility(View.VISIBLE);
                    this.answerMessageText.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    // answer message button
    public void handleDownButtonTapEvent(View view) {
        Log.d(TAG, "handleDownButtonTapEvent");
        String content = this.mainMessage.getText().toString();
        if (!this.mainMessage.getText().toString().equals("")) {
            new SendEmailThroughGmail((MainActivity)getActivity(), content,
                    MainActivity.emailAccount, MainActivity.oauthToken, contact.getEmail()).execute();
        }
    }

    // microphone button
    public void handleUpButtonTapEvent(View view) {
        Log.d(TAG, "handleUpButtonTapEvent");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().toString());

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getActivity().getApplicationContext(),
                    "Su dispositivo no soporta ingresar texto por Voz.",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
