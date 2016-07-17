package com.bmob.im.demo.config;

import android.annotation.SuppressLint;
import android.os.Environment;


/**
 * @author smile
 * @ClassName: BmobConstants
 * @Description: TODO
 * @date 2014-6-19 ����2:48:33
 */
@SuppressLint("SdCardPath")
public class BmobConstants {

    /**
     * ���ջص�
     */
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//�����޸�ͷ��
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//��������޸�ͷ��
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//ϵͳ�ü�ͷ��
    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//����
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//����ͼƬ
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//λ��
    public static final String EXTRA_STRING = "extra_string";
    public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";//ע��ɹ�֮���½ҳ���˳�
    /**
     * ��ŷ���ͼƬ��Ŀ¼
     */
    public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory() + "/bmobimdemo/image/";
    /**
     * �ҵ�ͷ�񱣴�Ŀ¼
     */
    public static String MyAvatarDir = "/sdcard/bmobimdemo/avatar/";
    public static int IS_STARTED = 0;
}
