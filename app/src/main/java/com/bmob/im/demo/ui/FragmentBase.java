package com.bmob.im.demo.ui;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.HeaderLayout.HeaderStyle;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.xy.fy.main.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.util.BmobLog;

/**
 * Fragmenet ����
 *
 * @author smile
 * @ClassName: FragmentBase
 * @Description: TODO
 * @date 2014-5-22 ����2:43:50
 */
public abstract class FragmentBase extends Fragment {

    public BmobUserManager userManager;
    public BmobChatManager manager;

    /**
     * ���õ�Header����
     */
    public HeaderLayout mHeaderLayout;
    public LayoutInflater mInflater;
    public CustomApplcation mApplication;
    protected View contentView;
    Toast mToast;
    private Handler handler = new Handler();

    public FragmentBase() {

    }

    public void runOnWorkThread(Runnable action) {
        new Thread(action).start();
    }

    public void runOnUiThread(Runnable action) {
        handler.post(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApplication = CustomApplcation.getInstance();
        userManager = BmobUserManager.getInstance(getActivity());
        manager = BmobChatManager.getInstance(getActivity());
        mInflater = LayoutInflater.from(getActivity());
    }

    public void ShowToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public void ShowToast(int text) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    /**
     * ��Log
     * ShowLog
     *
     * @return void
     */
    public void ShowLog(String msg) {
        BmobLog.i(msg);
    }

    public View findViewById(int paramInt) {
        return getView().findViewById(paramInt);
    }

    /**
     * ֻ��title initTopBarLayoutByTitle
     *
     * @Title: initTopBarLayoutByTitle
     */
    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    /**
     * ��ʼ��������-�����Ұ�ť
     *
     * @return void
     */
    public void initTopBarForBoth(String titleName, int rightDrawableId,
                                  onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
    }

    /**
     * ֻ����߰�ť��Title initTopBarLayout
     */
    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
    }

    /**
     * �ұ�+title
     * initTopBarForRight
     *
     * @return void
     */
    public void initTopBarForRight(String titleName, int rightDrawableId,
                                   onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
    }

    /**
     * ��������ҳ�� startAnimActivity
     */
    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    public void startAnimActivity(Class<?> cla) {
        getActivity().startActivity(new Intent(getActivity(), cla));
    }

    // ��߰�ť�ĵ���¼�
    public class OnLeftButtonClickListener implements
            onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            getActivity().finish();
        }
    }

}
