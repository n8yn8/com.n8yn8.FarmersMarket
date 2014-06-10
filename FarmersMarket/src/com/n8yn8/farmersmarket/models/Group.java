package com.n8yn8.farmersmarket.models;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Group {
	private final String TAG = "Group";
	public String string;
	public /*final*/ List<Item> children = new ArrayList<Item>();
	private boolean added;

	public Group(String string) {
		Log.i(TAG, "constructor, string = " + string);
		this.string = string;
	}
	/*
	public void updateGroupCount(){
		this.string = this.string + " (" + childrenCount() + ")";
	}
	*/
	public boolean childrenEmpty(){
		Log.v(TAG, "childrenEmpty = " + children.isEmpty());
		return children.isEmpty();
	}
	
	public int childrenCount(){
		Log.v(TAG, "childreCount = " + children.size());
		return children.size();
	}
}
