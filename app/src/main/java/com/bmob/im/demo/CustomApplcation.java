package com.bmob.im.demo;

import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xy.fy.main.R;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * �Զ���ȫ��Applcation��
 *
 * @author smile
 * @ClassName: CustomApplcation
 * @Description: TODO
 * @date 2014-5-19 3:25:00
 */
public class CustomApplcation extends Application {

    public static final String PREFERENCE_NAME = "_sharedinfo";
    public static CustomApplcation mInstance;
    public static BmobGeoPoint lastPoint = null;// ��һ�ζ�λ���ľ�γ��
    public final String PREF_LONGTITUDE = "longtitude";// ����
    public final String PREF_LATITUDE = "latitude";// ����
    SharePreferenceUtil mSpUtil;
    NotificationManager mNotificationManager;
    MediaPlayer mMediaPlayer;
    private String longtitude = "";
    private String latitude = "";
    private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();

    /** ��ʼ��ImageLoader */
    public static void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "bmobim/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                // �̳߳��ڼ��ص�����
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                // �������ʱ���URI�����MD5 ����
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiscCache(cacheDir))// �Զ��建��·��
                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// ȫ�ֳ�ʼ��������
    }

    public static CustomApplcation getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        // �Ƿ���debugģʽ--Ĭ�Ͽ���״̬
        BmobChat.DEBUG_MODE = true;
        mInstance = this;
        init();
    }

    private void init() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        initImageLoader(getApplicationContext());
        // ���û���½�����ȴӺ�����ݿ���ȡ������list�����ڴ���
        if (BmobUserManager.getInstance(getApplicationContext())
                .getCurrentUser() != null) {
            // ��ȡ���غ���user list���ڴ�,�����Ժ��ȡ����list
            contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
        }
    }

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null) {
            String currentId = BmobUserManager.getInstance(
                    getApplicationContext()).getCurrentUserObjectId();
            String sharedName = currentId + PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this, sharedName);
        }
        return mSpUtil;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null)
            mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
        return mMediaPlayer;
    }

    /**
     * ��ȡ����
     */
    public String getLongtitude() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        longtitude = preferences.getString(PREF_LONGTITUDE, "");
        return longtitude;
    }

    /**
     * ���þ���
     */
    public void setLongtitude(String lon) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LONGTITUDE, lon).commit()) {
            longtitude = lon;
        }
    }

    /**
     * ��ȡγ��
     */
    public String getLatitude() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = preferences.getString(PREF_LATITUDE, "");
        return latitude;
    }

    /**
     * ����ά��
     */
    public void setLatitude(String lat) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (editor.putString(PREF_LATITUDE, lat).commit()) {
            latitude = lat;
        }
    }

    /**
     * ��ȡ�ڴ��к���user list
     */
    public Map<String, BmobChatUser> getContactList() {
        return contactList;
    }

    /**
     * ���ú���user list���ڴ���
     */
    public void setContactList(Map<String, BmobChatUser> contactList) {
        if (this.contactList != null) {
            this.contactList.clear();
        }
        this.contactList = contactList;
    }

    /**
     * �˳���¼,��ջ������
     */
    public void logout() {
        BmobUserManager.getInstance(getApplicationContext()).logout();
//		CustomApplcation.getInstance().getContactList().clear();
        setContactList(null);
        setLatitude(null);
        setLongtitude(null);
    }

}
