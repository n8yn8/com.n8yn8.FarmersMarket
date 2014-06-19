package com.n8yn8.farmersmarket.parse;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

public class VendorSpinnerAdapter extends ParseQueryAdapter<Vendor>{

	private static String TAG = "VendorSpinnerAdapter";

	public VendorSpinnerAdapter(Context context) {
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
            v = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
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
