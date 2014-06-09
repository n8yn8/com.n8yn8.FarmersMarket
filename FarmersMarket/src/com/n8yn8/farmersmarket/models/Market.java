package com.n8yn8.farmersmarket.models;

public class Market {

	long _ID;
	String name;
	Double latitude;
	Double longitude;
	String days;
	String open;
	String close;
	boolean selected = false;

	public Market() {
		super();
	}

	public Market(String name, Double latitude, Double longitude, String days,
			String open, String close) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.days = days;
		this.open = open;
		this.close = close;
	}

	public Market(long id, String name, Double latitude, Double longitude,
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

	public long get_ID() {
		return _ID;
	}

	public void set_ID(long id) {
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

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
