package com.n8yn8.farmersmarket.fragments;

import java.util.Calendar;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.EditItem;
import com.n8yn8.farmersmarket.EditVendor;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.adapter.ItemListAdapter;
import com.n8yn8.farmersmarket.adapter.VendorSpinnerAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Item;
import com.n8yn8.farmersmarket.models.Vendor;

public class InventoryFragment extends Fragment {
	
	private String TAG = "InventoryFragment";
	
	private static final int ACTIVITY_CREATE_ITEM=0;
	private static final int ACTIVITY_CREATE_VENDOR=1;
	private static final int ACTIVITY_EDIT=2;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST +1;
	
	private DatabaseHelper db;
	boolean seasonalOnly;

	List<Item> items;
	ListView list;
	Spinner vendorSpin;
	
	public InventoryFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreatView");
		View rootView = inflater.inflate(R.layout.activity_inventory, container, false);
		vendorSpin=(Spinner)rootView.findViewById(R.id.vendor_spinner);
		list=(ListView)rootView.findViewById(R.id.item_list);
		Button newItem = (Button) rootView.findViewById(R.id.new_item);
		newItem.setOnClickListener(addItem);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		db = new DatabaseHelper(this.getActivity());
		loadVendors();	
	}
	
public void loadVendors(){
	Log.i(TAG, "loadVendors");
		
		List<Vendor> vendors = db.getAllVendors();
		//TODO "add vendor first" when no vendors.
		final VendorSpinnerAdapter spinAdapter = new VendorSpinnerAdapter(this.getActivity(),
                android.R.layout.simple_spinner_item, vendors);
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vendorSpin.setAdapter(spinAdapter);
		vendorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
					Vendor vendor = spinAdapter.getVendor(position);
					//TODO switch null to vendor
					fillData(null);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	

	private void fillData(Vendor vendor) {
		
		Log.i(TAG, "fillData");
		
		int month = Calendar.getInstance().get(Calendar.MONTH);
		if(seasonalOnly){
			//TODO
		}
		
		// Get all of the rows from the database and create the item list
		if(vendor==null)
			items = db.getAllItems();
		else
			items = db.getItemsOf(vendor.getName(), db.KEY_VENDOR_NAME);
		
		ItemListAdapter listAdapter = new ItemListAdapter(this.getActivity(), items);
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent i = new Intent(getActivity(), EditItem.class);
				i.putExtra(FeedEntry._ID, items.get((int) id).get_ID());
				startActivityForResult(i, ACTIVITY_EDIT);
			}
		});
		registerForContextMenu(list);
	}
	
	public void onToggleClicked(View view) {
		Log.i(TAG, "onToggleClicked");
		seasonalOnly = ((CheckBox) view).isChecked();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.i(TAG, "onCreatContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, "onContextItemSelected");
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:
			db.deleteItem(items.get((int) info.id).get_ID());
			loadVendors();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this.getActivity(), EditItem.class);
			i.putExtra(FeedEntry._ID, items.get((int) info.id).get_ID());
			Log.v(TAG, "item to edit = "+items.get((int) info.id).getName());
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	private OnClickListener addItem = new OnClickListener() {
		public void onClick(View view) {
			Log.i(TAG, "onClick addItem");
			Intent intent = new Intent(getActivity(), EditItem.class);
			startActivityForResult(intent, ACTIVITY_CREATE_VENDOR);
		}
	};

	public void newItem() {
		Log.i(TAG, "newItem");
		Intent i = new Intent(this.getActivity(), EditItem.class);
		startActivityForResult(i, ACTIVITY_CREATE_ITEM);
	}
	
	public void newVendor(){
		Log.i(TAG, "newVendor");
		Intent i = new Intent(this.getActivity(), EditVendor.class);
		startActivityForResult(i, ACTIVITY_CREATE_VENDOR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, intent);
		loadVendors();
	}
}
