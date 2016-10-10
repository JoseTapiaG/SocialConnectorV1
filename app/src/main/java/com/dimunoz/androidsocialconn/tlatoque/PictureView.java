package com.dimunoz.androidsocialconn.tlatoque;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dimunoz.androidsocialconn.R;
import com.loopj.android.image.BitmapImage;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 27/11/12
 * Time: 04:00 PM
 */
public class PictureView extends FrameLayout {

    private SmartTouchImageView image;

    public PictureView(Context context) {
        super(context);

        image = new SmartTouchImageView(context);
        image.setDrawingCacheEnabled(false);
        LayoutParams imageLayout = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 8);
        image.setLayoutParams(imageLayout);
        image.setMaxZoom(10f);
        addView(image);
    }

    public void setDetector(final GestureDetector gestureDetector) {
	    //TODO set detector on creation?
        image.setGestureDetector(gestureDetector);
    }

	public void loadPicture(Bitmap bitmap) {
        image.setVisibility(View.GONE);
        image.setImageBitmap(bitmap);
        image.setVisibility(View.VISIBLE);
    }

    public boolean isZoomed() {
        return image.getScale() != 1f;
    }

    public void resetZoom() {
        image.resetScale();
    }

    public void setHandler(Handler handler) {
        image.setHandler(handler);
    }
}
