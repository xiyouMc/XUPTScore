package com.xy.fy.asynctask;

import com.mc.db.DBConnection;
import com.mc.util.CircleImageView;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

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
