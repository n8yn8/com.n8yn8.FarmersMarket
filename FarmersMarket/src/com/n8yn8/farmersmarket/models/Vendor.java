package com.n8yn8.farmersmarket.models;

public class Vendor {
	int _ID;
	String name;
	
	public Vendor() {
		
	}
	
	public Vendor(int id, String name) {
		super();
		this._ID = id;
		this.name = name;
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
}
