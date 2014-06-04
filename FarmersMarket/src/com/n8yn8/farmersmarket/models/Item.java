package com.n8yn8.farmersmarket.models;

public class Item {
	long _ID;
	String name;
	String type;
	String price;
	String unit;
	long vendorId;
	String vendorName;
	String seasonStart;
	String seasonEnd;
	String added;
	String photo;
	private boolean selected;
	
	public Item() {
		super();
	}

	/**
	 * @param _ID
	 * @param name
	 * @param type
	 * @param price
	 * @param unit
	 * @param vendorId
	 * @param vendorName
	 * @param seasonStart
	 * @param seasonEnd
	 * @param added
	 * @param photo
	 */
	public Item(long _ID, String name, String type, String price, String unit,
			long vendorId, String vendorName, String seasonStart,
			String seasonEnd, String added, String photo) {
		super();
		this._ID = _ID;
		this.name = name;
		this.type = type;
		this.price = price;
		this.unit = unit;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.seasonStart = seasonStart;
		this.seasonEnd = seasonEnd;
		this.added = added;
		this.photo = photo;
	}

	/**
	 * @param name
	 * @param type
	 * @param price
	 * @param unit
	 * @param vendorId
	 * @param vendorName
	 * @param seasonStart
	 * @param seasonEnd
	 * @param added
	 * @param photo
	 */
	public Item(String name, String type, String price, String unit,
			long vendorId, String vendorName, String seasonStart,
			String seasonEnd, String added, String photo) {
		super();
		this.name = name;
		this.type = type;
		this.price = price;
		this.unit = unit;
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.seasonStart = seasonStart;
		this.seasonEnd = seasonEnd;
		this.added = added;
		this.photo = photo;
	}

	/**
	 * @return the _ID
	 */
	public long get_ID() {
		return _ID;
	}

	/**
	 * @param _ID the _ID to set
	 */
	public void set_ID(long _ID) {
		this._ID = _ID;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the vendorId
	 */
	public long getVendorId() {
		return vendorId;
	}

	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}

	/**
	 * @return the vendorName
	 */
	public String getVendorName() {
		return vendorName;
	}

	/**
	 * @param vendorName the vendorName to set
	 */
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	/**
	 * @return the seasonStart
	 */
	public String getSeasonStart() {
		return seasonStart;
	}

	/**
	 * @param seasonStart the seasonStart to set
	 */
	public void setSeasonStart(String seasonStart) {
		this.seasonStart = seasonStart;
	}

	/**
	 * @return the seasonEnd
	 */
	public String getSeasonEnd() {
		return seasonEnd;
	}

	/**
	 * @param seasonEnd the seasonEnd to set
	 */
	public void setSeasonEnd(String seasonEnd) {
		this.seasonEnd = seasonEnd;
	}

	/**
	 * @return the added
	 */
	public String getAdded() {
		return added;
	}

	/**
	 * @param added the added to set
	 */
	public void setAdded(String added) {
		this.added = added;
	}

	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}

	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return added to grocery list
	 */
	public boolean isAdded() {
		return this.added.equals("yes");
	}
	
}
