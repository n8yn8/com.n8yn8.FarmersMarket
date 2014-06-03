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
import com.n8yn8.farmersmarket.models.Vendor;

public class VendorListAdapter extends BaseAdapter{
	
	private Activity activity;
    private List<Vendor> vendors;
    private static LayoutInflater inflater=null;
    
    public VendorListAdapter(Activity a, List<Vendor> d) {
        activity = a;
        vendors=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return vendors.size();
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
            vi = inflater.inflate(android.R.layout.simple_list_item_1, null);
 
        TextView name = (TextView)vi.findViewById(android.R.id.text1);
        //ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image);
 
        Vendor vendor = new Vendor();
        vendor = vendors.get(position);
 
        // Setting all values in listview
        name.setText(vendor.getName());
        return vi;
    }

}