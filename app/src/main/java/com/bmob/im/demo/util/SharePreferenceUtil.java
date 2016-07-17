package com.bmob.im.demo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * ��ѡ�����
 *
 * @author smile
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @date 2014-6-10 ����4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
    private static SharedPreferences.Editor editor;
    private SharedPreferences mSharedPreferences;
    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    private String SHARED_KEY_VOICE = "shared_key_sound";
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
    public SharePreferenceUtil(Context context, String name) {
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    // �Ƿ���������֪ͨ
    public boolean isAllowPushNotify() {
        return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
        editor.commit();
    }

    // ��������
    public boolean isAllowVoice() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VOICE, isChecked);
        editor.commit();
    }

    // ������
    public boolean isAllowVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
        editor.commit();
    }

}
