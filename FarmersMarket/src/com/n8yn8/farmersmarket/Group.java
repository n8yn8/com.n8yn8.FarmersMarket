package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.List;

public class Group {
	public String string;
	public final List<Model> children = new ArrayList<Model>();
	private boolean selected;

	public Group(String string) {
		this.string = string;
	}
	
	public boolean childrenEmpty(){
		return children.isEmpty();
	}
}
