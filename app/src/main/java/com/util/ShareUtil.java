package com.util;

import com.xy.fy.main.R;

import android.content.Context;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ShareUtil {

    private Context context;

    public ShareUtil(Context context) {
        this.context = context;
    }

    /**
     * ����ģ��
     */
    public OnekeyShare showShare() {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        // �ر�sso��Ȩ
        // oks.disableSSOWhenAuthorize();

        // ����ʱNotification��ͼ�������

    /*
     * oks.setNotification(R.drawable.default_head_photo, context.getString(R.string.app_name));
     */

        // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š��������QQ�ռ�ʹ��
        oks.setTitle(context.getString(R.string.app_name));
        // titleUrl�Ǳ�����������ӣ������������QQ�ռ�ʹ��
        oks.setTitleUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
        // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
        oks.setText(
                "������ʹ�á����ʳɼ�����ѯƽʱ�ɼ��;���ɼ��������Բ�ѯרҵ����£�����������԰ɡ���������:http://www.xiyoumobile.com/wechat_app/xiyouscore/");
        // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
        // oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcard������ڴ���ͼƬ
        // url����΢�ţ��������Ѻ�����Ȧ����ʹ��
        // oks.setImagePath("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
        oks.setUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
        // comment���Ҷ�������������ۣ������������QQ�ռ�ʹ��
        oks.setComment("�����ʳɼ������?��,���ص�ַ:");
        // site�Ƿ�������ݵ���վ��ƣ�����QQ�ռ�ʹ��
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
        oks.setSiteUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
        return oks;
    }

    /**
     * ����ģ��
     */
    public OnekeyShare showShare(String filePath) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        // �ر�sso��Ȩ
        // oks.disableSSOWhenAuthorize();

        // ����ʱNotification��ͼ�������

    /*
     * oks.setNotification(R.drawable.default_head_photo, context.getString(R.string.app_name));
     */

        // title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š��������QQ�ռ�ʹ��
//    oks.setTitle(context.getString(R.string.app_name));
        // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
        oks.setImagePath(filePath);// ȷ��SDcard������ڴ���ͼƬ
        return oks;
    }

    public void showShareUI(OnekeyShare oks) {
        // ��������GUI
        oks.show(context);
    }
}
