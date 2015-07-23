package com.bmob.im.demo.ui;

import com.bmob.im.demo.adapter.FileResourceAdapter;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.xy.fy.main.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

public class FileSourceActivity extends BaseActivity implements OnItemLongClickListener{

  ListView listview;
 
  FileResourceAdapter adapter;
  String from = "";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_file_resource);
    from = getIntent().getStringExtra("from");
    initView();
  }

  private void initView(){
    initTopBarForLeft("文件资料");
    listview = (ListView)findViewById(R.id.list_file);
    listview.setOnItemLongClickListener(this);
    adapter = new FileResourceAdapter(this,null);
    listview.setAdapter(adapter);
    if(from==null){//若来自通知栏的点击，则定位到最后一条
      listview.setSelection(adapter.getCount());
    }
    
    initTopBarForBoth("文件资料", R.drawable.iconfont_uploading, new onRightImageButtonClickListener() {
      
      @Override
      public void onClick() {
        // TODO Auto-generated method stub
        
      }
    });
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    // TODO Auto-generated method stub
    return false;
  }

  
}
