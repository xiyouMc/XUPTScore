package com.xy.fy.main;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mc.util.CrashHandler;
import com.mc.util.H5Log;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;

public class WelcomeActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    try {
      CrashHandler crashHandler = CrashHandler.getInstance();
      crashHandler.init(this);
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
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

        GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask();
        getImageMsgAsytask.execute();
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
      return HttpUtilMc.IsReachIP() ? HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL
          + "GetPollImageTimeIspoll") : HttpUtilMc.CONNECT_EXCEPTION;
    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      try {
        Intent i = new Intent();
        i.setClass(getApplicationContext(), LoginActivity.class);
        // 如果网络原因，则直接返回0|0
        i.putExtra("image", !HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? result : "0|0|0");
        startActivity(i);
        finish();
      } catch (Exception e) {
        // TODO: handle exception
        Log.i("WelcomeActivity", e.toString());
      }
    }
  }
}
