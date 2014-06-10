package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.n8yn8.farmersmarket.adapter.ItemCheckListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Item;

public class ShopMarket extends Activity {
	
	private static final String TAG = "ShopMarket";
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    
    private DatabaseHelper db;
    long marketId;
    private List<Item> items;
    ItemCheckListAdapter adapter;
	Spinner categorySpinner;
	TextView noItems;
	TextView marketName;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_market);
		noItems = (TextView)findViewById(R.id.no_items);
		marketName = (TextView)findViewById(R.id.market_name);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		db = new DatabaseHelper(this);
        
		Bundle extras = getIntent().getExtras();
		marketId = extras != null ? extras.getLong("market_id")
				: null;
		Log.d(TAG, "marketId = "+marketId);
		
		String name = extras != null ? extras.getString("market_name")
				: null;
		marketName.setText(name);
        
		categorySpinner = (Spinner) findViewById(R.id.selectCategory);
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				Log.i(TAG, "onItemSelected");
				if(position != 0){
					String category = parent.getItemAtPosition(position).toString();
					fillData(category);
				}else
					fillData(null);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		Button confirmButton = (Button) findViewById(R.id.toList);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				List<Item> items = adapter.items;
				for (int i = 0; i < items.size(); i++) {
					Item item = items.get(i);
					if (item.isSelected()) {
						Log.v(TAG + "Selected", item.getName());
						db.updateItem(item.get_ID(), "yes");
					}
				}
				/*
				SparseBooleanArray checked = list.getCheckedItemPositions();
				Log.d("ShopMarket onClick", ""+checked.size());
				for (int i = 0; i < checked.size(); i++) {
		            // Item position in adapter
		            int position = checked.keyAt(i);
		            // Add item if it is checked i.e.) == TRUE!
		            if (checked.valueAt(i)){
		            	Item item = (Item)adapter.getItem(position);
		            	Log.d("ShopMarket onClick item checked", item);
		            	db.updateItem(rowId, "yes");
		            }
		        }
				*/
				Toast.makeText(getBaseContext(), "Selected items added to grocery list", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void fillData(String category){
		Log.i(TAG, "fillData");
        
		//For use with InteractiveArrayAdapter
		items = new ArrayList<Item>();
		if(category==null){
			items = db.getItemsAtMarket(marketId);
			if (items.size()==0)
				noItems.setText("No items are at this market.");
		} else {
			//TODO
			items = db.getItemsAtMarketByCategory(marketId, category);
			if (items.size()==0)
				noItems.setText("No items are at this market in this category.");
		}
		adapter = new ItemCheckListAdapter(this, items);
		
		list=(ListView)findViewById(R.id.selectItem);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);
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
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.map_view:
			Intent intent = new Intent(this, MarketsMap.class);
			startActivity(intent);
			
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
	
	

}
