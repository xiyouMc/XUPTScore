package com.bmob.im.demo.ui;

import com.xy.fy.main.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cn.bmob.im.BmobUserManager;

/**
 * ���½ע��ͻ�ӭҳ����̳еĻ���-���ڼ���Ƿ��������豸��¼��ͬһ�˺�
 *
 * @author smile
 * @ClassName: ActivityBase
 * @Description: TODO
 * @date 2014-6-13 ����5:18:24
 */
public class ActivityBase extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //�Զ���½״̬�¼���Ƿ��������豸��½
        checkLogin();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //����״̬�µļ��
        checkLogin();
    }

    public void checkLogin() {
        BmobUserManager userManager = BmobUserManager.getInstance(this);
        if (userManager.getCurrentUser() == null) {
            ShowToast("����˺����������豸�ϵ�¼!");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    /**
     * ���������
     * hideSoftInputView
     *
     * @return void
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
