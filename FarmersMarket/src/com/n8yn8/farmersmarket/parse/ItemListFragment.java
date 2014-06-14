package com.n8yn8.farmersmarket.parse;

import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.MarketsMap;
import com.n8yn8.farmersmarket.R;



public class ItemListFragment extends Fragment {

	private String TAG = "ItemListFragment";

	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_CREATE_VENDOR=1;
	private static final int ACTIVITY_EDIT=2;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST +1;

	boolean seasonalOnly;

	List<Item> items;
	TextView noItems;
	ListView list;
	Spinner vendorSpin;

	public ItemListFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreatView");
		View rootView = inflater.inflate(R.layout.activity_item_list, container, false);
		vendorSpin=(Spinner)rootView.findViewById(R.id.vendor_spinner);
		noItems = (TextView)rootView.findViewById(R.id.no_items);
		list=(ListView)rootView.findViewById(R.id.item_list);
		Button newItem = (Button) rootView.findViewById(R.id.new_item);
		newItem.setOnClickListener(addItem);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		loadVendors();	
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add_bar, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

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

	public void loadVendors(){
		Log.i(TAG, "loadVendors");

		//TODO "add vendor first" when no vendors.
		final VendorSpinnerAdapter spinAdapter = new VendorSpinnerAdapter(this.getActivity());
		//spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vendorSpin.setAdapter(spinAdapter);
		vendorSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				Log.i(TAG, "vendorSpin onItemSelected");
				
					Vendor vendor = spinAdapter.getItem(position);
					fillData(vendor);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}



	private void fillData(Vendor vendor) {

		Log.i(TAG, "fillData");

		int month = Calendar.getInstance().get(Calendar.MONTH);
		if(seasonalOnly){
			//TODO
		}

		noItems.setText("");
		/*
		// Get all of the rows from the database and create the item list
		if(vendor==null) {
			items = db.getAllItems();
			if (items.size() == 0) {
				noItems.setText("There are no items added yet.");
				Log.d(TAG, "no items yet.");
			}
		} else {
			items = db.getItemsOf(vendor.getName(), db.KEY_VENDOR_NAME);
			if (items.size() == 0) {
				noItems.setText("There are no items added yet at this vendor.");
				Log.d(TAG, "no items yet at this vendor");
			}
		}
		*/

		final ItemListAdapter listAdapter = new ItemListAdapter(this.getActivity());
		list.setAdapter(listAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Log.i(TAG, "listView onItemClick");
				Intent i = new Intent(getActivity(), EditItem.class);
				i.putExtra(FeedEntry._ID, listAdapter.getItemId(position));
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
			//TODO
			loadVendors();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this.getActivity(), EditItem.class);
			//i.putExtra(FeedEntry._ID, items.get((int) info.id).get_ID());
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
			startActivityForResult(intent, ACTIVITY_CREATE);
		}
	};

	public void newItem() {
		Log.i(TAG, "newItem");
		Intent i = new Intent(this.getActivity(), EditItem.class);
		startActivityForResult(i, ACTIVITY_CREATE);
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
		if(resultCode == Activity.RESULT_OK){
			loadVendors();
		}
	}
}
