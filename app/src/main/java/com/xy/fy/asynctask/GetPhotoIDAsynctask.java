package com.xy.fy.asynctask;

import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.db.DBConnection;
import com.xy.fy.view.CircleImageView;
import top.codemc.common.util.Util;
import top.codemc.rpcapi.HttpUtilMc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;

public class GetPhotoIDAsynctask extends AsyncTask<String, String, String> {

    private CircleImageView headPhoto;
    private Activity mActivity;

    public GetPhotoIDAsynctask(Activity mActivity, CircleImageView headPhoto) {
        this.mActivity = mActivity;
        this.headPhoto = headPhoto;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        return HttpUtilMc.queryStringForPost(
                HttpUtilMc.BASE_URL + "getuserphoto.jsp?username=" + StaticVarUtil.student.getAccount());
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if (result == null) {
            return;
        }
        if ("no_photo".equals(result) || result.split("/").length < 2) {
            return;
        }
        StaticVarUtil.PHOTOFILENAME = result.split("/")[1];
        // �ж� ͷ���ļ������Ƿ�� ���û���ͷ��
        if (DBConnection.getPhotoName(StaticVarUtil.student.getAccount(), mActivity)
                .equals(StaticVarUtil.PHOTOFILENAME)
                && new File(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG")
                .exists()) {
            // ������
            Bitmap bitmap = Util.convertToBitmap(
                    StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG", 240, 240);
            if (bitmap != null) {
                headPhoto.setImageBitmap(bitmap);
            }
        } else {
            // ����ļ����в��������ͷ��
            GetHeadPictureAsyncTask getHeadPictureAsyncTask = new GetHeadPictureAsyncTask(mActivity,
                    headPhoto);
            getHeadPictureAsyncTask.execute(
                    new String[]{HttpUtilMc.BASE_URL + "user_photo/" + StaticVarUtil.PHOTOFILENAME});
        }
    }

}
