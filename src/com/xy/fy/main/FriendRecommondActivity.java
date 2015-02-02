package com.xy.fy.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class FriendRecommondActivity extends Activity {

	private ListView kecheng_Listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend_recommond);
		
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		kecheng_Listview = (ListView)findViewById(R.id.recommond_list);
	}

	
}
