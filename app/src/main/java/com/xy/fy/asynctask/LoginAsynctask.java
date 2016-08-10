package com.xy.fy.asynctask;

import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.Util;
import top.codemc.rpcapi.HttpUtilMc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.net.URLEncoder;

public class LoginAsynctask extends AsyncTask<Object, String, String> {

    private LoginResult loginResult;
    private String account, password;
    private String txtSecretCode;
    private Activity mActivity;
    private boolean isAutoLogin;

    public LoginAsynctask(Activity mActivity, String account, String password, String txtSecretCode,
                          LoginResult loginResult, ProgressDialog progressDialog, boolean isAutoLogin) {
        // TODO Auto-generated constructor stub
        this.account = account;
        this.password = password;
        this.loginResult = loginResult;
        this.txtSecretCode = txtSecretCode;
        this.mActivity = mActivity;
        this.isAutoLogin = isAutoLogin;
    }

    @Override
    protected String doInBackground(Object... params) {
        // TODO Auto-generated method stub
        StaticVarUtil.loginTimes++;
        if (!isAutoLogin) {
            return HttpUtilMc.queryStringForPost(
                    HttpUtilMc.BASE_URL + "login.jsp?username=" + account.trim() + "&password="
                            + URLEncoder.encode(password.trim()) + "&session=" + StaticVarUtil.session
                            + "&txtSecretCode=" + txtSecretCode + "&version=" + Util.getVersion(this.mActivity));
        } else {
            SharedPreferences preferences = mActivity.getSharedPreferences(StaticVarUtil.USER_INFO,
                    mActivity.MODE_PRIVATE);
            String account = preferences.getString(StaticVarUtil.ACCOUNT, "");
            String session = preferences.getString(StaticVarUtil.SESSION, "");
            return HttpUtilMc.queryStringForPost(
                    HttpUtilMc.BASE_URL + "mclogin?username=" + account + "&mc=" + session);
        }

    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        // progress.cancel();

        loginResult.onLogin(result);

    }

    public interface LoginResult {
        void onLogin(String result);
    }

}
