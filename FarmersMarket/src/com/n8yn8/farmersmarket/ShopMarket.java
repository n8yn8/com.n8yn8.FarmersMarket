package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class ShopMarket extends Activity {
	
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    
    private ItemDbController mDbHelper;
    private List<Model> items;
    //ArrayList<String> items;
    long[] ids;
    ArrayAdapter<Model> adapter;
    //ArrayAdapter<String> adapter;
	Spinner categorySpinner;
	ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_market);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDbHelper = new ItemDbController(this);
        mDbHelper.open();
        
        
        
		categorySpinner = (Spinner) findViewById(R.id.selectCategory);
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
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
				SparseBooleanArray checked = list.getCheckedItemPositions();
				Log.d("ShopMarket onClick", ""+checked.size());
				for (int i = 0; i < checked.size(); i++) {
		            // Item position in adapter
		            int position = checked.keyAt(i);
		            // Add item if it is checked i.e.) == TRUE!
		            if (checked.valueAt(i)){
		            	String item = adapter.getItem(position).toString();
		            	Log.d("ShopMarket onClick item checked", item);
		            	mDbHelper.added(ids[i], "yes");
		            }
		        }
				Toast.makeText(getBaseContext(), "Selected items added to grocery list", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void fillData(String category){
        
		//For use with InteractiveArrayAdapter
		items = new ArrayList<Model>();
		Cursor itemCursor;
		if(category==null)
			itemCursor = mDbHelper.getAllItems();
		else
			itemCursor = mDbHelper.getItemsOf(category, FeedEntry.COLUMN_NAME_Type, FeedEntry.COLUMN_NAME_Type);
		ids = new long[itemCursor.getCount()];
		if (itemCursor.moveToFirst()) {
			int i = 0;
	        do {
	        	items.add(get(itemCursor.getLong(0), itemCursor.getString(1), itemCursor.getString(3), itemCursor.getString(4), itemCursor.getString(5), itemCursor.getString(6)));
	        	ids[i] = itemCursor.getLong(0);
	        	i++;
	        } while (itemCursor.moveToNext());
		} else
			items.add(get(0,"Add a new Item first", null, null, null, null));
		adapter = new InteractiveArrayAdapter(this, items);
		
		
		//For use with ArrayAdapter
		/*items = new ArrayList<String>();
		Cursor itemCursor;
		if(category==null)
			itemCursor = mDbHelper.getAllItems();
		else
			itemCursor = mDbHelper.getItemsOf(category, FeedEntry.COLUMN_NAME_Type, FeedEntry.COLUMN_NAME_Type);
		ids = new long[itemCursor.getCount()];
		if (itemCursor.moveToFirst()) {
	        int i = 0;
			do {
				String item = itemCursor.getString(1);
				String price = " $" + itemCursor.getString(3) + "/" + itemCursor.getString(4);
				String vendor = " @ " + itemCursor.getString(5);
	        	items.add(item+price+vendor);
	        	ids[i] = itemCursor.getLong(0);
	        	i++;
	        } while (itemCursor.moveToNext());
		}
		adapter = new ArrayAdapter<String>(this, R.layout.row_shop2, items);*/
		
		list=(ListView)findViewById(R.id.selectItem);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setAdapter(adapter);
		
		/*Cursor notesCursor;
		if(category==null)
			notesCursor = mDbHelper.getAllItems();
		else
			notesCursor = mDbHelper.getItemsOf(category, FeedEntry.COLUMN_NAME_Type, FeedEntry.COLUMN_NAME_Type);
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{FeedEntry.COLUMN_NAME_Item, FeedEntry.COLUMN_NAME_Price, FeedEntry.COLUMN_NAME_Unit, FeedEntry.COLUMN_NAME_Vendor};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.item_name, R.id.item_price, R.id.item_unit, R.id.item_vendor};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.row_shop, notesCursor, from, to);
        list=(ListView)findViewById(R.id.selectItem);
        list.setAdapter(notes);
        registerForContextMenu(list);*/
	}
	
	private Model get(long id, String item, String price, String unit, String vendor, String added) {
	    return new Model(id, item, price, unit, vendor, added);
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
	
	

}
