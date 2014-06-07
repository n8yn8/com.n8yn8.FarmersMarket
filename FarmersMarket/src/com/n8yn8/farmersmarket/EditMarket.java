package com.n8yn8.farmersmarket;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.n8yn8.farmersmarket.Contract.FeedEntry;
import com.n8yn8.farmersmarket.fragments.NoNameAlertFragment;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;

public class EditMarket extends Activity implements NoNameAlertFragment.NoticeDialogListener {
	private static final String TAG = "EditMarket";
	EditText marketName;
	EditText openTime;
	EditText closeTime;
	LatLng location;
	String[] week = new String[7];
	private Long mRowId;
	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new DatabaseHelper(this);
    	
		setContentView(R.layout.activity_edit_market);
		marketName = (EditText) findViewById(R.id.marketName);
		openTime = (EditText) findViewById(R.id.openTime);
		closeTime = (EditText) findViewById(R.id.closeTime);
		Button confirmButton = (Button) findViewById(R.id.saveNewMarket);
		Button deleteButton = (Button) findViewById(R.id.deleteMarket);
		
		if (savedInstanceState == null) {
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
		}
		/*
		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(FeedEntry._ID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(FeedEntry._ID)
					: null;
		}*/
    	populateFields();
    	
    	confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Log.d(TAG, "itemName on Save button = "+marketName.getText().toString());
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
        if (mRowId != null) {
        	Log.v(TAG, "populateFields");
            Market market = db.getMarket(mRowId);
            marketName.setText(market.getName());
            openTime.setText(market.getOpen());
            closeTime.setText(market.getClose());
            location = new LatLng(market.getLatitude(), market.getLongitude());
            //TODO populate week fields
        }
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.v(TAG, "onSaveInstanceState");
        saveState();
        outState.putSerializable(FeedEntry._ID, mRowId);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        db.close();
        //Log.v(TAG, "onPause");
        //saveState();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        populateFields();
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
	
	private boolean saveState() {
		Log.i(TAG, "saveState");
		String name = marketName.getText().toString();
		String open = openTime.getText().toString();
		String close = closeTime.getText().toString();
		Double lat = location.latitude;
		Double lng = location.longitude;
		String daysOpen = "";
		for(int i = 0; i < week.length; i++)
			if(week[i] != null)
				daysOpen += week[i];
		Market market;
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
        }
        return (!name.equals(""));
    }
	
	private void deleteState() {
		setResult(RESULT_CANCELED);
		if(mRowId != null){
			Log.d(TAG, "mRowId to delete = "+mRowId);
			db.deleteMarket(mRowId);
		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_market, menu);
		return true;
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    
	    switch(view.getId()) {
	        case R.id.checkMon:
	            if (checked)
	                week[0] = "Mo ";
	            else
	            	week[0] = "";
	            break;
	        case R.id.checkTue:
	            if (checked)
	            	week[1] = "Tu ";
	            else
	            	week[1] = "";
	            break;
	        case R.id.checkWed:
	            if (checked)
	            	week[2] = "We ";
	            else
	            	week[2] = "";
	            break;
	        case R.id.checkThu:
	            if (checked)
	            	week[3] = "Th ";
	            else
	            	week[3] = "";
	            break;
	        case R.id.checkFri:
	            if (checked)
	            	week[4] = "Fr ";
	            else
	            	week[4] = "";
	            break;
	        case R.id.checkSat:
	            if (checked)
	            	week[5] = "Sa ";
	            else
	            	week[5] = "";
	            break;
	        case R.id.checkSun:
	            if (checked)
	            	week[6] = "Su ";
	            else
	            	week[6] = "";
	            break;
	    }
	    
	}
}
