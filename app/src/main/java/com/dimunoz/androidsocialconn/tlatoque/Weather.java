package com.dimunoz.androidsocialconn.tlatoque;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 26/11/12
 * Time: 02:47 PM
 * Edited by: Diego Munoz
 * Date: 21/09/15
*/
public class Weather {

	public static final int UPDATE_WEATHER = 1;
	public static final int UPDATE_LOCATION = 2;
	public static final String API_KEY = "1ad19f4e05f77648";

	private final String TAG = "Tlatoque.Weather";

	private Context context;
	private Handler handlerWeather;
	private LocationManager locationManager;

	private final long weatherUpdateTime = 3600000;

	public static WeatherSample weatherSample = new WeatherSample();

	public Weather(Context context, Handler handlerWeather) {
		this.context = context;
		this.handlerWeather = handlerWeather;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_LOCATION) {
				Log.d(TAG, "Updating Location");
				getLocation();

				// remove scheduled updates
				removeMessages(UPDATE_LOCATION);

				// schedule new update
				sendMessageDelayed(obtainMessage(UPDATE_LOCATION), weatherUpdateTime);
			}
			else if (msg.what == UPDATE_WEATHER) {
				getWeather(API_KEY);
			}
		}
	};

	public void refreshWeather() {
		Message msg = mHandler.obtainMessage(UPDATE_LOCATION);
		mHandler.sendMessage(msg);
	}

	public void getWeather(String apiKey) {
		Log.d(TAG, "Getting weather conditions");
		if (weatherSample.getLatitude() != 0 && weatherSample.getLongitude() != 0){
			WundergroundRestClient.get(apiKey + "/conditions/q/" + weatherSample.getLatitude() + "," + weatherSample.getLongitude() + ".json",
					null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject weather) {
					if (weather.has(WundergroundRestClient.CURRENT_OBSERVATION)) {
						try {
							JSONObject currentObservation = weather.getJSONObject(WundergroundRestClient.CURRENT_OBSERVATION);
							if (currentObservation.has(WundergroundRestClient.DISPLAY_LOCATION)) {
								weatherSample.setCity(currentObservation.getJSONObject(
										WundergroundRestClient.DISPLAY_LOCATION).getString(WundergroundRestClient.CITY));
							}
							if (currentObservation.has(WundergroundRestClient.CONDITION)) {
								weatherSample.setCondition(
										currentObservation.getString(WundergroundRestClient.CONDITION));
							}
							if (currentObservation.has(WundergroundRestClient.TEMP_F)) {
								weatherSample.setTempF(
										currentObservation.getString(WundergroundRestClient.TEMP_F));
							}
							if (currentObservation.has(WundergroundRestClient.TEMP_C)) {
								weatherSample.setTempC(
										currentObservation.getString(WundergroundRestClient.TEMP_C));
							}
							if (currentObservation.has(WundergroundRestClient.HUMIDITY)) {
								weatherSample.setHumidity(
										currentObservation.getString(WundergroundRestClient.HUMIDITY));
							}
							if (currentObservation.has(WundergroundRestClient.WIND)) {
								weatherSample.setWind(
										currentObservation.getString(WundergroundRestClient.WIND));
							}
							if (currentObservation.has(WundergroundRestClient.ICON)) {
								weatherSample.setIconName(
										currentObservation.getString(WundergroundRestClient.ICON));
							}
						} catch (JSONException e) {
							Log.e(TAG, "Error while getting weather", e);
						}
					}

					if (weatherSample.getTempC() != null && weatherSample.getTempF() != null) {
						updateWeather();
						weatherSample.setUpdatedAt(System.currentTimeMillis());
						Log.d(TAG, "UPDATED AT: " + System.currentTimeMillis());
					}
				}
			});
		}
	}

	private void updateWeather() {
		Message msg = handlerWeather.obtainMessage(UPDATE_WEATHER);
		handlerWeather.sendMessage(msg);
	}

	private void getLocation() {
		Log.d(TAG, "Getting location");
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Get weather with last known location
		getLocationInfo(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			Log.d(TAG, "Network Location provider on.");

			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					5000,   // 5-second interval.
					5,      // 5 meters.
					locationListener);
		}
		else {
			Log.d(TAG, "Network Location provider off.");
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			getLocationInfo(location);
			stopLocationListener();
		}

		@Override
		public void onProviderEnabled(String s) {
			Log.d(TAG, "PROVIDER ENABLED");
		}

		@Override
		public void onProviderDisabled(String s) {
			Log.d(TAG, "PROVIDER DISABLED");
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {
			Log.d(TAG, "STATUS CHANGED");
		}
	};

	private void stopLocationListener() {
		Log.d(TAG, "Stopping location listener");
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	private void getLocationInfo(Location location) {
		if (location != null) {
			weatherSample.setLatitude(location.getLatitude());
			weatherSample.setLongitude(location.getLongitude());

			Log.d(TAG, "LOCATION: lat:" + location.getLatitude() + " lon: " + location.getLongitude());

			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses;
			try {
				addresses = geocoder.getFromLocation(weatherSample.getLatitude(), weatherSample.getLongitude(), 10);
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);

				weatherSample.setCountry(addresses.get(0).getCountryCode());
				Log.d(TAG, weatherSample.getCountry() + " " + address + " " + city + " " + country);

				Message msg = mHandler.obtainMessage(UPDATE_WEATHER);
				mHandler.sendMessage(msg);

			} catch (IOException e) {
				Log.e(TAG, "Error processing location", e);
			}
		}
	}
}
