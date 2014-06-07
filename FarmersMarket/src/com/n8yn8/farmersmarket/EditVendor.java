package com.n8yn8.farmersmarket;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
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
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;
import com.n8yn8.farmersmarket.models.Vendor;

public class EditVendor extends Activity implements NoNameAlertFragment.NoticeDialogListener {
	
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
		Button deleteButton = (Button) findViewById(R.id.deleteVendor);

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
				Log.d(TAG, "itemName on Save button = "+vendorNameField.getText().toString());
				if(saveState()) {
					setResult(RESULT_OK);
					finish();
				} else {
					showNoticeDialog();
				}
			}

		});

		deleteButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View veiw){
				deleteState();
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

	private boolean saveState() {
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
		return (!vendorName.equals(""));
	}
	
	private void deleteState() {
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
			db.deleteItem(mRowId);
		}
		finish();
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
	
	public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoNameAlertFragment();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(TAG, "Positive button pressed");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    	deleteState();
    }

}
