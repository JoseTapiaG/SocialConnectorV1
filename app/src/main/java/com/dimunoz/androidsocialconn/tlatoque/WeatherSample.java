package com.dimunoz.androidsocialconn.tlatoque;

/**
 * Created by: Eduardo Quintana Contreras
 * Date: 28/11/12
 * Time: 12:05 PM
 * Edited by: Diego Munoz
 * Date: 21/09/15
 */
public class WeatherSample {
	private String city;
	private String condition;
	private String tempF;
	private String tempC;
	private String humidity;
	private String wind;
	private String iconName;
	private String country = "US";
	private double latitude;
	private double longitude;
	private long updatedAt;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(long updatedAt) {
		this.updatedAt = updatedAt;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getTempF() {
		return tempF;
	}

	public void setTempF(String tempF) {
		this.tempF = round(tempF);
	}

	public String getTempC() {
		return tempC;
	}

	public void setTempC(String tempC) {
		this.tempC = round(tempC);
	}

    private String round(String value) {
        return "" + (int) Math.round(Double.parseDouble(value));
    }

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
