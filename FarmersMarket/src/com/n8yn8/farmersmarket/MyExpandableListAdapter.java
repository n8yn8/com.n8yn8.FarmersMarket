package com.n8yn8.farmersmarket;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	private final SparseArray<Group> groups;
	public LayoutInflater inflater;
	public Activity context;

	public MyExpandableListAdapter(Activity context, SparseArray<Group> groups) {
		this.context = context;
		this.groups = groups;
		inflater = context.getLayoutInflater();
	}

	@Override
	public Model getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View view = null;
		Model child = getChild(groupPosition, childPosition);
		if (convertView == null) {
	      view = inflater.inflate(R.layout.row_shop, null);
	      final ViewHolder viewHolder = new ViewHolder();
	      viewHolder.itemName = (TextView) view.findViewById(R.id.item_name);
	      viewHolder.price = (TextView) view.findViewById(R.id.item_price);
	      viewHolder.unit = (TextView) view.findViewById(R.id.item_unit);
	      viewHolder.vendor = (TextView) view.findViewById(R.id.item_vendor);
	      viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
	      viewHolder.checkbox
	          .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	            @Override
	            public void onCheckedChanged(CompoundButton buttonView,
	                boolean isChecked) {
	              Model element = (Model) viewHolder.checkbox
	                  .getTag();
	              element.setSelected(buttonView.isChecked());

	            }
	          });
	      view.setTag(viewHolder);
	      viewHolder.checkbox.setTag(groups.get(groupPosition).children.get(childPosition));
	    } else {
	      view = convertView;
	      ((ViewHolder) view.getTag()).checkbox.setTag(groups.get(groupPosition).children.get(childPosition));
	    }
	    ViewHolder holder = (ViewHolder) view.getTag();
	    holder.itemName.setText(child.getName());
	    holder.price.setText(child.getPrice());
		holder.unit.setText(child.getUnit());
		holder.vendor.setText(child.getVendor());
	    holder.checkbox.setFocusable(false);
	    holder.checkbox.setChecked(child.isSelected());
	    return view;
	    
		/*Model children = getChild(groupPosition, childPosition);
		TextView name = null;
		TextView price = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_shop, null);
		}
		name = (TextView) convertView.findViewById(R.id.item_name);
		price = (TextView) convertView.findViewById(R.id.item_price);
		name.setText(children.getName());
		price.setText(children.getPrice());
		return convertView;*/
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_group, null);
		}
		Group group = (Group) getGroup(groupPosition);
		((CheckedTextView) convertView).setText(group.string);
		((CheckedTextView) convertView).setChecked(isExpanded);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}