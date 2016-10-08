package com.dimunoz.androidsocialconn.photos;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import com.dimunoz.androidsocialconn.R;
import com.dimunoz.androidsocialconn.utils.Utils;
import com.dimunoz.androidsocialconn.views.GifView;
import com.loopj.android.image.SmartImageView;

/**
 * Created by dmunoz on 14-09-15.
 *
 */
public class FullPhotoActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep screen on
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.changeScreenBrightness(1F, this);

        String url = getIntent().getExtras().getString("url");
        setContentView(R.layout.full_photo);
        SmartImageView fullPhoto = (SmartImageView) findViewById(R.id.full_photo);
        fullPhoto.setImageUrl(url);
        GifView gv = (GifView) findViewById(R.id.full_photo_clock);
        gv.setMyDrawable(R.drawable.clock_10_seconds_countdown);
        gv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        gv.init(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 10500);
    }
}
