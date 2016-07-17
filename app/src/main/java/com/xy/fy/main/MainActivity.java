package com.xy.fy.main;

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
import com.bmob.im.demo.view.HeaderLayout;
import com.fima.cardsui.views.CardUI;
import com.mc.db.DBConnection;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
    private static final int PIC = 11;// ͼƬ
    private static final int PHO = 22;// ����
    private static final int RESULT = 33;// ���ؽ��
    private static final int GO_HOME = 100;
    private static final int GO_LOGIN = 200;
    public static TextView bukao_tip = null;
    public static SlidingMenu slidingMenu;
    private static int requestTimes = 0;
    private static OnekeyShare share;
    private static ShareUtil shareUtil;
    private static boolean isFirst = true;
    private static CircleImageView headPhoto;
    private static boolean isCheck = false;
    /*
     * ��һ���˵���
     */
    CardUI mCardView;
    // TODO Auto-generated method stub
    Spinner xnSpinner;
    Spinner xqSpinner;
    ArrayAdapter<String> xnAdapter;
    ArrayAdapter<String> xqAdapter;
    boolean isTouchXNSpinner = false;
    boolean isTouchXQSpinner = false;
    boolean isReq = true;
    int times = 0;
    String loginResult = null;
    ImageView iv_recent_tips, iv_contact_tips, iv_bukao_tips;// ��Ϣ��ʾ
    NewBroadcastReceiver newReceiver;
    TagBroadcastReceiver userReceiver;
    private TextView nickname;
    private String name;//
    private LinearLayout menuBang = null;
    private LinearLayout menuMyBukao = null;
    private LinearLayout menuMyPaiming = null;
    private LinearLayout menuMyCjTongji = null;
    private LinearLayout menuMyCET = null;
    private LinearLayout menuLib = null;
    private LinearLayout menuIdea_back = null;

    // ��������
    private LinearLayout menuSetting = null;// ����
    private LinearLayout menuAbout = null;// ����
    private Button check_version = null;
    private TextView ideaMsgText = null;
    private TextView phoneText = null;
    private TextView nameText;
    private AutoCompleteTextView search_edittext;
    private Bitmap bitmap = null;// �޸�ͷ��
    private MyHandler mHandler;
    private LibAdapter adapter;
    private Button[] mTabs;
    private ContactFragment contactFragment;
    private RecentFragment recentFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
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
                    // ����ÿ��Ӧ�õ�ע����������϶���һ���IM sdkδ�ṩע�᷽�����û��ɰ���bmod SDK��ע�᷽ʽ����ע�ᡣ
                    // ע���ʱ����Ҫע�����㣺1��User���а��豸id��type��2���豸���а�username�ֶ�
                    final User bu = new User();
                    bu.setUsername(StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount());
                    bu.setPassword(StaticVarUtil.student.getPassword());
                    // ��user���豸id���а�
                    bu.setDeviceType("android");
                    bu.setInstallId(BmobInstallation.getInstallationId(getApplicationContext()));
                    bu.signUp(getApplicationContext(), new SaveListener() {

                        @Override
                        public void onSuccess() {
                            // TODO Auto-generated method stub
                            // ShowToast("ע��ɹ�");
                            // ���豸��username���а�
                            userManager.bindInstallationForRegister(bu.getUsername());
                            // ���µ���λ����Ϣ
                            updateUserLocation();
                            // ���㲥֪ͨ��½ҳ���˳�
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
                                    File file = new File(StaticVarUtil.PATH, StaticVarUtil.student.getAccount()
                                            + ".JPEG");
                                    if (file.exists()) {
                                        uploadAvatar(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount()
                                                + ".JPEG");
                                    } else {
                                        // ������ҳ
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
                                H5Toast.showToast(getApplicationContext(), "ҵ��æ�����Ժ����ԣ�");
                                menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
                                setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��
                                slidingMenu.setContent(R.layout.card_main);
                                menu1();
                                return;
                            }
                            if ("already taken.".equals(arg1.split("'")[2].trim())) {// �Ѿ�ע���
                                userManager.login(
                                        StaticVarUtil.student.getName() + "-" + StaticVarUtil.student.getAccount(),
                                        StaticVarUtil.student.getPassword(), new SaveListener() {

                                            @Override
                                            public void onSuccess() {
                                                // �����û��ĵ���λ���Լ����ѵ�����
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

    public static void updataPhoto(Bitmap tBitmap) {
        headPhoto.setImageBitmap(tBitmap);
    }

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(R.layout.activity_main);
        Util.setContext(getApplicationContext());
        mHandler = new MyHandler(this);
        // save session
        SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(StaticVarUtil.ACCOUNT, StaticVarUtil.student.getAccount());
        editor.putString(StaticVarUtil.SESSION, StaticVarUtil.session);
        editor.commit();

        try {
            BadgeUtil.resetBadgeCount(getApplicationContext());
        } catch (Exception e) {
            // TODO: handle exception
        }
        CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(this, false,
                new CheckVersionAsynctask.OnCheck() {

                    @Override
                    public void onCheck() {
                        // TODO Auto-generated method stub
                    }
                });
        if (!isCheck) {
            checkVersionAsyntask.execute();
            isCheck = true;
        }
        shareUtil = new ShareUtil(getApplicationContext());
        share = shareUtil.showShare();

        softDeclare();// ������ ���� ����Ϊ������

        setMenuItemListener();

        // ��ǰActivity��ջ
        StaticVarUtil.activities.add(MainActivity.this);
        // �ҵ�ID
        slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenuXyScore);
        // ��sliding�������
        slidingMenu.setOnOpenListener(new OnOpenListener() {
            @Override
            public void onOpen() {
                // ��ȡ��ǰ�˵���ѡ��
                int item = getCurrentMeunItem();
                if (item == StaticVarUtil.MENU_BANG) {
                    setMenuItemState(menuBang, true, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
                            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting,
                            false, menuAbout, false);
                } else if (item == StaticVarUtil.MENU_BUKAO) {
                    setMenuItemState(menuBang, false, menuMyBukao, true, menuMyCjTongji, false, menuMyCET,
                            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting,
                            false, menuAbout, false);
                } else if (item == StaticVarUtil.MENU_CJ_TJ) {
                    setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, true, menuMyCET,
                            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting,
                            false, menuAbout, false);
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
                            false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting,
                            false, menuAbout, true);
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

        if (Util.isNull(StaticVarUtil.student) || Util.isNull(StaticVarUtil.student.getAccount())) {
            deleteCatch();
            LogcatHelper.getInstance(MainActivity.this).stop();
            Intent i = new Intent();
            i.setClass(getApplicationContext(), WelcomeActivity.class);
            startActivity(i);
            finish();
            return;
        }
        if (!Util.checkPWD(StaticVarUtil.student.getPassword())) {
            ViewUtil.showToast(getApplicationContext(), "���벻��ȫ����������������");
            setMenuItemState(menuBang, false, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
                    false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, true,
                    menuAbout, false);
            setCurrentMenuItem(StaticVarUtil.MENU_SETTING);
            slidingMenu.toggle();// ҳ����ת
            slidingMenu.setContent(R.layout.activity_setting);
            try {
                menuSetting();
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            if (StaticVarUtil.listItem != null) {
                nickname.setText(StaticVarUtil.student.getName());
                menu1();
                return;
            }
            GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
            ProgressDialogUtil.getInstance(MainActivity.this).show();
            getScoreAsyntask.execute();

        }
    }

    private void softDeclare() {
        // TODO Auto-generated method stub

    }

    private void menu1() {
        // �˵���ť
        Button menu = (Button) findViewById(R.id.butMenu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        // ���?ť
        Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // ViewUtil.showShare(getApplicationContext());
                shareUtil.showShareUI(MainActivity.share);
            }
        });

        if (mCardView != null) {
            isFirst = false;
        }
        mCardView = (CardUI) findViewById(R.id.cardsview);
        mCardView.setSwipeable(true);
        ShowCardAsyncTask showCardAsyntask = new ShowCardAsyncTask(this, getResources(), isFirst,
                mCardView, StaticVarUtil.listItem, ScoreUtil.scoreJson);
        showCardAsyntask.execute();
    }

    /*
     * ���õ�ǰMenuItem��״̬
     *
     * @param item MenuItem�����flag������״̬
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
     * ����һЩmenuItem����
     */
    private void setMenuItemListener() {

        nickname = (TextView) findViewById(R.id.nickname);// �û���
        headPhoto = (CircleImageView) findViewById(R.id.headphoto);// ͷ��
        menuBang = (LinearLayout) findViewById(R.id.menu_bang);// 1.�ɼ���ѯ
        menuMyBukao = (LinearLayout) findViewById(R.id.menu_my_bukao);// 2.������ѯ
        iv_bukao_tips = (ImageView) findViewById(R.id.iv_bukao_tips);

        menuMyCjTongji = (LinearLayout) findViewById(R.id.menu_my_cj_tongji);// �ɼ�ͳ��
        menuMyCjTongji.setVisibility(View.GONE);
        menuMyPaiming = (LinearLayout) findViewById(R.id.menu_my_paiming);// 3.�ҵ�����
        menuMyCET = (LinearLayout) findViewById(R.id.menu_my_cet);// CET���
        menuLib = (LinearLayout) findViewById(R.id.menu_my_lib);
        menuIdea_back = (LinearLayout) findViewById(R.id.idea_back);// 4.���ղص�
        menuSetting = (LinearLayout) findViewById(R.id.menu_setting);// 5.����
        menuAbout = (LinearLayout) findViewById(R.id.menu_about);
        bukao_tip = (TextView) findViewById(R.id.bukao_tip);

        LinearLayout menuQuit = (LinearLayout) findViewById(R.id.menu_quit);
        menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
        setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��

        // �����������ȡͷ��id �����жϱ����Ƿ�������ļ�
        GetPhotoIDAsynctask getPhotoID = new GetPhotoIDAsynctask(MainActivity.this, headPhoto);
        getPhotoID.execute();

        headPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated methodstub

                File file = new File(StaticVarUtil.PATH);
                if (!file.exists()) {
                    file.mkdirs();// �����ļ�
                }
                chooseHeadPhoto();// �ı�ͷ��
            }
        });

        menuBang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMenuItemState(menuBang, true, menuMyBukao, false, menuMyCjTongji, false, menuMyCET,
                        false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
                        menuAbout, false);
                slidingMenu.toggle();// ҳ����ת
                if (getCurrentMeunItem() == StaticVarUtil.MENU_BANG) {
                    return;
                }
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

        // ��������
        menuMyBukao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                setMenuItemState(menuBang, false, menuMyBukao, true, menuMyCjTongji, false, menuMyCET,
                        false, menuLib, false, menuMyPaiming, false, menuIdea_back, false, menuSetting, false,
                        menuAbout, false);
                slidingMenu.toggle();// ҳ����ת
                if (getCurrentMeunItem() == StaticVarUtil.MENU_BUKAO) {
                    return;
                }
                // �ж����û��ͷ��Ļ�������ѡ��ͷ�񣬲���д�ǳ�
                // ������ת�������б�
                // showToast("����Գ������Ŭ�������У�������ע...");
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
                    slidingMenu.toggle();// ҳ����ת
                    if (getCurrentMeunItem() == StaticVarUtil.MENU_PAIMING) {
                        return;
                    }
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
                    ViewUtil.showToast(getApplicationContext(), "���粻�ȶ������Ժ��ѯ");
                }
            }
        });

        menuIdea_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();// ҳ����ת
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
                slidingMenu.toggle();// ҳ����ת
                if (getCurrentMeunItem() == StaticVarUtil.MENU_SETTING) {
                    return;
                }
                slidingMenu.setContent(R.layout.activity_option);
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
                slidingMenu.toggle();// ҳ����ת
                if (getCurrentMeunItem() == StaticVarUtil.MENU_CET) {
                    return;
                }
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

                slidingMenu.toggle();// ҳ����ת
                if (getCurrentMeunItem() == StaticVarUtil.MENU_LIB) {
                    return;
                }
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

                slidingMenu.toggle();// ҳ����ת
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

    private void aboutListener() {
        if (Util.getAndroidSDKVersion() > 10) {
            findViewById(R.id.newTip).setAlpha(100);
        }
        // �˵���ť
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
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "���ʳɼ�");
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "xiyouMc"));

            }
        });
        // ���?ť
        Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // ViewUtil.showShare(getApplicationContext());
                shareUtil.showShareUI(MainActivity.share);
            }
        });
        check_version = (Button) findViewById(R.id.checkversion);// ����°汾��ť
        TextView version = (TextView) findViewById(R.id.version);
        version.setText(Util.getVersion(getApplicationContext()));
        check_version.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(MainActivity.this,
                        true, new CheckVersionAsynctask.OnCheck() {

                    @Override
                    public void onCheck() {
                        // TODO Auto-generated method stub
                        ViewUtil.showToast(MainActivity.this,
                                Util.getContext().getResources().getString(R.string.check_version));
                    }
                });
                ProgressDialogUtil.getInstance(MainActivity.this).show();
                checkVersionAsyntask.execute();
            }
        });
    }

    /*
     * �����ά��
     */
    private void showDialogSaveQrcode() {
        final CharSequence[] items = {Util.getContext().getResources()
                .getString(R.string.save_Qr_code)};
        Builder builder = new Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (!new File(StaticVarUtil.PATH + Util.QRCODE_FILENAME).exists()) {
                    Bitmap bt = ((BitmapDrawable) getApplicationContext().getResources().getDrawable(
                            R.drawable.qrcode)).getBitmap();
                    BitmapUtil.saveFileAndDB(getApplicationContext(), bt, Util.QRCODE_FILENAME);
                    bt.recycle();
                }
                ViewUtil.showToast(getApplicationContext(), "��ά���ѱ��棬�뽫������ͬѧ��");

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void rank() {

        // menu���� �ж�Ϊ��һ�� Ϊ�˳�ʼ�� listview
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
                    RankUtils.allRankList.setSelection(Integer.parseInt(RankUtils.rankText.getText()
                            .toString()) - 1);
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
                    // ��λ
                    for (HashMap<String, Object> map : RankUtils.showRankArrayList) {
                        if ((map.get("name").toString()).equals(search_edittext.getText().toString())) {
                            search_edittext.clearFocus();
                            closeInputMethod();
                            RankUtils.allRankList.setSelection(Integer.parseInt(map.get("rankId").toString()) - 1);

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
                            RankUtils.allRankList.setSelection(Integer.parseInt(map.get("rankId").toString()) - 1);
                            isSearch = true;
                        }
                    }
                    if (!isSearch) {
                        ViewUtil.showToast(getApplicationContext(), "û�и�ѧ����Ϣ");
                    }
                    search_edittext.clearFocus();
                    closeInputMethod();
                    return true;
                }
                return false;
            }
        });

        // �˵���ť
        Button menu = (Button) findViewById(R.id.butMenu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
        // ���?ť
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
        xnSpinner.setLayoutParams(new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // xnSpinner.setDropDownWidth(width);
        xqSpinner = (Spinner) findViewById(R.id.xqSpinner);
        xqSpinner.setLayoutParams(new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        String[] xns = new String[StaticVarUtil.list_Rank_xnAndXq.size()];
        int i = 0;

        Iterator<?> it = StaticVarUtil.list_Rank_xnAndXq.entrySet().iterator();
        while (it.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>) it.next();
            xns[i] = String.valueOf(entry.getKey());// ����������Ӧ�ļ�
            i++;
            // entry.getValue() ����������Ӧ��ֵ
        }
        // ����dropDownItem ���
        xnAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xns, width);// ����Adapter
        String xq = StaticVarUtil.list_Rank_xnAndXq.get(xns[0]);// Ĭ�ϵ�һѧ����׸�ѧ������
        String[] xqs = xq.split("\\,");
        xqAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xqs, width);// ����Adapter
        xnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ѡ�������˵���ʽ
        xqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ѡ�������˵���ʽ
        xnSpinner.setAdapter(xnAdapter);
        xnSpinner.setSelection(0, false);// �������������̬
        xqSpinner.setAdapter(xqAdapter);
        xqSpinner.setSelection(0, false);
        listener(xns, width);
        String result = "";// �ɼ������

        if (RankUtils.allRankMap.size() != 0) {
            for (String xnAndXq : RankUtils.allRankMap.keySet()) {
                // ������ menu���������Ա����ж�
                if (xnAndXq.equals(RankUtils.selectXn + RankUtils.selectXq)) {
                    // ��������ֱ�� ����value
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
     * ȡ�������
     */
    private void closeInputMethod() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// û����ʾ����ʾ
        }
    }

    private void requestRankAsyntask() {
        // Ĭ��
        RankUtils.selectXn = xnSpinner.getSelectedItem().toString();
        RankUtils.selectXq = xqSpinner.getSelectedItem().toString();
        Util.getRequestParmas(getApplicationContext(), StaticVarUtil.student.getAccount() + "|"
                + RankUtils.selectXn.split("\\-")[0] + "|"
                + (RankUtils.selectXq.equals("��һѧ��") ? "1" : "2"));
        String result = "";
        // ���Ȳ�ѯ�ڴ����Ƿ��и�ѧ�ڳɼ�
        for (String xnAndXq : RankUtils.allRankMap.keySet()) {
            if (xnAndXq.equals(RankUtils.selectXn + RankUtils.selectXq)) {
                // ��������ֱ�� ����value
                result = RankUtils.allRankMap.get(xnAndXq);
                RankUtils.refeshRank(result, RankUtils.isFirstListView, getApplicationContext());
                break;
            }
        }
        if (result.equals("")) {
            // �����ڣ�������
            GetRankAsycntask getRankAsyntask = new GetRankAsycntask(this, nickname, name);
            getRankAsyntask.execute();
        }

    }

    private void listener(final String[] xns, final int width) {
        // TODO Auto-generated method stub

        xnSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (!ConnectionUtil.isConn(getApplicationContext())) {
                    ConnectionUtil.setNetworkMethod(MainActivity.this);
                    xnSpinner.setSelection(0, false);// �������������̬
                    return;
                }
                if (isTouchXNSpinner) {
                    // ��spinner�ϵ�ѡ�����ʾ��TextView����
                    RankUtils.selectXn = xnAdapter.getItem(arg2);
                    // �Զ����� ѧ��
                    String xq = StaticVarUtil.list_Rank_xnAndXq.get(xns[arg2]);// Ĭ�ϵ�һѧ����׸�ѧ������
                    String[] xqs = xq.split("\\,");
                    xqAdapter = new TestArrayAdapter(R.layout.list_item, getApplicationContext(), xqs, width);// ����Adapter
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
                if (!ConnectionUtil.isConn(getApplicationContext())) {
                    ConnectionUtil.setNetworkMethod(MainActivity.this);
                    xqSpinner.setSelection(0, false);
                    return;
                }
                if (isTouchXQSpinner) {
                    // ��spinner�ϵ�ѡ�����ʾ��TextView����
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

    // �����б�
    protected void friend_list() {
        // TODO Auto-generated method stub
        // �˵���ť
        Button menu = (Button) findViewById(R.id.butMenu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        // ��Ӻ��Ѱ�ť
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

    // ����
    protected void cet() {
        // TODO Auto-generated method stub
        // �˵���ť
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
                        String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
                StaticVarUtil.cet_data = Passport.jiami(accoutStr, String.valueOf(time));
                StaticVarUtil.cet_viewstate = time_s;
                GetCETAsyntask getCETAsyntask = new GetCETAsyntask(MainActivity.this);
                ProgressDialogUtil.getInstance(MainActivity.this).show();
                try {
                    getCETAsyntask.execute(new String[]{URLEncoder.encode(
                            URLEncoder.encode(nameStr.length() < 3 ? nameStr : nameStr.substring(0, 2), "utf-8"),
                            "utf-8")});
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
     * �޸ĸ�����Ϣ��ֻ���޸��ǳƣ����룬ͷ��
     */
    protected void menuSetting() {

        // �˵���ť
        Button menu = (Button) findViewById(R.id.butMenu);
        menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });

        if (StaticVarUtil.student == null) {
            ViewUtil.showToast(getApplicationContext(), "�Բ��𣬲鿴ѡ�����ȵ�¼");
            return;
        }

        RelativeLayout changePwdLayout = (RelativeLayout) findViewById(R.id.change_pwd);
        changePwdLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(MainActivity.this, ChangePwdActivity.class);
                startActivityForResult(i, Util.CHANGE_PWD_RESULT);
            }
        });
        RelativeLayout languageLayout = (RelativeLayout) findViewById(R.id.language);
        languageLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LanguageActivity.class);
                intent.putExtra("optionType", "Language");
                startActivity(intent);
            }
        });

    }

    /*
     * ѡ��ͷ��
     *
     * @return
     */
    protected void chooseHeadPhoto() {
        String[] items = new String[]{
                Util.getContext().getResources().getString(R.string.select_picture),
                Util.getContext().getResources().getString(R.string.photo)};
        new Builder(this)
                .setTitle(Util.getContext().getResources().getString(R.string.setPhoto))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// ѡ�񱾵�ͼƬ
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, PIC);
                                break;
                            case 1:// ����
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri imageUri = Uri.fromFile(new File(StaticVarUtil.PATH, "temp.JPEG"));
                                // ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent2, PHO);
                                break;
                        }
                    }
                })
                .setNegativeButton(Util.getContext().getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    /*
     * ȡ�ûش������
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ������벻��ȡ���ʱ��
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case PHO:
                    File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case PIC:
                    // ��Ƭ��ԭʼ��Դ��ַ
                    Uri originalUri = data.getData();
                    startPhotoZoom(originalUri);
                    break;
                case RESULT:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmap = extras.getParcelable("data");
                        }
                        bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 240);// �����Ϊ240���ص�ͼƬ
                        BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
                                StaticVarUtil.student.getAccount() + ".JPEG");
                        headPhoto.setImageBitmap(bitmap);
                        // �ϴ�ͷ��
                        UploadFileAsytask uploadFileAsytask = new UploadFileAsytask(MainActivity.this, bitmap);
                        uploadFileAsytask.execute(new String[]{StaticVarUtil.PATH + "/"
                                + StaticVarUtil.student.getAccount() + ".JPEG"});
                    }
                    break;
                case Util.CHANGE_PWD_RESULT:
                    Bundle extras = data.getExtras();
                    if (extras.getBoolean("isQuit")) {
                        quit(true);
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
     * �ü�ͼƬ����ʵ��
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");// ����ϵͳ�Ľ�ͼ���ܡ�
        intent.setDataAndType(uri, "image/*");
        // ���òü�
        intent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);// �ڱ�
        intent.putExtra("scaleUpIfNeeded", true);// �ڱ�
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT);
    }

    /*
     * ���ֻ�ť�ļ���
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // ����Ƿ��ذ�ť,�˳�
                if (getCurrentMeunItem() != 1) {// ���ڵ�һ��ҳ��,���ص�һ��ҳ��
                    menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
                    setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��
                    slidingMenu.setContent(R.layout.card_main);
                    menu1();
                } else
                    quit(false);

                break;
            case KeyEvent.KEYCODE_MENU:// ����ǲ˵���ť
                slidingMenu.toggle();
                break;
            default:
                break;
        }
        return true;
    }

    /*
     * ����ڴ���еĹ������
     */
    private void deleteCatch() {
        // �������
        removePassword();
        StaticVarUtil.list_Rank_xnAndXq.clear();
        StaticVarUtil.allBookList = null;
        RankUtils.allRankArrayList = null;
        ScoreUtil.mapScoreOne.clear();
        ScoreUtil.mapScoreTwo.clear();

        StaticVarUtil.listItem = null;
        CustomApplcation.getInstance().logout();
        StaticVarUtil.quit();

        RankUtils.isFirstListView = true;
        // ��ճɼ�����
        isFirst = true;
        fragments = null;

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

    private void removePassword() {
        SharedPreferences preferences = getSharedPreferences(StaticVarUtil.USER_INFO, MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(StaticVarUtil.ACCOUNT, StaticVarUtil.student.getAccount());
        // ɾ����ݿ�
        DBConnection.updateUser(StaticVarUtil.student.getAccount(), MainActivity.this);
        editor.putString(StaticVarUtil.PASSWORD, "");
        editor.putBoolean(StaticVarUtil.IS_REMEMBER, true);// ��ס����
        editor.commit();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (!ConnectionUtil.isConn(getApplicationContext())) {
            ConnectionUtil.setNetworkMethod(MainActivity.this);
            return;
        }
        // СԲ����ʾ
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
        MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
        // ���
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// ȡ��������͵���Ϣ
        RankUtils.allRankMap.clear();
    }

    /*
     * �˳�ģ��
     *
     * @param logout �Ƿ�ע��
     */
    private void quit(final boolean logout) {
        Builder builder = new Builder(MainActivity.this);

        if (logout) {
            builder.setMessage(Util.getContext().getResources().getString(R.string.isQuit));
        } else {
            if (slidingMenu.isMenuShowing()) {
                slidingMenu.showContent();
            } else {
                moveTaskToBack(true);
            }
            return;
        }

        builder.setPositiveButton(Util.getContext().getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        BmobDB.create(getApplicationContext()).queryBmobInviteList().clear();
                        deleteCatch();
                        LogcatHelper.getInstance(MainActivity.this).stop();
                        // ȡ��ʱ������
                        BmobChat.getInstance(getApplicationContext()).stopPollService();

                        userManager.logout();
                        if (logout) {
                            Intent i = new Intent();
                            i.setClass(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                        }
                    }
                });
        builder.setNegativeButton(Util.getContext().getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /*
     * ��¼���õ�ǰMenuItem��λ�ã�1��2��3��4��5�ֱ���ɼ���ѯ��������ѯ���ҵ��������ղصģ�ѡ��
     *
     * @param menuItem �˵���ѡ��
     */
    private void setCurrentMenuItem(int menuItem) {
        SharedPreferences preferences = getSharedPreferences("currentMenuItem", MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putInt("item", menuItem);
        editor.commit();
    }

    /*
     * ȡ�õ�ǰMenuItem��λ��
     *
     * @return ��ǰ��menu�Ĳ˵��� 1��2��3��4��5�ֱ���ɼ���ѯ��������ѯ���ҵ��������ղصģ�ѡ��,0���û�����
     */
    private int getCurrentMeunItem() {
        SharedPreferences preferences = getSharedPreferences("currentMenuItem", MODE_PRIVATE);
        int flag = preferences.getInt("item", 0);
        return flag;
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
                if (ideaMsgText.getText().toString().equals("")) {
                    H5Toast.showToast(Util.getContext(),
                            Util.getContext().getResources().getString(R.string.edit_feedmsg));
                    return;
                }
                ProgressDialogUtil.getInstance(MainActivity.this).show();
                SIMCardInfo siminfo = new SIMCardInfo(getApplicationContext());
                final String number = siminfo.getNativePhoneNumber();
                final String data = ideaMsgText.getText().toString().trim()
                        + "|"
                        + ("".equals(phoneText.getText().toString().trim()) ? "" : phoneText.getText()
                        .toString().trim()) + "|" + StaticVarUtil.student.getAccount() + "|"
                        + Util.getVersion(getApplicationContext()) + "|" + number;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Util.sendMail(data, MainActivity.this);
                        Message msg = new Message();
                        msg.what = StaticVarUtil.IDEA_BACK_TOAST;
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    private void lib() {
        initTopBarForOnlyTitle(Util.getContext().getResources().getString(R.string.bind));
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
                if ("none".equals(result) || result == null) {// δ��
                    if (isReq) {
                        lib();
                        isReq = false;
                        return;
                    }
                    bindLib(bind_layout);
                    ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
                } else {// �� ֱ������
                    StaticVarUtil.LIB_NAME = result;
                    login_lib(result, bind_layout, false);
                }
            }
        });
        bindXuptLibAsyncTask.execute();
    }

    private void bindLib(final LinearLayout bind_layout) {
        initTopBarForOnlyTitle(Util.getContext().getResources().getString(R.string.bind));

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
                    H5Toast.showToast(getApplicationContext(), "�������˺ź�����");
                    ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
                    return;
                }
                login_lib("account=" + libAccount.getText().toString().trim() + "&password="
                        + libPW.getText().toString().trim(), bind_layout, true);

            }
        });
    }

    private void login_lib(final String libName, final LinearLayout bind_layout, final boolean isBind) {
        ProgressDialogUtil.getInstance(MainActivity.this).show();
        XuptLibLoginAsynctask xuptLibLoginAsynctask = new XuptLibLoginAsynctask(MainActivity.this,
                libName, new XuptLibLoginAsynctask.Login() {

            @Override
            public void onPostLogin(String result) {
                // TODO Auto-generated method stub
                if (result != null && !"fail".equals(result.trim())) {// ��ȡ���
                    StaticVarUtil.LIB_NAME = libName;
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

                        loginResult = result;
                        try {
                            ArrayList<BookList> allBookList = new ArrayList<BookList>();
                            JSONArray bookArray = new JSONArray(result);
                            for (int i = 0; i < bookArray.length(); i++) {
                                JSONObject jo = (JSONObject) bookArray.get(i);
                                BookList bookList = new BookList();
                                bookList.setLibName(jo.getString("name"));
                                bookList.setNumber(jo.getString("id"));
                                String[] date = jo.getString("date").split("/");
                                bookList.setLibRenewDate(date[0] + "��" + date[1] + "��" + date[2]);
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
                        H5Toast.showToast(getApplicationContext(), "��ʧ�ܣ���ȷ���˺ź����룡");
                    } else if (times < 3) {
                        times++;
                        login_lib(libName, bind_layout, isBind);
                    }
                }
                ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
            }

        });
        xuptLibLoginAsynctask.execute();
    }

    private void ShowLibMessage(final LinearLayout bind_layout, final ArrayList<BookList> allBookList) {
        final LinearLayout show_lib_layout = (LinearLayout) findViewById(R.id.common_show_lib);
        initTopBarForRight(Util.getContext().getResources().getString(R.string.my_lib),
                R.drawable.chongzhi, new HeaderLayout.onRightImageButtonClickListener() {

                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        Builder builder = new Builder(MainActivity.this);

                        builder.setMessage("��ȷ��Ҫ������");

                        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                StaticVarUtil.LIB_NAME = "";
                                StaticVarUtil.allBookList = null;
                                // bind_layout.setVisibility(View.VISIBLE);
                                show_lib_layout.setVisibility(View.GONE);
                                bindLib(bind_layout);
                            }
                        });
                        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        Dialog dialog = builder.create();
                        dialog.show();
                    }
                });

        show_lib_layout.setVisibility(View.VISIBLE);
        bind_layout.setVisibility(View.GONE);
        ListView libList = (ListView) findViewById(R.id.book_list);
        adapter = new LibAdapter(allBookList, MainActivity.this, new LibAdapter.OnAdapter() {

            @Override
            public void onAdapter() {
                // TODO Auto-generated method stub
                adapter.notifyDataSetChanged();
            }

        });
        libList.setAdapter(adapter);
    }

    private void bind(final String libName, final LinearLayout bind_layout) {
        BindXuptLibAsyncTask bindXuptLibAsyncTask = new BindXuptLibAsyncTask(MainActivity.this,
                libName, "1", new BindXuptLibAsyncTask.OnPostExecute() {

            @Override
            public void returnResult(String result) {
                if ("success".equals(result)) {
                    login_lib(libName, bind_layout, false);
                    // if (loginResult != null) {
                    // try {
                    // ArrayList<BookList> allBookList = new ArrayList<BookList>();
                    // JSONArray bookArray = new JSONArray(result);
                    // for (int i = 0; i < bookArray.length(); i++) {
                    // JSONObject jo = (JSONObject) bookArray.get(i);
                    // BookList bookList = new BookList();
                    // bookList.setLibName(jo.getString("name"));
                    // bookList.setNumber(jo.getString("id"));
                    // String[] date = jo.getString("date").split("/");
                    // bookList.setLibRenewDate(date[0] + "��" + date[1] + "��" + date[2]);
                    // bookList.setBarcode(jo.getString("barcode"));
                    // bookList.setRenew(jo.getBoolean("isRenew"));
                    // allBookList.add(bookList);
                    // }
                    //
                    // StaticVarUtil.allBookList = allBookList;
                    // ShowLibMessage(bind_layout, allBookList);
                    //
                    // } catch (JSONException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }
                    // }
                } else {// ��ʧ��
                    bind_layout.setVisibility(View.VISIBLE);
                    H5Toast.showToast(getApplicationContext(), "�����°�");
                }
            }
        });
        bindXuptLibAsyncTask.execute();
    }

    private void chat() {
        // BmobIM SDK��ʼ��--ֻ��Ҫ��һ�δ��뼴����ɳ�ʼ��
        // �뵽Bmob����(http://www.bmob.cn/)����ApplicationId,�����ַ:http://docs.bmob.cn/android/faststart/index.html?menukey=fast_start&key=start_android
        BmobChat.getInstance(this).init(Config.applicationId);
        ProgressDialogUtil.getInstance(MainActivity.this).show();
        if (userManager.getCurrentUser() != null) {
            // ÿ���Զ���½��ʱ�����Ҫ�����µ�ǰλ�úͺ��ѵ����ϣ���Ϊ���ѵ�ͷ���ǳ�ɶ���Ǿ����䶯��
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
        MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
        // ���
        MyMessageReceiver.mNewNum = 0;
        // �ѵ�һ��tab��Ϊѡ��״̬
        mTabs[0].setSelected(true);
    }

    private void initTab() {
        contactFragment = new ContactFragment();
        recentFragment = new RecentFragment();
        settingFragment = new SettingsFragment();
        fragments = new Fragment[]{recentFragment, contactFragment, settingFragment};
        // �����ʾ��һ��fragment
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
        isCanTouch = true;
    }

    /**
     * button����¼�
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
        // �ѵ�ǰtab��Ϊѡ��״̬
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    /**
     * ˢ�½���
     *
     * @Title: refreshNewMsg @Description: TODO @param @param message @return void @throws
     */
    private void refreshNewMsg(BmobMsg message) {
        // ������ʾ
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);
        iv_bukao_tips.setVisibility(View.VISIBLE);
        // ҲҪ�洢����
        if (message != null) {
            BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true, message);
        }
        if (currentTabIndex == 0) {
            // ��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
            if (recentFragment != null) {
                recentFragment.refresh();
            }
        }
    }

    private void initNewMessageBroadCast() {
        // ע�������Ϣ�㲥
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // ���ȼ�Ҫ����ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    private void initTagMessageBroadCast() {
        // ע�������Ϣ�㲥
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        // ���ȼ�Ҫ����ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
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
     * ˢ�º�������
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
            // ͬʱ����֪ͨ
            String tickerText = message.getFromname() + "������Ӻ���";
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
        // finish();
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
    }

    /**
     * �������η��ؼ���˳�
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        ViewUtil.cancelToast();
        super.onBackPressed();
    }

    private void startChatActivity() {
        BmobConstants.IS_STARTED = 1;
        // ������ʱ�����񣨵�λΪ�룩-���������̨�Ƿ���δ������Ϣ���еĻ���ȡ����
        BmobChat.getInstance(getApplicationContext()).startPollService(30);
        // �����㲥������
        initNewMessageBroadCast();
        initTagMessageBroadCast();
        initView();
        initTab();
    }

    // �����û�ͷ��
    private void uploadAvatar(String path) {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                String url = bmobFile.getFileUrl(getApplicationContext());
                // ����BmobUser����
                updateUserAvatar(url);
            }

            @Override
            public void onProgress(Integer arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int arg0, String msg) {
                // TODO Auto-generated method stub
                ShowToast("ͷ���ϴ�ʧ�ܣ�" + msg);
            }
        });
    }

    // �����û���Ϣ
    private void updateUserAvatar(final String url) {
        User user = (User) userManager.getCurrentUser(User.class);
        user.setAvatar(url);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                startChatActivity();
                // ����ͷ��
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                ShowToast("ͷ�����ʧ�ܣ�" + msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        try {
            CustomApplcation.getInstance().logout();
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
        // ȡ��ʱ������
        BmobChat.getInstance(this).stopPollService();
        super.onDestroy();
    }

    private void showShareQrcodeDialog() {
        Builder builder = new Builder(MainActivity.this);

        builder.setMessage("���ڱ�רҵʹ���������,������������\n�����ѯ׼ȷ����������ͬѧ���ظ������");

        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ViewUtil.showShare(getApplicationContext());
                shareUtil.showShareUI(MainActivity.share);
            }
        });
        builder.create().show();
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

    // �첽���ػ�ȡ�ɼ�
    class GetScoreAsyntask extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            // TODO Auto-generated method stub
            String url = "";
            String canshu = Util.getURL(StaticVarUtil.QUERY_SCORE);
            System.out.println(TAG + "canshu:" + canshu + " \n + " + StaticVarUtil.listHerf.toString());
            String[] can = canshu.split("&");
            String url_str = "/" + can[0];
            String xm = can[1];
            try {
                name = xm.split("=")[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                // TODO: handle exception
                return "error";
            }

            String gnmkdm = can[2];
            try {
                url = HttpUtilMc.BASE_URL + "xscjcx.aspx?session=" + StaticVarUtil.session + "&url="
                        + url_str + "&xm="
                        + URLEncoder.encode(URLEncoder.encode(xm.split("=")[1], "utf8"), "utf8") + "&" + gnmkdm;
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // ��ѯ���ؽ��
            String result = HttpUtilMc.queryStringForPost(url);
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            System.out.println("score:" + result);
            // ��ʾ�û���
            if (name != null && !name.isEmpty()) {
                StaticVarUtil.student.setName(name);
            }
            nickname.setText(StaticVarUtil.student.getName());
            try {
                if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result) || result.isEmpty()) {
                    if (!result.equals("error")) {
            /*
             * ���ַ� д��xml�ļ���
             */
                        if (!result.equals("no_evaluation")) {
                            requestTimes = 0;
                            ScoreUtil.scoreJson = result;
                            StaticVarUtil.listItem = new ArrayList<HashMap<String, Object>>();
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = (JSONArray) jsonObject.get("liScoreModels");// ������array
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o = (JSONObject) jsonArray.get(i);
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("xn", o.get("xn"));
                                map.put("list_xueKeScore", o.get("list_xueKeScore"));
                                StaticVarUtil.listItem.add(map);
                            }
                        } else {
                            Builder builder = new Builder(MainActivity.this);

                            builder.setMessage("�뵽������н�ʦ���ۺ��ѯ�ɼ���");

                            builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    quit(true);
                                }
                            });
                            builder.create().show();
                        }

                        menu1();
                    } else {
                        ViewUtil.showToast(getApplicationContext(), "��ѯʧ��,�����ԡ�");
                    }

                } else {
                    if (!ConnectionUtil.isConn(getApplicationContext())) {
                        ConnectionUtil.setNetworkMethod(MainActivity.this);
                        return;
                    }
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
                        setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��
                        return;
                    case StaticVarUtil.MENU_ABOUT:
                        aboutListener();
                        setCurrentMenuItem(StaticVarUtil.MENU_ABOUT);// ��¼��ǰѡ��λ��
                        return;
                }
                if (!ConnectionUtil.isConn(getApplicationContext())) {
                    ConnectionUtil.setNetworkMethod(MainActivity.this);
                    menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
                    setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��
                    slidingMenu.setContent(R.layout.card_main);
                    isCanTouch = true;
                    menu1();
                    return;
                }
                switch (msg.what) {
                    case StaticVarUtil.MENU_BUKAO:
                        friend_list();
                        setCurrentMenuItem(StaticVarUtil.MENU_BUKAO);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.MENU_PAIMING:
                        rank();
                        setCurrentMenuItem(StaticVarUtil.MENU_PAIMING);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.MENU_IDEA_BACK:
                        menuIdeaBack();
                        setCurrentMenuItem(StaticVarUtil.MENU_IDEA_BACK);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.MENU_SETTING:
                        menuSetting();
                        setCurrentMenuItem(StaticVarUtil.MENU_SETTING);// ��¼��ǰѡ��λ��
                        break;

                    case StaticVarUtil.SHARE:
                        showShareQrcodeDialog();
                        break;
                    case StaticVarUtil.MENU_CET:
                        cet();
                        setCurrentMenuItem(StaticVarUtil.MENU_CET);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.MENU_LIB:
                        lib();
                        setCurrentMenuItem(StaticVarUtil.MENU_LIB);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.IDEA_BACK_TOAST:
                        ProgressDialogUtil.getInstance(MainActivity.this).dismiss();
                        // ViewUtil.showToast(getApplicationContext(), "��л����");
                        ViewUtil.showDialog("��л���������ǻ�����������", "����", activity, false,
                                new ViewUtil.DialogCallback() {

                                    @Override
                                    public void onPost() {
                                        // TODO Auto-generated method stub
                                        closeInputMethod();
                                        menuBang.setPressed(true);// ��ʼ��Ĭ���Ƿ��ư񱻰���
                                        setCurrentMenuItem(StaticVarUtil.MENU_BANG);// ��¼��ǰѡ��λ��
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

                        setCurrentMenuItem(StaticVarUtil.MENU_BUKAO);// ��¼��ǰѡ��λ��
                        break;
                    case StaticVarUtil.CHECK_VERSION:
                        CheckVersionAsynctask checkVersionAsyntask = new CheckVersionAsynctask(MainActivity.this,
                                true, new CheckVersionAsynctask.OnCheck() {

                            @Override
                            public void onCheck() {
                                // TODO Auto-generated method stub
                            }
                        });
                        checkVersionAsyntask.execute(new String[]{"login"});
                        break;
                }
            }
        }

    }

    /**
     * ����Ϣ�㲥������
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // ˢ�½���
            refreshNewMsg(null);
            // �ǵðѹ㲥���ս��
            abortBroadcast();
        }
    }

    // @Override
    // protected void onRestart() {
    // // TODO Auto-generated method stub
    // super.onRestart();
    // Util.setLanguageShare(MainActivity.this);
    //
    // if (StaticVarUtil.listItem != null) {
    // // ��������Ӧ�ó���
    // Intent intent = mActivity.getIntent();
    // // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    // // mActivity.closeApplication();
    // mActivity.overridePendingTransition(0, 0);
    // mActivity.startActivity(intent);
    // }
    // }

    /**
     * ��ǩ��Ϣ�㲥������
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // �ǵðѹ㲥���ս��
            abortBroadcast();
        }
    }
}
