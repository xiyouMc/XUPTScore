package com.bmob.im.demo.ui;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.HeaderLayout.HeaderStyle;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;

import top.codemc.common.util.MyPreferences;
import top.codemc.common.util.Util;
import com.xy.fy.main.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import top.codemc.common.util.ViewUtil;

/**
 * ����
 *
 * @author smile
 * @ClassName: BaseActivity
 * @Description: TODO
 * @date 2014-6-13 ����5:05:38
 */
public class BaseActivity extends FragmentActivity {

    protected static boolean isCanTouch = true;
    protected BmobUserManager userManager;
    protected HeaderLayout mHeaderLayout;
    protected int mScreenWidth;
    protected int mScreenHeight;
    BmobChatManager manager;
    CustomApplcation mApplication;
    Toast mToast;
    private int guideResourceId = 0;// ��ҳͼƬ��Դid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Util.setLanguageShare(this);
        super.onCreate(savedInstanceState);
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        mApplication = CustomApplcation.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }

    /**
     * �����ͼƬ
     */
    public void addGuideImage() {
        View view = getWindow().getDecorView().findViewById(R.id.my_content_view);// ����ͨ��setContentView�ϵĸ��
        if (view == null)
            return;
        if (MyPreferences.activityIsGuided(this, this.getClass().getName())) {
            // �����
            return;
        }
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof FrameLayout) {
            final FrameLayout frameLayout = (FrameLayout) viewParent;
            if (guideResourceId != 0) {// ��������ͼƬ
                final ImageView guideImage = new ImageView(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                guideImage.setLayoutParams(params);
                guideImage.setScaleType(ScaleType.FIT_XY);
                guideImage.setImageResource(guideResourceId);
                guideImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        frameLayout.removeView(guideImage);
                        MyPreferences.setIsGuided(getApplicationContext(), BaseActivity.this.getClass()
                                .getName());// ��Ϊ����
                    }
                });
                frameLayout.addView(guideImage);// �����ͼƬ

            }
        }
    }

    /**
     * ������onCreate�е��ã�������ͼƬ����Դid ���ڲ���xml�ĸ�Ԫ��������android:id="@id/my_content_view"
     */
    protected void setGuideResId(int resId) {
        this.guideResourceId = resId;
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
            }
        });
    }

    /**
     * ��Log ShowLog @return void @throws
     */
    public void ShowLog(String msg) {
        BmobLog.i(msg);
    }

    /**
     * ֻ��title initTopBarLayoutByTitle @Title: initTopBarLayoutByTitle @throws
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * ��ʼ��������-�����Ұ�ť @return void @throws
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId, String text,
                                  onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text, listener);
    }

    public void initTopBarForBoth(String titleName, int rightDrawableId,
                                  onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, listener);
    }

    public void initTopBarForRight(String titleName, int rightDrawableId,
                                   onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId, listener);
    }

    /**
     * ֻ����߰�ť��Title initTopBarLayout
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector, new OnLeftButtonClickListener());
    }

    /**
     * ��ʾ���ߵĶԻ��� showOfflineDialog @return void @throws
     */
    public void showOfflineDialog(final Context context) {
        DialogTips dialog = new DialogTips(this, "����˺����������豸�ϵ�¼!", "���µ�¼");
        // ���óɹ��¼�
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                CustomApplcation.getInstance().logout();
                startActivity(new Intent(context, com.xy.fy.main.LoginActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        // ��ʾȷ�϶Ի���
        dialog.show();
        dialog = null;
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    /**
     * ���ڵ�½�����Զ���½����µ��û����ϼ��������ϵļ����� @Title: updateUserInfos @Description: TODO @param @return void
     * @throws
     */
    public void updateUserInfos() {
        // ���µ���λ����Ϣ
        final ProgressDialog dialog = ViewUtil.getProgressDialog(this, "���ڼ�����ϵ��...");
        dialog.show();
        updateUserLocation();
        // ��ѯ���û��ĺ����б�(��������б���ȥ������û���Ŷ),Ŀǰ֧�ֵĲ�ѯ���Ѹ���Ϊ100�������޸����ڵ����������ǰ����BmobConfig.LIMIT_CONTACTS���ɡ�
        // ����Ĭ�ϲ�ȡ���ǵ�½�ɹ�֮�󼴽������б�洢����ݿ��У������µ���ǰ�ڴ���,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                if (arg0 == BmobConfig.CODE_COMMON_NONE) {
                    ShowLog(arg1);
                } else {
                    ShowLog("��ѯ�����б�ʧ�ܣ�" + arg1);
                }
                isCanTouch = true;
                dialog.cancel();
                ;
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {
                // TODO Auto-generated method stub
                // ���浽application�з���Ƚ�
                CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(arg0));
                isCanTouch = true;
                dialog.cancel();
            }
        });
    }

    /**
     * �����û��ľ�γ����Ϣ @Title: uploadLocation @Description: TODO @param @return void @throws
     */
    public void updateUserLocation() {
        if (CustomApplcation.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(CustomApplcation.lastPoint.getLatitude());
            String newLong = String.valueOf(CustomApplcation.lastPoint.getLongitude());
            // ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
            // ShowLog("newLat ="+newLat+",newLong = "+newLong);
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// ֻ��λ���б仯�͸��µ�ǰλ�ã��ﵽʵʱ���µ�Ŀ��
                final User user = (User) userManager.getCurrentUser(User.class);
                user.setLocation(CustomApplcation.lastPoint);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        CustomApplcation.getInstance().setLatitude(
                                String.valueOf(user.getLocation().getLatitude()));
                        CustomApplcation.getInstance().setLongtitude(
                                String.valueOf(user.getLocation().getLongitude()));
                        // ShowLog("��γ�ȸ��³ɹ�");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        // TODO Auto-generated method stub
                        // ShowLog("��γ�ȸ��� ʧ��:"+msg);
                    }
                });
            } else {
                // ShowLog("�û�λ��δ�����仯");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return isCanTouch ? super.dispatchTouchEvent(ev) : true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Resources res = getResources();
        // Configuration config = res.getConfiguration();
        // if (!(getResources().getConfiguration().locale.getCountry().equals("CN"))) {// English
        // config.locale = Locale.ENGLISH;
        // } else {
        // config.locale = Locale.CHINESE;
        // }
        // DisplayMetrics dm = res.getDisplayMetrics();
        // res.updateConfiguration(config, dm);
        // StaticVarUtil.quit();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        addGuideImage();//�����ҳ
        // Resources res = getResources();
        // Configuration config = res.getConfiguration();
        // if (!(getResources().getConfiguration().locale.getCountry().equals("CN"))) {// English
        // config.locale = Locale.ENGLISH;
        // } else {
        // config.locale = Locale.CHINESE;
        // }
        // DisplayMetrics dm = res.getDisplayMetrics();
        // res.updateConfiguration(config, dm);
        // StaticVarUtil.quit();
    }

    // ��߰�ť�ĵ���¼�
    public class OnLeftButtonClickListener implements onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            finish();
        }
    }

}
