package com.n8yn8.farmersmarket.parse;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class ItemListAdapter extends ParseQueryAdapter<Item>{
	
	private static String TAG = "ItemsListAdapter";
    
    public ItemListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
            	Log.i(TAG, "ParseQuery");
                ParseQuery query = new ParseQuery("item");
                query.orderByDescending("name");
                return query;
            }
        });
    }
 
    @Override
    public View getItemView(Item item, View v, ViewGroup parent) {
    	Log.i(TAG, "getItemView");
     
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_inventory, null);
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
     
        TextView name = (TextView)v.findViewById(R.id.item_name);
        TextView price = (TextView)v.findViewById(R.id.item_price);
        TextView unit = (TextView)v.findViewById(R.id.item_unit);
        
        name.setText(item.getName());
        price.setText(item.getPrice());
        unit.setText(item.getUnit());

        return v;
    }

}