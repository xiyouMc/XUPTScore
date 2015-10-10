package com.xy.fy.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import com.mc.db.DBConnection;
import com.mc.db.DBConnection.UserSchema;
import com.mc.util.CircleImageView;
import com.mc.util.HttpUtilMc;
import com.mc.util.SystemBarTintManager;
import com.mc.util.Util;
import com.xy.fy.asynctask.GetPicAsynctask;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.ConnectionUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.PullDoorView;
import com.xy.fy.view.ToolClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity {

  private CircleImageView photo;
  private AutoCompleteTextView account;
  private EditText password;
  private Button forgetPassWord;
  private CheckBox rememberPassword;
  private Button login;
  private TextView selectLanguage;
  private ProgressDialog progressDialog;
  private DBConnection helper;
  SQLiteDatabase sqLiteDatabase;
  private ImageView savePic;
  private TextView tvHint;

  private PullDoorView pullDoorView;
  private Handler mHandler;
  private Bitmap bitmap;
  private String scaletype;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Util.setContext(getApplicationContext());
    Intent i = getIntent();
    String imageMsg = i.getStringExtra("image") != null ? i.getStringExtra("image") : "0|0|0";
    if (imageMsg.equals("0|0|0") || imageMsg.equals("0|0")) {
      super.setContentView(R.layout.activity_login_normal);
    } else {
      super.setContentView(R.layout.activity_login);
      setPullDoorViewImage(imageMsg);
    }

    StaticVarUtil.quit();
    StaticVarUtil.activities.add(LoginActivity.this);
    
    setStatusStyle();
    helper = new DBConnection(LoginActivity.this);
    sqLiteDatabase = helper.getWritableDatabase();
    this.findViewById();
    if (!this.initData()) {
      ViewUtil.toastLength("内存卡有问题", LoginActivity.this);
    }
    this.isRemember();// 是否是记住密码的
    ToolClass.map();// 初始化映射关系，防止以后用到
    // 忘记密码按钮
    this.forgetPassWord.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
         * Intent intent = new Intent(); intent.setClass(getApplicationContext(),
         * ForgetPasswordActivity.class); startActivity(intent);
         */
        ViewUtil.showToast(getApplicationContext(), "暂不可用，请持续关注。。。");
      }
    });
    this.login.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Util.isFastDoubleClick()) {
          return;
        }
//        login();
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);
        if (progressDialog != null) {
          progressDialog.dismiss();
        }
        StaticVarUtil.student.setAccount("aaaa");
        StaticVarUtil.student.setPassword("aaaa");
        // progressDialog.cancel();
        startActivity(intent);
      }
    });
    this.selectLanguage.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, LanguageActivity.class);
        
        startActivity(intent);
      }
    });
  }

  private void setPullDoorViewImage(String imageMsg) {

    // if (imageMsg.equals("0|0|0")||imageMsg.equals("0|0")) {
    // return;
    // }
    savePic = (ImageView) this.findViewById(R.id.btn_above);
    tvHint = (TextView) this.findViewById(R.id.tv_hint);
    Animation ani = new AlphaAnimation(0f, 1f);
    ani.setDuration(1500);
    ani.setRepeatMode(Animation.REVERSE);
    ani.setRepeatCount(Animation.INFINITE);
    tvHint.startAnimation(ani);

    String[] imageAndTime = imageMsg.split("\\|");
    final String imageTime = imageAndTime[0];
    String isPoll = imageAndTime[1];
    scaletype = imageAndTime.length == 2 ? "1" : imageAndTime[2];
    savePic.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View arg0) {
        if (Util.isExternalStorageWritable()) {
          BitmapUtil.saveFileAndDB(getApplicationContext(),
              bitmap != null ? bitmap
                  : BitmapFactory.decodeResource(getResources(), R.drawable.left1),
              imageTime + ".jpg");
          ViewUtil.showToast(getApplicationContext(), "保存文件成功");
        } else {
          ViewUtil.showToast(getApplicationContext(), "sdcard不存在");
        }

      }
    });
    mHandler = new Handler();
    pullDoorView = (PullDoorView) findViewById(R.id.myImage);
    if (isPoll.equals("1")) {// 下拉下载
      final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
          R.anim.animated_remote);
      LinearInterpolator lir = new LinearInterpolator();
      anim.setInterpolator(lir);
      savePic.startAnimation(anim);
      new Thread() {
        public void run() {
          try {
            sleep(1500);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          bitmap = Util.getBitmap(HttpUtilMc.BASE_URL + "image/" + imageTime + ".jpg");
          mHandler.post(runnableUi);
        }
      }.start();
    } else {
      savePic.setBackgroundResource(R.drawable.picture_down_up);
      pullDoorView.setBgImage(R.drawable.left1);
    }

    if (bitmap != null) {
      bitmap.recycle();
    }
  }

  Runnable runnableUi = new Runnable() {
    @Override
    public void run() {
      pullDoorView.setScaletype(
          scaletype.equals("0") ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.CENTER_CROP);
      pullDoorView.setBgBitmap(bitmap);
      savePic.clearAnimation();
      savePic.setBackgroundResource(R.drawable.picture_down_up);

    }

  };

  @SuppressLint("InlinedApi")
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

      SystemBarTintManager tintManager = new SystemBarTintManager(LoginActivity.this);
      tintManager.setStatusBarTintEnabled(true);
      tintManager.setStatusBarTintResource(R.color.logincolor);
    }
  }

  private boolean initData() {
    this.progressDialog = ViewUtil.getProgressDialog(LoginActivity.this, this.getString(R.string.logining,""));
    // 获取数据库
    boolean isSDcardExist = Environment.getExternalStorageState()
        .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
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
    SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
    String account = preferences.getString(StaticVarUtil.ACCOUNT, "");
    String password = preferences.getString(StaticVarUtil.PASSWORD, "");
    boolean isRemember = preferences.getBoolean(StaticVarUtil.IS_REMEMBER, false);
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
    SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.putString(StaticVarUtil.ACCOUNT, account);
    if (rememberPassword.isChecked() == true) {
      // 插入数据库
      ContentValues values = new ContentValues();
      values.put(com.mc.db.DBConnection.UserSchema.USERNAME, account);
      values.put(com.mc.db.DBConnection.UserSchema.PASSWORD, password);
      int i = sqLiteDatabase.update(UserSchema.TABLE_NAME, values, "username='" + account + "'",
          null);
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
    if (strAccount == null || strAccount.equals("") || strPassword.equals("")
        || strPassword == null) {
      ViewUtil.showToast(getApplicationContext(), "账号密码不能为空");
      return;
    }
    if (!ConnectionUtil.isConn(getApplicationContext())) {
      ConnectionUtil.setNetworkMethod(LoginActivity.this);
      return;
    }
    rememberPassword(strAccount, strPassword);

    GetPicAsynctask getPicAsyntask = new GetPicAsynctask(LoginActivity.this, strAccount,
        strPassword, progressDialog, new GetPicAsynctask.GetPic() {

          @Override
          public void onReturn(String result) {
            // TODO Auto-generated method stub

            if ("error".equals(result)) {
              password.setText("");
            } else if ("no_user".equals(result)) {
              account.setText("");
              password.setText("");
            }
          }
        });
    progressDialog.show();
    getPicAsyntask.execute();

  }

  /**
   * 找到控件ID
   */
  @SuppressLint("SdCardPath")
  private void findViewById() {
    if (Util.isExternalStorageWritable()) {
      StaticVarUtil.PATH = "/sdcard/xuptscore/";// 设置文件目录
    } else {
      StaticVarUtil.PATH = "/data/data/com.xy.fy.main/";// 设置文件目录
    }

    if (!new File(StaticVarUtil.PATH).exists()) {
      new File(StaticVarUtil.PATH).mkdirs();
    }
    // this.account = (EditText) findViewById(R.id.etAccount);
    this.photo = (CircleImageView) findViewById(R.id.profile_image);
    this.password = (EditText) findViewById(R.id.etPassword);
    password.setOnKeyListener(new OnKeyListener() {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
          login();
          return true;
        }
        return false;
      }
    });
    String[] USERSFROM = { UserSchema.ID, UserSchema.USERNAME, UserSchema.PASSWORD, };
    Cursor c = sqLiteDatabase.query(UserSchema.TABLE_NAME, USERSFROM, null, null, null, null, null);
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
        if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
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
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

      }

      @Override
      public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        if (account.getText().toString().length() < 8) {
          password.setText("");// 密码置空
          // 设置 默认头像
          Drawable drawable = LoginActivity.this.getResources().getDrawable(R.drawable.person);
          photo.setImageDrawable(drawable);
        }
        if (account.getText().toString().length() == 8) {
          password
              .setText(DBConnection.getPassword(account.getText().toString(), LoginActivity.this));
          // 判断 头像文件夹中是否包含 该用户的头像
          File file = new File(StaticVarUtil.PATH + "/" + account.getText().toString() + ".JPEG");
          if (file.exists()) {// 如果存在
            Bitmap bitmap = Util.convertToBitmap(
                StaticVarUtil.PATH + "/" + account.getText().toString() + ".JPEG", 240, 240);
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
    this.selectLanguage = (TextView) findViewById(R.id.setLoginLanguage);
    
    /*
     * Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate);
     * LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
     * layout.setAnimation(animation);
     */
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!ConnectionUtil.isConn(getApplicationContext())) {
      ConnectionUtil.setNetworkMethod(LoginActivity.this);
      return;
    }

  }

}
