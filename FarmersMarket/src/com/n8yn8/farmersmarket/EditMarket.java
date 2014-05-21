package com.n8yn8.farmersmarket;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class EditMarket extends Activity {
	private static final String TAG = "EditMarket";
	EditText marketName;
	EditText openTime;
	EditText closeTime;
	LatLng location;
	String[] week = new String[7];
	private Long mRowId;
	private MarketDbController mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDbHelper = new MarketDbController(this);
    	mDbHelper.open();
    	
		setContentView(R.layout.activity_edit_market);
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		location = bundle.getParcelable("lat_lng");
		marketName = (EditText) findViewById(R.id.marketName);
		openTime = (EditText) findViewById(R.id.openTime);
		closeTime = (EditText) findViewById(R.id.closeTime);
		Button confirmButton = (Button) findViewById(R.id.saveNewMarket);
		
		/*
		 * 
		 * if(savedInstanceState.getSerializable("new_market").equals("true")){
    		mRowId = null;
    	}
    	else{
    		mRowId = (Long) savedInstanceState.getSerializable(FeedEntry._ID);
    	}
    	if (mRowId == null) {
    	    Bundle extras = getIntent().getExtras();
    	    mRowId = extras != null ? extras.getLong(FeedEntry._ID)
    	                            : null;
    	}*/
    	
    	populateFields();
    	
    	confirmButton.setOnClickListener(new View.OnClickListener() {

    	    public void onClick(View view) {
    	    	//Log.v(TAG, "Save button clicked");
    	        setResult(RESULT_OK);
    	        finish();
    	        saveState();
    	        mDbHelper.close();
    	    }

    	});
	}
	
	private void populateFields() {
        if (mRowId != null) {
        	Log.v(TAG, "populateFields");
            Cursor market = mDbHelper.getMarket(mRowId);
            marketName.setText(market.getString(market.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Market)));
            openTime.setText(market.getString(market.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Open)));
            closeTime.setText(market.getString(market.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Close)));
            location = new LatLng(market.getLong(market.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Lat)),
            		market.getLong(market.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Lng)));
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
        //Log.v(TAG, "onPause");
        //saveState();
    }
	
	@Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
	
	private void saveState() {
		//Log.v(TAG, "saveState");
		String name = marketName.getText().toString();
		String open = openTime.getText().toString();
		String close = closeTime.getText().toString();
		Double lat = location.latitude;
		Double lng = location.longitude;
		String daysOpen = "";
		for(int i = 0; i < week.length; i++)
			if(week[i] != null)
				daysOpen += week[i];
		
        if (mRowId == null) {
        	//Log.v(TAG, "saveState inserting new Market");
            long id = mDbHelper.insertMarket(lat, lng, name, daysOpen, open, close);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateMarket(mRowId, lat, lng, name, daysOpen, open, close);
        }
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
