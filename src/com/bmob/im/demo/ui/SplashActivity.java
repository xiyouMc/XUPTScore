package com.bmob.im.demo.ui;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.config.Config;
import com.xy.fy.main.R;
import com.xy.fy.util.StaticVarUtil;

/**
 * 引导页
 * 
 * @ClassName: SplashActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-4 上午9:45:43
 */
public class SplashActivity extends BaseActivity {

  private static final int GO_HOME = 100;
  private static final int GO_LOGIN = 200;

  // 定位获取当前用户的地理位置
  private LocationClient mLocationClient;

  private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash);
    // BmobIM SDK初始化--只需要这一段代码即可完成初始化
    // 请到Bmob官网(http://www.bmob.cn/)申请ApplicationId,具体地址:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
    BmobChat.getInstance(this).init(Config.applicationId);
    // 开启定位
    initLocClient();
    // 注册地图 SDK 广播监听者
    IntentFilter iFilter = new IntentFilter();
    iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
    iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
    mReceiver = new BaiduReceiver();
    registerReceiver(mReceiver, iFilter);

    if (userManager.getCurrentUser() != null) {
      // 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
      updateUserInfos();
      mHandler.sendEmptyMessageDelayed(GO_HOME, 100);
    } else {
      mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
    }
  }

  /**
   * 开启定位，更新当前用户的经纬度坐标
   * 
   * @Title: initLocClient @Description: TODO @param @return void @throws
   */
  private void initLocClient() {
    mLocationClient = CustomApplcation.getInstance().mLocationClient;
    LocationClientOption option = new LocationClientOption();
    option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
    option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
    option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
    option.setIsNeedAddress(false);// 不需要包含地址信息
    mLocationClient.setLocOption(option);
    mLocationClient.start();
  }

  @SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case GO_HOME:
        startAnimActivity(MainActivity.class);
        finish();
        break;
      case GO_LOGIN:
        // 由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        // 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(
            StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount());
        bu.setPassword(StaticVarUtil.student.getPassword());
        // 将user和设备id进行绑定
        bu.setDeviceType("android");
        bu.setInstallId(BmobInstallation.getInstallationId(getApplicationContext()));
        bu.signUp(getApplicationContext(), new SaveListener() {

          @Override
          public void onSuccess() {
            // TODO Auto-generated method stub
            // ShowToast("注册成功");
            // 将设备与username进行绑定
            userManager.bindInstallationForRegister(bu.getUsername());
            // 更新地理位置信息
            updateUserLocation();
            // 发广播通知登陆页面退出
            sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
            updateInfo(StaticVarUtil.student.getName());
          }

          private void updateInfo(String nick) {
            final User user = userManager.getCurrentUser(User.class);
            user.setNick(nick);
            user.update(getApplicationContext(), new UpdateListener() {
              @Override
              public void onSuccess() {
                // TODO Auto-generated method stub
                File file = new File(StaticVarUtil.PATH,
                    StaticVarUtil.student.getAccount() + ".JPEG");
                if (file.exists()) {
                  uploadAvatar(
                      StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG");
                } else {
                  // 启动主页
                  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                  startActivity(intent);
                  finish();
                }
              }

              @Override
              public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowToast("onFailure:" + arg1);
              }
            });
          }

          @Override
          public void onFailure(int arg0, String arg1) {
            // TODO Auto-generated method stub
            BmobLog.i(arg1);
            if ("already taken.".equals(arg1.split("'")[2].trim())) {// 已经注册过
              userManager.login(
                  StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount(),
                  StaticVarUtil.student.getPassword(), new SaveListener() {

                @Override
                public void onSuccess() {
                  // TODO Auto-generated method stub
                  runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                      // TODO Auto-generated method stub
                    }
                  });
                  // 更新用户的地理位置以及好友的资料
                  updateUserInfos();
                  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                  startActivity(intent);
                  finish();
                }

                @Override
                public void onFailure(int errorcode, String arg0) {
                  // TODO Auto-generated method stub
                  BmobLog.i(arg0);
                  ShowToast(arg0);
                }
              });

            }
            // ShowToast("注册失败:" + arg1);
          }
        });
        /*
         * startAnimActivity(LoginActivity.class); finish();
         */break;
      }
    }
  };

  // 更新用户头像
  private void uploadAvatar(String path) {
    final BmobFile bmobFile = new BmobFile(new File(path));
    bmobFile.upload(this, new UploadFileListener() {

      @Override
      public void onSuccess() {
        // TODO Auto-generated method stub
        String url = bmobFile.getFileUrl(getApplicationContext());
        // 更新BmobUser对象
        updateUserAvatar(url);
      }

      @Override
      public void onProgress(Integer arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void onFailure(int arg0, String msg) {
        // TODO Auto-generated method stub
        ShowToast("头像上传失败：" + msg);
      }
    });
  }

  // 更新用户信息
  private void updateUserAvatar(final String url) {
    User user = (User) userManager.getCurrentUser(User.class);
    user.setAvatar(url);
    user.update(this, new UpdateListener() {
      @Override
      public void onSuccess() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        // 更新头像
      }

      @Override
      public void onFailure(int code, String msg) {
        // TODO Auto-generated method stub
        ShowToast("头像更新失败：" + msg);
      }
    });
  }

  /**
   * 构造广播监听类，监听 SDK key 验证以及网络异常广播
   */
  public class BaiduReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
      String s = intent.getAction();
      if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
        ShowToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
      } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
        ShowToast("当前网络连接不稳定，请检查您的网络设置!");
      }
    }
  }

  @Override
  protected void onDestroy() {
    // 退出时销毁定位
    if (mLocationClient != null && mLocationClient.isStarted()) {
      mLocationClient.stop();
    }
    unregisterReceiver(mReceiver);
    super.onDestroy();
  }

}
