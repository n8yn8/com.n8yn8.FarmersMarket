package com.n8yn8.farmersmarket.models;

public class Market {
	
	int _ID;
	String name;
	Double latitude;
	Double longitude;
	String days;
	String open;
	String close;
	
	public Market() {
		super();
	}

	public Market(int id, String name, Double latitude, Double longitude,
			String days, String open, String close) {
		super();
		this._ID = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.days = days;
		this.open = open;
		this.close = close;
	}

	public int getId() {
		return _ID;
	}

	public void setId(int id) {
		this._ID = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getClose() {
		return close;
	}

	public void setClose(String close) {
		this.close = close;
	}
	

}
