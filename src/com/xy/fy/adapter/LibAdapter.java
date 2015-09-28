package com.xy.fy.adapter;

import java.util.ArrayList;

import com.xy.fy.main.R;
import com.xy.fy.singleton.BookList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class LibAdapter extends BaseAdapter {
  
  private ArrayList<BookList> allBookList = null;
  private LayoutInflater inflater = null;
  public LibAdapter(ArrayList<BookList> allBookList,Context context) {
    // TODO Auto-generated constructor stub
    this.allBookList = allBookList;
    this.inflater = LayoutInflater.from(context);
        
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return allBookList.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub
    return allBookList.get(position);
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
      view = inflater.inflate(R.layout.list_item_sample, null);
    }
    ViewHolder holder = new ViewHolder();
    holder.book_numbe = (TextView) view.findViewById(R.id.book_numbe);
    holder.book_name = (TextView) view.findViewById(R.id.book_name);
    holder.renew_date = (TextView) view.findViewById(R.id.renew_date);
    holder.renewBtn = (Button) view.findViewById(R.id.renewBtn);

    holder.book_numbe.setText(allBookList.get(position).getNumber());
    holder.book_name.setText(allBookList.get(position).getLibName());
    holder.renew_date.setText(allBookList.get(position).getLibRenewDate());
    Log.d("LibAdapter", allBookList.get(position).getLibRenewDate() + " " +allBookList.get(position).isRenew());
    if (allBookList.get(position).isRenew()) {
      holder.renew_date.setVisibility(View.VISIBLE);
      holder.renewBtn.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
          // TODO Auto-generated method stub
          
        }
      });
    }else {
      holder.renewBtn.setVisibility(View.GONE);
    }
    return view;
  }

  class ViewHolder {
    TextView book_numbe;//number
    TextView book_name;// 日期
    TextView renew_date;// 时间
    Button renewBtn;
  }
}
