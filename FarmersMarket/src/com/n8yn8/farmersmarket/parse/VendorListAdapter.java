package com.n8yn8.farmersmarket.parse;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class VendorListAdapter extends ParseQueryAdapter<Vendor>{
	
	private static String TAG = "MarketListAdapter";
    
    public VendorListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Vendor>() {
            public ParseQuery<Vendor> create() {
            	Log.i(TAG, "ParseQuery");
                ParseQuery query = new ParseQuery("vendor");
                query.orderByDescending("name");
                return query;
            }
        });
    }
 
    @Override
    public View getItemView(Vendor vendor, View v, ViewGroup parent) {
    	Log.i(TAG, "getItemView");
     
        if (v == null) {
            v = View.inflate(getContext(), android.R.layout.simple_list_item_1, null);
        }
     
        /* For adding an image.
        ParseImageView mealImage = (ParseImageView) v.findViewById(R.id.icon);
        ParseFile photoFile = meal.getParseFile("photo");
        if (photoFile != null) {
            mealImage.setParseFile(photoFile);
            mealImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    // nothing to do
                }
            });
        }*/
     
        TextView name = (TextView)v.findViewById(android.R.id.text1);
        
        name.setText(vendor.getName());

        return v;
    }

}