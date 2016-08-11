package com.xy.fy.adapter;

import java.util.ArrayList;

import com.xy.fy.main.R;
import com.xy.fy.singleton.Language;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LanguageAdapter extends BaseAdapter {

  private ArrayList<Language> allLanguageList = null;
  private LayoutInflater inflater = null;

  public LanguageAdapter(ArrayList<Language> allLanguageList, Context context) {
    // TODO Auto-generated constructor stub
    this.allLanguageList = allLanguageList;
    this.inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return allLanguageList.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return allLanguageList.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    // TODO Auto-generated method stub
    if (view == null) {
      view = inflater.inflate(R.layout.language_item, null);
    }
    
    ViewHolder holder = new ViewHolder();
    holder.language = (TextView)view.findViewById(R.id.language);
    holder.icon = (ImageView)view.findViewById(R.id.icon);
    
    holder.language.setText(allLanguageList.get(position).getLanguage());
    if (allLanguageList.get(position).getSelect()) {
      holder.icon.setVisibility(View.VISIBLE);
    } else {
      holder.icon.setVisibility(View.INVISIBLE);
    }
    return view;
  }

  class ViewHolder {
    TextView language;
    ImageView icon;
  }
}
