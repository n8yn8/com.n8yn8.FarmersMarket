package com.n8yn8.farmersmarket.parse;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Group {
	String TAG = "Group";
	public String title;
	public final List<Item> children = new ArrayList<Item>();
	private boolean selected;

	public Group(String string) {
		this.title = string;
	}
	
	public void updateGroupCount(){
		this.title = this.title + " (" + childrenCount() + ")";
	}
	
	public boolean childrenEmpty(){
		return children.isEmpty();
	}
	
	public int childrenCount(){
		return children.size();
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		Log.i(TAG, "equals");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
}
