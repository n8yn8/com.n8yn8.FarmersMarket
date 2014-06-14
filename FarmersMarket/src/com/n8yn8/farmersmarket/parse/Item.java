package com.n8yn8.farmersmarket.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("item")
public class Item extends ParseObject {
	long _ID;
	String name;
	String type;
	String price;
	String unit;
	String vendorId;
	String vendorName;
	String seasonStart;
	String seasonEnd;
	boolean inGroceries;
	String photo;
	private boolean selected;
	
	public Item() {
		super();
	}
	
	public ParseUser getAuthor() {
        return getParseUser("author");
    }
 
    public void setAuthor(ParseUser user) {
        put("author", user);
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		put("name", name);
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return getString("type");
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		put("type", type);
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return getString("price");
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		put("price", price);
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return getString("unit");
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		put("unit", unit);
	}

	/**
	 * @return the vendorId
	 */
	public String getVendorId() {
		return getString("vendorId");
	}

	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(String vendorId) {
		put("vendorId", vendorId);
	}

	/**
	 * @return the vendorName
	 */
	public String getVendorName() {
		return getString("vendorName");
	}

	/**
	 * @param vendorName the vendorName to set
	 */
	public void setVendorName(String vendorName) {
		put("vendorName", vendorName);
	}

	/**
	 * @return the seasonStart
	 */
	public String getSeasonStart() {
		return getString("seasonStart");
	}

	/**
	 * @param seasonStart the seasonStart to set
	 */
	public void setSeasonStart(String seasonStart) {
		put("seasonStart", seasonStart);
	}

	/**
	 * @return the seasonEnd
	 */
	public String getSeasonEnd() {
		return getString("seasonEnd");
	}

	/**
	 * @param seasonEnd the seasonEnd to set
	 */
	public void setSeasonEnd(String seasonEnd) {
		put("seasonEnd", seasonEnd);
	}

	/**
	 * @return the inGroceries
	 */
	public boolean isInGroceries() {
		return getBoolean("inGroceries");
	}

	/**
	 * @param inGroceries the inGroceries to set
	 */
	public void setInGroceries(boolean inGroceries) {
		put("inGroceries", inGroceries);
	}

	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return getString("photo");
	}

	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		put("photo", photo);
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
	
}
