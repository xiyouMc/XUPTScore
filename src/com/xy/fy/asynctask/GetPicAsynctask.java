package com.xy.fy.asynctask;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mc.util.HttpUtilMc;
import com.xy.fy.asynctask.LoginAsynctask.LoginResult;
import com.xy.fy.main.MainActivity;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetPicAsynctask extends AsyncTask<Object, String, String> {

  public Activity mActivity;
  private String account, password;
  private ProgressDialog progressDialog;
  private GetPic onResult;

  public interface GetPic {
    void onReturn(String result);
  }

  public GetPicAsynctask(Activity mActivity, String account, String password,
      ProgressDialog progressDialog, GetPicAsynctask.GetPic onResult) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.account = account;
    this.password = password;
    this.progressDialog = progressDialog;
    this.onResult = onResult;
  }

  @Override
  protected String doInBackground(Object... params) {
    StaticVarUtil.loginTimes++;
    // TODO Auto-generated method stub
    return HttpUtilMc.IsReachIP()
        ? HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL + "GetPic.jsp")
        : HttpUtilMc.CONNECT_EXCEPTION;
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    // progress.cancel();
    try {
      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
        if (!result.equals("error")) {
          if (!result.equals("ip warning!!!")) {
            JSONObject json = new JSONObject(result);
            String session = json.getString("cookieSessionID");// session
            StaticVarUtil.session = session;
            StaticVarUtil.loginTimes = 0;// 将登陆次数置零
            LoginAsynctask loginAsyntask = new LoginAsynctask(mActivity, account, password,
                new LoginResult() {

                  @Override
                  public void onLogin(String result) {
                    // TODO Auto-generated method stub

                    try {
                      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                        if (result.equals("error")) {
                          ViewUtil.showToast(mActivity, "密码错误");

                          onResult.onReturn("error");
                          // password.setText("");
                          if (progressDialog != null) {
                            progressDialog.dismiss();
                          }
                          // progressDialog.cancel();
                        } else {
                          if (result.equals("no_user")) {
                            ViewUtil.showToast(mActivity, "账号不存在");
                            onResult.onReturn("no_user");
                            if (progressDialog != null) {
                              progressDialog.dismiss();
                            }
                            // progressDialog.cancel();
                          } else {// 登录成功'

                            StaticVarUtil.listHerf = new ArrayList<HashMap<String, String>>();
                            JSONObject json = new JSONObject(result);
                            JSONArray jsonArray = (JSONArray) json.get("listHerf");
                            for (int i = 0; i < jsonArray.length(); i++) {
                              JSONObject o = (JSONObject) jsonArray.get(i);
                              HashMap<String, String> map = new HashMap<String, String>();
                              map.put("herf", o.getString("herf"));
                              map.put("tittle", o.getString("tittle"));
                              StaticVarUtil.listHerf.add(map);
                            }

                            StaticVarUtil.student.setAccount(account.trim());
                            StaticVarUtil.student.setPassword(password.trim());
                            Intent intent = new Intent();
                            intent.setClass(mActivity, MainActivity.class);
                            if (progressDialog != null) {
                              progressDialog.dismiss();
                            }
                            // progressDialog.cancel();
                            mActivity.startActivity(intent);
                            mActivity.finish();
                            StaticVarUtil.quit();

                          }

                        }

                      } else {

                        // 重新登录
                        if (StaticVarUtil.loginTimes < 3) {
                          LoginAsynctask loginAsyntask = new LoginAsynctask(mActivity, account,
                              password, this, progressDialog);
                          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_REPEAT_EXCEPTION);
                          loginAsyntask.execute();
                        } else {
                          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_EXCEPTION);
                          if (progressDialog != null) {
                            progressDialog.dismiss();
                          }
                          onResult.onReturn(HttpUtilMc.CONNECT_EXCEPTION);
                          // progressDialog.cancel();
                        }
                      }
                    } catch (Exception e) {
                      // TODO: handle exception
                      Log.i("LoginActivity", e.toString());
                      if (progressDialog != null) {
                        progressDialog.dismiss();
                      }
                    }

                  }
                }, progressDialog);
            loginAsyntask.execute();
          }
        } else {
          ViewUtil.showToast(mActivity, "服务器维护中。。。");
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          // progressDialog.cancel();
        }
      } else {
        if (StaticVarUtil.loginTimes < 3) {
          GetPicAsynctask getPicAsyntask = new GetPicAsynctask(mActivity, account, password,
              progressDialog, onResult);
          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_REPEAT_EXCEPTION);
          getPicAsyntask.execute();
        } else {
          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_EXCEPTION);
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          onResult.onReturn(HttpUtilMc.CONNECT_EXCEPTION);
          // progressDialog.cancel();
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
      Log.i("LoginActivity", e.toString());
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
    }

  }
}
