package com.n8yn8.farmersmarket.parse;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.n8yn8.farmersmarket.R;

public class MarketCheckListAdapter extends BaseAdapter{
	
	private String TAG = "MarketCheckListAdapter";
	
	private Activity activity;
    public List<Market> markets;
    private static LayoutInflater inflater=null;
    
    public class MarketCheckListViewHolder {
		protected CheckBox checkbox;
	}
    
    public MarketCheckListAdapter(Activity a, List<Market> d) {
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
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        Market market = markets.get(position);
        if(convertView==null){
            vi = inflater.inflate(R.layout.row_checklist_markets, null);
            final MarketCheckListViewHolder viewHolder = new MarketCheckListViewHolder();
            viewHolder.checkbox = (CheckBox)vi.findViewById(R.id.marketCheck);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                  Market element = (Market) viewHolder.checkbox.getTag();
                  Log.v(TAG, "selected market = "+element.getName());
                  element.setSelected(buttonView.isChecked());
                }
              });
            vi.setTag(viewHolder);
  	      viewHolder.checkbox.setTag(markets.get(position));
        } else {
  	      vi = convertView;
  	      ((MarketCheckListViewHolder) vi.getTag()).checkbox.setTag(markets.get(position));
  	    }
        
        MarketCheckListViewHolder holder = (MarketCheckListViewHolder) vi.getTag();
	    holder.checkbox.setText(market.getName());
	    holder.checkbox.setFocusable(false);
	    holder.checkbox.setChecked(market.isSelected());
	    
        return vi;
    }

}
