package com.xy.fy.asynctask;

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.os.AsyncTask;

public class BindXuptLibAsyncTask extends AsyncTask<String, String, String> {

  private Activity mActivity;
  private String libName;
  private String type;

  private OnPostExecute onPostExecute;

  public interface OnPostExecute {
    void returnResult(String result);
  }

  public BindXuptLibAsyncTask(Activity mActivity, String libName, String type,
      OnPostExecute oExecute) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.libName = libName;
    this.type = type;
    this.onPostExecute = oExecute;
  }

  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    String url = HttpUtilMc.LIB_URL + "/servlet/BindXuptScoreServlet?data="
        + URLEncoder.encode(Util.getBindLibParmas(mActivity, StaticVarUtil.student.getAccount()))
        + "&viewstate=" + URLEncoder.encode(StaticVarUtil.viewstate) + "&type=" + type;
    // if (type.equals("1")) {
    url = url + "&lib=" + URLEncoder.encode(Util.getBindLibParmas(mActivity, libName));
    // }
    return HttpUtilMc.queryStringForPost(url);
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    System.out.println("mcmcmc" + result);
    onPostExecute.returnResult(result);
  
  }

}
