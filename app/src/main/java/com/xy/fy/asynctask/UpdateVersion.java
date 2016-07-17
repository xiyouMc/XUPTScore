package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.os.AsyncTask;

public class UpdateVersion extends AsyncTask<String, String, String> {

    Activity mActivity;

    public UpdateVersion(Activity mActivity) {
        // TODO Auto-generated constructor stub
        this.mActivity = mActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        String url = HttpUtilMc.LIB_URL + "/servlet/CheckUserVersion?version="
                + Util.getVersion(mActivity) + "&username="
                + StaticVarUtil.student.getAccount();
        return HttpUtilMc.queryStringForPost(url);
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
    }


}
