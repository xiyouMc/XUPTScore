package com.bmob.im.demo.ui;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.mc.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xy.fy.asynctask.UploadFileAsytask;
import com.xy.fy.main.MainActivity;
import com.xy.fy.main.R;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.StaticVarUtil;

/**
 * 个人资料页面
 * 
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午2:55:19
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

  TextView tv_set_name, tv_set_nick, tv_set_gender;
  ImageView iv_set_avator, iv_arraw, iv_nickarraw;
  LinearLayout layout_all;

  Button btn_chat, btn_back, btn_add_friend;
  RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips;

  String from = "";
  String username = "";
  User user;
  private static final int PIC = 11;// 图片
  private static final int PHO = 22;// 照相
  private static final int RESULT = 33;// 返回结果

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    // 因为魅族手机下面有三个虚拟的导航按钮，需要将其隐藏掉，不然会遮掉拍照和相册两个按钮，且在setContentView之前调用才能生效
    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    if (currentapiVersion >= 14) {
      getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    setContentView(R.layout.activity_set_info);
    from = getIntent().getStringExtra("from");// me add other
    username = getIntent().getStringExtra("username");
    initView();
  }

  private void initView() {
    layout_all = (LinearLayout) findViewById(R.id.layout_all);
    iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
    iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
    iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
    tv_set_name = (TextView) findViewById(R.id.tv_set_name);
    tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
    layout_head = (RelativeLayout) findViewById(R.id.layout_head);
    layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
    layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
    // 黑名单提示语
    layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
    tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
    btn_chat = (Button) findViewById(R.id.btn_chat);
    btn_back = (Button) findViewById(R.id.btn_back);
    btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
    btn_add_friend.setEnabled(false);
    btn_chat.setEnabled(false);
    btn_back.setEnabled(false);
    if (from.equals("me")) {
      initTopBarForLeft("个人资料");
      layout_head.setOnClickListener(this);
      layout_nick.setOnClickListener(this);
      layout_gender.setOnClickListener(this);
      iv_nickarraw.setVisibility(View.VISIBLE);
      iv_arraw.setVisibility(View.VISIBLE);
      btn_back.setVisibility(View.GONE);
      btn_chat.setVisibility(View.GONE);
      btn_add_friend.setVisibility(View.GONE);
    } else {
      initTopBarForLeft("详细资料");
      iv_nickarraw.setVisibility(View.INVISIBLE);
      iv_arraw.setVisibility(View.INVISIBLE);
      // 不管对方是不是你的好友，均可以发送消息--BmobIM_V1.1.2修改
      btn_chat.setVisibility(View.VISIBLE);
      btn_chat.setOnClickListener(this);
      if (from.equals("add")) {// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
        if (mApplication.getContactList().containsKey(username)) {// 是好友
          // btn_chat.setVisibility(View.VISIBLE);
          // btn_chat.setOnClickListener(this);
          btn_back.setVisibility(View.VISIBLE);
          btn_back.setOnClickListener(this);
        } else {
          // btn_chat.setVisibility(View.GONE);
          btn_back.setVisibility(View.GONE);
          btn_add_friend.setVisibility(View.VISIBLE);
          btn_add_friend.setOnClickListener(this);
        }
      } else {// 查看他人
        // btn_chat.setVisibility(View.VISIBLE);
        // btn_chat.setOnClickListener(this);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
      }
      initOtherData(username,false);
    }
  }

  private void initMeData() {
    User user = userManager.getCurrentUser(User.class);
    initOtherData(user.getUsername(),true);
  }

  private void initOtherData(String name,final boolean isInit) {
    userManager.queryUser(name, new FindListener<User>() {

      @Override
      public void onError(int arg0, String arg1) {
        // TODO Auto-generated method stub
        ShowLog("onError onError:" + arg1);
      }

      @Override
      public void onSuccess(List<User> arg0) {
        // TODO Auto-generated method stub
        if (arg0 != null && arg0.size() > 0) {
          user = arg0.get(0);
          btn_chat.setEnabled(true);
          btn_back.setEnabled(true);
          btn_add_friend.setEnabled(true);
          updateUser(user,isInit);
        } else {
          ShowLog("onSuccess 查无此人");
        }
      }
    });
  }

  private void updateUser(User user, boolean isInit) {
    // 更改
    if (isInit) {
      refreshAvatar();
    } else {
      refreshAvatar(user.getAvatar());
    }

    tv_set_name.setText(user.getUsername());
    tv_set_nick.setText(user.getNick());
    tv_set_gender.setText(user.getSex() == true ? "男" : "女");
    // 检测是否为黑名单用户
    if (from.equals("other")) {
      if (BmobDB.create(this).isBlackUser(user.getUsername())) {
        btn_back.setVisibility(View.GONE);
        layout_black_tips.setVisibility(View.VISIBLE);
      } else {
        btn_back.setVisibility(View.VISIBLE);
        layout_black_tips.setVisibility(View.GONE);
      }
    }
  }

  /**
   * 更新头像 refreshAvatar
   * 
   * @return void @throws
   */
  private void refreshAvatar() {

    // 判断 头像文件夹中是否包含 该用户的头像
    File file = new File(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG");
    if (file.exists()) {// 如果存在
      Bitmap bitmap = Util.convertToBitmap(
          StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG", 240, 240);
      if (bitmap != null) {
        iv_set_avator.setImageBitmap(bitmap);
      } else {
        file.delete();
      }

    } else {// 如果文件夹中不存在这个头像。
      iv_set_avator.setImageResource(R.drawable.person);
    }
  }

  /**
   * 更新头像 refreshAvatar
   * 
   * @return void @throws
   */
  private void refreshAvatar(String avatar) {
    if (avatar != null && !avatar.equals("")) {
      ImageLoader.getInstance().displayImage(avatar, iv_set_avator, ImageLoadOptions.getOptions());
    } else {
      iv_set_avator.setImageResource(R.drawable.person);
    }
  }

  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
    if (from.equals("me")) {
      initMeData();
    }
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
    case R.id.btn_chat:// 发起聊天
      Intent intent = new Intent(this, ChatActivity.class);
      intent.putExtra("user", user);
      startAnimActivity(intent);
      finish();
      break;
    case R.id.layout_head:
      File file = new File(StaticVarUtil.PATH);
      if (!file.exists()) {
        file.mkdirs();// 创建文件
      }
      chooseHeadPhoto();// 改变头像
      break;
    case R.id.layout_nick:
      startAnimActivity(UpdateInfoActivity.class);
      break;
    case R.id.layout_gender:// 性别
      showSexChooseDialog();
      break;
    case R.id.btn_back:// 黑名单
      showBlackDialog(user.getUsername());
      break;
    case R.id.btn_add_friend:// 添加好友
      addFriend();
      break;
    }
  }

  String[] sexs = new String[] { "男", "女" };

  private void showSexChooseDialog() {
    new AlertDialog.Builder(this).setTitle("单选框").setIcon(android.R.drawable.ic_dialog_info)
        .setSingleChoiceItems(sexs, 0, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            BmobLog.i("点击的是" + sexs[which]);
            updateInfo(which);
            dialog.dismiss();
          }
        }).setNegativeButton("取消", null).show();
  }

  /**
   * 修改资料 updateInfo @Title: updateInfo @return void @throws
   */
  private void updateInfo(int which) {
    final User user = userManager.getCurrentUser(User.class);
    BmobLog.i("updateInfo 性别：" + user.getSex());
    if (which == 0) {
      user.setSex(true);
    } else {
      user.setSex(false);
    }
    user.update(this, new UpdateListener() {

      @Override
      public void onSuccess() {
        // TODO Auto-generated method stub
        ShowToast("修改成功");
        final User u = userManager.getCurrentUser(User.class);
        BmobLog.i("修改成功后的sex = " + u.getSex());
        tv_set_gender.setText(user.getSex() == true ? "男" : "女");
      }

      @Override
      public void onFailure(int arg0, String arg1) {
        // TODO Auto-generated method stub
        ShowToast("onFailure:" + arg1);
      }
    });
  }

  /**
   * 添加好友请求
   * 
   * @Title: addFriend @Description: TODO @param @return void @throws
   */
  private void addFriend() {
    final ProgressDialog progress = new ProgressDialog(this);
    progress.setMessage("正在添加...");
    progress.setCanceledOnTouchOutside(false);
    progress.show();
    // 发送tag请求
    BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT, user.getObjectId(),
        new PushListener() {

          @Override
          public void onSuccess() {
            // TODO Auto-generated method stub
            progress.dismiss();
            ShowToast("发送请求成功，等待对方验证！");
          }

          @Override
          public void onFailure(int arg0, final String arg1) {
            // TODO Auto-generated method stub
            progress.dismiss();
            ShowToast("发送请求成功，等待对方验证！");
            ShowLog("发送请求失败:" + arg1);
          }
        });
  }

  /**
   * 显示黑名单提示框
   * 
   * @Title: showBlackDialog @Description: TODO @param @return void @throws
   */
  private void showBlackDialog(final String username) {
    DialogTips dialog = new DialogTips(this, "加入黑名单", "加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true,
        true);
    dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialogInterface, int userId) {
        // 添加到黑名单列表
        userManager.addBlack(username, new UpdateListener() {

          @Override
          public void onSuccess() {
            // TODO Auto-generated method stub
            ShowToast("黑名单添加成功!");
            btn_back.setVisibility(View.GONE);
            layout_black_tips.setVisibility(View.VISIBLE);
            // 重新设置下内存中保存的好友列表
            CustomApplcation.getInstance().setContactList(
                CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
          }

          @Override
          public void onFailure(int arg0, String arg1) {
            // TODO Auto-generated method stub
            ShowToast("黑名单添加失败:" + arg1);
          }
        });
      }
    });
    // 显示确认对话框
    dialog.show();
    dialog = null;
  }

  RelativeLayout layout_choose;
  RelativeLayout layout_photo;
  PopupWindow avatorPop;

  public String filePath = "";
  Bitmap newBitmap;
  boolean isFromCamera = false;// 区分拍照旋转
  int degree = 0;

  /*
   * 裁剪图片方法实现
   * 
   * @param uri
   */
  public void startPhotoZoom(Uri uri) {

    Intent intent = new Intent("com.android.camera.action.CROP");// 调用系统的截图功能。
    intent.setDataAndType(uri, "image/*");
    // 设置裁剪
    intent.putExtra("crop", "true");
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // outputX outputY 是裁剪图片宽高
    intent.putExtra("outputX", 320);
    intent.putExtra("outputY", 320);
    intent.putExtra("scale", true);// 黑边
    intent.putExtra("scaleUpIfNeeded", true);// 黑边
    intent.putExtra("return-data", true);
    startActivityForResult(intent, RESULT);
  }

  private Bitmap bitmap = null;// 修改头像

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 如果结果码不是取消的时候
    if (resultCode != RESULT_CANCELED) {
      switch (requestCode) {
      case PHO:
        File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
        startPhotoZoom(Uri.fromFile(tempFile));
        break;
      case PIC:
        // 照片的原始资源地址
        Uri originalUri = data.getData();
        startPhotoZoom(originalUri);
        break;
      case RESULT:
        if (data != null) {
          Bundle extras = data.getExtras();
          if (extras != null) {
            bitmap = extras.getParcelable("data");
          }
          bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 240);// 把它变为240像素的图片
          BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
              StaticVarUtil.student.getAccount() + ".JPEG");
          iv_set_avator.setImageBitmap(bitmap);
          MainActivity.updataPhoto(bitmap);
          // 上传头像
          UploadFileAsytask uploadFileAsytask = new UploadFileAsytask(SetMyInfoActivity.this,
              bitmap);
          uploadFileAsytask.execute(new String[] {
              StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG" });
        }
        break;
      default:
        break;
      }
    } else {
      bitmap = null;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  String path;

  protected void chooseHeadPhoto() {
    String[] items = new String[] {
        Util.getContext().getResources().getString(R.string.select_picture),
        Util.getContext().getResources().getString(R.string.photo) };
    new AlertDialog.Builder(this)
        .setTitle(Util.getContext().getResources().getString(R.string.setPhoto))
        .setItems(items, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
            case 0:// 选择本地图片
              Intent intent = new Intent();
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(intent, PIC);
              break;
            case 1:// 拍照
              Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              Uri imageUri = Uri.fromFile(new File(StaticVarUtil.PATH, "temp.JPEG"));
              // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
              intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
              startActivityForResult(intent2, PHO);
              break;
            }
          }
        }).setNegativeButton(Util.getContext().getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            })
        .show();
  }

}
