package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.RankUtils;
import com.xy.fy.main.MainActivity;
import com.xy.fy.main.R;
import com.xy.fy.util.ConnectionUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class GetRankAsycntask extends AsyncTask<String, String, String> {

  private TextView nickName;
  private Activity mActivity;
  private String name;
  private ProgressDialog dialog;
  
  public GetRankAsycntask(Activity mActivity,TextView nickName,String name) {
    // TODO Auto-generated constructor stub
    
    this.mActivity = mActivity;
    this.nickName = nickName;
    this.name = name;
    dialog = ViewUtil.getProgressDialog(mActivity, "正在查询");
    dialog.show();
  }
  @Override
  protected String doInBackground(String... params) {
    // TODO Auto-generated method stub
    return HttpUtilMc
        .queryStringForPost(HttpUtilMc.BASE_URL + "RankServlet.jsp?data=" + StaticVarUtil.data
            + "&viewstate=" + StaticVarUtil.viewstate + "&content=" + StaticVarUtil.content);
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub

    nickName.setText(name);
    try {
      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
        if (!result.equals("error")) {
          /*
           * 将字符串 写入xml文件中
           */
          if (!result.equals("")) {
            dialog.dismiss();
            StaticVarUtil.requestTimes = 0;
            RankUtils.refeshRank(result, RankUtils.isFirstListView,mActivity);
            RankUtils.allRankMap.put(RankUtils.selectXn + RankUtils.selectXq, result);// 将数据保存到内存中，下次就不用重复获取。
          }
        } else {
          ViewUtil.showToast(mActivity, "查询失败");
          dialog.dismiss();
        }
      } else {
        if (!ConnectionUtil.isConn(mActivity)) {
          ConnectionUtil.setNetworkMethod(mActivity);
          dialog.dismiss();
          return;
        }
        if (StaticVarUtil.requestTimes < 4) {
          StaticVarUtil.requestTimes++;
          GetRankAsycntask getRankAsyntask = new GetRankAsycntask(mActivity,nickName,name);
          getRankAsyntask.execute();
        } else {
          dialog.dismiss();
          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_EXCEPTION);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      dialog.dismiss();
      Log.i("LoginActivity", e.toString());
    }

  }
  
    

}
