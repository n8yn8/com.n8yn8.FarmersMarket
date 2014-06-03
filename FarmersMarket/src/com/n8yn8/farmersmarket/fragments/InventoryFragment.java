package com.n8yn8.farmersmarket.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.EdiItem;
import com.n8yn8.farmersmarket.EditVendor;
import com.n8yn8.farmersmarket.ItemDbController;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.VendorDbController;

public class InventoryFragment extends Fragment {
	
	private String TAG = "InventoryFragment";
	
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
	
	public InventoryFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_inventory, container, false);
		vendorSpin=(Spinner)rootView.findViewById(R.id.vendor_spinner);
		list=(ListView)rootView.findViewById(R.id.item_list);
		loadVendors();
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mItemDbHelper = new ItemDbController(this.getActivity());
		mItemDbHelper.open();
		
		mVendorDbHelper = new VendorDbController(this.getActivity());
		mVendorDbHelper.open();
	}
	
public void loadVendors(){
		
		
		ArrayList<String> vendors = new ArrayList <String>();
		Cursor vendorCursor = mVendorDbHelper.getAllVendors();
		if (vendorCursor.moveToFirst()) {
	        do {
	        	vendors.add(vendorCursor.getString(1));
	        } while (vendorCursor.moveToNext());
		} else
			vendors.add("Add a new Vendor first");
		ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this.getActivity(),
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
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this.getActivity(), R.layout.row_inventory, itemsCursor, from, to);
		
		list.setAdapter(notes);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent i = new Intent(getActivity(), EdiItem.class);
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
			Intent i = new Intent(this.getActivity(), EdiItem.class);
			i.putExtra(FeedEntry._ID, info.id);
			Log.v(TAG, ""+info.id);
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void newItem() {
		Intent i = new Intent(this.getActivity(), EdiItem.class);
		startActivityForResult(i, ACTIVITY_CREATE_ITEM);
	}
	
	public void newVendor(){
		Intent i = new Intent(this.getActivity(), EditVendor.class);
		startActivityForResult(i, ACTIVITY_CREATE_VENDOR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		loadVendors();
	}
}
