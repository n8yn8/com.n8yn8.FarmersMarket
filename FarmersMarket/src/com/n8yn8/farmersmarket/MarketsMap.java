package com.n8yn8.farmersmarket;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.n8yn8.farmersmarket.models.DatabaseHelper;
import com.n8yn8.farmersmarket.models.Market;

public class MarketsMap extends Activity implements OnMapLongClickListener, OnInfoWindowClickListener{
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	private GoogleMap mMap;
	private DatabaseHelper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_markets_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setUpMapIfNeeded();
		db = new DatabaseHelper(this);
        //db.open();
		//placeMarkers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_markets, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setMyLocationEnabled(true);
    }
    
    private void placeMarkers(){

    	List<Market> markets = db.getAllMarkets();
    	for (Market market : markets) {
    		LatLng here = new LatLng(market.getLatitude(), market.getLongitude());
    		String hours = market.getOpen()+"-" + market.getClose();
    		mMap.addMarker(new MarkerOptions().position(here).title(market.getName()).snippet(market.getDays() + " " + hours));
    	}
    	
    }

	@Override
	public void onMapLongClick(LatLng location) {
		Intent intent = new Intent(this, EditMarket.class);
		Bundle args = new Bundle();
		args.putParcelable("lat_lng", location);
		intent.putExtra("bundle", args);
		intent.putExtra("new_market", "true");
		startActivityForResult(intent, ACTIVITY_CREATE);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        placeMarkers();
    }
	
	public void onInfoWindowClick(Marker marker) {
		String market = marker.getTitle();
		Intent intent = new Intent(this, ShopMarket.class);
		intent.putExtra("market_name", market);
		startActivity(intent);
    }

}
