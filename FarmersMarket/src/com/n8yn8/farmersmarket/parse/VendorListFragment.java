package com.n8yn8.farmersmarket.parse;

import java.util.List;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.MarketsMap;
import com.n8yn8.farmersmarket.R;

public class VendorListFragment extends Fragment {
	
	private String TAG = "VendorListFragment";
	
	private static final int ACTIVITY_CREATE_ITEM=0;
	private static final int ACTIVITY_CREATE_VENDOR=1;
	private static final int ACTIVITY_EDIT=2;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST +1;
	
	ListView list;
	VendorListAdapter vendorAdapter;
	Spinner marketSpin;
	List<Vendor> vendors;
	Market chosenMarket;
	TextView noVendors;
	
	public VendorListFragment (){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_vendor_list, container, false);
		marketSpin=(Spinner)rootView.findViewById(R.id.market_spinner);
		Button newVendor = (Button) rootView.findViewById(R.id.new_vendor);
		list=(ListView)rootView.findViewById(R.id.vendor_list);
		noVendors = (TextView)rootView.findViewById(R.id.no_vendors);
		loadMarkets();
		newVendor.setOnClickListener(addVendor);
		return rootView;
	}
	
	private OnClickListener addVendor = new OnClickListener() {
		public void onClick(View view) {
			Intent intent = new Intent(getActivity(), EditVendor.class);
			intent.putExtra("new_vendor", "true");
			startActivityForResult(intent, ACTIVITY_CREATE_VENDOR);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
			startActivityForResult(intent, ACTIVITY_CREATE_ITEM);
			return true;
        case R.id.add_item:
			intent = new Intent(this.getActivity(), EditItem.class);
			startActivityForResult(intent, ACTIVITY_CREATE_ITEM);
			return true;
		case R.id.add_vendor:
			intent = new Intent(this.getActivity(), EditVendor.class);
			intent.putExtra("new_vendor", "true");
			startActivityForResult(intent, ACTIVITY_CREATE_VENDOR);
			return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

	private void loadMarkets () {
		final MarketSpinnerAdapter spinAdapter = new MarketSpinnerAdapter(this.getActivity());
		//spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		marketSpin.setAdapter(spinAdapter);
		marketSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				chosenMarket = spinAdapter.getItem(position);
				Log.v(TAG, "ChosenMarket = " + chosenMarket.getName());
				fillData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	private void fillData() {
		
		vendorAdapter = new VendorListAdapter(getActivity());
		/*
		if (chosenMarket == null){
			vendors = db.getAllVendors();
			Log.v(TAG, "fillData() get all vendors");
		} else {
			vendors = db.getAllVendorsAtMarket(chosenMarket.get_ID());
			Log.v(TAG, "fillData() get vendors at "+chosenMarket.getName());
		}
		if (vendors.size() == 0)
			noVendors.setText("No vendors are at this market yet.");
		VendorListAdapter vendorAdapter = new VendorListAdapter(getActivity(), vendors);
		*/
		
		list.setAdapter(vendorAdapter);
		registerForContextMenu(list);
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, "Delete Vendor");
		menu.add(0, EDIT_ID, 0, "Edit Vendor");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:
			//TODO
			fillData();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this.getActivity(), EditVendor.class);
			i.putExtra("vendor_id", vendorAdapter.getItem((int) info.id).getObjectId());
			i.putExtra("new_vendor", "false");
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
