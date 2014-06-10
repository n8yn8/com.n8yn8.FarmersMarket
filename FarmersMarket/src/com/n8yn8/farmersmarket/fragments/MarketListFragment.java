package com.n8yn8.farmersmarket.fragments;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.EditItem;
import com.n8yn8.farmersmarket.EditMarket;
import com.n8yn8.farmersmarket.EditVendor;
import com.n8yn8.farmersmarket.MarketsMap;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.ShopMarket;
import com.n8yn8.farmersmarket.adapter.MarketListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;

public class MarketListFragment extends Fragment {
	
	private String TAG = "MarketListFragment";
	
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST +2;
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	
	private DatabaseHelper db;
	ListView list;
	List<Market> markets;
	MarketListAdapter marketsAdapter;
	Market market;

	public MarketListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_market_list, container, false);
		list=(ListView)rootView.findViewById(R.id.market_list);
    	fillData();
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		db = new DatabaseHelper(this.getActivity());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.market_list, menu);
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
	
	private void fillData(){
		markets = db.getAllMarkets();
		marketsAdapter = new MarketListAdapter(getActivity(), markets);
		list.setAdapter(marketsAdapter);
		registerForContextMenu(list);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				startShopping((Market) marketsAdapter.getMarket(position));				
			}
		});
	}
	
	private void startShopping(Market market){
		Intent intent = new Intent(this.getActivity(), ShopMarket.class);
		intent.putExtra("market_id", market.get_ID());
		intent.putExtra("market_name", market.getName());
		startActivity(intent);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete_market);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit_market);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case DELETE_ID:
			db.deleteMarket(markets.get((int) info.id).get_ID());
			fillData();
			return true;
		case EDIT_ID:
			Intent i = new Intent(this.getActivity(), EditMarket.class);
			i.putExtra(FeedEntry._ID, markets.get((int) info.id).get_ID());
			i.putExtra("new_market", "false");
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
