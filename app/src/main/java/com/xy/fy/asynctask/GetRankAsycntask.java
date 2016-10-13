package com.xy.fy.asynctask;

import com.util.RankUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import top.codemc.common.util.ConnectionUtil;
import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.ViewUtil;
import top.codemc.rpcapi.HttpUtilMc;

public class GetRankAsycntask extends AsyncTask<String, String, String> {

    private TextView nickName;
    private Activity mActivity;
    private String name;
    private ProgressDialog dialog;

    public GetRankAsycntask(Activity mActivity, TextView nickName, String name) {
        // TODO Auto-generated constructor stub

        this.mActivity = mActivity;
        this.nickName = nickName;
        this.name = name;
        dialog = ViewUtil.getProgressDialog(mActivity, "���ڲ�ѯ");
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
           * ���ַ� д��xml�ļ���
           */
                    if (!result.equals("")) {
                        dialog.dismiss();
                        StaticVarUtil.requestTimes = 0;
                        RankUtils.refeshRank(result, RankUtils.isFirstListView, mActivity);
                        RankUtils.allRankMap.put(RankUtils.selectXn + RankUtils.selectXq, result);// ����ݱ��浽�ڴ��У��´ξͲ����ظ���ȡ��
                    }
                } else {
                    ViewUtil.showToast(mActivity, "��ѯʧ��");
                    dialog.dismiss();
                }
            } else {
                if (!ConnectionUtil.isConn(mActivity)) {
                    dialog.dismiss();
                    return;
                }
                if (StaticVarUtil.requestTimes < 4) {
                    StaticVarUtil.requestTimes++;
                    GetRankAsycntask getRankAsyntask = new GetRankAsycntask(mActivity, nickName, name);
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
