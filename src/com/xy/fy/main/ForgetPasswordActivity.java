package com.xy.fy.main;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xy.fy.util.ConnectionUtil;
import com.xy.fy.util.HttpUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

@SuppressLint("HandlerLeak")
public class ForgetPasswordActivity extends Activity {

	private Button back;// 返回键
	private Button getPassword1;// 找回密码方式一
	private Button getPassword2;// 找回密码方式二
	private EditText account;// 账号
	private EditText email;// 邮箱
	private String strReturn1 = null;// 方式一返回数据
	private String strReturn2 = null;// 方式二返回数据
	private ProgressDialog progressDialog;// 进度框

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_forget_password);
		this.findViewById();
		// 返回按钮
		this.back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 第一种找回密码方式
		this.getPassword1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String strAccount = account.getText().toString();
				final String strEmail = email.getText().toString();
				if (strAccount.equals("") || strAccount == null
						|| strEmail.equals("") || strEmail == null) {
					Toast.makeText(getApplicationContext(), "参数不全",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (!ConnectionUtil.isConn(ForgetPasswordActivity.this)) {
					ConnectionUtil
							.setNetworkMethod(ForgetPasswordActivity.this);
					return;
				}
				// 找回密码
				new Thread() {
					public void run() {
						getPassword1(strAccount, strEmail);
					};
				}.start();
			}
		});
		// 第二种找回密码方式
		this.getPassword2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String strAccount = account.getText().toString();
				if (strAccount.equals("") || strAccount == null) {
					Toast.makeText(getApplicationContext(), "账号不能为空！",
							Toast.LENGTH_LONG).show();
					return;
				}
				new Thread() {
					public void run() {
						getPassword2(strAccount);
					};
				}.start();
			}
		});
	}

	/**
	 * 处理前台控件
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case StaticVarUtil.GET_PASSWORD_BACK1_START:
				progressDialog.show();
				break;
			case StaticVarUtil.GET_PASSWORD_BACK1_END:
				progressDialog.cancel();
				if (strReturn1 == null || strReturn1.equals("")) {
					ViewUtil.toastShort("网络异常,请稍后重试",
							ForgetPasswordActivity.this);
				} else if (strReturn1.equals(HttpUtil.FAIL)) {
					ViewUtil.showDialog("账号密码不匹配", "找回密码",
							ForgetPasswordActivity.this);
				} else {
					ViewUtil.showDialog("密码是:" + strReturn1, "找回密码",
							ForgetPasswordActivity.this);
				}
				break;
			case StaticVarUtil.GET_PASSWORD_BACK2_START:
				progressDialog.show();
				break;
			case StaticVarUtil.GET_PASSWORD_BACK2_END:
				progressDialog.cancel();
				if (strReturn2 == null || strReturn2.equals("")) {
					ViewUtil.toastShort("网络异常,请稍后重试",
							ForgetPasswordActivity.this);
				} else if (strReturn2.equals(HttpUtil.SUCCESS)) {
					ViewUtil.showDialog("已经将密码发到你的邮箱，请登录邮箱验收", "找回密码",
							ForgetPasswordActivity.this);
				} else if (strReturn2.equals(HttpUtil.FAIL)) {
					ViewUtil.showDialog("系统中没有这个账号,请重新检查账号是否输入错误", "找回密码",
							ForgetPasswordActivity.this);
				} else {
					ViewUtil.showDialog("服务器错误,请稍后重试", "找回密码",
							ForgetPasswordActivity.this);
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 方式一找回密码
	 */
	private void getPassword1(String account, String email) {
		Message msg = new Message();
		msg.what = StaticVarUtil.GET_PASSWORD_BACK1_START;
		handler.sendMessage(msg);

		HttpUtil http = new HttpUtil();
		HashMap<String, String> allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.FIND_PASSWORD_1 + "");
		allParams.put(HttpUtil.ACCOUNT, account);
		allParams.put(HttpUtil.EMAIL, email);
		try {
			strReturn1 = http.downLoad(HttpUtil.URL1, allParams);
		} catch (Exception e) {
			e.printStackTrace();
			strReturn1 = null;
		}

		msg = new Message();
		msg.what = StaticVarUtil.GET_PASSWORD_BACK1_END;
		handler.sendMessage(msg);
	}

	/**
	 * 方式二找回密码
	 */
	private void getPassword2(String account) {
		Message msg = new Message();
		msg.what = StaticVarUtil.GET_PASSWORD_BACK2_START;
		handler.sendMessage(msg);

		HttpUtil http = new HttpUtil();
		HashMap<String, String> allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.FIND_PASSWORD_2 + "");
		allParams.put(HttpUtil.ACCOUNT, account);
		try {
			strReturn2 = http.downLoad(HttpUtil.URL1, allParams);
		} catch (Exception e) {
			e.printStackTrace();
			strReturn2 = null;
		}

		msg = new Message();
		msg.what = StaticVarUtil.GET_PASSWORD_BACK2_END;
		handler.sendMessage(msg);
	}

	/**
	 * 找到控件
	 */
	private void findViewById() {
		this.account = (EditText) findViewById(R.id.etAccount);
		this.email = (EditText) findViewById(R.id.etEmail);
		this.getPassword1 = (Button) findViewById(R.id.butGetPassword1);
		this.getPassword2 = (Button) findViewById(R.id.butGetPassword2);
		this.back = (Button) findViewById(R.id.butBack);
		progressDialog = ViewUtil.getProgressDialog(
				ForgetPasswordActivity.this, "正在找回密码");
	}

}
