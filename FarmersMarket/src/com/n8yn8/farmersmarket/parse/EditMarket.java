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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditMarket extends Activity implements NoNameAlertFragment.NoticeDialogListener {
	private static final String TAG = "EditMarket";
	EditText marketName;
	EditText openTime;
	EditText closeTime;
	LatLng location;
	String[] week;
	Boolean newMarket;
	private String mRowId;
	List<CheckBox> daysCheckBoxes;
	Market market;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_market);
		marketName = (EditText) findViewById(R.id.marketName);
		openTime = (EditText) findViewById(R.id.openTime);
		closeTime = (EditText) findViewById(R.id.closeTime);
		daysCheckBoxes = new ArrayList<CheckBox>();
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkMon));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkTue));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkWed));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkThu));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkFri));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkSat));
		daysCheckBoxes.add((CheckBox)findViewById(R.id.checkSun));
		week = new String[] {"Mo ", "Tu ", "We ", "Th ", "Fr ", "Sa ", "Su "};
		Button confirmButton = (Button) findViewById(R.id.saveNewMarket);
		Button deleteButton = (Button) findViewById(R.id.deleteMarket);

		Bundle extras = getIntent().getExtras();
		newMarket = extras.get("new_market").equals("true");
		Log.v(TAG, "newMarket = "+newMarket);
		if (newMarket) {
			Bundle bundle = getIntent().getParcelableExtra("bundle");
			location = bundle.getParcelable("lat_lng");
		} else {
			mRowId = extras.getString("market_id");
			ParseQuery<Market> query = ParseQuery.getQuery("market");
			query.getInBackground(mRowId, new GetCallback<Market>() {
				public void done(Market object, ParseException e) {
					if (e == null) {
						market = object;
						populateFields();
					} else {
						// something went wrong
					}
				}
			});
		}

		/*if (savedInstanceState == null) {
			mRowId = null;
		} else {
			mRowId = (Long) savedInstanceState.getSerializable(FeedEntry._ID);
		}

		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			Boolean newMarket = extras.get("new_market").equals("true");
			Log.v(TAG, "newMarket = "+newMarket);
			if (newMarket) {
				Bundle bundle = getIntent().getParcelableExtra("bundle");
				location = bundle.getParcelable("lat_lng");
			} else {
				mRowId = extras.getLong(FeedEntry._ID);
			}
		}*/

		

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Log.d(TAG, "itemName on Save button = "+marketName.getText().toString());
				saveState();
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
		marketName.setText(market.getName());
		openTime.setText(market.getOpen());
		closeTime.setText(market.getClose());
		location = new LatLng(market.getLatitude(), market.getLongitude());
		String daysOpen = market.getDays();
		List<String> daysOpenArray = Arrays.asList(daysOpen.split("\\s+"));
		for(int i = 0; i < daysOpenArray.size(); i++) {
			String day = daysOpenArray.get(i);
			if(day.equals("Mo")){
				daysCheckBoxes.get(0).setChecked(true);
			} else if(day.equals("Tu")){
				daysCheckBoxes.get(1).setChecked(true);
			} else if(day.equals("We")){
				daysCheckBoxes.get(2).setChecked(true);
			} else if(day.equals("Th")){
				daysCheckBoxes.get(3).setChecked(true);
			} else if(day.equals("Fr")){
				daysCheckBoxes.get(4).setChecked(true);
			} else if(day.equals("Sa")){
				daysCheckBoxes.get(5).setChecked(true);
			} else if(day.equals("Su")){
				daysCheckBoxes.get(6).setChecked(true);
			}
		}
		Log.d(TAG, "populateField " + market.toString());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i(TAG, "onSaveInstanceState");
		//saveState();
		//outState.putSerializable(FeedEntry._ID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Log.v(TAG, "onPause");
		//saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//populateFields();
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

	private void saveState() {
		Log.i(TAG, "saveState");
		Log.d(TAG, "marketId = "+mRowId);
		String name = marketName.getText().toString();
		if (name.equals(""))
			showNoticeDialog();
		String open = openTime.getText().toString();
		String close = closeTime.getText().toString();
		Double lat = location.latitude;
		Double lng = location.longitude;
		String daysOpen = "";
		for(int i = 0;  i < 7; i++){
			if (daysCheckBoxes.get(i).isChecked()){
				daysOpen += week[i];
			}
		}

		if (newMarket){
			market = new Market();
		}
		market.setName(name);
		market.setLatitude(lat);
		market.setLongitude(lng);
		market.setDays(daysOpen);
		market.setOpen(open);
		market.setClose(close);
		
		market.saveInBackground(new SaveCallback() {

	        @Override
	        public void done(ParseException e) {
	            if (e == null) {
	                setResult(Activity.RESULT_OK);
	                Toast.makeText(
	                        getApplicationContext(),
	                        "Market Saved",
	                        Toast.LENGTH_SHORT).show();
	                finish();
	            } else {
	                Toast.makeText(
	                        getApplicationContext(),
	                        "Error saving: " + e.getMessage(),
	                        Toast.LENGTH_SHORT).show();
	            }
	        }

	    });

		/*Market market;
        if (mRowId == null) {
        	//Log.v(TAG, "saveState inserting new Market");
        	market = new Market(name, lat, lng, daysOpen, open, close);
            long id = db.createMarket(market);
            if (id > 0) {
                mRowId = id;
            }
        } else {
        	market = new Market(mRowId, name, lat, lng, daysOpen, open, close);
            db.updateMarket(market);
        }*/
	}

	private void deleteState() {
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
			market.deleteInBackground();
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_market, menu);
		return true;
	}
}
