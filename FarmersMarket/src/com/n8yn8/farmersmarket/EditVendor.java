package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.adapter.MarketCheckListAdapter;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;
import com.n8yn8.farmersmarket.models.Vendor;

public class EditVendor extends Activity {
	
	private String TAG = "EditVendor";
	private DatabaseHelper db;
	MarketCheckListAdapter marketAdapter;
	EditText vendorNameField;
	ListView marketName;
	CheckBox isOrganic;
	private Long mRowId;
	boolean mOrganic;
	String mMarket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_vendor);

		db = new DatabaseHelper(this);

		vendorNameField = (EditText) findViewById(R.id.vendor_name);
		marketName = (ListView) findViewById(R.id.market_spinner);
		isOrganic = (CheckBox) findViewById(R.id.check_organic);
		
		Button confirmButton = (Button) findViewById(R.id.saveVendor);

		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(FeedEntry._ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(FeedEntry._ID)
					: null;
		}

		populateFields();
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});
	}

	private void populateFields(){
		List<Market> markets;
		if (mRowId != null) {
        	Log.v(TAG, "populateFields");
            Vendor vendor = db.getVendor(mRowId);
            vendorNameField.setText(vendor.getName());
            markets = db.getAllMarketsForVendor(mRowId, vendor.getName());
            //TODO populate markets that the vendor is at.
        } else {
        	markets = db.getAllMarkets();
        }
		
		marketAdapter = new MarketCheckListAdapter(this, markets);
		marketName.setAdapter(marketAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(FeedEntry._ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String vendorName = vendorNameField.getText().toString();
		Vendor vendor;
		List<Market> markets = marketAdapter.markets;
		ArrayList<Long> market_ids = new ArrayList<Long>();
		for (int i = 0; i < markets.size(); i++) {
			Market market = markets.get(i);
			if (market.isSelected()) {
				Log.v(TAG + "Selected", market.getName());
				market_ids.add(market.getId());
			}
		}
		if (mRowId == null) {
			vendor = new Vendor(vendorName);
			long id = db.createVendor(vendor, market_ids);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			vendor = new Vendor(mRowId, vendorName);
			db.updateVendor(vendor);
			//TODO update vendor at markets.
		}
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = isOrganic.isChecked();

		// Check which checkbox was clicked
		switch(view.getId()) {
		case R.id.check_organic:
			if (checked)
				mOrganic = true;
			else
				mOrganic = false;
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_vendor, menu);
		return true;
	}

}
