package com.xy.fy.main;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class AddFriendListActivity extends ActivityGroup {

	private final static Class<?>[] sActivityClasses = {
			FriendRecommondActivity.class, FriendApplyActivity.class };

	private final static String[] sActivityIds = { "FriendRecommondActivity",
			"FriendApplyActivity" };
	Button back;
	Button friend_recommand;// 好友推荐
	Button friend_apply;// 好友申请
	private LinearLayout add_friend_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_friend);

		init();
		click();
	}

	// 切换activity
	private void processViews(int rid) {
		add_friend_layout.removeAllViews();
		final int index = rid;
		final View tempView = getLocalActivityManager().startActivity(
				sActivityIds[index],
				new Intent(this, sActivityClasses[index])
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		add_friend_layout.addView(tempView);
	}

	private void click() {
		// TODO Auto-generated method stub
		/**
		 * 返回
		 */
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				// overridePendingTransition(R.anim.out_to_right,
				// R.anim.in_from_left);
			}
		});

		friend_recommand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recommand_focus();
				friend_apply.setFocusable(false);
				processViews(0);
			}
		});

		friend_apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				apply_focus();
				friend_recommand.setFocusable(false);
				processViews(1);
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		add_friend_layout = (LinearLayout) findViewById(R.id.recommondAndApply);
		back = (Button) findViewById(R.id.back);
		friend_recommand = (Button) findViewById(R.id.friend_recommand);
		friend_apply = (Button) findViewById(R.id.friend_apply);
		// 默认 好友推荐 获取焦点
		recommand_focus();
	}

	/**
	 * 好友推荐 获取焦点
	 */
	private void recommand_focus() {
		friend_recommand.setFocusable(true);
		friend_recommand.setFocusableInTouchMode(true);
		friend_recommand.requestFocus();
		friend_recommand.requestFocusFromTouch();
		
		final int index = 0;
		final View tempView = getLocalActivityManager().startActivity(
				sActivityIds[index],
				new Intent(this, sActivityClasses[index])
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
				.getDecorView();
		add_friend_layout.addView(tempView);
	}

	/**
	 * 好友申请 获取焦点
	 */
	private void apply_focus() {
		friend_apply.setFocusable(true);
		friend_apply.setFocusableInTouchMode(true);
		friend_apply.requestFocus();
		friend_apply.requestFocusFromTouch();
	}

}
