package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.os.AsyncTask;

public class BindXuptLibAsyncTask extends AsyncTask<String, String, String> {

    private Activity mActivity;
    private String libName;
    private String type;

    private OnPostExecute onPostExecute;

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
        Util.getAccountParmas(mActivity, StaticVarUtil.student.getAccount());
        String accountData = StaticVarUtil.accountData;
        String url = HttpUtilMc.LIB_URL + "/servlet/BindXuptScoreServlet?data=" + accountData
                + "&viewstate=" + StaticVarUtil.accountViewstate + "&type=" + type;
        // if (type.equals("1")) {
        Util.getBindLibNameParmas(mActivity, StaticVarUtil.LIB_NAME);
        url = url + "&lib=" + StaticVarUtil.bindLibName;
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

    public interface OnPostExecute {
        void returnResult(String result);
    }

}
