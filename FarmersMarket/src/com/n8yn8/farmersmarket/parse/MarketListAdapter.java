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

public class MarketListAdapter extends ParseQueryAdapter<Market>{
	
	private static String TAG = "MarketListAdapter";
    
    public MarketListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Market>() {
            public ParseQuery<Market> create() {
            	Log.i(TAG, "ParseQuery");
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
                ParseQuery query = new ParseQuery("market");
                //query.whereContainedIn("rating", Arrays.asList("5", "4"));
                //query.orderByDescending("rating");
                return query;
            }
        });
    }
 
    @Override
    public View getItemView(Market market, View v, ViewGroup parent) {
    	Log.i(TAG, "getItemView");
     
        if (v == null) {
            v = View.inflate(getContext(), R.layout.row_markets, null);
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
     
        TextView name = (TextView)v.findViewById(R.id.marketName); 
        TextView days = (TextView)v.findViewById(R.id.days);
        TextView open = (TextView)v.findViewById(R.id.open);
        TextView close = (TextView)v.findViewById(R.id.close);
        
        name.setText(market.getName());
        Log.d(TAG, "market retreived = "+market.getName());
        days.setText(market.getDays());
        open.setText(market.getOpen());
        close.setText(market.getClose());

        return v;
    }
    
    /*public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.row_markets, null);
 
        TextView name = (TextView)vi.findViewById(R.id.marketName); 
        TextView days = (TextView)vi.findViewById(R.id.days);
        TextView open = (TextView)vi.findViewById(R.id.open);
        TextView close = (TextView)vi.findViewById(R.id.close);
        //ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
 
        Market market = new Market();
        market = markets.get(position);
 
        // Setting all values in listview
        name.setText(market.getName());
        days.setText(market.getDays());
        open.setText(market.getOpen());
        close.setText(market.getClose());
        return vi;
    }*/

}
