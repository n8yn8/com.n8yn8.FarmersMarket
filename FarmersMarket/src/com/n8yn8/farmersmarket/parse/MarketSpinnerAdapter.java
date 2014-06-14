package com.n8yn8.farmersmarket.parse;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MarketSpinnerAdapter extends ParseQueryAdapter<Market>{

private static String TAG = "MarketSpinnerAdapter";
    
    public MarketSpinnerAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Market>() {
            public ParseQuery<Market> create() {
            	Log.i(TAG, "ParseQuery");
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
                ParseQuery query = new ParseQuery("market");
                //query.whereContainedIn("rating", Arrays.asList("5", "4"));
                query.orderByDescending("name");
                return query;
            }
        });
    }

    @Override
    public View getItemView(Market market, View v, ViewGroup parent) {
    	Log.i(TAG, "getItemView");
     
        if (v == null) {
            v = View.inflate(getContext(), android.R.layout.simple_spinner_item, null);
        }
     
        super.getItemView(market, v, parent);
     
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
        
        name.setText(market.getName());

        return v;
    }
}
