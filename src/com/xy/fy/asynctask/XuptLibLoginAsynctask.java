package com.xy.fy.asynctask;

import java.net.URL;
import java.net.URLEncoder;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.os.AsyncTask;

public class XuptLibLoginAsynctask extends AsyncTask<String, String, String> {

  private Activity mActivity;
  private String libName;
  private Login login;

  public interface Login {
    void onPostLogin(String result);
  }

  public XuptLibLoginAsynctask(Activity mActivity, String libName, Login login) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.libName = libName;
    this.login = login;
    ProgressDialogUtil.getInstance(mActivity).show();
  }

  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    String url = HttpUtilMc.LIB_URL+ "/servlet/LoginServlet?data="
        + URLEncoder.encode(Util.getBindLibParmas(mActivity, libName)) + "&viewstate="
        + URLEncoder.encode(StaticVarUtil.viewstate);
    return HttpUtilMc.queryStringForPost(url);
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    login.onPostLogin(result);
    ProgressDialogUtil.getInstance(mActivity).dismiss();
  }

}
