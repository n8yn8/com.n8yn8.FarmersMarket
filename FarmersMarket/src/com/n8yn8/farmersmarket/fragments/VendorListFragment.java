package com.n8yn8.farmersmarket.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.EditVendor;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.adapter.MarketSpinnerAdapter;
import com.n8yn8.farmersmarket.adapter.VendorListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;
import com.n8yn8.farmersmarket.models.Vendor;

public class VendorListFragment extends Fragment {
	
	private String TAG = "VendorListFragment";
	
	private static final int ACTIVITY_CREATE_ITEM=0;
	private static final int ACTIVITY_CREATE_VENDOR=1;
	private static final int ACTIVITY_EDIT=2;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST +1;
	
	DatabaseHelper db;
	ListView list;
	Spinner marketSpin;
	List<Vendor> vendors;
	Market chosenMarket;
	
	public VendorListFragment (){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_vendor_list, container, false);
		marketSpin=(Spinner)rootView.findViewById(R.id.market_spinner);
		Button newVendor = (Button) rootView.findViewById(R.id.new_vendor);
		list=(ListView)rootView.findViewById(R.id.vendor_list);
		loadMarkets();
		newVendor.setOnClickListener(addVendor);
		return rootView;
	}
	
	private OnClickListener addVendor = new OnClickListener() {
		public void onClick(View view) {
			Intent intent = new Intent(getActivity(), EditVendor.class);
			startActivityForResult(intent, ACTIVITY_CREATE_VENDOR);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		db = new DatabaseHelper(this.getActivity());
	}

	private void loadMarkets () {
		List<Market> markets = db.getAllMarkets();
		final MarketSpinnerAdapter spinAdapter = new MarketSpinnerAdapter(this.getActivity(),
                android.R.layout.simple_spinner_item, markets);
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		marketSpin.setAdapter(spinAdapter);
		marketSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				chosenMarket = spinAdapter.getMarket(position);
				Log.v(TAG, "ChosenMarket = " + chosenMarket.getName());
				fillData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	private void fillData() {
		
		if (chosenMarket == null){
			vendors = db.getAllVendors();
			Log.v(TAG, "fillData() get all vendors");
		} else {
			vendors = db.getAllVendorsAtMarket(chosenMarket.getId());
			Log.v(TAG, "fillData() get vendors at "+chosenMarket.getName());
		}
		VendorListAdapter vendorAdapter = new VendorListAdapter(getActivity(), vendors);
		list.setAdapter(vendorAdapter);
		registerForContextMenu(list);
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
			db.deleteVendor(vendors.get((int) info.id).getId());
			fillData();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this.getActivity(), EditVendor.class);
			i.putExtra(FeedEntry._ID, vendors.get((int) info.id).getId());
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

}
