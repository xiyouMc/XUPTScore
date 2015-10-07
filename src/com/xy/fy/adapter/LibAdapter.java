package com.xy.fy.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mc.util.H5Toast;
import com.xy.fy.asynctask.RenewLibAsynctask;
import com.xy.fy.asynctask.XuptLibLoginAsynctask;
import com.xy.fy.main.R;
import com.xy.fy.singleton.BookList;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LibAdapter extends BaseAdapter {
  
  private ArrayList<BookList> allBookList = null;
  private LayoutInflater inflater = null;
  private Context context;
  private LibAdapter libAdapter;
  public LibAdapter(ArrayList<BookList> allBookList,Context context) {
    // TODO Auto-generated constructor stub
    this.allBookList = allBookList;
    this.inflater = LayoutInflater.from(context);
    this.context = context;
    this.libAdapter = this;
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
    final String barcode = allBookList.get(position).getBarcode();
    if (allBookList.get(position).isRenew()) {
      holder.renew_date.setVisibility(View.VISIBLE);
      holder.renewBtn.setOnClickListener(new OnClickListener() {
        
        @Override
        public void onClick(View v) {
          // TODO Auto-generated method stub
          RenewLibAsynctask renewLibAsynctask = new RenewLibAsynctask((Activity)context,new RenewLibAsynctask.OnRenew() {
            
            @Override
            public void onRenew(String result,ProgressDialog progressDialog) {
              // TODO Auto-generated method stub
              if ("success".equals(result)) {
                login_lib(progressDialog);
              }
            }
          });
          renewLibAsynctask.execute(new String[]{StaticVarUtil.LIB_NAME + "&barcode="+barcode+"&libraryId=A&departmentId=05"});
        }
      });
    }else {
      holder.renewBtn.setVisibility(View.GONE);
    }
    return view;
  }

  private void login_lib(final ProgressDialog progressDialog) {
    XuptLibLoginAsynctask xuptLibLoginAsynctask = new XuptLibLoginAsynctask((Activity)context,
        StaticVarUtil.LIB_NAME, new XuptLibLoginAsynctask.Login() {

          @Override
          public void onPostLogin(String result) {
            // TODO Auto-generated method stub
            if (!"fail".equals(result.trim())) {// 获取数据
                try {
                  ArrayList<BookList> allBookList = new ArrayList<BookList>();
                  JSONArray bookArray = new JSONArray(result);
                  for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject jo = (JSONObject) bookArray.get(i);
                    BookList bookList = new BookList();
                    bookList.setLibName(jo.getString("name"));
                    bookList.setNumber(jo.getString("id"));
                    String[] date = jo.getString("date").split("/");
                    bookList.setLibRenewDate(date[0] + "年" + date[1] + "月" + date[2]);
                    bookList.setBarcode(jo.getString("barcode"));
                    bookList.setRenew(jo.getBoolean("isRenew"));
                    allBookList.add(bookList);
                  }

                  StaticVarUtil.allBookList = allBookList;
//                  ShowLibMessage(bind_layout, allBookList);
                  H5Toast.showToast(context, "续借成功");
                  libAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
            } 
            progressDialog.dismiss();
          }

        });
    xuptLibLoginAsynctask.execute();
  }
  class ViewHolder {
    TextView book_numbe;//number
    TextView book_name;// 日期
    TextView renew_date;// 时间
    Button renewBtn;
  }
}
