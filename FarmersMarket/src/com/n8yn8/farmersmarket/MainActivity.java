package com.n8yn8.farmersmarket;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.testflightapp.lib.TestFlight;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Initialize TestFlight with your app token.
        TestFlight.takeOff(getApplication(), "17759818-827d-469a-80a5-1db253d778bd");
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void viewMarkets(View veiw){
		Intent intent = new Intent(this, MarketList.class);
		startActivity(intent);
	}
	
	public void viewGrocery(View view){
		Intent intent = new Intent(this, GroceryList.class);
		startActivity(intent);
	}
	
	public void viewInventory(View view){
		Intent intent = new Intent(this, Inventory.class);
		startActivity(intent);
	}
}
