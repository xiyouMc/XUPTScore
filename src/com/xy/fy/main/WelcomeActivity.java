package com.xy.fy.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.mc.util.CrashHandler;
import com.mc.util.Util;

public class WelcomeActivity extends Activity {

	/*
	 * // 设置状态栏 NotificationManager notificationManager = null; Notification
	 * notification = null; PendingIntent pendingIntent = null;// 即将发送的事件
	 */@SuppressLint("ShowToast")
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

		final View view = View.inflate(this, R.layout.activity_welcome, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);

		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(1000);
		view.startAnimation(aa);

		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				if (!Util.isRecordLoginMessage(getApplicationContext())) {//是否已经上传了手机信息
					//未上传,则保存手机信息到本地
					Util.saveDeviceInfo(getApplicationContext());
					//上传各种信息
					Util.uploadDevInfos(getApplicationContext());
				}
				Intent i = new Intent();
				i.setClass(getApplicationContext(), LoginActivity.class);
				startActivity(i);
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {

				// i = System.currentTimeMillis();
				// 动画开始的时候进行发送广播，进行数据的更新
				// Toast.makeText(getApplicationContext(), "欢迎", 1000).show();

			}
		});
	}

}
