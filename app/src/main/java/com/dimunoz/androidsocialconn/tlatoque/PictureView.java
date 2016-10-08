package com.dimunoz.androidsocialconn.tlatoque;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.dimunoz.androidsocialconn.R;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 27/11/12
 * Time: 04:00 PM
 */
public class PictureView extends FrameLayout {

    private SmartTouchImageView image;
    private View viewLoading;

    public PictureView(Context context) {
        super(context);

        LayoutInflater li = LayoutInflater.from(context);
        viewLoading = li.inflate(R.layout.loading, this, false);
        addView(viewLoading);

        image = new SmartTouchImageView(context);
        image.setDrawingCacheEnabled(false);
        LayoutParams imageLayout = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        image.setLayoutParams(imageLayout);
        image.setMaxZoom(10f);
        addView(image);
    }

    public void setDetector(final GestureDetector gestureDetector) {
	    //TODO set detector on creation?
        image.setGestureDetector(gestureDetector);
	    viewLoading.setOnTouchListener(new OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
			    if (gestureDetector.onTouchEvent(event)) {
				    return true;
			    }
			    return true;
		    }
	    });
    }

	public void loadPicture(String url) {
        image.setVisibility(View.GONE);
        image.setImageUrl(url);
        viewLoading.setVisibility(View.VISIBLE);
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
