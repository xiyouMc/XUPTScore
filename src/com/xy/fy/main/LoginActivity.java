package com.xy.fy.main;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mc.db.DBConnection;
import com.mc.db.DBConnection.UserSchema;
import com.mc.util.CircleImageView;
import com.mc.util.HttpUtilMc;
import com.mc.util.SystemBarTintManager;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.ConnectionUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.PullDoorView;
import com.xy.fy.view.ToolClass;

@SuppressLint({ "HandlerLeak", "NewApi", "ShowToast", "SdCardPath" })
public class LoginActivity extends Activity {

	private CircleImageView photo;// 登录界面的头像
	private AutoCompleteTextView account;// 账号
	private EditText password;// 密码
	private Button forgetPassWord;// 忘记密码
	private CheckBox rememberPassword;// 记住密码
	private Button login;// 登陆
	private ProgressDialog progressDialog;
	// 保存所有herf
	private List<HashMap<String, String>> listHerf;
	private DBConnection helper;// 数据库
	SQLiteDatabase sqLiteDatabase;
	private Button savePic;
	private TextView tvHint;

	private PullDoorView pullDoorView;
	private Handler mHandler;
	private Bitmap bitmap;
	private String scaletype;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.activity_login);
		// LogcatHelper.getInstance(this).start(); // 将log保存到文件，便于调试，实际发布时请注释掉
		setPullDoorViewImage();
		setStatusStyle();
		CheckVersionAsyntask checkVersionAsyntask = new CheckVersionAsyntask();
		checkVersionAsyntask.execute();

		// **创建数据库
		helper = new DBConnection(LoginActivity.this);
		sqLiteDatabase = helper.getWritableDatabase();
		this.findViewById();
		if (!this.initData()) {// 初始化一些必须的数据
			ViewUtil.toastLength("内存卡有问题", LoginActivity.this);
		}

		this.isRemember();// 是否是记住密码的

		ToolClass.map();// 初始化映射关系，防止以后用到

		// 忘记密码按钮
		this.forgetPassWord.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * Intent intent = new Intent();
				 * intent.setClass(getApplicationContext(),
				 * ForgetPasswordActivity.class); startActivity(intent);
				 */
				ViewUtil.showToast(getApplicationContext(), "暂不可用，请持续关注。。。");
			}
		});
		// 登陆按钮
		this.login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
	}

	private void setPullDoorViewImage() {
		// TODO Auto-generated method stub

		savePic = (Button) this.findViewById(R.id.btn_above);
		tvHint = (TextView) this.findViewById(R.id.tv_hint);
		Animation ani = new AlphaAnimation(0f, 1f);
		ani.setDuration(1500);
		ani.setRepeatMode(Animation.REVERSE);
		ani.setRepeatCount(Animation.INFINITE);
		tvHint.startAnimation(ani);

		Intent i = getIntent();
		String imageMsg = i.getStringExtra("image") != null ? i
				.getStringExtra("image") : "0|0|0";
		String[] imageAndTime = imageMsg.split("\\|");
		final String imageTime = imageAndTime[0];
		String isPoll = imageAndTime[1];
		scaletype = imageAndTime.length==2?"0":imageAndTime[2];
		savePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				BitmapUtil.saveFileAndDB(
						getApplicationContext(),
						bitmap != null ? bitmap : BitmapFactory.decodeResource(
								getResources(), R.drawable.left1), imageTime
								+ ".jpg");
				ViewUtil.showToast(getApplicationContext(), "保存文件成功");
			}
		});
		mHandler = new Handler();
		pullDoorView = (PullDoorView) findViewById(R.id.myImage);
		if (isPoll.equals("1")) {// 下拉下载
			new Thread() {
				public void run() {
					bitmap = Util.getBitmap(HttpUtilMc.BASE_URL + "image/"
							+ imageTime + ".jpg");
					mHandler.post(runnableUi);
				}
			}.start();
		} else
			pullDoorView.setBgImage(R.drawable.left1);
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			// bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 480);
			pullDoorView
					.setScaletype(scaletype.equals("0") ? ImageView.ScaleType.FIT_XY
							: ImageView.ScaleType.CENTER_CROP);
			pullDoorView.setBgBitmap(bitmap);

		}

	};

	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	private void setStatusStyle() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
		}

		SystemBarTintManager tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.logincolor);
		// tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.main_title_bar));
	}

	/**
	 * 初始化一些必须的数据
	 */
	private boolean initData() {
		this.progressDialog = ViewUtil.getProgressDialog(LoginActivity.this,
				"正在登录");
		// 获取数据库
		boolean isSDcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		return isSDcardExist;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		// LogcatHelper.getInstance(LoginActivity.this).stop();
		finish();
	}

	/**
	 * 是否是记住密码
	 */
	private void isRemember() {
		SharedPreferences preferences = getSharedPreferences(
				StaticVarUtil.USER_INFO, MODE_PRIVATE);
		String account = preferences.getString(StaticVarUtil.ACCOUNT, "");
		String password = preferences.getString(StaticVarUtil.PASSWORD, "");
		boolean isRemember = preferences.getBoolean(StaticVarUtil.IS_REMEMBER,
				false);
		if (isRemember == true) {
			this.rememberPassword.setChecked(true);
			this.account.setText(account);
			this.password.setText(password);
		} else {
			this.rememberPassword.setChecked(false);
			this.account.setText(account);
			this.password.setText("");
		}

	}

	/**
	 * 记住密码
	 */
	private void rememberPassword(String account, String password) {
		SharedPreferences preferences = getSharedPreferences(
				StaticVarUtil.USER_INFO, MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(StaticVarUtil.ACCOUNT, account);
		if (rememberPassword.isChecked() == true) {
			// 插入数据库
			ContentValues values = new ContentValues();
			values.put(com.mc.db.DBConnection.UserSchema.USERNAME, account);
			values.put(com.mc.db.DBConnection.UserSchema.PASSWORD, password);
			int i = sqLiteDatabase.update(UserSchema.TABLE_NAME, values,
					"username='" + account + "'", null);
			if (i == 0) {// 说明没有这个用户，所以得插入
				sqLiteDatabase.insert(UserSchema.TABLE_NAME, null, values);// 插入
			}

			editor.putString(StaticVarUtil.PASSWORD, password);
			editor.putBoolean(StaticVarUtil.IS_REMEMBER, true);// 记住密码
		} else {
			editor.putString(StaticVarUtil.PASSWORD, "");
			editor.putBoolean(StaticVarUtil.IS_REMEMBER, false);// 不记住密码
			// 删除数据库中的该用户
			DBConnection.updateUser(account, LoginActivity.this);
		}
		editor.commit();
	}

	/**
	 * 开启新线程登陆
	 */
	private void login() {

		String strAccount = account.getText().toString();
		String strPassword = password.getText().toString();
		try {
			Integer.parseInt(strAccount);
		} catch (Exception e) {
			ViewUtil.showToast(getApplicationContext(), "账号必须为十位以内的数字！");
			return;
		}
		if (strAccount == null || strAccount.equals("")
				|| strPassword.equals("") || strPassword == null) {
			ViewUtil.showToast(getApplicationContext(), "账号密码不能为空");
			return;
		}
		if (!ConnectionUtil.isConn(getApplicationContext())) {
			ConnectionUtil.setNetworkMethod(LoginActivity.this);
			return;
		}
		rememberPassword(strAccount, strPassword);
		GetPicAsyntask getPicAsyntask = new GetPicAsyntask();
		progressDialog.show();
		getPicAsyntask.execute();

	}

	/**
	 * 找到控件ID
	 */
	private void findViewById() {
		StaticVarUtil.PATH = "/sdcard/xuptscore";// 设置文件目录
		// this.account = (EditText) findViewById(R.id.etAccount);
		this.photo = (CircleImageView) findViewById(R.id.profile_image);
		this.password = (EditText) findViewById(R.id.etPassword);
		password.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					login();
					return true;
				}
				return false;
			}
		});
		String[] USERSFROM = { UserSchema.ID, UserSchema.USERNAME,
				UserSchema.PASSWORD, };
		Cursor c = sqLiteDatabase.query(UserSchema.TABLE_NAME, USERSFROM, null,
				null, null, null, null);
		HashSet<String> set = new HashSet<String>();
		while (c.moveToNext()) {
			set.add(c.getString(1));// 获取用户名
		}
		// 读取所有用户
		String[] users = new String[set.size()];
		set.toArray(users);
		c.close();
		// 创建一个ArrayAdapter封装数组
		ArrayAdapter<String> av = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, users);
		// 账号 自动提示
		account = (AutoCompleteTextView) findViewById(R.id.etAccount);
		account.setAdapter(av);
		account.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					password.setFocusable(true);
					password.setFocusableInTouchMode(true);
					password.requestFocus();
					return true;
				}
				return false;
			}
		});
		account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (account.getText().toString().length() < 8) {
					password.setText("");// 密码置空
					// 设置 默认头像
					Drawable drawable = LoginActivity.this.getResources()
							.getDrawable(R.drawable.person);
					photo.setImageDrawable(drawable);
				}
				if (account.getText().toString().length() == 8) {
					password.setText(DBConnection.getPassword(account.getText()
							.toString(), LoginActivity.this));
					// 判断 头像文件夹中是否包含 该用户的头像
					File file = new File(StaticVarUtil.PATH + "/"
							+ account.getText().toString() + ".JPEG");
					if (file.exists()) {// 如果存在
						Bitmap bitmap = Util.convertToBitmap(StaticVarUtil.PATH
								+ "/" + account.getText().toString() + ".JPEG",
								240, 240);
						if (bitmap != null) {
							photo.setImageBitmap(bitmap);
						} else {
							file.delete();
						}

					} else {// 如果文件夹中不存在这个头像。
						;
					}
				}
			}
		});
		this.forgetPassWord = (Button) findViewById(R.id.butForgetPassword);
		this.rememberPassword = (CheckBox) findViewById(R.id.butRememberPassword);
		this.login = (Button) findViewById(R.id.butLogin);
		this.progressDialog = ViewUtil.getProgressDialog(LoginActivity.this,
				"正在登录");

		Animation animation = AnimationUtils.loadAnimation(LoginActivity.this,
				R.anim.translate);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		layout.setAnimation(animation);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!ConnectionUtil.isConn(getApplicationContext())) {
			ConnectionUtil.setNetworkMethod(LoginActivity.this);
			return;
		}

	}

	// 异步加载登录
	class LoginAsyntask extends AsyncTask<Object, String, String> {

		@SuppressWarnings("deprecation")
		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url;
			url = HttpUtilMc.BASE_URL + "login.jsp?username="
					+ account.getText().toString().trim() + "&password="
					+ URLEncoder.encode(password.getText().toString().trim())
					+ "&session=" + StaticVarUtil.session;// 增加urlendcoder编码
															// 防止密码中出现空格而崩掉
			System.out.println("url" + url);
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			System.out.println("=========================  " + result);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// progress.cancel();
			progressDialog.cancel();
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					System.out.println("result:" + result);
					if (result.equals("error")) {
						ViewUtil.showToast(getApplicationContext(), "密码错误");
						password.setText("");
					} else {
						if (result.equals("no_user")) {
							ViewUtil.showToast(getApplicationContext(), "账号不存在");
							account.setText("");
							password.setText("");
						} else {// 登录成功
							listHerf = new ArrayList<HashMap<String, String>>();
							JSONObject json = new JSONObject(result);
							JSONArray jsonArray = (JSONArray) json
									.get("listHerf");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject o = (JSONObject) jsonArray.get(i);
								HashMap<String, String> map = new HashMap<String, String>();
								map.put("herf", o.getString("herf"));
								map.put("tittle", o.getString("tittle"));
								listHerf.add(map);
							}
							StaticVarUtil.listHerf = listHerf;// 设置为静态
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this,
									MainActivity.class);
							startActivity(intent);
							StaticVarUtil.student.setAccount(account.getText()
									.toString().trim());
							StaticVarUtil.student.setPassword(password
									.getText().toString().trim());

							finish();
						}

					}

				} else {
					ViewUtil.showToast(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION);
					progressDialog.cancel();
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	// 异步加载登录
	class GetPicAsyntask extends AsyncTask<Object, String, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return HttpUtilMc.IsReachIP() ? HttpUtilMc
					.queryStringForPost(HttpUtilMc.BASE_URL + "GetPic.jsp")
					: HttpUtilMc.CONNECT_EXCEPTION;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// progress.cancel();
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("error")) {
						if (!result.equals("ip warning!!!")) {
							JSONObject json = new JSONObject(result);
							String session = json.getString("cookieSessionID");// session
							StaticVarUtil.session = session;
							LoginAsyntask loginAsyntask = new LoginAsyntask();
							loginAsyntask.execute();
						}
					} else {
						ViewUtil.showToast(getApplicationContext(), "服务器维护中。。。");
						progressDialog.cancel();
					}
				} else {
					ViewUtil.showToast(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION);
					progressDialog.cancel();
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.i("LoginActivity", e.toString());
			}

		}
	}

	private String new_version;// 最新版本
	private String update_content;// 更新内容
	private static String apk_url;// 下载地址

	// 异步检测版本
	class CheckVersionAsyntask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "";
			url = HttpUtilMc.BASE_URL + "checkversion.jsp?version="
					+ Util.getVersion(getApplicationContext());
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("no")) {
						String[] str = result.split("\\|");
						apk_url = str[0];
						new_version = str[1];
						update_content = str[2];
						VersionUpdate versionUpdate = new VersionUpdate(
								LoginActivity.this);
						versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
						versionUpdate.updateMsg = new_version + "\n\n"
								+ update_content;
						versionUpdate.checkUpdateInfo();
					}
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}
}