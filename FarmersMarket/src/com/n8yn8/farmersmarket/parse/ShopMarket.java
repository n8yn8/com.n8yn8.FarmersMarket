package com.n8yn8.farmersmarket.parse;

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

import com.n8yn8.farmersmarket.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class ShopMarket extends Activity {
	
	private static final String TAG = "ShopMarket";
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    
    Market market;
    String category;
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
        
		Bundle extras = getIntent().getExtras();
		String marketId = extras != null ? extras.getString("market_id")
				: null;
		Log.d(TAG, "marketId = "+marketId);
		ParseQuery<Market> query = ParseQuery.getQuery("market");
		query.getInBackground(marketId, new GetCallback<Market>() {
			public void done(Market object, ParseException e) {
				if (e == null) {
					market = object;
					setSpinner();
				} else {
					Log.e(TAG, e.getMessage());
				}
			}
		});
		
		String name = extras != null ? extras.getString("market_name")
				: null;
		marketName.setText(name);
        
		
		Button confirmButton = (Button) findViewById(R.id.toList);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				List<Item> items = adapter.items;
				for (int i = 0; i < items.size(); i++) {
					Item item = items.get(i);
					if (item.isSelected()) {
						Log.v(TAG + " Selected", item.getName());
						item.setInGroceries(true);
						try {
							item.save();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				Toast.makeText(getBaseContext(), "Selected items added to grocery list", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void setSpinner() {
		categorySpinner = (Spinner) findViewById(R.id.selectCategory);
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				Log.i(TAG, "onItemSelected");
				if(position != 0){
					category = parent.getItemAtPosition(position).toString();
					vendorsAtMarket();
				}else
					category = null;
					vendorsAtMarket();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void vendorsAtMarket() {
		ParseQuery<Vendor> query = ParseQuery.getQuery("vendor");
		query.whereEqualTo("vendor_at_market", market);
		query.findInBackground(new FindCallback<Vendor>() {
			@Override
			public void done(List<Vendor> vendors, ParseException e) {
				if (e == null) {
					Log.v(TAG, "query returned: " + vendors.toString());
					findItems(vendors);
				} else {
					Log.e(TAG, e.getMessage());
				}
				
			}
		});
	}
	
	private void findItems(List<Vendor> vendors){
		Log.i(TAG, "fillData");
		
		ParseQuery<Item> query = ParseQuery.getQuery("item");
		for(int i = 0; i < vendors.size(); i++) {
			query.whereEqualTo("vendor", vendors.get(i));
		}
		if(category!=null){
			query.whereEqualTo("type", category);
		}
				
		query.findInBackground(new FindCallback<Item>() {
			@Override
			public void done(List<Item> items, ParseException e) {
				if (e == null) {
					Log.v(TAG, "query returned: " + items.toString());
					if (items.isEmpty()) {
						noItems.setText("No items are at this market in this category.");
					} else { 
						setAdapter(items);
					}
				} else {
					Log.e(TAG, e.getMessage());
				}
			}
		});
	}
	
	private void setAdapter(List<Item> items) {
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
		Intent intent;
		switch(item.getItemId()) {
		case R.id.add_item:
			intent = new Intent(this, EditItem.class);
			intent.putExtra("new_item", "true");
			startActivityForResult(intent, ACTIVITY_CREATE);
			return true;
		case R.id.add_vendor:
			intent = new Intent(this, EditVendor.class);
			intent.putExtra("new_vendor", "true");
			startActivityForResult(intent, ACTIVITY_CREATE);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		category = null;
		setSpinner();
	}
	
	

}
