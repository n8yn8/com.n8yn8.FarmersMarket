package com.n8yn8.farmersmarket.models;

public class Item {
	long _ID;
	String name;
	String type;
	String price;
	String unit;
	String vendor;
	String seasonStart;
	String seasonEnd;
	String added;
	String photo;
	
	public Item() {
		super();
	}

	public Item(String name, String type, String price, String unit,
			String vendor, String seasonStart, String seasonEnd, String added,
			String photo) {
		super();
		this.name = name;
		this.type = type;
		this.price = price;
		this.unit = unit;
		this.vendor = vendor;
		this.seasonStart = seasonStart;
		this.seasonEnd = seasonEnd;
		this.added = added;
		this.photo = photo;
	}

	public Item(long _ID, String name, String type, String price, String unit,
			String vendor, String seasonStart, String seasonEnd, String added,
			String photo) {
		super();
		this._ID = _ID;
		this.name = name;
		this.type = type;
		this.price = price;
		this.unit = unit;
		this.vendor = vendor;
		this.seasonStart = seasonStart;
		this.seasonEnd = seasonEnd;
		this.added = added;
		this.photo = photo;
	}

	public long get_ID() {
		return _ID;
	}

	public void set_ID(long _ID) {
		this._ID = _ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getSeasonStart() {
		return seasonStart;
	}

	public void setSeasonStart(String seasonStart) {
		this.seasonStart = seasonStart;
	}

	public String getSeasonEnd() {
		return seasonEnd;
	}

	public void setSeasonEnd(String seasonEnd) {
		this.seasonEnd = seasonEnd;
	}

	public String getAdded() {
		return added;
	}

	public void setAdded(String added) {
		this.added = added;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
