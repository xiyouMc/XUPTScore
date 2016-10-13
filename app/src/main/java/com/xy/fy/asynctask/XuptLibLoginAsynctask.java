package com.xy.fy.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.Util;
import top.codemc.rpcapi.HttpUtilMc;

public class XuptLibLoginAsynctask extends AsyncTask<String, String, String> {

    private Activity mActivity;
    private String libName;
    private Login login;

    public XuptLibLoginAsynctask(Activity mActivity, String libName, Login login) {
        // TODO Auto-generated constructor stub
        this.mActivity = mActivity;
        this.libName = libName;
        this.login = login;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        Util.getBindLibParmas(mActivity, libName);
        String url = HttpUtilMc.LIB_URL + "/servlet/LoginServlet?data="
                + StaticVarUtil.libData + "&viewstate="
                + StaticVarUtil.libViewstate;
        return HttpUtilMc.queryStringForPost(url);
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        System.out.println("lib" + result);
        login.onPostLogin(result);
    }

    public interface Login {
        void onPostLogin(String result);
    }

}
