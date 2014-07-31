package com.xy.fy.main;

import com.mc.util.LogcatHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class WelcomeActivity extends Activity {


	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.activity_welcome, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
	
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(3000);
		view.startAnimation(aa);

		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
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
			
				//i = System.currentTimeMillis();
				// 动画开始的时候进行发送广播，进行数据的更新
//					Toast.makeText(getApplicationContext(), "欢迎", 1000).show();
					
				
			}
		});
	}
	

	

}
