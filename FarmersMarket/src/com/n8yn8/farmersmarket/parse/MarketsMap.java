package com.n8yn8.farmersmarket.parse;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.ShopMarket;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MarketsMap extends Activity implements OnMapLongClickListener, 
OnInfoWindowClickListener, GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener{
	
	private static final String TAG = "MarketsMap";
	
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;
	LocationClient mLocationClient;
	Location mCurrentLocation;
	private GoogleMap mMap;
	private final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.activity_markets_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);

		setUpMapIfNeeded();
		//db.open();
		//placeMarkers();
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        // Connect the client.
        mLocationClient.connect();
    }
	@Override
    protected void onStop() {
		Log.i(TAG, "onStop");
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_markets, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "onOptionsItemSelected");
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
		Log.i(TAG, "onResume");
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		Log.i(TAG, "setUpMapIfNeeded");
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		Log.i(TAG, "setUpMap");
		mMap.setOnMapLongClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setMyLocationEnabled(true);
	}

	private void placeMarkers(){
		Log.i(TAG, "placeMarkers");

		ParseQuery<Market> query = ParseQuery.getQuery("market");
		query.findInBackground(new FindCallback<Market>() {
		    public void done(List<Market> markets, ParseException e) {
		        if (e == null) {
		            Log.d("TAG", "Retrieved " + markets.size() + " scores");
		            for (Market market : markets) {
		    			LatLng here = new LatLng(market.getLatitude(), market.getLongitude());
		    			String hours = market.getOpen()+"-" + market.getClose();
		    			mMap.addMarker(new MarkerOptions().position(here).title(market.getName()).snippet(market.getDays() + " " + hours));
		    		}
		        } else {
		            Log.d("TAG", "Error: " + e.getMessage());
		        }
		    }
		});
		

	}

	@Override
	public void onMapLongClick(LatLng location) {
		Log.i(TAG, "onMapLongClick");
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
		Log.i(TAG, "onActivityResult");
		
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK :
				/*
				 * Try the request again
				 */
				break;
			}
		case ACTIVITY_CREATE :
			placeMarkers();
		}
	}

	/*
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Get the error code
			int errorCode = connectionResult.getErrorCode();
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					errorCode,
					this,
					CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment =
						new ErrorDialogFragment();
				// Set the dialog in the DialogFragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the DialogFragment
				errorFragment.show(getFragmentManager(),
						"Location Updates");
			}
		}
	}
*/
	public void onInfoWindowClick(Marker marker) {
		Log.i(TAG, "onInfoWindowClick");
		String market = marker.getTitle();
		Intent intent = new Intent(this, ShopMarket.class);
		intent.putExtra("market_name", market);
		startActivity(intent);
	}
	
	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
    	Log.i(TAG, "onConnected");
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

		mCurrentLocation = mLocationClient.getLastLocation();
		LatLng mCurrentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
		Log.d(TAG, mCurrentLatLng.toString());
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, 13));
		placeMarkers();
    }
    
    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
    	Log.i(TAG, "onDisconnected");
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    	Log.i(TAG, "onConnectionFailed");
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showDialog(connectionResult.getErrorCode());
        }
    }

}
