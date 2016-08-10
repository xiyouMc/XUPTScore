package com.xy.fy.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import top.codemc.common.util.ProgressDialogUtil;
import top.codemc.common.util.Util;
import top.codemc.common.util.VersionUpdate;
import top.codemc.rpcapi.HttpUtilMc;

public class CheckVersionAsynctask extends AsyncTask<String, String, String> {

    private Activity mActivity;

    private boolean isShowDialog;

    private OnCheck onCheck;

    public CheckVersionAsynctask(Activity mActivity, boolean isShow, OnCheck onCheck) {
        // TODO Auto-generated constructor stub
        this.mActivity = mActivity;
        this.isShowDialog = isShow;
        this.onCheck = onCheck;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return HttpUtilMc
                .queryStringForPost(HttpUtilMc.BASE_URL + "checkversion.jsp?version="
                        + Util.getVersion(mActivity));
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (isShowDialog) {
            ProgressDialogUtil.getInstance(mActivity).dismiss();
        }
        try {
            if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                if (!result.equals("no")) {
                    String[] str = result.split("\\|");
                    String apk_url = str[0];
                    String new_version = str[1];
                    String update_content = str[2];
                    VersionUpdate versionUpdate = new VersionUpdate(mActivity);
                    versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
                    versionUpdate.updateMsg = new_version + "\n\n" + update_content;
                    versionUpdate.checkUpdateInfo();
                    return;
                }
                UpdateVersion updateVersion = new UpdateVersion(mActivity);
                updateVersion.execute();
                onCheck.onCheck();
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.i("LoginActivity", e.toString());
        }
    }

    public interface OnCheck {
        void onCheck();
    }

}
