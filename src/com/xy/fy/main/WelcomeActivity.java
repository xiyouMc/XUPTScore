package com.xy.fy.main;

import java.io.File;

import com.mc.util.CrashHandler;
import com.mc.util.H5Log;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.asynctask.GetPicAsynctask;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class WelcomeActivity extends Activity {
  @SuppressLint("NewApi")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      if (!Util.isDebug(getApplicationContext())) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

    int alwaysFinish = Settings.Global.getInt(getContentResolver(),
        Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
    if (alwaysFinish == 1) {
      Dialog dialog = null;
      dialog = new AlertDialog.Builder(this)
          .setMessage("由于您已开启'不保留活动',导致i呼部分功能无法正常使用.我们建议您点击左下方'设置'按钮,在'开发者选项'中关闭'不保留活动'功能.")
          .setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          }).setPositiveButton("设置", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
              startActivity(intent);
            }
          }).create();
      dialog.show();
    }

    H5Log.d(getApplicationContext(), String.valueOf(Util.isDebugable(getApplicationContext())));
    final View view = View.inflate(this, R.layout.activity_welcome, null);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(view);

    AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
    aa.setDuration(2000);
    view.startAnimation(aa);

    aa.setAnimationListener(new AnimationListener() {
      @Override
      public void onAnimationEnd(Animation arg0) {

        SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
        String account = preferences.getString(StaticVarUtil.ACCOUNT, "");
        String password = preferences.getString(StaticVarUtil.PASSWORD, "");
        boolean isRemember = preferences.getBoolean(StaticVarUtil.IS_REMEMBER, false);
        if (isRemember == true) {

          if (Util.isExternalStorageWritable()) {
            StaticVarUtil.PATH = "/sdcard/xuptscore/";// 设置文件目录
          } else {
            StaticVarUtil.PATH = "/data/data/com.xy.fy.main/";// 设置文件目录
          }

          if (!new File(StaticVarUtil.PATH).exists()) {
            new File(StaticVarUtil.PATH).mkdirs();
          }
//          ProgressDialog progressDialog = ViewUtil.getProgressDialog(WelcomeActivity.this, "正在登录");
          GetPicAsynctask getPicAsyntask = new GetPicAsynctask(WelcomeActivity.this, account,
              password, null, new GetPicAsynctask.GetPic() {

            @Override
            public void onReturn(String result) {
              // TODO Auto-generated method stub

              if ("error".equals(result)) {
                GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask();
                getImageMsgAsytask.execute();
              } else if ("no_user".equals(result)) {
                GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask();
                getImageMsgAsytask.execute();
              }else if (HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                try {
                  Intent i = new Intent();
                  i.setClass(getApplicationContext(), LoginActivity.class);
                  // 如果网络原因，则直接返回0|0
                  i.putExtra("image", !HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? result : "0|0|0");//
                  startActivity(i);
                  finish();
                } catch (Exception e) {
                  // TODO: handle exception
                  Log.i("WelcomeActivity", e.toString());
                }
              }
            }
          });
//          progressDialog.show();
          getPicAsyntask.execute();
        } else {
          GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask();
          getImageMsgAsytask.execute();
        }

      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationStart(Animation animation) {
      }
    });
  }

  class GetImageMsgAsytask extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
      // TODO Auto-generated method stub
      return HttpUtilMc.IsReachIP()
          ? HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL + "GetPollImageTimeIspoll")
          : HttpUtilMc.CONNECT_EXCEPTION;
    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      try {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), LoginActivity.class);
        // 如果网络原因，则直接返回0|0
        i.putExtra("image", !HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? result : "0|0|0");//
        startActivity(i);
        finish();
      } catch (Exception e) {
        // TODO: handle exception
        Log.i("WelcomeActivity", e.toString());
      }
    }
  }
}
