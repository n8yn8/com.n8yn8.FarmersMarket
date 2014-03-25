package com.n8yn8.farmersmarket;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class EditVendor extends Activity {
	private VendorDbController mVendorDbHelper;
	private MarketDbController mMarketDbHelper;
	EditText vendorName;
	Spinner marketName;
	CheckBox isOrganic;
	private Long mRowId;
	boolean mOrganic;
	String mMarket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_vendor);

		mVendorDbHelper = new VendorDbController(this);
		mVendorDbHelper.open();
		
		mMarketDbHelper = new MarketDbController(this);
		mMarketDbHelper.open();

		vendorName = (EditText) findViewById(R.id.vendor_name);
		marketName = (Spinner) findViewById(R.id.market_spinner);
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
		// Get all of the rows from the database and create the item list
		ArrayList<String> markets = new ArrayList <String>();
		Cursor marketCursor = mMarketDbHelper.getAllMarkets();
		if (marketCursor.moveToFirst()) {
	        do {
	        	markets.add(marketCursor.getString(3));
	        } while (marketCursor.moveToNext());
		} else
			markets.add("Add a new Market first");
		ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, markets);
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		marketName.setAdapter(spinAdapter);
		marketName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				mMarket = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
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
		mVendorDbHelper.close();
		mMarketDbHelper.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String vendor = vendorName.getText().toString();

		if (mRowId == null) {
			long id = mVendorDbHelper.insertVendor(vendor, mMarket, mOrganic);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mVendorDbHelper.updateVendor(mRowId, vendor, mMarket, mOrganic);
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
