package com.bmob.im.demo.adapter;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.xy.fy.main.R;
import com.xy.fy.util.FileUtils;
import com.xy.fy.util.StaticVarUtil;

/**
 * 云文件
 * 
 * @ClassName: NewFriendAdapter
 * @Description: TODO
 * @author smile
 * @date 2014-6-9 下午1:26:12
 */
public class FileResourceAdapter extends BaseListAdapter<String> {

  public FileResourceAdapter(Context context, List<String> listFilename) {
    super(context, listFilename);
    // TODO Auto-generated constructor stub
  }

  @SuppressLint("InflateParams")
@Override
  public View bindView(int arg0, View convertView, ViewGroup arg2) {
    // TODO Auto-generated method stub
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_add_friend, null);
    }

    final String filename = getList().get(arg0);
    TextView name = ViewHolder.get(convertView, R.id.name);
    ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

    final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);
    btn_add.setText("下载");

    String prefix = filename.substring(filename.lastIndexOf(".") + 1);
    if (prefix == null || prefix.equals("")) {
      iv_avatar.setImageResource(R.drawable.iconfont_txt);
    } else if (prefix.equalsIgnoreCase("doc")) {
      iv_avatar.setImageResource(R.drawable.iconfont_word);
    } else if (prefix.equalsIgnoreCase("pdf")) {
      iv_avatar.setImageResource(R.drawable.iconfont_pdf);
    } else {
      iv_avatar.setImageResource(R.drawable.iconfont_txt);
    }

    btn_add.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub

        downloadFile(filename);

      }

    });
    name.setText(filename);

    return convertView;
  }

  ProgressDialog dialog = null;

  private void downloadFile(final String filename) {
    Log.i("bmob", "文件名：" + filename);
    dialog = new ProgressDialog(mContext);
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setTitle("正在下载...");
    dialog.setIndeterminate(false);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(false);
    dialog.show();

    BmobProFile.getInstance(mContext).download(filename, new DownloadListener() {

      @Override
      public void onSuccess(String fullPath) {
        // TODO Auto-generated method stub
        dialog.dismiss();
//        /data/data/com.xy.fy.main/cache/BmobCache/Download/78817953cfa14bbebd423769621d419d.pdf
        Log.i("bmob", "下载成功：" + fullPath);
        FileUtils.copyFile(fullPath, StaticVarUtil.PATH+"/download/files",filename);
      }

      @Override
      public void onProgress(String localPath, int percent) {
        // TODO Auto-generated method stub
        Log.i("bmob", "download-->onProgress :" + percent);
        dialog.setProgress(percent);
      }

      @Override
      public void onError(int statuscode, String errormsg) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        Log.i("bmob", "下载出错：" + statuscode + "--" + errormsg);
      }
    });
  }

}
