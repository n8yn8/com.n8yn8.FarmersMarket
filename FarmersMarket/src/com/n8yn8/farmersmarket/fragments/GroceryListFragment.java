package com.n8yn8.farmersmarket.fragments;

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

import com.n8yn8.farmersmarket.EditItem;
import com.n8yn8.farmersmarket.EditVendor;
import com.n8yn8.farmersmarket.MarketsMap;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.adapter.MyExpandableListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Group;
import com.n8yn8.farmersmarket.models.Item;
import com.n8yn8.farmersmarket.models.Vendor;

public class GroceryListFragment extends Fragment {
	
	private static String TAG = "GroceryListFragment";
	
	private static final int ACTIVITY_CREATE=0;

	private DatabaseHelper db;
	String sortBy;
	SparseArray<Group> groups;
	TextView noGroceries;
	ExpandableListView listView;
	MyExpandableListAdapter adapter;
	RadioButton rb1;
	RadioButton rb2;

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
				for(int i = 0; i<groups.size(); i++){
					Group group = groups.get(i);
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
				}
				adapter.notifyDataSetChanged();
			}
		});
		load();
		return rootView;
	}
	
	public void load(){

		List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
		fillDataByTypes(categories);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		db = new DatabaseHelper(this.getActivity());
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.grocery_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		db.close();
		super.onPause();
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_bar, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add_item:
			Intent newItem = new Intent(this, EdiItem.class);
			startActivity(newItem);
			return true;
		case R.id.add_vendor:
			Intent newVendor = new Intent(this, EditVendor.class);
			startActivity(newVendor);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	 */
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
		load();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated");
		rb1 = (RadioButton) getActivity().findViewById(R.id.byType);
		rb2 = (RadioButton) getActivity().findViewById(R.id.byVendor);
		rb1.setOnClickListener(next_Listener);
		rb2.setOnClickListener(next_Listener);
	}

	private OnClickListener next_Listener = new OnClickListener() {
		public void onClick(View v) {

			if(rb1.isChecked()){
				List<String> types = Arrays.asList(getResources().getStringArray(R.array.categories_array));
				fillDataByTypes(types);
			} else {
				List<Vendor> vendors = db.getAllVendors();
				fillDataByVendors(vendors);
			}

		}
	};

	private void fillDataByTypes(List<String> categories) {
		Log.i(TAG, "fillData");
		groups = db.getGroceriesByType(categories);
		setGroceryList(groups);
		if(groups.size()==0){
			noGroceries.setText("Add items to the grocery list by selecting a market.");
		}
	}
	
	private void fillDataByVendors(List<Vendor> vendors) {
		Log.i(TAG, "fillData");
		groups = db.getGroceriesByVendor(vendors);
		setGroceryList(groups);
		if(groups.size()==0){
			noGroceries.setText("Add items to the grocery list by selecting a market.");
		}
	}
	
	private void setGroceryList(SparseArray<Group> groups) {
		adapter = new MyExpandableListAdapter(this.getActivity(), groups);
		listView.setAdapter(adapter);
	}

}
