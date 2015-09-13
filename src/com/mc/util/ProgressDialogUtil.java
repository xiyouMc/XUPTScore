package com.mc.util;

import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {

  public static ProgressDialog dialog;
  private static Context mContext;
  private static ProgressDialogUtil instance;

  public static ProgressDialogUtil getInstance(Context context) {
    if (instance == null || context != mContext) {
      mContext = context;
      instance = new ProgressDialogUtil();
    }
    return instance;
  }

  private ProgressDialogUtil() {
   
    dialog = ViewUtil.getProgressDialog(mContext, "ÕýÔÚ²éÑ¯");
    dialog.setCancelable(false);
  }

  public void show() {
      dialog.show();
  }

  public void dismiss() {
      dialog.dismiss();
  }
}
