package com.mc.util;

import com.xy.fy.main.R;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {

  public static ProgressDialog dialog;
  private static Activity mContext;
  private static String msg = Util.getContext().getResources().getString(R.string.querying);
  private static ProgressDialogUtil instance;

  public static ProgressDialogUtil getInstance(Activity context) {
    if (instance == null || context != mContext) {
      mContext = context;
      instance = new ProgressDialogUtil();
    }
    return instance;
  }

  private ProgressDialogUtil() {
   
    dialog = ViewUtil.getProgressDialog(mContext, msg);
    dialog.setCancelable(false);
  }

  private void setMsg(String msg){
    this.msg = msg;
  }
  public void show() {
      dialog.show();
  }

  public void dismiss() {
      dialog.dismiss();
  }
}
