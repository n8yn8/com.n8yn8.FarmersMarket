package com.n8yn8.farmersmarket.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.models.Market;

public class MarketListAdapter extends BaseAdapter{

	private Activity activity;
    private List<Market> markets;
    private static LayoutInflater inflater=null;
    
    public MarketListAdapter(Activity a, List<Market> d) {
        activity = a;
        markets=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return markets.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
    
    public Market getMarket(int position){
        return markets.get(position);
     }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
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
    }

}