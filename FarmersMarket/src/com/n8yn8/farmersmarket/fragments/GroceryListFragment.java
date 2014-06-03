package com.n8yn8.farmersmarket.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Fragment;
import android.database.Cursor;
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
import android.widget.Toast;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.Group;
import com.n8yn8.farmersmarket.ItemDbController;
import com.n8yn8.farmersmarket.Model;
import com.n8yn8.farmersmarket.MyExpandableListAdapter;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.VendorDbController;

public class GroceryListFragment extends Fragment {

	private ItemDbController mIDbHelper;
	private VendorDbController mVDbHelper;
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
  
        View rootView = inflater.inflate(R.layout.activity_grocery_list, container, false);
        listView = (ExpandableListView) rootView.findViewById(R.id.listView);
        Button removeItems = (Button) rootView.findViewById(R.id.remove);
        
        List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
        fillData(categories, FeedEntry.COLUMN_NAME_Type);
        
		removeItems.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				for(int i = 0; i<groups.size(); i++){
					Group group = groups.get(i);
					List <Model> children = group.children;
					for(int j = 0; j < group.childrenCount(); j++){
						Model child = children.get(j);
						if (child.isSelected()) {
							mIDbHelper.updateItem(child.getId(), "no");
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
		mIDbHelper = new ItemDbController(this.getActivity());
		mIDbHelper.open();

		mVDbHelper = new VendorDbController(this.getActivity());
		mVDbHelper.open();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		mIDbHelper.close();
		mVDbHelper.close();
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
		rb1 = (RadioButton) getActivity().findViewById(R.id.byType);
		rb2 = (RadioButton) getActivity().findViewById(R.id.byVendor);
		rb1.setOnClickListener(next_Listener);
		rb2.setOnClickListener(next_Listener);
	}
	
	private OnClickListener next_Listener = new OnClickListener() {
        public void onClick(View v) {

        	if(rb1.isChecked()){
        		List<String> types = Arrays.asList(getResources().getStringArray(R.array.categories_array));
				fillData(types, FeedEntry.COLUMN_NAME_Type);
        	} else {
        		List<String> vendors = new ArrayList<String>();
				Cursor vCursor = mVDbHelper.getAllVendors();
				if(vCursor.moveToFirst()){
					do {
						vendors.add(vCursor.getPosition(), vCursor.getString(1));
					} while (vCursor.moveToNext());
				}
				fillData(vendors, FeedEntry.COLUMN_NAME_Vendor);
        	}
        	
        }
    };

	private void fillData(List<String> categories, String column) {
		groups = new SparseArray<Group>();
		int groupID = 0;
		
		//Loop through categories to create groups.
		for(int i = 1; i < categories.size(); i++){
			String category = categories.get(i);
			
			//Loop through items of given group
			Cursor cursor = mIDbHelper.getItemsOf(category, column, FeedEntry.COLUMN_NAME_Item);
			if (cursor.moveToFirst()) {
				Group group = new Group(category);
				int childID = 0;
				//Add children to the group.
				do {
					String vendor = "";
					if(column.equals(FeedEntry.COLUMN_NAME_Type))
						vendor = cursor.getString(6);
					//Include items in the group if they are added to the grocery list.
					if (cursor.getString(8).equals("yes")) {
						group.children.add(get(cursor.getLong(0), cursor.getString(1), cursor.getString(3), cursor.getString(4), cursor.getString(5), vendor));
						childID++;
					}
				} while (cursor.moveToNext());
				//Include the group if it contains children.
				if(!group.childrenEmpty()){
					group.updateGroupCount();
					groups.put(groupID, group);
					groupID++;
				}
			}
		}
		
		adapter = new MyExpandableListAdapter(this.getActivity(), groups);
		listView.setAdapter(adapter);
	}
	
	private Model get(long id, String item, String price, String unit, String vendor, String added) {
	    return new Model(id, item, price, unit, vendor, added);
	  }
	
	

}
