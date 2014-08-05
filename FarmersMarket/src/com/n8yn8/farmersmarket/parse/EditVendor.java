package com.n8yn8.farmersmarket.parse;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

public class EditVendor extends Activity implements NoNameAlertFragment.NoticeDialogListener {

	private String TAG = "EditVendor";
	MarketCheckListAdapter marketAdapter;
	List<Market> checkedMarkets;
	EditText vendorNameField;
	ListView marketName;
	CheckBox isOrganic;
	private String mRowId;
	boolean mOrganic, newVendor;
	String mMarket;
	Vendor vendor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_vendor);


		vendorNameField = (EditText) findViewById(R.id.vendor_name);
		marketName = (ListView) findViewById(R.id.market_spinner);
		isOrganic = (CheckBox) findViewById(R.id.check_organic);

		Button confirmButton = (Button) findViewById(R.id.saveVendor);
		Button deleteButton = (Button) findViewById(R.id.deleteVendor);

		Bundle extras = getIntent().getExtras();
		newVendor = extras.get("new_vendor").equals("true");
		Log.v(TAG, "newVendor = "+newVendor);
		if (!newVendor) {
			mRowId = extras.getString("vendor_id");
			ParseQuery<Vendor> query = ParseQuery.getQuery("vendor");
			query.getInBackground(mRowId, new GetCallback<Vendor>() {
				public void done(Vendor object, ParseException e) {
					if (e == null) {
						vendor = object;
						populateFields();
					} else {
						// something went wrong
					}
				}
			});
		} else {
			getMarkets();
		}

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

	private void populateFields() {
		Log.i(TAG, "populateFields");
		vendorNameField.setText(vendor.getName());
		ParseRelation<Market> relation = vendor.getRelation("vendor_at_market");
		relation.getQuery().findInBackground(new FindCallback<Market>() {
			public void done(List<Market> relationMarkets, ParseException e) {
				if (e != null) {
					// There was an error
				} else {
					Log.d(TAG, "populate fields: " + relationMarkets.toString());
					checkedMarkets = relationMarkets;
					getMarkets();
				}
			}
		});
	}

	private void getMarkets(){
		Log.i(TAG, "getMarkets");
		ParseQuery<Market> query = ParseQuery.getQuery("market");
		query.findInBackground(new FindCallback<Market>() {
			public void done(List<Market> markets, ParseException e) {
				if (e == null) {
					if (!newVendor) {
						for (int i = 0; i < markets.size(); i++) {
							for (int j = 0; j < checkedMarkets.size(); j++) {
								if (markets.get(i).equals(checkedMarkets.get(j))) {
									markets.get(i).setSelected(true);
									Log.d(TAG, "Market match " + markets.get(i).getObjectId() + " " + checkedMarkets.get(j).getObjectId());
								} else {
									Log.d(TAG, "Markets not in relation " + markets.get(i).getObjectId() + " " + checkedMarkets.get(j).getObjectId());
								}
							}
						}
					}
					Log.d(TAG, "getMarkets retreived "+markets.toString());
					populateMarkets(markets);
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

	public void populateMarkets(List<Market> markets) {
		Log.i(TAG, "populateMarkets");
		marketAdapter = new MarketCheckListAdapter(this, markets);
		marketName.setAdapter(marketAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		//saveState();
		//outState.putSerializable(FeedEntry._ID, mRowId);
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
		//saveState();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "onResume");
		super.onResume();
		//getMarkets();
	}

	private boolean saveState() {
		Log.i(TAG, "saveState");
		String vendorName = vendorNameField.getText().toString();

		List<Market> markets = marketAdapter.markets;
		if (newVendor){
			vendor = new Vendor();
		}
		vendor.setName(vendorName);

		for (int i = 0; i < markets.size(); i++) {
			Market market = markets.get(i);
			ParseRelation<ParseObject> relation = vendor.getRelation("vendor_at_market");
			if (market.isSelected()) {
				Log.v(TAG + "Selected", market.getName());
				relation.add(market);
			} else {
				relation.remove(market);
			}
		}

		vendor.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					Toast.makeText(
							getApplicationContext(),
							"Error saving: " + e.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});


		return (!vendorName.equals(""));
	}

	private void deleteState() {
		Log.i(TAG, "deleteState");
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
		}
		finish();
	}

	public void onCheckboxClicked(View view) {
		Log.i(TAG, "onCheckboxClicked");
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
		Log.i(TAG, "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_vendor, menu);
		return true;
	}

	public void showNoticeDialog() {
		Log.i(TAG, "showNoticeDialog");
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
