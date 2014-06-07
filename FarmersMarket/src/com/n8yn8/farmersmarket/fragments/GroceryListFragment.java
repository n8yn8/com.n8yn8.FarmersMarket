package com.n8yn8.farmersmarket.fragments;

import java.util.Arrays;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;

import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.adapter.MyExpandableListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Group;
import com.n8yn8.farmersmarket.models.Item;
import com.n8yn8.farmersmarket.models.Vendor;

public class GroceryListFragment extends Fragment {
	
	private static String TAG = "GroceryListFragment";

	private DatabaseHelper db;
	String sortBy;
	SparseArray<Group> groups;
	//long[][] ids;
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
		Button removeItems = (Button) rootView.findViewById(R.id.remove);

		List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
		fillDataByTypes(categories);

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
				}
				adapter.notifyDataSetChanged();
				//Toast.makeText(getBaseContext(), "Items removed", Toast.LENGTH_SHORT).show();
			}
		});

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		db = new DatabaseHelper(this.getActivity());
		super.onCreate(savedInstanceState);
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
	}
	
	private void fillDataByVendors(List<Vendor> vendors) {
		Log.i(TAG, "fillData");
		groups = db.getGroceriesByVendor(vendors);
		setGroceryList(groups);
	}
	
	private void setGroceryList(SparseArray<Group> groups) {
		adapter = new MyExpandableListAdapter(this.getActivity(), groups);
		listView.setAdapter(adapter);
	}

}
