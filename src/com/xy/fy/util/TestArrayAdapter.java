package com.xy.fy.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestArrayAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private String[] mStringArray;
	private int resource;
	private int width;

	public TestArrayAdapter(int resource, Context context,
			String[] stringArray, int width) {
		super(context, android.R.layout.simple_spinner_item, stringArray);
		mContext = context;
		mStringArray = stringArray;
		this.resource = resource;
		this.width = width;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner展开后的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(resource, parent, false);
			convertView.setBackgroundColor(Color.WHITE);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(14f);
		tv.setTextColor(Color.GRAY);

		return convertView;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner选择后结果的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			convertView = inflater.inflate(
					android.R.layout.simple_spinner_item, parent, false);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(14f);
		tv.setTextColor(Color.GRAY);
		return convertView;
	}

}
