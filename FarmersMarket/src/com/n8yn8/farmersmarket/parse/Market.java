package com.n8yn8.farmersmarket.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("market")
public class Market extends ParseObject {

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Market [_ID=" + get_ID() + ", name=" + getName() + ", latitude="
				+ getLatitude() + ", longitude=" + getLongitude() + ", days=" + getDays()
				+ ", open=" + getOpen() + ", close=" + getClose() + ", selected="
				+ isSelected() + "]";
	}

	public long get_ID() {
		return _ID;
	}

	public void set_ID(long id) {
		this._ID = id;
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

	public Double getLatitude() {
		return getDouble("lat");
	}

	public void setLatitude(Double latitude) {
		put("lat", latitude);
	}

	public Double getLongitude() {
		return getDouble("lng");
	}

	public void setLongitude(Double longitude) {
		put("lng", longitude);
	}

	public String getDays() {
		return getString("daysOpen");
	}

	public void setDays(String days) {
		put("daysOpen", days);
	}

	public String getOpen() {
		return getString("open");
	}

	public void setOpen(String open) {
		put("open", open);
	}

	public String getClose() {
		return getString("close");
	}

	public void setClose(String close) {
		put("close", close);
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
