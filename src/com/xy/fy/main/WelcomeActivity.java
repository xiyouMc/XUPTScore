package com.xy.fy.main;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import com.mc.util.HttpUtilMc;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WelcomeActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_welcome);
		GetPicAsyntask getPicAsyntask = new GetPicAsyntask();
		getPicAsyntask.execute();
	}
	

	// 异步加载登录
	class GetPicAsyntask extends AsyncTask<Object, String, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url;
			url = HttpUtilMc.BASE_URL
					+ "GetPic.jsp";
			System.out.println("url"+url);
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			System.out.println("=========================  " + result);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			progress.cancel();
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {

					if (!result.equals("error")) {

						JSONObject json = new JSONObject(result);
						String session = json.getString("cookieSessionID");//session
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@");
						/*Declare declare = (Declare) getApplicationContext();
						declare.setId(Integer.parseInt(result));
						declare.setUserName(userEditText.getText().toString());*/
						Toast.makeText(getApplicationContext(), "登录成功", 1)
								.show();
						Intent intent = new Intent();
						intent.setClass(WelcomeActivity.this, LoginActivity.class);
						Bundle b = new Bundle();
						b.putString("session", session);
						intent.putExtras(b);
						startActivity(intent);
						finish();

					}
					else {
						Toast.makeText(getApplicationContext(), "登录失败", 1)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION, 1000).show();
					// progress.cancel();
					finish();
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.i("LoginActivity", e.toString());
			}

		}

	}
}
