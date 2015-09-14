package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class CheckVersionAsynctask extends AsyncTask<String, String, String> {

  private Activity mActivity;

  private boolean isShowDialog;

  public CheckVersionAsynctask(Activity mActivity, boolean isShow) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.isShowDialog = isShow;
  }

  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    return HttpUtilMc.queryStringForPost(
        HttpUtilMc.BASE_URL + "checkversion.jsp?version=" + Util.getVersion(mActivity));
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    if (isShowDialog) {
      ProgressDialogUtil.getInstance(mActivity).dismiss();
    }
    try {
      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
        if (!result.equals("no")) {
          String[] str = result.split("\\|");
          String apk_url = str[0];
          String new_version = str[1];
          String update_content = str[2];
          VersionUpdate versionUpdate = new VersionUpdate(
              mActivity);
          versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
          versionUpdate.updateMsg = new_version + "\n\n"
              + update_content;
          versionUpdate.checkUpdateInfo();
          return;
        }
        ViewUtil.showToast(mActivity, "×îÐÂ°æ±¾£¡");
      }

    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
      Log.i("LoginActivity", e.toString());
    }
  }

}
