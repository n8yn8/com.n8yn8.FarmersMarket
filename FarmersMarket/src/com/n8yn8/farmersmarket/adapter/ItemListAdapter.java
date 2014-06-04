package com.n8yn8.farmersmarket.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.models.Item;
import com.n8yn8.farmersmarket.models.Vendor;

public class ItemListAdapter extends BaseAdapter{
	
	private Activity activity;
    private List<Item> items;
    private static LayoutInflater inflater=null;
    
    public ItemListAdapter(Activity a, List<Item> d) {
        activity = a;
        items=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return items.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.row_inventory, null);
 
        TextView name = (TextView)vi.findViewById(R.id.item_name);
        TextView price = (TextView)vi.findViewById(R.id.item_price);
        TextView unit = (TextView)vi.findViewById(R.id.item_unit);
        //ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
 
        Item item = items.get(position);
 
        // Setting all values in listview
        name.setText(item.getName());
        price.setText(item.getPrice());
        unit.setText(item.getUnit());
        return vi;
    }

}