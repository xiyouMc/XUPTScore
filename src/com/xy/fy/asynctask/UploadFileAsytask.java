package com.xy.fy.asynctask;

import java.io.File;

import com.mc.util.HttpAssist;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

public class UploadFileAsytask extends AsyncTask<String, String, String> {

  private Context context;
  private Bitmap bitmap;

  public UploadFileAsytask(Context context, Bitmap bitmap) {
    // TODO Auto-generated constructor stub
    this.context = context;
    this.bitmap = bitmap;
  }

  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    return HttpAssist.uploadFile(new File(params[0]), StaticVarUtil.student.getAccount());
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    try {
      if (result.equals("error")) {
        return;
      }
      if (Util.isExternalStorageWritable()) {
        Util.saveBitmap2file(bitmap, result, context);
        bitmap.recycle();
      }
      ViewUtil.showToast(context, !HttpUtilMc.CONNECT_EXCEPTION.equals(result)
          ? !result.equals("error") ? "修改成功" : "修改失败" : HttpUtilMc.CONNECT_EXCEPTION);
    } catch (Exception e) {
      Log.i("LoginActivity", e.toString());
    }
  }

}
