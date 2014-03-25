package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class Inventory extends Activity {

	private static final int ACTIVITY_CREATE_ITEM=0;
	private static final int ACTIVITY_CREATE_VENDOR=1;
	private static final int ACTIVITY_EDIT=2;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST +1;

	private ItemDbController mItemDbHelper;
	private VendorDbController mVendorDbHelper;
	boolean seasonalOnly;

	ListView list;
	Spinner vendorSpin;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);

		mItemDbHelper = new ItemDbController(this);
		mItemDbHelper.open();
		
		mVendorDbHelper = new VendorDbController(this);
		mVendorDbHelper.open();

		loadVendors();
	}

	public void loadVendors(){
		
		vendorSpin=(Spinner)findViewById(R.id.vendor_spinner);
		ArrayList<String> vendors = new ArrayList <String>();
		Cursor vendorCursor = mVendorDbHelper.getAllVendors();
		if (vendorCursor.moveToFirst()) {
	        do {
	        	vendors.add(vendorCursor.getString(1));
	        } while (vendorCursor.moveToNext());
		} else
			vendors.add("Add a new Vendor first");
		ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, vendors);
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vendorSpin.setAdapter(spinAdapter);
		vendorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				if(id == 0){
					fillData(null);
				}else{
					String vendor = parent.getItemAtPosition(position).toString();
					fillData(vendor);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	

	private void fillData(String vendor) {
		
		int month = Calendar.getInstance().get(Calendar.MONTH);
		if(seasonalOnly){
			//TODO
		}
		
		// Get all of the rows from the database and create the item list
		Cursor itemsCursor;
		if(vendor==null)
			itemsCursor = mItemDbHelper.getAllItems();
		else
			itemsCursor = mItemDbHelper.getItemsOf(vendor, FeedEntry.COLUMN_NAME_Vendor, FeedEntry.COLUMN_NAME_Type);
		
		String[] from = new String[]{FeedEntry.COLUMN_NAME_Item, FeedEntry.COLUMN_NAME_Price, FeedEntry.COLUMN_NAME_Unit};
		int[] to = new int[]{R.id.item_name, R.id.item_price, R.id.item_unit};
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.row_inventory, itemsCursor, from, to);
		list=(ListView)findViewById(R.id.item_list);
		list.setAdapter(notes);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent i = new Intent(getApplicationContext(), EdiItem.class);
				i.putExtra(FeedEntry._ID, id);
				startActivity(i);
			}
		});
		registerForContextMenu(list);
	}
	
	public void onToggleClicked(View view) {
		seasonalOnly = ((CheckBox) view).isChecked();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_bar, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add_item:
			newItem();
			return true;
		case R.id.add_vendor:
			newVendor();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:
			
			mItemDbHelper.deleteItem(info.id);
			loadVendors();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this, EdiItem.class);
			i.putExtra(FeedEntry._ID, info.id);
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void newItem() {
		Intent i = new Intent(this, EdiItem.class);
		startActivityForResult(i, ACTIVITY_CREATE_ITEM);
	}
	
	public void newVendor(){
		Intent i = new Intent(this, EditVendor.class);
		startActivityForResult(i, ACTIVITY_CREATE_VENDOR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		loadVendors();
	}

}
