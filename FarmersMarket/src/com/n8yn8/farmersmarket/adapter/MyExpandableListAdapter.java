package com.n8yn8.farmersmarket.adapter;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.n8yn8.farmersmarket.R;
import com.n8yn8.farmersmarket.models.Group;
import com.n8yn8.farmersmarket.models.Item;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

	private final String TAG = "MyExpandableListAdapter";

	private final SparseArray<Group> groups;
	public LayoutInflater inflater;
	public Activity context;

	public MyExpandableListAdapter(Activity context, SparseArray<Group> groups) {
		Log.i(TAG, "Constructor");
		this.context = context;
		this.groups = groups;
		inflater = context.getLayoutInflater();
	}

	@Override
	public Item getChild(int groupPosition, int childPosition) {
		Log.i(TAG, "getChild");
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		Log.i(TAG, "getChildId");
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		Log.i(TAG, "getChildView");
		View view = null;
		Item child = getChild(groupPosition, childPosition);
		if (convertView == null) {
			Log.v(TAG, "converView = null, creating convertview");
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
					Item element = (Item) viewHolder.checkbox
							.getTag();
					if(buttonView.isChecked())
						element.setAdded("yes");
					else
						element.setAdded("no");
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
		Log.v(TAG, "getChildView child name = " + child.getName());
		holder.price.setText(child.getPrice());
		holder.unit.setText(child.getUnit());
		holder.vendor.setText(child.getVendor());
		holder.checkbox.setFocusable(false);
		holder.checkbox.setChecked(child.isAdded());
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
		Log.i(TAG, "getChildrenCount");
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		Log.i(TAG, "getGroup");
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		Log.i(TAG, "getGroupCount");
		Log.v(TAG, "groups.size = " + groups.size());
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
		Log.i(TAG, "onGroupCollapsed");
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
		Log.i(TAG, "onGroupExpanded");
	}

	@Override
	public long getGroupId(int groupPosition) {
		Log.i(TAG, "getGroupId");
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		Log.i(TAG, "getGroupView");
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
		Log.i(TAG, "hasStableIds");
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		Log.i(TAG, "isChildSelectable");
		return false;
	}
}