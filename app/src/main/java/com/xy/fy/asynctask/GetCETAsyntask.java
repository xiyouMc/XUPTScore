package com.xy.fy.asynctask;

import com.mc.util.HttpUtilMc;
import com.mc.util.ProgressDialogUtil;
import com.xy.fy.main.CETActivity;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class GetCETAsyntask extends AsyncTask<String, String, String> {

    private Activity mActivity;

    public GetCETAsyntask(Activity mActivity) {
        // TODO Auto-generated constructor stub
        this.mActivity = mActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return HttpUtilMc.queryStringForPost(HttpUtilMc.XIYOUMC_BASE_IP + "servlet/CetServlet?ticket="
                + StaticVarUtil.cet_data + "&time=" + StaticVarUtil.cet_viewstate + "&name=" + params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        ProgressDialogUtil.getInstance(mActivity).dismiss();
        try {
            if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                if (!result.equals("error")) {
                    if (result.equals("2")) {
                        ViewUtil.showToast(mActivity, "׼��֤�������");
                        return;
                    }
                    if (result.equals("4")) {
                        ViewUtil.showToast(mActivity, "�����������");
                        return;
                    }
                    if (result.length() < 2) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("data", result);
                    Intent intent = new Intent(mActivity, CETActivity.class);
                    intent.putExtras(bundle);
                    mActivity.startActivity(intent);
                } else {
                    ViewUtil.showToast(mActivity, "��ѯʧ��");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("LoginActivity", e.toString());
        }
    }


}
