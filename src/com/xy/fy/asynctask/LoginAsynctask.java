package com.xy.fy.asynctask;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.xy.fy.main.MainActivity;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class LoginAsynctask extends AsyncTask<Object, String, String> {

  private Activity mActivity;
  private LoginResult loginResult;
  private String account, password;
  private ProgressDialog progressDialog;
  private String txtSecretCode;

  public LoginAsynctask(Activity mActivity, String account, String password, String txtSecretCode,
      LoginResult loginResult, ProgressDialog progressDialog) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.account = account;
    this.password = password;
    this.loginResult = loginResult;
    this.progressDialog = progressDialog;
    this.txtSecretCode = txtSecretCode;
  }

  interface LoginResult {
    void onLogin(String result);
  }

  @Override
  protected String doInBackground(Object... params) {
    // TODO Auto-generated method stub
    StaticVarUtil.loginTimes++;
    return HttpUtilMc.queryStringForPost(
        HttpUtilMc.BASE_URL + "login.jsp?username=" + account.trim() + "&password="
            + URLEncoder.encode(password.trim()) + "&session=" + StaticVarUtil.session + "&txtSecretCode=" + txtSecretCode);

  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    // progress.cancel();

    loginResult.onLogin(result);

  }

}
