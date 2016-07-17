package com.xy.fy.asynctask;

import com.mc.util.HttpAssist;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

public class UploadFileAsytask extends AsyncTask<String, String, String> {

    private Activity mActivity;
    private Bitmap bitmap;

    public UploadFileAsytask(Activity mActivity, Bitmap bitmap) {
        // TODO Auto-generated constructor stub
        this.mActivity = mActivity;
        this.bitmap = bitmap;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return HttpAssist.uploadFile(new File(params[0]), StaticVarUtil.student.getAccount());
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        try {
            if (result.equals("error")) {
                return;
            }
            if (Util.isExternalStorageWritable()) {
                Util.saveBitmap2file(bitmap, result, mActivity);
                bitmap.recycle();
            }
            ViewUtil.showToast(mActivity, !HttpUtilMc.CONNECT_EXCEPTION.equals(result)
                    ? !result.equals("error") ? "�޸ĳɹ�" : "�޸�ʧ��" : HttpUtilMc.CONNECT_EXCEPTION);
        } catch (Exception e) {
            Log.i("LoginActivity", e.toString());
        }
    }

}
