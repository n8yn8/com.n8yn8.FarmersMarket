package com.n8yn8.farmersmarket;

import com.n8yn8.farmersmarket.parse.Item;
import com.n8yn8.farmersmarket.parse.Market;
import com.n8yn8.farmersmarket.parse.Vendor;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;
import android.util.Log;

public class ParseApplication extends Application {
	String TAG = "ParseApplication";

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		
		super.onCreate();

		// Add your initialization code here
		//Switch imports to parse package to use parse.
		ParseObject.registerSubclass(Market.class);
		ParseObject.registerSubclass(Vendor.class);
		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "RNk2C6FdTlpXuNWgCNkF4Hk4Q38XrUpyxCFISFo7", "nRVqyC5iuOLANNWmjwq41VAS1bJf2Hl6IJ4Ad4Sy");


		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}

}
