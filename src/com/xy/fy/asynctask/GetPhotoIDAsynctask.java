package com.xy.fy.asynctask;

import java.io.File;

import com.mc.db.DBConnection;
import com.mc.util.CircleImageView;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class GetPhotoIDAsynctask extends AsyncTask<String, String, String> {

  private CircleImageView headPhoto;
  private Context context;

  public GetPhotoIDAsynctask(Context context, CircleImageView headPhoto) {
    this.context = context;
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
    // 判断 头像文件夹中是否包含 该用户的头像
    if (DBConnection.getPhotoName(StaticVarUtil.student.getAccount(), context)
        .equals(StaticVarUtil.PHOTOFILENAME)
        && new File(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG")
            .exists()) {
      // 如果存在
      Bitmap bitmap = Util.convertToBitmap(
          StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG", 240, 240);
      if (bitmap != null) {
        headPhoto.setImageBitmap(bitmap);
      }
    } else {
      // 如果文件夹中不存在这个头像。
      GetHeadPictureAsyncTask getHeadPictureAsyncTask = new GetHeadPictureAsyncTask(context,
          headPhoto);
      getHeadPictureAsyncTask.execute(
          new String[] { HttpUtilMc.BASE_URL + "user_photo/" + StaticVarUtil.PHOTOFILENAME });
    }
  }

}
