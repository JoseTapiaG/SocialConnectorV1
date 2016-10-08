package com.dimunoz.androidsocialconn.tlatoque;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 14/11/12
 * Time: 03:57 PM
 * Updated by: Diego Munoz
 * Date: 15/09/15
 */

public class TlatoqueViewFlipper extends ViewFlipper {

	public static final int FLIP_MSG = 1;
	public static final int STOP_FLIP = 2;
	public static final int CONTINUE_FLIP = 3;
	public static final int ZOOMED_IN = 5;
	public static final int ZOOMED_OUT = 6;
	public static final int ZOOM_STARTED = 7;

	public static final int REFRESH = 20;

	private boolean mStarted = false;
	private boolean mRunning = false;

	private Handler handler;

	public TlatoqueViewFlipper(Context context) {
		super(context);
	}

	public TlatoqueViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isFlipping() {
		return mStarted;
	}

	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * Start a timer to cycle through child views
	 */
	@Override
	public void startFlipping() {
		mStarted = true;
		updateRunning();
	}

	/**
	 * No more flips
	 */
	@Override
	public void stopFlipping() {
		mStarted = false;
		updateRunning();
	}

	/**
	 * Internal method to start or stop dispatching flip {@link Message}
	 */
	private void updateRunning() {
		boolean running = mStarted;
		if (running != mRunning) {
			if (running) {
				Message msg = handler.obtainMessage(FLIP_MSG);
				handler.sendMessageDelayed(msg,
                        TlatoqueFragment.flipInterval - TlatoqueFragment.elapsed);
			} else {
				handler.removeMessages(FLIP_MSG);
			}
			mRunning = running;
		}
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
