package com.n8yn8.farmersmarket.models;

public class Vendor {
	long _ID;
	String name;
	
	public Vendor() {
		
	}
	
	public Vendor(String name) {
		super();
		this.name = name;
	}

	public Vendor(long id, String name) {
		super();
		this._ID = id;
		this.name = name;
	}
	
	public long getId() {
		return _ID;
	}
	public void setId(long id) {
		this._ID = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
