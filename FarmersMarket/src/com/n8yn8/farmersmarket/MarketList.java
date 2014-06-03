package com.n8yn8.farmersmarket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class MarketList extends Activity {
	private MarketDbController mDbHelper;
	//ListView marketView;
	String market;
	
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST +2;
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_market_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mDbHelper = new MarketDbController(this);
    	mDbHelper.open();
    	
    	fillData();
	}
	
	private void fillData(){
		Cursor marketsCursor = mDbHelper.getAllMarkets();
		startManagingCursor(marketsCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{FeedEntry.COLUMN_NAME_Market, FeedEntry.COLUMN_NAME_Days, FeedEntry.COLUMN_NAME_Open, FeedEntry.COLUMN_NAME_Close};

		// and an array of the fields we want to bind those fields to (in this case just text1)
		int[] to = new int[]{R.id.marketName, R.id.days, R.id.open, R.id.close};

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter marketsAdapter = 
				new SimpleCursorAdapter(this, R.layout.row_markets, marketsCursor, from, to);
		ListView list=(ListView)findViewById(R.id.market_list);
		list.setAdapter(marketsAdapter);
		registerForContextMenu(list);
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				market = parent.getItemAtPosition(position).toString();
				startShopping(id);				
			}
		});
	}
	
	private void startShopping(long id){
		Intent intent = new Intent(this, ShopMarket.class);
		intent.putExtra("market_name", market);
		startActivity(intent);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.market_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.map_view:
			Intent intent = new Intent(this, MarketsMap.class);
			startActivity(intent);
			
		}
		return super.onOptionsItemSelected(item);
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
			mDbHelper.deleteMarket(info.id);
			fillData();
			return true;
		case EDIT_ID:
			Toast.makeText(this, "work in progress", Toast.LENGTH_LONG).show();
			/*Intent i = new Intent(this, EditMarket.class);
			i.putExtra(FeedEntry._ID, info.id);
			startActivityForResult(i, ACTIVITY_EDIT);
			fillData();
			return true;*/
		}
		return super.onContextItemSelected(item);
	}

}
