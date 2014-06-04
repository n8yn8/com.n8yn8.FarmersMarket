package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class GroceryList extends Activity {

	private ItemDbController mIDbHelper;
	private VendorDbController mVDbHelper;
	String sortBy;
	SparseArray<Group> groups;
	//long[][] ids;
	ExpandableListView listView;
	MyExpandableListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grocery_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mIDbHelper = new ItemDbController(this);
		mIDbHelper.open();

		mVDbHelper = new VendorDbController(this);
		mVDbHelper.open();
		
		List<String> categories = Arrays.asList(getResources().getStringArray(R.array.categories_array));
		fillData(categories, FeedEntry.COLUMN_NAME_Type);
		
		Button removeItems = (Button) findViewById(R.id.remove);
		removeItems.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				for(int i = 0; i<groups.size(); i++){
					Group group = groups.get(i);
					List <Model> children = group.children;
					for(int j = 0; j < group.childrenCount(); j++){
						Model child = children.get(j);
						if (child.isSelected()) {
							mIDbHelper.updateItem(child.id, "no");
							children.remove(j);
						}
					}
				}
				adapter.notifyDataSetChanged();
				//Toast.makeText(getBaseContext(), "Items removed", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onPause() {
		mIDbHelper.close();
		mVDbHelper.close();
		
		super.onPause();
	}

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
			Intent newItem = new Intent(this, EditItem.class);
			startActivity(newItem);
			return true;
		case R.id.add_vendor:
			Intent newVendor = new Intent(this, EditVendor.class);
			startActivity(newVendor);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
		case R.id.byType:
			if (checked){
				List<String> types = Arrays.asList(getResources().getStringArray(R.array.categories_array));
				fillData(types, FeedEntry.COLUMN_NAME_Type);
			}
			break;
		case R.id.byVendor:
			if (checked){
				List<String> vendors = new ArrayList<String>();
				Cursor vCursor = mVDbHelper.getAllVendors();
				if(vCursor.moveToFirst()){
					do {
						vendors.add(vCursor.getPosition(), vCursor.getString(1));
					} while (vCursor.moveToNext());
				}
				fillData(vendors, FeedEntry.COLUMN_NAME_Vendor);
			}
			break;
		}
	}

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
		listView = (ExpandableListView) findViewById(R.id.listView);
		adapter = new MyExpandableListAdapter(this, groups);
		listView.setAdapter(adapter);
	}
	
	private Model get(long id, String item, String price, String unit, String vendor, String added) {
	    return new Model(id, item, price, unit, vendor, added);
	  }
}
