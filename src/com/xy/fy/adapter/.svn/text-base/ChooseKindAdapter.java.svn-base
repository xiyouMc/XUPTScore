package com.xy.fy.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xy.fy.main.R;

/**
 * 选择不同类别的适配器，专用于选择种类和排序
 */
public class ChooseKindAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> allInfo = new ArrayList<HashMap<String, String>>();
	private LayoutInflater inflater;

	public ChooseKindAdapter(ArrayList<HashMap<String, String>> allInfo,
			Activity activity) {
		this.allInfo = allInfo;
		this.inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return allInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return allInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(allInfo.get(position).get("kind"));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {// 不写这个很容易内存溢出
			convertView = inflater.inflate(R.layout.choose_kind_row, null);
		}
		// 设置图片
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		image.setImageResource(Integer.parseInt(allInfo.get(position).get(
				"image")));
		// 设置文字
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(allInfo.get(position).get("text"));

		return convertView;
	}

}
