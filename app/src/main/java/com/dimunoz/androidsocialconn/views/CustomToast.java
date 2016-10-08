package com.dimunoz.androidsocialconn.views;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.dimunoz.androidsocialconn.R;

public class CustomToast {

	public static Toast getCustomToast(Activity activity, String text) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast,
				(ViewGroup) activity.findViewById(R.id.toast_layout_root));
		TextView textView = (TextView) layout.findViewById(R.id.toast_text);
		textView.setText(text);
		textView.setTextColor(activity.getResources().getColor(R.color.white));
		textView.setTextSize(40);

		Toast toast = new Toast(activity.getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		return toast;
	}

}
