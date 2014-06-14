package com.n8yn8.farmersmarket.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("vendor")
public class Vendor extends ParseObject{
	long _ID;
	String name;
	
	public Vendor() {
		super();
	}
	
	public long getId() {
		return _ID;
	}
	public void setId(long id) {
		this._ID = id;
	}
	public String getName() {
		return getString("name");
	}
	public void setName(String name) {
		put("name", name);
	}
}
