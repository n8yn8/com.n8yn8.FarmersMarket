package com.n8yn8.farmersmarket;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

//from vogella.com
public class InteractiveArrayAdapter extends ArrayAdapter<Model> {

	private final List<Model> list;
	private final Activity context;

	public InteractiveArrayAdapter(Activity context, List<Model> list) {
		super(context, R.layout.row_shop, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
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
					Model element = (Model) viewHolder.checkbox
							.getTag();
					element.setSelected(buttonView.isChecked());

				}
			});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.itemName.setText(list.get(position).getName());
		holder.price.setText(list.get(position).getPrice());
		holder.unit.setText(list.get(position).getUnit());
		holder.vendor.setText(list.get(position).getVendor());
		holder.checkbox.setFocusable(false);
		holder.checkbox.setChecked(list.get(position).isSelected());
		return view;
	}
} 