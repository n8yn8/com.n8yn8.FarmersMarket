package com.n8yn8.farmersmarket.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;


public class GroceryListFragment extends Fragment {

	private static String TAG = "GroceryListFragment";
	private static final int ACTIVITY_CREATE=0;
	private boolean sortByType = true;
	
	SparseArray<Group> groups;
	TextView noGroceries;
	ExpandableListView listView;
	MyExpandableListAdapter adapter;
	RadioButton rb1;
	RadioButton rb2;
	List<Item> items;

	public GroceryListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");

		View rootView = inflater.inflate(R.layout.activity_grocery_list, container, false);
		listView = (ExpandableListView) rootView.findViewById(R.id.listView);
		noGroceries = (TextView)rootView.findViewById(R.id.no_groceries);
		Button removeItems = (Button) rootView.findViewById(R.id.remove);
		removeItems.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Log.i(TAG, "removeItem onClick");
				for(int i = 0; i<groups.size(); i++){
					Group group = groups.get(i);
					/*
					List <Item> children = group.children;
					for(int j = 0; j < group.childrenCount(); j++){
						Item child = children.get(j);
						if (child.isAdded()) {
							db.updateItem(child.get_ID(), "no");
							children.remove(j);
						}
					}
					//group.updateGroupCount();
					if (group.childrenEmpty()) {
						groups.remove(i);
					}
					 */
				}
				adapter.notifyDataSetChanged();
			}
		});
		//load();
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.grocery_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "onOptionsItemSelected");
		Intent intent;
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.map_view:
			intent = new Intent(this.getActivity(), MarketsMap.class);
			startActivityForResult(intent, ACTIVITY_CREATE);
			return true;
		case R.id.add_item:
			intent = new Intent(this.getActivity(), EditItem.class);
			startActivityForResult(intent, ACTIVITY_CREATE);
			return true;
		case R.id.add_vendor:
			intent = new Intent(this.getActivity(), EditVendor.class);
			startActivityForResult(intent, ACTIVITY_CREATE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated");
		rb1 = (RadioButton) getActivity().findViewById(R.id.byType);
		rb2 = (RadioButton) getActivity().findViewById(R.id.byVendor);
		rb1.setOnClickListener(next_Listener);
		rb2.setOnClickListener(next_Listener);

		ParseUser currentUser = ParseUser.getCurrentUser();
		ParseRelation<Item> groceries = currentUser.getRelation("groceries");
		groceries.getQuery().findInBackground(new FindCallback<Item>() {

			@Override
			public void done(List<Item> objects, ParseException e) {
				if (e != null) {
					Log.e(TAG, e.getMessage());
				} else {
					items = objects;
					fillData();
				}
			}

		});
		
	}
	
	private void fillData(){
		Log.i(TAG, "fillData");
		int groupId = 0;
		groups = new SparseArray<Group>();
		List<String> categories = new ArrayList<String>(); //Used for reference since can't detect if group is already present
		for (Item item : items) {
			String category;
			if (sortByType){
				category = item.getType();
			} else {
				category = item.getVendorName();
			}
			if (categories.contains(category)) {
				//category already exists, so 
				int index = categories.indexOf(category);
				Group group = groups.get(index);
				group.children.add(item);
				groups.put(index, group);
			} else {
				categories.add(category);
				Group group = new Group(category);
				group.children.add(item);
				groups.put(groupId, group);
				groupId++;
			}
		}
		setGroceryList(groups);
	}

	private OnClickListener next_Listener = new OnClickListener() {
		public void onClick(View v) {
			Log.i(TAG, "next_Listener onClick");
			if(rb1.isChecked()){
				sortByType = true;
				fillData();
			} else {
				sortByType = false;
				fillData();
			}

		}
	};

	private void setGroceryList(SparseArray<Group> groups) {
		Log.i(TAG, "setGroceryList");
		adapter = new MyExpandableListAdapter(this.getActivity(), groups);
		listView.setAdapter(adapter);
	}

}
