package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.xy.fy.util.ViewUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CheckVersionAsynctask extends AsyncTask<String, String, String> {

  private Context mContext;

  private boolean isShowDialog;

  public CheckVersionAsynctask(Context context, boolean isShow) {
    // TODO Auto-generated constructor stub
    this.mContext = context;
    this.isShowDialog = isShow;
  }

  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    return HttpUtilMc.queryStringForPost(
        HttpUtilMc.BASE_URL + "checkversion.jsp?version=" + Util.getVersion(mContext));
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    if (isShowDialog) {
      ProgressDialogUtil.getInstance(mContext).dismiss();
    }
    try {
      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
        if (!result.equals("no")) {
          String[] str = result.split("\\|");
          String apk_url = str[0];
          String new_version = str[1];
          String update_content = str[2];
          VersionUpdate versionUpdate = new VersionUpdate(
              mContext);
          versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
          versionUpdate.updateMsg = new_version + "\n\n"
              + update_content;
          versionUpdate.checkUpdateInfo();
          return;
        }
        ViewUtil.showToast(mContext, "×îÐÂ°æ±¾£¡");
      }

    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      Log.i("LoginActivity", e.toString());
    }
  }

}
