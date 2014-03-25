package com.n8yn8.farmersmarket;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class MarketsMap extends Activity implements OnMapLongClickListener, OnInfoWindowClickListener{
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
	private GoogleMap mMap;
	private MarketDbController mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_markets_map);
		setUpMapIfNeeded();
		mDbHelper = new MarketDbController(this);
        mDbHelper.open();
		//placeMarkers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_markets, menu);
		return true;
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

    	Cursor cursor = mDbHelper.getAllMarkets();
    	if (cursor.moveToFirst()) {
    		do{
    			long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
    			LatLng here = new LatLng(
    					cursor.getDouble(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Lat)),
    					cursor.getDouble(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Lng))
    					);
    			String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Market));
    			String days = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Days));
    			String hours = cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Open))+
    					"-" + cursor.getString(cursor.getColumnIndexOrThrow(FeedEntry.COLUMN_NAME_Close));
    			mMap.addMarker(new MarkerOptions().position(here).title(name).snippet(days + " " + hours));
    		}while (cursor.moveToNext());
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
