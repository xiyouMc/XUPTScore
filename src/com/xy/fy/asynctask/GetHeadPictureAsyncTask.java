package com.xy.fy.asynctask;

import com.mc.util.CircleImageView;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class GetHeadPictureAsyncTask extends AsyncTask<String, Bitmap, Bitmap> {

  private Context context;
  private CircleImageView headPhoto;
  public GetHeadPictureAsyncTask(Context context,CircleImageView headPhoto) {
    // TODO Auto-generated constructor stub
    this.context = context;
    this.headPhoto = headPhoto;
  }
  @Override
  protected Bitmap doInBackground(String... params) {
    // TODO Auto-generated method stub
    return Util.getBitmap(params[0]);
  }

  @Override
  protected void onPostExecute(Bitmap bitmap) {
    // TODO Auto-generated method stub

  if (bitmap == null) {
    return;
  }

  if (Util.isExternalStorageWritable()) {
    Util.saveBitmap2file(bitmap, StaticVarUtil.PHOTOFILENAME, context);
  }
  headPhoto.setImageBitmap(Util.convertToBitmap(
      StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG", 240, 240));// œ‘ æÕº∆¨
  bitmap.recycle();
}
  
}
