package com.xy.fy.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.config.Config;
import com.bmob.im.demo.ui.BaseActivity;
import com.bmob.im.demo.ui.NewFriendActivity;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.ui.fragment.SettingsFragment;
import com.fima.cardsui.views.CardUI;
import com.mc.util.BadgeUtil;
import com.mc.util.CircleImageView;
import com.mc.util.CustomRankListView;
import com.mc.util.H5Log;
import com.mc.util.H5Toast;
import com.mc.util.HttpUtilMc;
import com.mc.util.LogcatHelper;
import com.mc.util.Passport;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.RankUtils;
import com.mc.util.SIMCardInfo;
import com.mc.util.ScoreUtil;
import com.mc.util.Util;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.xy.fy.adapter.LibAdapter;
import com.xy.fy.asynctask.BindXuptLibAsyncTask;
import com.xy.fy.asynctask.CheckVersionAsynctask;
import com.xy.fy.asynctask.GetCETAsyntask;
import com.xy.fy.asynctask.GetPhotoIDAsynctask;
import com.xy.fy.asynctask.GetRankAsycntask;
import com.xy.fy.asynctask.ShowCardAsyncTask;
import com.xy.fy.asynctask.UploadFileAsytask;
import com.xy.fy.asynctask.XuptLibLoginAsynctask;
import com.xy.fy.singleton.BookList;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.ConnectionUtil;
import com.xy.fy.util.ShareUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.TestArrayAdapter;
import com.xy.fy.util.ViewUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends BaseActivity implements EventListener {

  private final static String TAG = "MainActivity";

  private static int requestTimes = 0;
  private static OnekeyShare share;
  private static ShareUtil shareUtil;
  private static boolean isFirst = true;
  private static final int PIC = 11;// 图片
  private static final int PHO = 22;// 照相
  private static final int RESULT = 33;// 返回结果
  public static TextView bukao_tip = null;

  public static SlidingMenu slidingMenu;

  private TextView nickname;
  private String name;//
  private CircleImageView headPhoto;
  private LinearLayout menuBang = null;
  private LinearLayout menuMyBukao = null;
  private LinearLayout menuMyPaiming = null;
  private LinearLayout menuMyCjTongji = null;
  private LinearLayout menuMyCET = null;
  private LinearLayout menuLib = null;
  private LinearLayout menuIdea_back = null;
  private LinearLayout menuSetting = null;// 设置
  private LinearLayout menuAbout = null;// 关于
  private Button check_version = null;

  private TextView ideaMsgText = null;
  private TextView phoneText = null;

  private TextView nameText;

  private AutoCompleteTextView search_edittext;

  private Bitmap bitmap = null;// 修改头像
  private MyHandler mHandler;

  private ArrayList<HashMap<String, Object>> listItem;

  @SuppressLint("ShowToast")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    super.setContentView(R.layout.activity_main);

    mHandler = new MyHandler(this);

    BadgeUtil.resetBadgeCount(getApplicationContext());
    CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(this, false);
    checkVersionAsyntask.execute();
    shareUtil = new ShareUtil(getApplicationContext());
    share = shareUtil.showShare();
    softDeclare();// 将部分 变量 定义为弱引用

    setMenuItemListener();

    // 当前Activity进栈
    StaticVarUtil.activities.add(MainActivity.this);
    // 找到ID
    slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenuXyScore);
    // 打开sliding组件监听
    slidingMenu.setOnOpenListener(new OnOpenListener() {
      @Override
      public void onOpen() {
        // 读取当前菜单的选项
        int item = getCurrentMeunItem();
        if (item == StaticVarUtil.MENU_BANG) {
          setMenuItemState(menuBang, true, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_BUKAO) {
          setMenuItemState(menuBang, false, menuMyBukao, true, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_CJ_TJ) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, true, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_PAIMING) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, true, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_IDEA_BACK) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, true, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_SETTING) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, true,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_ABOUT) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, true);
        } else if (item == StaticVarUtil.MENU_CET) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              true, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        } else if (item == StaticVarUtil.MENU_LIB) {
          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, true, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
        }
      }
    });

    if (Util.isNull(StaticVarUtil.student.getAccount())) {
      deleteCatch();
      LogcatHelper.getInstance(MainActivity.this).stop();
      Intent i = new Intent();
      i.setClass(getApplicationContext(), LoginActivity.class);
      startActivity(i);
      return;
    }
    if (!Util.checkPWD(StaticVarUtil.student.getPassword())) {
      ViewUtil.showToast(getApplicationContext(), "密码不安全，请重新设置密码");
      setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET, false,
          menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, true, menuAbout,
          false);
      setCurrentMenuItem(StaticVarUtil.MENU_SETTING);
      slidingMenu.toggle();// 页面跳转
      slidingMenu.setContent(R.layout.activity_setting);
      menuSetting();
    } else {
      GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
      ProgressDialogUtil.getInstance(MainActivity.this).show();
      getScoreAsyntask.execute();

    }
  }

  private void softDeclare() {
    // TODO Auto-generated method stub

  }

  /*
   * 第一个菜单项
   */
  CardUI mCardView;

  private void menu1() {
    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });

    if (mCardView != null) {
      isFirst = false;
    }
    mCardView = (CardUI) findViewById(R.id.cardsview);
    mCardView.setSwipeable(true);
    ShowCardAsyncTask showCardAsyntask = new ShowCardAsyncTask(this, getResources(), isFirst,
        mCardView, listItem, ScoreUtil.scoreJson);
    showCardAsyntask.execute();
  }

  /*
   * 设置当前MenuItem的状态
   * 
   * @param item MenuItem组件，flag代表组件状态
   */
  private void setMenuItemState(LinearLayout item1, boolean flag1, LinearLayout item2,
      boolean flag2, LinearLayout item3, boolean flag3, LinearLayout item4, boolean flag4,
      LinearLayout item5, boolean flag5, LinearLayout item6, boolean flag6, LinearLayout item7,
      boolean flag7, LinearLayout item8, boolean flag8, LinearLayout item9, boolean flag9) {
    item1.setPressed(flag1);
    item2.setPressed(flag2);
    item3.setPressed(flag3);
    item4.setPressed(flag4);
    item5.setPressed(flag5);
    item6.setPressed(flag6);
    item7.setPressed(flag7);
    item8.setPressed(flag8);
    item9.setPressed(flag9);
  }

  /*
   * 设置一些menuItem监听
   */
  private void setMenuItemListener() {

    nickname = (TextView) findViewById(R.id.nickname);// 用户名
    headPhoto = (CircleImageView) findViewById(R.id.headphoto);// 头像
    menuBang = (LinearLayout) findViewById(R.id.menu_bang);// 1.成绩查询
    menuMyBukao = (LinearLayout) findViewById(R.id.menu_my_bukao);// 2.补考查询
    iv_bukao_tips = (ImageView) findViewById(R.id.iv_bukao_tips);

    menuMyCjTongji = (LinearLayout) findViewById(R.id.menu_my_cj_tongji);// 成绩统计
    menuMyCjTongji.setVisibility(View.GONE);
    menuMyPaiming = (LinearLayout) findViewById(R.id.menu_my_paiming);// 3.我的排名
    menuMyCET = (LinearLayout) findViewById(R.id.menu_my_cet);// CET查分
    menuLib = (LinearLayout) findViewById(R.id.menu_my_lib);
    menuIdea_back = (LinearLayout) findViewById(R.id.idea_back);// 4.我收藏的
    menuSetting = (LinearLayout) findViewById(R.id.menu_setting);// 5.设置
    menuAbout = (LinearLayout) findViewById(R.id.menu_about);
    bukao_tip = (TextView) findViewById(R.id.bukao_tip);

    LinearLayout menuQuit = (LinearLayout) findViewById(R.id.menu_quit);
    menuBang.setPressed(true);// 初始化默认是风云榜被按下
    setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置

    // 请求服务器获取头像id 并且判断本地是否有这个文件
    GetPhotoIDAsynctask getPhotoID = new GetPhotoIDAsynctask(MainActivity.this, headPhoto);
    getPhotoID.execute();

    headPhoto.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated methodstub

        File file = new File(StaticVarUtil.PATH);
        if (!file.exists()) {
          file.mkdirs();// 创建文件
        }
        chooseHeadPhoto();// 改变头像
      }
    });

    menuBang.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setMenuItemState(menuBang, true, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
            menuAbout, false);
        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.card_main);
        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_BANG;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });

    // 补考助手
    menuMyBukao.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        setMenuItemState(menuBang, false, menuMyBukao, true, menuMyCjTongji, false, menuMyCET,
            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
            menuAbout, false);
        slidingMenu.toggle();// 页面跳转
        // 判断如果没有头像的话，先让选择头像，并填写昵称
        // 暂且跳转到好友列表
        // showToast("程序猿们正在努力开发中，请持续关注...");
        slidingMenu.setContent(R.layout.activity_chat_main);
        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(400);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            isCanTouch = false;
            Message msg = new Message();
            msg.what = StaticVarUtil.BMOB_CHAT;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });

    menuMyPaiming.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (StaticVarUtil.list_Rank_xnAndXq.size() != 0) {

          setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
              false, menuLib, false, menuMyPaiming, true, menuIdea_back, false, menuSetting, false,
              menuAbout, false);
          slidingMenu.toggle();// 页面跳转
          slidingMenu.setContent(R.layout.activity_rank);
          new Thread(new Runnable() {
            @Override
            public void run() {
              // TODO Auto-generated method stub
              try {
                Thread.sleep(300);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              Message msg = new Message();
              msg.what = StaticVarUtil.MENU_PAIMING;
              mHandler.sendMessage(msg);
            }
          }).start();
        } else {
          ViewUtil.showToast(getApplicationContext(), "网络不稳定，请稍后查询");
        }
      }
    });

    menuIdea_back.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.activity_ideaback);
        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_IDEA_BACK;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });

    menuSetting.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, true,
            menuAbout, false);
        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.activity_setting);
        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_SETTING;
            mHandler.sendMessage(msg);
          }
        }).start();

      }
    });

    menuMyCET.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
            true, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
            menuAbout, false);
        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.activity_cet);
        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_CET;
            mHandler.sendMessage(msg);
          }
        }).start();

      }
    });

    menuLib.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
            true, menuLib, true, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
            menuAbout, true);

        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.activity_lib);

        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_LIB;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });
    menuAbout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
            menuAbout, true);

        slidingMenu.toggle();// 页面跳转
        slidingMenu.setContent(R.layout.activity_about);

        new Thread(new Runnable() {
          @Override
          public void run() {
            // TODO Auto-generated method stub
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = StaticVarUtil.MENU_ABOUT;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });

    menuQuit.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        quit(true);
      }
    });

  }

  // 补考好友

  private void aboutListener() {
    if (Util.getAndroidSDKVersion() > 10) {
      findViewById(R.id.newTip).setAlpha(100);
    }
    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });

    ImageView qrcode_imageview = (ImageView) findViewById(R.id.code);
    qrcode_imageview.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        showDialogSaveQrcode();
      }
    });
    TextView guanwang = (TextView) findViewById(R.id.ip);
    String ip = "http://www.xiyoumobile.com";
    CharSequence cs = Html.fromHtml(ip);
    guanwang.setText(cs);
    guanwang.setMovementMethod(LinkMovementMethod.getInstance());
    findViewById(R.id.email).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, "ideaback_mc@126.com");
        emailIntent.putExtra(Intent.EXTRA_CC, "ideaback_mc@126.com");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "西邮成绩");
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "xiyouMc"));

      }
    });
    // 分享按钮
    Button share = (Button) findViewById(R.id.share);
    share.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        // ViewUtil.showShare(getApplicationContext());
        shareUtil.showShareUI(MainActivity.share);
      }
    });
    check_version = (Button) findViewById(R.id.checkversion);// 检测新版本按钮
    TextView version = (TextView) findViewById(R.id.version);
    version.setText(Util.getVersion(getApplicationContext()));
    check_version.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(MainActivity.this,
            true);
        ProgressDialogUtil.getInstance(MainActivity.this).show();
        checkVersionAsyntask.execute();
      }
    });
  }

  /*
   * 保存二维码
   */
  private void showDialogSaveQrcode() {
    final CharSequence[] items = { "保存二维码" };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        if (!new File(StaticVarUtil.PATH + Util.QRCODE_FILENAME).exists()) {
          Bitmap bt = ((BitmapDrawable) getApplicationContext().getResources()
              .getDrawable(R.drawable.qrcode)).getBitmap();
          BitmapUtil.saveFileAndDB(getApplicationContext(), bt, Util.QRCODE_FILENAME);
          bt.recycle();
        }
        ViewUtil.showToast(getApplicationContext(), "二维码已保存，请将其分享给同学！");

      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  // TODO Auto-generated method stub
  Spinner xnSpinner;
  Spinner xqSpinner;
  ArrayAdapter<String> xnAdapter;
  ArrayAdapter<String> xqAdapter;

  private void rank() {

    // menu出发 判断为第一次 为了初始化 listview
    RankUtils.isFirstListView = true;
    findviewById();
    if (RankUtils.rankScoreText == null) {
      return;
    }
    RankUtils.rankScoreText.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!RankUtils.rankText.getText().equals("")) {
          RankUtils.allRankList
              .setSelection(Integer.parseInt(RankUtils.rankText.getText().toString()) - 1);
        }
      }
    });
    // search
    search_edittext.addTextChangedListener(new TextWatcher() {

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
        if (search_edittext.getText().toString().length() > 0) {
          // 定位
          for (HashMap<String, Object> map : RankUtils.showRankArrayList) {
            if ((map.get("name").toString()).equals(search_edittext.getText().toString())) {
              search_edittext.clearFocus();
              closeInputMethod();
              RankUtils.allRankList
                  .setSelection(Integer.parseInt(map.get("rankId").toString()) - 1);

            }
          }
        }
      }
    });
    search_edittext.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
          boolean isSearch = false;
          for (HashMap<String, Object> map : RankUtils.showRankArrayList) {
            if ((map.get("name").toString()).equals(search_edittext.getText().toString())) {
              RankUtils.allRankList
                  .setSelection(Integer.parseInt(map.get("rankId").toString()) - 1);
              isSearch = true;
            }
          }
          if (!isSearch) {
            ViewUtil.showToast(getApplicationContext(), "没有该学生信息");
          }
          search_edittext.clearFocus();
          closeInputMethod();
          return true;
        }
        return false;
      }
    });

    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });
    // 分享按钮
    Button share = (Button) findViewById(R.id.share);
    share.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        shareUtil.showShareUI(MainActivity.share);
      }
    });

    nameText.setText(name);
    WindowManager wm = this.getWindowManager();
    @SuppressWarnings("deprecation")
    int width = wm.getDefaultDisplay().getWidth() / 4 + 10;
    xnSpinner = (Spinner) findViewById(R.id.xnSpinner);
    xnSpinner.setLayoutParams(
        new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));

    // xnSpinner.setDropDownWidth(width);
    xqSpinner = (Spinner) findViewById(R.id.xqSpinner);
    xqSpinner.setLayoutParams(
        new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
    String[] xns = new String[StaticVarUtil.list_Rank_xnAndXq.size()];
    int i = 0;

    Iterator<?> it = StaticVarUtil.list_Rank_xnAndXq.entrySet().iterator();
    while (it.hasNext()) {
      Entry<?, ?> entry = (Entry<?, ?>) it.next();
      xns[i] = String.valueOf(entry.getKey());// 返回与此项对应的键
      i++;
      // entry.getValue() 返回与此项对应的值
    }
    // 设置dropDownItem 宽度
    xnAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xns, width);// 配置Adapter
    String xq = StaticVarUtil.list_Rank_xnAndXq.get(xns[0]);// 默认第一学年的首个学期数组
    String[] xqs = xq.split("\\,");
    xqAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xqs, width);// 配置Adapter
    xnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 选择下拉菜单样式
    xqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 选择下拉菜单样式
    xnSpinner.setAdapter(xnAdapter);
    xnSpinner.setSelection(0, false);// 这两个方法真变态
    xqSpinner.setAdapter(xqAdapter);
    xqSpinner.setSelection(0, false);
    listener(xns, width);
    String result = "";// 成绩的数据

    if (RankUtils.allRankMap.size() != 0) {
      for (String xnAndXq : RankUtils.allRankMap.keySet()) {
        // 由于是 menu触发的所以必须判断
        if (xnAndXq.equals(RankUtils.selectXn + RankUtils.selectXq)) {
          // 如果存在则直接 或者value
          result = RankUtils.allRankMap.get(xnAndXq);
          RankUtils.refeshRank(result, RankUtils.isFirstListView, getApplicationContext());
          break;
        }
      }
    } else {
      requestRankAsyntask();
    }
  }

  private void findviewById() {
    // TODO Auto-generated method stub
    RankUtils.allRankList = (CustomRankListView) findViewById(R.id.allRank);
    RankUtils.rankScoreText = (TextView) findViewById(R.id.score);
    search_edittext = (AutoCompleteTextView) findViewById(R.id.search);
    RankUtils.rankText = (TextView) findViewById(R.id.rank);
    nameText = (TextView) findViewById(R.id.name);
  }

  /*
   * 取消软键盘
   */
  private void closeInputMethod() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    boolean isOpen = imm.isActive();
    if (isOpen) {
      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 没有显示则显示
    }
  }

  private void requestRankAsyntask() {
    // 默认
    RankUtils.selectXn = xnSpinner.getSelectedItem().toString();
    RankUtils.selectXq = xqSpinner.getSelectedItem().toString();
    Util.getRequestParmas(getApplicationContext(),
        StaticVarUtil.student.getAccount() + "|" + RankUtils.selectXn.split("\\-")[0] + "|"
            + (RankUtils.selectXq.equals("第一学期") ? "1" : "2"));
    String result = "";
    // 首先查询内存中是否有该学期成绩
    for (String xnAndXq : RankUtils.allRankMap.keySet()) {
      if (xnAndXq.equals(RankUtils.selectXn + RankUtils.selectXq)) {
        // 如果存在则直接 或者value
        result = RankUtils.allRankMap.get(xnAndXq);
        RankUtils.refeshRank(result, RankUtils.isFirstListView, getApplicationContext());
        break;
      }
    }
    if (result.equals("")) {
      // 不存在，则请求
      GetRankAsycntask getRankAsyntask = new GetRankAsycntask(this, nickname, name);
      getRankAsyntask.execute();
    }

  }

  boolean isTouchXNSpinner = false;
  boolean isTouchXQSpinner = false;

  private void listener(final String[] xns, final int width) {
    // TODO Auto-generated method stub

    xnSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if (isTouchXNSpinner) {
          // 将spinner上的选择答案显示在TextView上面
          RankUtils.selectXn = xnAdapter.getItem(arg2);
          // 自动适配 学期
          String xq = StaticVarUtil.list_Rank_xnAndXq.get(xns[arg2]);// 默认第一学年的首个学期数组
          String[] xqs = xq.split("\\,");
          xqAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xqs, width);// 配置Adapter
          xqSpinner.setAdapter(xqAdapter);
          requestRankAsyntask();
          isTouchXNSpinner = false;
        }
      }

      public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
      }
    });
    xnSpinner.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        isTouchXNSpinner = true;
        return false;
      }
    });
    xqSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if (isTouchXQSpinner) {
          // 将spinner上的选择答案显示在TextView上面
          RankUtils.selectXq = xqAdapter.getItem(arg2);
          requestRankAsyntask();
          isTouchXQSpinner = false;
        }
      }

      public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
      }

    });
    xqSpinner.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        isTouchXQSpinner = true;

        return false;
      }
    });
  }

  // 好友列表
  protected void friend_list() {
    // TODO Auto-generated method stub
    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });

    // 添加好友按钮
    Button add_friend = (Button) findViewById(R.id.add_friend);
    add_friend.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        i.setClass(MainActivity.this, AddFriendListActivity.class);
        startActivity(i);
      }
    });

  }

  // 四六级
  protected void cet() {
    // TODO Auto-generated method stub
    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });

    final EditText accout = (EditText) findViewById(R.id.cet_account);

    final EditText name = (EditText) findViewById(R.id.cet_name);

    Button query = (Button) findViewById(R.id.butQuery);
    query.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        String accoutStr = accout.getText().toString();
        String nameStr = name.getText().toString();// 1435229674374
        if (accoutStr == null || "".equals(accoutStr) || nameStr == null || "".equals(nameStr)) {
          return;
        }
        StaticVarUtil.cet_account = accoutStr;
        long time = System.currentTimeMillis();
        String time_s = Passport.jiami(String.valueOf(time),
            String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
        StaticVarUtil.cet_data = Passport.jiami(accoutStr, String.valueOf(time));
        StaticVarUtil.cet_viewstate = time_s;
        GetCETAsyntask getCETAsyntask = new GetCETAsyntask(MainActivity.this);
        ProgressDialogUtil.getInstance(MainActivity.this).show();
        try {
          getCETAsyntask.execute(new String[] { URLEncoder.encode(
              URLEncoder.encode(nameStr.length() < 3 ? nameStr : nameStr.substring(0, 2), "utf-8"),
              "utf-8") });
        } catch (UnsupportedEncodingException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
    Button searchCet = (Button) findViewById(R.id.butForgetCet);
    searchCet.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(getApplicationContext(), ForgetCetActivity.class);
        startActivity(intent);
      }
    });
  }

  /*
   * 修改个人信息，只能修改昵称，密码，头像
   */
  protected void menuSetting() {

    // 菜单按钮
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });

    if (StaticVarUtil.student == null) {
      ViewUtil.showToast(getApplicationContext(), "对不起，查看选项请先登录");
      return;
    }
    EditText etAccount = (EditText) findViewById(R.id.etAccount);
    etAccount.setText(StaticVarUtil.student.getAccount() + "");// 修改多显示一个0的问题
    etAccount.setEnabled(false);// 不可用
    final EditText etPassword1 = (EditText) findViewById(R.id.etPassword1);
    final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
    final EditText cofPassword2 = (EditText) findViewById(R.id.corfimPassword2);// 确认密码
    // 修改
    Button butAlter = (Button) findViewById(R.id.butAlter);
    butAlter.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // 控件值
        String password1 = etPassword1.getText().toString();
        String password2 = etPassword2.getText().toString().trim();
        String password3 = cofPassword2.getText().toString().trim();
        if (password1.equals("") && password2.equals("") && bitmap == null
            && password3.equals("")) {
          ViewUtil.showToast(getApplicationContext(), "您没有信息需要修改");
          return;
        }

        // 密码
        if (password1.equals("") && password2.equals("")) {
          // 如果不修改

        } else {
          if (password1.equals(StaticVarUtil.student.getPassword())
              && password2.equals("") == false) {
            ;
          } else {
            ViewUtil.showToast(getApplicationContext(), "旧密码不正确或者新密码不能为空,请您检查");
            return;
          }
        }
        if (password2.equals(password3)) {
          if (!Util.hasDigitAndNum(password2)) {
            ViewUtil.showToast(getApplicationContext(), "密码中必须包含数字和字母");
          } else {
            if (password2.length() < 6) {
              // 增加修改密码必须超过6位
              ViewUtil.showToast(getApplicationContext(), "密码必须超过6位");
            } else {
              ChangePwAsyntask changePwAsyntask = new ChangePwAsyntask();
              changePwAsyntask.execute(new String[] { password1, password2 });
            }

          }

        } else {
          ViewUtil.showToast(getApplicationContext(), "新密码不正确");

          return;
        }

      }
    });

  }

  /*
   * 选择头像
   * 
   * @return
   */
  protected void chooseHeadPhoto() {
    String[] items = new String[] { "选择本地图片", "拍照" };
    new AlertDialog.Builder(this).setTitle("设置头像")
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
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).show();
  }

  /*
   * 取得回传的数据
   */
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
          headPhoto.setImageBitmap(bitmap);
          // 上传头像
          UploadFileAsytask uploadFileAsytask = new UploadFileAsytask(MainActivity.this, bitmap);
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

  /*
   * 对手机按钮的监听
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
    case KeyEvent.KEYCODE_BACK:
      // 如果是返回按钮,退出
      if (getCurrentMeunItem() != 1) {// 不在第一个页面,返回第一个页面
        menuBang.setPressed(true);// 初始化默认是风云榜被按下
        setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置
        slidingMenu.setContent(R.layout.card_main);
        menu1();
      } else
        quit(false);

      break;
    case KeyEvent.KEYCODE_MENU:// 如果是菜单按钮
      slidingMenu.toggle();
      break;
    default:
      break;
    }
    return true;
  }

  /*
   * 清除内存块中的共享数据
   */
  private void deleteCatch() {
    StaticVarUtil.list_Rank_xnAndXq.clear();
    StaticVarUtil.allBookList = null;
    RankUtils.allRankArrayList = null;
    ScoreUtil.mapScoreOne.clear();
    ;
    ScoreUtil.mapScoreTwo.clear();
    ;
    CustomApplcation.getInstance().logout();
    StaticVarUtil.quit();
    RankUtils.isFirstListView = true;
    // 清空成绩缓存
    isFirst = true;
    fragments = null;
    try {
      unregisterReceiver(mReceiver);
    } catch (Exception e) {
      // TODO: handle exception
    }

    try {
      unregisterReceiver(newReceiver);
    } catch (Exception e) {
    }
    newReceiver = null;
    try {
      unregisterReceiver(userReceiver);
    } catch (Exception e) {
    }
    userReceiver = null;
    contactFragment = null;
    recentFragment = null;
    settingFragment = null;

  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();

    if (!ConnectionUtil.isConn(getApplicationContext())) {
      ConnectionUtil.setNetworkMethod(MainActivity.this);
      return;
    }
    // 小圆点提示
    if (iv_recent_tips == null || iv_bukao_tips == null) {
      return;
    }
    if (BmobDB.create(this).hasUnReadMsg()) {
      iv_recent_tips.setVisibility(View.VISIBLE);
      iv_bukao_tips.setVisibility(View.VISIBLE);
    } else {
      iv_bukao_tips.setVisibility(View.GONE);
      iv_recent_tips.setVisibility(View.GONE);
    }
    if (BmobDB.create(this).hasNewInvite()) {
      iv_contact_tips.setVisibility(View.VISIBLE);
      iv_bukao_tips.setVisibility(View.VISIBLE);
    } else {
      iv_contact_tips.setVisibility(View.GONE);
      iv_bukao_tips.setVisibility(View.GONE);
    }
    MyMessageReceiver.ehList.add(this);// 监听推送的消息
    // 清空
    MyMessageReceiver.mNewNum = 0;
  }

  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    RankUtils.allRankMap.clear();
  }

  /*
   * 退出模块
   * 
   * @param logout 是否注销
   */
  private void quit(final boolean logout) {
    Builder builder = new AlertDialog.Builder(MainActivity.this);

    if (logout) {
      builder.setMessage("你确定要注销吗？");
    } else {
      moveTaskToBack(true);
      return;
    }

    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
        BmobDB.create(getApplicationContext()).queryBmobInviteList().clear();
        deleteCatch();
        LogcatHelper.getInstance(MainActivity.this).stop();
        // 取消定时检测服务
        BmobChat.getInstance(getApplicationContext()).stopPollService();

        userManager.logout();
        if (logout) {
          Intent i = new Intent();
          i.setClass(getApplicationContext(), LoginActivity.class);
          startActivity(i);
        }
      }
    });
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    Dialog dialog = builder.create();
    dialog.show();
  }

  /*
   * 记录设置当前MenuItem的位置，1，2，3，4，5分别代表成绩查询，补考查询，我的排名，我收藏的，选项
   * 
   * @param menuItem 菜单的选项
   */
  private void setCurrentMenuItem(int menuItem) {
    SharedPreferences preferences = getSharedPreferences("currentMenuItem", MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.putInt("item", menuItem);
    editor.commit();
  }

  /*
   * 取得当前MenuItem的位置
   * 
   * @return 当前的menu的菜单项 1，2，3，4，5分别代表成绩查询，补考查询，我的排名，我收藏的，选项,0代表没有这个
   */
  private int getCurrentMeunItem() {
    SharedPreferences preferences = getSharedPreferences("currentMenuItem", MODE_PRIVATE);
    int flag = preferences.getInt("item", 0);
    return flag;
  }

  // 异步加载获取成绩
  class GetScoreAsyntask extends AsyncTask<Object, String, String> {

    @Override
    protected String doInBackground(Object... params) {
      // TODO Auto-generated method stub
      String url = "";
      String canshu = Util.getURL(StaticVarUtil.QUERY_SCORE);
      String[] can = canshu.split("&");
      String url_str = can[0];
      String xm = can[1];
      name = xm.split("=")[1];
      String gnmkdm = can[2];
      try {
        url = HttpUtilMc.BASE_URL + "xscjcx.aspx?session=" + StaticVarUtil.session + "&url="
            + url_str + "&xm="
            + URLEncoder.encode(URLEncoder.encode(xm.split("=")[1], "utf8"), "utf8") + "&" + gnmkdm;
      } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // 查询返回结果
      String result = HttpUtilMc.queryStringForPost(url);
      return result;

    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      // 显示用户名
      nickname.setText(name);
      StaticVarUtil.student.setName(name);
      try {
        if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result) || result.isEmpty()) {
          if (!result.equals("error")) {
            /*
             * 将字符串 写入xml文件中
             */
            if (!result.equals("no_evaluation")) {
              requestTimes = 0;
              ScoreUtil.scoreJson = result;
              listItem = new ArrayList<HashMap<String, Object>>();
              JSONObject jsonObject = new JSONObject(result);
              JSONArray jsonArray = (JSONArray) jsonObject.get("liScoreModels");// 最外层的array
              for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = (JSONObject) jsonArray.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("xn", o.get("xn"));
                map.put("list_xueKeScore", o.get("list_xueKeScore"));
                listItem.add(map);
              }
            } else {
              Builder builder = new AlertDialog.Builder(MainActivity.this);

              builder.setMessage("请到官网进行教师评价后查询成绩！");

              builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  quit(true);
                }
              });
              builder.create().show();
            }

            menu1();
          } else {
            ViewUtil.showToast(getApplicationContext(), "查询失败");
          }

        } else {
          if (requestTimes < 5) {
            requestTimes++;
            GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
            getScoreAsyntask.execute();
          } else {
            ViewUtil.showToast(getApplicationContext(), HttpUtilMc.CONNECT_EXCEPTION);
          }
        }
      } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
        Log.i("LoginActivity", e.toString());
      }

    }

  }

  private void menuIdeaBack() {
    Button menu = (Button) findViewById(R.id.butMenu);
    menu.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        slidingMenu.toggle();
      }
    });
    ideaMsgText = (TextView) findViewById(R.id.text);
    phoneText = (TextView) findViewById(R.id.phone);
    findViewById(R.id.send).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        ProgressDialogUtil.getInstance(MainActivity.this).show();
        SIMCardInfo siminfo = new SIMCardInfo(getApplicationContext());
        final String number = siminfo.getNativePhoneNumber();
        final String data = ideaMsgText.getText().toString().trim() + "|"
            + ("".equals(phoneText.getText().toString().trim()) ? ""
                : phoneText.getText().toString().trim())
            + "|" + StaticVarUtil.student.getAccount() + "|"
            + Util.getVersion(getApplicationContext()) + "|" + number;
        new Thread(new Runnable() {
          @Override
          public void run() {
            Util.sendMail(data);
            Message msg = new Message();
            msg.what = StaticVarUtil.IDEA_BACK_TOAST;
            mHandler.sendMessage(msg);
          }
        }).start();
      }
    });
  }

  class MyHandler extends Handler {

    WeakReference<Activity> mActivityReference;

    MyHandler(Activity activity) {
      mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
      // TODO Auto-generated method stub
      final Activity activity = mActivityReference.get();
      if (activity != null) {
        switch (msg.what) {
        case StaticVarUtil.MENU_BANG:
          menu1();
          setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置
          return;
        case StaticVarUtil.MENU_ABOUT:
          aboutListener();
          setCurrentMenuItem(StaticVarUtil.MENU_ABOUT);// 记录当前选项位置
          return;
        }
        if (!ConnectionUtil.isConn(getApplicationContext())) {
          ConnectionUtil.setNetworkMethod(MainActivity.this);
          menuBang.setPressed(true);// 初始化默认是风云榜被按下
          setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置
          slidingMenu.setContent(R.layout.card_main);
          menu1();
          return;
        }
        switch (msg.what) {
        case StaticVarUtil.MENU_BUKAO:
          friend_list();
          setCurrentMenuItem(StaticVarUtil.MENU_BUKAO);// 记录当前选项位置
          break;
        case StaticVarUtil.MENU_PAIMING:
          rank();
          setCurrentMenuItem(StaticVarUtil.MENU_PAIMING);// 记录当前选项位置
          break;
        case StaticVarUtil.MENU_IDEA_BACK:
          menuIdeaBack();
          setCurrentMenuItem(StaticVarUtil.MENU_IDEA_BACK);// 记录当前选项位置
          break;
        case StaticVarUtil.MENU_SETTING:
          menuSetting();
          setCurrentMenuItem(StaticVarUtil.MENU_SETTING);// 记录当前选项位置
          break;

        case StaticVarUtil.SHARE:
          showShareQrcodeDialog();
          break;
        case StaticVarUtil.MENU_CET:
          cet();
          setCurrentMenuItem(StaticVarUtil.MENU_CET);// 记录当前选项位置
          break;
        case StaticVarUtil.MENU_LIB:
          lib();
          setCurrentMenuItem(StaticVarUtil.MENU_LIB);// 记录当前选项位置
          break;
        case StaticVarUtil.IDEA_BACK_TOAST:
          ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
          // ViewUtil.showToast(getApplicationContext(), "感谢反馈");
          ViewUtil.showDialog("感谢反馈，我们会做个更优秀", "反馈", activity, false,
              new ViewUtil.DialogCallback() {

                @Override
                public void onPost() {
                  // TODO Auto-generated method stub
                  closeInputMethod();
                  menuBang.setPressed(true);// 初始化默认是风云榜被按下
                  setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置
                  slidingMenu.setContent(R.layout.card_main);
                  menu1();
                }
              });
          break;

        case StaticVarUtil.BMOB_CHAT:
          try {
            chat();
          } catch (ClassCastException e) {
            // TODO: handle exception
            e.printStackTrace();
          }

          setCurrentMenuItem(StaticVarUtil.MENU_BUKAO);// 记录当前选项位置
          break;
        case StaticVarUtil.CHECK_VERSION:
          CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(MainActivity.this,
              true);
          checkVersionAsyntask.execute();
          break;
        }
      }
    }

  }

  boolean isReq = true;

  private void lib() {
    initTopBarForOnlyTitle("绑定");
    String lib = "password=123456&account=S04131071";
    final LinearLayout bind_layout = (LinearLayout) findViewById(R.id.common_bind);
    if (StaticVarUtil.allBookList != null) {
      ShowLibMessage(bind_layout, StaticVarUtil.allBookList);
      return;
    }
    ProgressDialogUtil.getInstance(MainActivity.this).show();
    final BindXuptLibAsyncTask bindXuptLibAsyncTask = new BindXuptLibAsyncTask(MainActivity.this,
        lib, "0", new BindXuptLibAsyncTask.OnPostExecute() {

          @Override
          public void returnResult(String result) {
            // TODO Auto-generated method stub
            if ("none".equals(result) || result == null) {// 未绑定
              if (isReq) {
                lib();
                isReq = false;
                return;
              }
              bindLib(bind_layout);
            } else {// 绑定 直接请求
              login_lib(result, bind_layout, false);
            }
          }
        });
    bindXuptLibAsyncTask.execute();
  }

  private void bindLib(final LinearLayout bind_layout) {
    initTopBarForOnlyTitle("绑定");

    bind_layout.setVisibility(View.VISIBLE);
    final EditText libAccount = (EditText) findViewById(R.id.libAccount);
    libAccount.setText("S" + StaticVarUtil.student.getAccount());
    final EditText libPW = (EditText) findViewById(R.id.libPW);
    Button bindBT = (Button) findViewById(R.id.bind);
    bindBT.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        if (libAccount.getText().toString().isEmpty() || libPW.getText().toString().isEmpty()) {
          H5Toast.showToast(getApplicationContext(), "请输入账号和密码");
          ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
          return;
        }
        login_lib("account=" + libAccount.getText().toString().trim() + "&password="
            + libPW.getText().toString().trim(), bind_layout, true);

      }
    });
  }

  int times = 0;

  private void login_lib(final String libName, final LinearLayout bind_layout,
      final boolean isBind) {
    XuptLibLoginAsynctask xuptLibLoginAsynctask = new XuptLibLoginAsynctask(MainActivity.this,
        libName, new XuptLibLoginAsynctask.Login() {

          @Override
          public void onPostLogin(String result) {
            // TODO Auto-generated method stub
            if (!"fail".equals(result.trim())) {// 获取数据
              if (isBind) {
                bind(libName, bind_layout);
              } else {

                Log.d(TAG, "login" + result);
                if (HttpUtilMc.CONNECT_EXCEPTION.equals(result) && times < 3) {
                  login_lib(libName, bind_layout, isBind);
                  times++;
                  return;
                }
                if (HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                  H5Toast.showToast(getApplicationContext(), HttpUtilMc.CONNECT_EXCEPTION);
                  ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
                  return;
                }
                try {
                  ArrayList<BookList> allBookList = new ArrayList<BookList>();
                  JSONArray bookArray = new JSONArray(result);
                  for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject jo = (JSONObject) bookArray.get(i);
                    BookList bookList = new BookList();
                    bookList.setLibName(jo.getString("name"));
                    bookList.setNumber(jo.getString("id"));
                    String[] date = jo.getString("date").split("/");
                    bookList.setLibRenewDate(date[0] + "年" + date[1] + "月" + date[2]);
                    bookList.setBarcode(jo.getString("barcode"));
                    bookList.setRenew(jo.getBoolean("isRenew"));
                    allBookList.add(bookList);
                  }

                  StaticVarUtil.allBookList = allBookList;
                  ShowLibMessage(bind_layout, allBookList);

                } catch (JSONException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            } else {
              Log.d(TAG, "login error" + result + " " + libName);
              if (isBind) {
                H5Toast.showToast(getApplicationContext(), "绑定失败，请确认账号和密码！");
              } else {
                login_lib(libName, bind_layout, isBind);
              }
            }
            ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
          }

        });
    xuptLibLoginAsynctask.execute();
  }

  private void ShowLibMessage(LinearLayout bind_layout, ArrayList<BookList> allBookList) {
    initTopBarForOnlyTitle("图书馆");
    LinearLayout show_lib_layout = (LinearLayout) findViewById(R.id.common_show_lib);
    show_lib_layout.setVisibility(View.VISIBLE);
    bind_layout.setVisibility(View.GONE);
    ListView libList = (ListView) findViewById(R.id.book_list);
    LibAdapter adapter = new LibAdapter(allBookList, getApplicationContext());
    libList.setAdapter(adapter);
  }

  private void bind(final String libName, final LinearLayout bind_layout) {
    BindXuptLibAsyncTask bindXuptLibAsyncTask = new BindXuptLibAsyncTask(MainActivity.this, libName,
        "1", new BindXuptLibAsyncTask.OnPostExecute() {

          @Override
          public void returnResult(String result) {
            if ("success".equals(result)) {
              login_lib(libName, bind_layout, false);
            } else {// 绑定失败
              bind_layout.setVisibility(View.VISIBLE);
              H5Toast.showToast(getApplicationContext(), "请重新绑定");
            }
          }
        });
    bindXuptLibAsyncTask.execute();
  }

  private static final int GO_HOME = 100;
  private static final int GO_LOGIN = 200;

  // 定位获取当前用户的地理位置
  private LocationClient mLocationClient;

  private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

  private void chat() {
    // BmobIM SDK初始化--只需要这一段代码即可完成初始化
    // 请到Bmob官网(http://www.bmob.cn/)申请ApplicationId,具体地址:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
    BmobChat.getInstance(this).init(Config.applicationId);
    ProgressDialogUtil.getInstance(MainActivity.this).show();
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
      File file = new File(StaticVarUtil.PATH, StaticVarUtil.student.getAccount() + ".JPEG");
      if (file.exists()) {
        uploadAvatar(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG");
      }

      updateUserInfos();
      chatHandler.sendEmptyMessageDelayed(GO_HOME, 0);
    } else {
      chatHandler.sendEmptyMessageDelayed(GO_LOGIN, 0);
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

  private Button[] mTabs;
  private ContactFragment contactFragment;
  private RecentFragment recentFragment;
  private SettingsFragment settingFragment;
  private Fragment[] fragments;
  private int index;
  private int currentTabIndex;
  ImageView iv_recent_tips, iv_contact_tips, iv_bukao_tips;// 消息提示

  private void initView() {
    mTabs = new Button[3];
    mTabs[0] = (Button) findViewById(R.id.btn_message);
    mTabs[1] = (Button) findViewById(R.id.btn_contract);
    mTabs[2] = (Button) findViewById(R.id.btn_set);
    iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
    iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);

    if (iv_recent_tips == null) {
      return;
    }
    if (BmobDB.create(this).hasUnReadMsg()) {
      iv_recent_tips.setVisibility(View.VISIBLE);
      iv_bukao_tips.setVisibility(View.VISIBLE);
    } else {
      iv_recent_tips.setVisibility(View.GONE);
      iv_bukao_tips.setVisibility(View.GONE);
    }
    if (BmobDB.create(this).hasNewInvite()) {
      iv_contact_tips.setVisibility(View.VISIBLE);
      iv_bukao_tips.setVisibility(View.VISIBLE);
    } else {
      iv_contact_tips.setVisibility(View.GONE);
      iv_bukao_tips.setVisibility(View.GONE);
    }
    MyMessageReceiver.ehList.add(this);// 监听推送的消息
    // 清空
    MyMessageReceiver.mNewNum = 0;
    // 把第一个tab设为选中状态
    mTabs[0].setSelected(true);
  }

  private void initTab() {
    contactFragment = new ContactFragment();
    recentFragment = new RecentFragment();
    settingFragment = new SettingsFragment();
    fragments = new Fragment[] { recentFragment, contactFragment, settingFragment };
    // 添加显示第一个fragment
    if (contactFragment.isAdded()) {
      return;
    }
    try {
      getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment)
          .add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment)
          .commitAllowingStateLoss();
    } catch (IllegalArgumentException e) {
      // TODO: handle exception
    }

    // while (contactFragment.isAdded()) {
    // break;
    // }
    ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
  }

  /**
   * button点击事件
   * 
   * @param view
   */
  public void onTabSelect(View view) {
    if (mTabs == null) {
      return;
    }
    switch (view.getId()) {
    case R.id.btn_message:
      index = 0;
      break;
    case R.id.btn_contract:
      index = 1;
      break;
    case R.id.btn_set:
      index = 2;
      break;
    }
    if (currentTabIndex != index) {
      FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
      trx.hide(fragments[currentTabIndex]);
      if (!fragments[index].isAdded()) {
        trx.add(R.id.fragment_container, fragments[index]);
      }
      trx.show(fragments[index]).commit();
    }
    mTabs[currentTabIndex].setSelected(false);
    // 把当前tab设为选中状态
    mTabs[index].setSelected(true);
    currentTabIndex = index;
  }

  /**
   * 刷新界面
   * 
   * @Title: refreshNewMsg @Description: TODO @param @param message @return void @throws
   */
  private void refreshNewMsg(BmobMsg message) {
    // 声音提示
    boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
    if (isAllow) {
      CustomApplcation.getInstance().getMediaPlayer().start();
    }
    iv_recent_tips.setVisibility(View.VISIBLE);
    iv_bukao_tips.setVisibility(View.VISIBLE);
    // 也要存储起来
    if (message != null) {
      BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true, message);
    }
    if (currentTabIndex == 0) {
      // 当前页面如果为会话页面，刷新此页面
      if (recentFragment != null) {
        recentFragment.refresh();
      }
    }
  }

  NewBroadcastReceiver newReceiver;

  private void initNewMessageBroadCast() {
    // 注册接收消息广播
    newReceiver = new NewBroadcastReceiver();
    IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
    // 优先级要低于ChatActivity
    intentFilter.setPriority(3);
    registerReceiver(newReceiver, intentFilter);
  }

  /**
   * 新消息广播接收者
   * 
   */
  private class NewBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      // 刷新界面
      refreshNewMsg(null);
      // 记得把广播给终结掉
      abortBroadcast();
    }
  }

  TagBroadcastReceiver userReceiver;

  private void initTagMessageBroadCast() {
    // 注册接收消息广播
    userReceiver = new TagBroadcastReceiver();
    IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
    // 优先级要低于ChatActivity
    intentFilter.setPriority(3);
    registerReceiver(userReceiver, intentFilter);
  }

  /**
   * 标签消息广播接收者
   */
  private class TagBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
      refreshInvite(message);
      // 记得把广播给终结掉
      abortBroadcast();
    }
  }

  @Override
  public void onNetChange(boolean isNetConnected) {
    // TODO Auto-generated method stub
    if (isNetConnected) {
      ShowToast(R.string.network_tips);
    }
  }

  @Override
  public void onAddUser(BmobInvitation message) {
    // TODO Auto-generated method stub
    refreshInvite(message);
  }

  /**
   * 刷新好友请求
   * 
   * @Title: notifyAddUser @Description: TODO @param @param message @return void @throws
   */
  private void refreshInvite(BmobInvitation message) {
    boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
    if (isAllow) {
      CustomApplcation.getInstance().getMediaPlayer().start();
    }
    iv_contact_tips.setVisibility(View.VISIBLE);
    iv_bukao_tips.setVisibility(View.VISIBLE);
    if (currentTabIndex == 1) {
      if (contactFragment != null) {
        contactFragment.refresh();
      }
    } else {
      // 同时提醒通知
      String tickerText = message.getFromname() + "请求添加好友";
      boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
      BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate,
          R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),
          NewFriendActivity.class);
    }
  }

  @Override
  public void onOffline() {
    // TODO Auto-generated method stub
    showOfflineDialog(this);
    finish();
  }

  @Override
  public void onReaded(String conversionId, String msgTime) {
    // TODO Auto-generated method stub
  }

  /**
   * 连续按两次返回键就退出
   */
  @Override
  public void onBackPressed() {
    // TODO Auto-generated method stub
    ViewUtil.cancelToast();
    super.onBackPressed();
  }

  private void startChatActivity() {
    BmobConstants.IS_STARTED = 1;
    // 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
    BmobChat.getInstance(getApplicationContext()).startPollService(30);
    // 开启广播接收器
    initNewMessageBroadCast();
    initTagMessageBroadCast();
    initView();
    initTab();
  }

  @SuppressLint("HandlerLeak")
  private Handler chatHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case GO_HOME:
        H5Log.d(getApplicationContext(), "go home");
        startChatActivity();
        break;
      case GO_LOGIN:
        H5Log.d(getApplicationContext(), "go login");
        // 由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
        // 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
        final User bu = new User();
        bu.setUsername(StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount());
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
                  // Intent intent = new Intent(getApplicationContext(),
                  // com.bmob.im.demo.ui.MainActivity.class);
                  // startActivity(intent);
                  startChatActivity();
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
            ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
            if (arg1 == null) {
              return;
            }
            BmobLog.i(arg1);

            if (arg1.split("'").length < 3) {
              H5Toast.showToast(getApplicationContext(), "业务繁忙，请稍后再试！");
              menuBang.setPressed(true);// 初始化默认是风云榜被按下
              setCurrentMenuItem(StaticVarUtil.MENU_BANG);// 记录当前选项位置
              slidingMenu.setContent(R.layout.card_main);
              menu1();
              return;
            }
            if ("already taken.".equals(arg1.split("'")[2].trim())) {// 已经注册过
              userManager.login(
                  StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount(),
                  StaticVarUtil.student.getPassword(), new SaveListener() {

                @Override
                public void onSuccess() {
                  // 更新用户的地理位置以及好友的资料
                  updateUserInfos();
                  startChatActivity();
                }

                @Override
                public void onFailure(int errorcode, String arg0) {
                  // TODO Auto-generated method stub
                  BmobLog.i(arg0);
                  ShowToast(arg0);
                }
              });

            }
          }
        });
        break;
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
        startChatActivity();
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
    try {
      CustomApplcation.getInstance().logout();
      unregisterReceiver(mReceiver);
    } catch (Exception e) {
      // TODO: handle exception
    }

    try {
      unregisterReceiver(newReceiver);
    } catch (Exception e) {
    }
    try {
      unregisterReceiver(userReceiver);
    } catch (Exception e) {
    }
    // 取消定时检测服务
    BmobChat.getInstance(this).stopPollService();
    super.onDestroy();
  }

  private void showShareQrcodeDialog() {
    Builder builder = new AlertDialog.Builder(MainActivity.this);

    builder.setMessage("由于本专业使用人数较少,因此排名会有误差。\n若需查询准确排名，请分享给同学下载该软件！");

    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // ViewUtil.showShare(getApplicationContext());
        shareUtil.showShareUI(MainActivity.share);
      }
    });
    builder.create().show();
  }

  // 异步改变密码
  class ChangePwAsyntask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
      // TODO Auto-generated method stub
      String canshu = Util.getURL(StaticVarUtil.CHANGE_PW);
      return HttpUtilMc
          .queryStringForPost(HttpUtilMc.BASE_URL + "changepw.jsp?session=" + StaticVarUtil.session
              + "&url=" + canshu + "&old_password=" + params[0] + "&new_password=" + params[1]);

    }

    @Override
    protected void onPostExecute(String result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);
      // progress.cancel();
      // 显示用户名
      nickname.setText(name);
      try {
        if (HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
          ViewUtil.showToast(getApplicationContext(), HttpUtilMc.CONNECT_EXCEPTION);
          return;
        }
        ViewUtil.showToast(getApplicationContext(),
            !result.equals("error") ? "修改成功,请重新登录" : "修改不成功");
        if (!result.equals("error")) {
          quit(true);// 注销重新登录
        }
      } catch (Exception e) {
        // TODO: handle exception
        e.printStackTrace();
        Log.i("LoginActivity", e.toString());
      }

    }

  }

  @Override
  public void onMessage(BmobMsg message) {
    // TODO Auto-generated method stub
    refreshNewMsg(message);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    // TODO Auto-generated method stub
    return isCanTouch ? super.dispatchTouchEvent(ev) : true;
  }

}
