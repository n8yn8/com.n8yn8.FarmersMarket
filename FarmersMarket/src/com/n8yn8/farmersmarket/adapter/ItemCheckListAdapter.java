package com.n8yn8.farmersmarket.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.models.Item;

//from vogella.com
public class ItemCheckListAdapter extends BaseAdapter {

	public final List<Item> items;
	private final Activity activity;
	private static LayoutInflater inflater=null;

	public ItemCheckListAdapter(Activity activity, List<Item> list) {
		this.activity = activity;
		this.items = list;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = activity.getLayoutInflater();
			view = inflator.inflate(R.layout.row_shop, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.itemName = (TextView) view.findViewById(R.id.item_name);
			viewHolder.price = (TextView) view.findViewById(R.id.item_price);
			viewHolder.unit = (TextView) view.findViewById(R.id.item_unit);
			viewHolder.vendor = (TextView) view.findViewById(R.id.item_vendor);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Item element = (Item) viewHolder.checkbox
							.getTag();
					element.setSelected(buttonView.isChecked());

				}
			});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(items.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(items.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.itemName.setText(items.get(position).getName());
		holder.price.setText(items.get(position).getPrice());
		holder.unit.setText(items.get(position).getUnit());
		holder.vendor.setText(items.get(position).getVendorName());
		holder.checkbox.setFocusable(false);
		holder.checkbox.setChecked(items.get(position).isSelected());
		return view;
	}
} 