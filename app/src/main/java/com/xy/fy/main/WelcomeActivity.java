package com.xy.fy.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xy.fy.asynctask.LoginAsynctask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import top.codemc.common.util.ConnectionUtil;
import top.codemc.common.util.H5Log;
import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.Util;
import top.codemc.common.util.ViewUtil;
import top.codemc.rpcapi.CrashHandler;
import top.codemc.rpcapi.HttpUtilMc;

public class WelcomeActivity extends Activity {

    private LinearLayout welcome;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Util.setContext(getApplicationContext());
        Util.setLanguageShare(WelcomeActivity.this);

        if (!ConnectionUtil.isConn(getApplicationContext())) {
            ConnectionUtil.setNetworkMethod(WelcomeActivity.this);
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        try {
            if (!Util.isDebug(getApplicationContext())) {
                CrashHandler crashHandler = CrashHandler.getInstance();
                crashHandler.init(this);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        try {
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
        } catch (Throwable e) {
            // TODO: handle exception
        }

        H5Log.d(getApplicationContext(), String.valueOf(Util.isDebugable(getApplicationContext())));
        final View view = View.inflate(this, R.layout.activity_welcome, null);

        setContentView(view);
        welcome = (LinearLayout) findViewById(R.id.welcome);
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);

        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
                String session = preferences.getString(StaticVarUtil.SESSION, "");
                if (session != null && !session.isEmpty()) {
                    autoLogin();
                } else {
                    GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask(false);
                    getImageMsgAsytask.execute();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask(true);
                getImageMsgAsytask.execute();
            }
        });
    }

    private Handler mHandler = new Handler();
    private Bitmap bitmap;
    Runnable runnableUi = new Runnable() {
        @SuppressLint("NewApi")
        @Override
        public void run() {

        }

    };

    class GetImageMsgAsytask extends AsyncTask<String, String, String> {
        private boolean isWelcome;

        private GetImageMsgAsytask(boolean isWelcome) {
            // TODO Auto-generated constructor stub
            this.isWelcome = isWelcome;
        }

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
                if (isWelcome) {
                    result = !HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? result : "0|0|0";
                    String[] imageAndTime = result.split("\\|");
                    final String imageTime = imageAndTime[0];
                    String isPoll = imageAndTime[1];
                    if (isPoll.equals("1")) {// 下拉下载
                        final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.animated_remote);
                        LinearInterpolator lir = new LinearInterpolator();
                        anim.setInterpolator(lir);
                        new Thread() {
                            public void run() {
                                try {
                                    sleep(1500);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                bitmap = Util.getBitmap(HttpUtilMc.BASE_URL + "image/" + imageTime + ".jpg");
                                mHandler.post(new Runnable() {

                                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        welcome.setBackground(new BitmapDrawable(bitmap));
                                    }
                                });
                            }
                        }.start();
                    } else {
                    }

                } else {
                    Intent i = new Intent();
                    i.setClass(getApplicationContext(), LoginActivity.class);
                    // 如果网络原因，则直接返回0|0
                    i.putExtra("image", !HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? result : "0|0|0");//
                    startActivity(i);
                    finish();
                }
            } catch (Exception e) {
                // TODO: handle exception
                Log.i("WelcomeActivity", e.toString());
            }
        }
    }

    private void autoLogin() {
        LoginAsynctask loginAsyntask = new LoginAsynctask(WelcomeActivity.this, "", "", "",
                new LoginAsynctask.LoginResult() {

                    @Override
                    public void onLogin(String result) {
                        // TODO Auto-generated method stub

                        try {
                            if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                                if (result.equals("error") || result.equals("errorReq")) {
                                    ViewUtil.showToast(WelcomeActivity.this, "证书过期，请重新登录。");
                                    GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask(false);
                                    getImageMsgAsytask.execute();
                                    // progressDialog.cancel();
                                } else {
                                    StaticVarUtil.listHerf = new ArrayList<HashMap<String, String>>();
                                    JSONObject json = new JSONObject(result);
                                    JSONArray jsonArray = (JSONArray) json.get("listHerf");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject o = (JSONObject) jsonArray.get(i);
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put("herf", o.getString("herf"));
                                        map.put("tittle", o.getString("tittle"));
                                        StaticVarUtil.listHerf.add(map);
                                    }
                                    SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO,
                                            MODE_PRIVATE);
                                    String accountStr = preferences.getString(StaticVarUtil.ACCOUNT, "");
                                    String sessionStr = preferences.getString(StaticVarUtil.SESSION, "");
                                    String passwordStr = preferences.getString(StaticVarUtil.PASSWORD, "");

                                    StaticVarUtil.student.setAccount(accountStr);
                                    StaticVarUtil.student.setPassword(passwordStr);
                                    StaticVarUtil.session = sessionStr;
                                    Intent intent = new Intent();
                                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                                    // progressDialog.cancel();
                                    startActivity(intent);
                                    finish();
                                    StaticVarUtil.quit();
                                }

                            } else {
                                GetImageMsgAsytask getImageMsgAsytask = new GetImageMsgAsytask(false);
                                getImageMsgAsytask.execute();
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.i("LoginActivity", e.toString());
                        }

                    }
                }, null, true);
        loginAsyntask.execute();
    }
}
