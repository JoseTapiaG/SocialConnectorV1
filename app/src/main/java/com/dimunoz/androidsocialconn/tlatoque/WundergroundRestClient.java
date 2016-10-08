package com.dimunoz.androidsocialconn.tlatoque;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 28/11/12
 * Time: 11:37 AM
 * Edited by: Diego Munoz
 * Date: 21/09/15
 */
public class WundergroundRestClient {
	private static final String BASE_URL = "http://api.wunderground.com/api/";

	public static final String CURRENT_OBSERVATION = "current_observation";
	public static final String DISPLAY_LOCATION = "display_location";
	public static final String CITY = "city";
	public static final String CONDITION = "icon";
	public static final String TEMP_F = "temp_f";
	public static final String TEMP_C = "temp_c";
	public static final String HUMIDITY = "relative_humidity";
	public static final String WIND = "wind_string";
	public static final String ICON = "icon";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
