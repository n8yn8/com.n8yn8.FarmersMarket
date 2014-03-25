package com.n8yn8.farmersmarket;

import android.util.Log;

public class Model {

	private String name;
	private String price;
	private String unit;
	private String vendor;
	private boolean selected;

	public Model(String name, String price, String unit, String vendor, String added) {
		this.name = name;
		this.price = price;
		this.unit = unit;
		this.vendor = vendor;
		selected = added.equals("yes");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

} 