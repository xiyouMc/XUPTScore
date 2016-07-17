package com.mc.util;

import com.nrs.utils.HttpAssistFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:	CrashHandler
 * Function: 	UncaughtException������,��������Uncaught�쳣��ʱ��,�ɸ������ӹܳ���,����¼���ʹ��󱨸�.
 *
 * @author Norris        Norris.sly@gmail.com
 * @Date 2013        2013-3-24		����12:27:10
 * @Fields ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
 * @Methods ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
 * 2013-3-24����12:27:10	Modified By Norris
 * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
 * @see ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
 * @since Ver 1.0		I used to be a programmer like you, then I took an arrow in the knee
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * Log��־��tag
     * String			:		TAG
     *
     * @since 2013-3-21����8:44:28
     */
    private static final String TAG = "NorrisInfo";
    /**
     * CrashHandlerʵ��
     * CrashHandler			:		mInstance
     *
     * @since 2013-3-21����8:44:53
     */
    private static CrashHandler mInstance = new CrashHandler();
    /**
     * ϵͳĬ�ϵ�UncaughtException������
     * Thread.UncaughtExceptionHandler			:		mDefaultHandler
     *
     * @since 2013-3-21����8:44:43
     */
    private UncaughtExceptionHandler mDefaultHandler;
    /**
     * �����Context����
     * Context			:		mContext
     *
     * @since 2013-3-21����8:45:02
     */
    private Context mContext;
    /**
     * �����洢�豸��Ϣ���쳣��Ϣ
     * Map<String,String>			:		mLogInfo
     *
     * @since 2013-3-21����8:46:15
     */
    private Map<String, String> mLogInfo = new HashMap<String, String>();
    /**
     * ���ڸ�ʽ������,��Ϊ��־�ļ����һ����(FIXME ע����windows���ļ����޷�ʹ�ã��ȷ�ţ�)
     * SimpleDateFormat			:		mSimpleDateFormat
     *
     * @since 2013-3-21����8:46:39
     */
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");

    /**
     * Creates a new instance of CrashHandler.
     */
    private CrashHandler() {
    }

    /**
     * getInstance:{��ȡCrashHandlerʵ�� ,����ģʽ }
     * ��������������������������������������������������������������������
     *
     * @return CrashHandler
     * @since I used to be a programmer like you, then I took an arrow in the knee��Ver 1.0
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     * 2013-3-21����8:52:24	Modified By Norris
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     */
    public static CrashHandler getInstance() {
        return mInstance;
    }

    /**
     * init:{��ʼ��}
     * ��������������������������������������������������������������������
     *
     * @return void
     * @since I used to be a programmer like you, then I took an arrow in the knee��Ver 1.0
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     * 2013-3-21����8:52:45	Modified By Norris
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     */
    public void init(Context paramContext) {
        mContext = paramContext;
        // ��ȡϵͳĬ�ϵ�UncaughtException������
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // ���ø�CrashHandlerΪ�����Ĭ�ϴ�����
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * ��UncaughtException����ʱ��ת�����д�ķ���������
     * (non-Javadoc)
     *
     * @see UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
     */
    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
        if (!handleException(paramThrowable) && mDefaultHandler != null) {
            // ����Զ����û�д�������ϵͳĬ�ϵ��쳣������������
            mDefaultHandler.uncaughtException(paramThread, paramThrowable);
        } else {
            try {
                // ������ˣ��ó����������1�����˳�����֤�ļ����沢�ϴ���������
                paramThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // �˳�����
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * handleException:{�Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����.}
     * ��������������������������������������������������������������������
     *
     * @return true:������˸��쳣��Ϣ;���򷵻�false.
     * @since I used to be a programmer like you, then I took an arrow in the knee��Ver 1.0
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     * 2013-3-24����12:28:53	Modified By Norris
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     */
    public boolean handleException(Throwable paramThrowable) {
        if (paramThrowable == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "�ܱ�Ǹ,��������쳣,�����˳�", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        // ��ȡ�豸������Ϣ
        getDeviceInfo(mContext);
        // ������־�ļ�
        saveCrashLogToFile(paramThrowable);
        return true;
    }

    /**
     * getDeviceInfo:{��ȡ�豸������Ϣ}
     * ��������������������������������������������������������������������
     *
     * @since I used to be a programmer like you, then I took an arrow in the knee��Ver 1.0
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     * 2013-3-24����12:30:02	Modified By Norris
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     */
    public Map<String, String> getDeviceInfo(Context paramContext) {
        try {
            // ��ð������
            PackageManager mPackageManager = paramContext.getPackageManager();
            // �õ���Ӧ�õ���Ϣ������Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(
                    paramContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null"
                        : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
                mLogInfo.put("versionName", versionName);
                mLogInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        // �������
        Field[] mFields = Build.class.getDeclaredFields();
        // ���Build���ֶ�key-value  �˴�����Ϣ��Ҫ��Ϊ���ڷ��������ֻ���ְ汾�ֻ���ԭ��
        for (Field field : mFields) {
            try {
                field.setAccessible(true);
                mLogInfo.put(field.getName(), field.get("").toString());
                Log.d(TAG, field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return mLogInfo;
    }

    /**
     * saveCrashLogToFile:{��������Log���浽����}
     * TODO ����չ����Log�ϴ���ָ��������·��
     * ��������������������������������������������������������������������
     *
     * @return FileName
     * @since I used to be a programmer like you, then I took an arrow in the knee��Ver 1.0
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     * 2013-3-24����12:31:01	Modified By Norris
     * ������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
     */
    public String saveCrashLogToFile(Throwable paramThrowable) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        Writer mWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mWriter);
        if (paramThrowable != null) {
            paramThrowable.printStackTrace(mPrintWriter);
            paramThrowable.printStackTrace();
            Throwable mThrowable = paramThrowable.getCause();
            // ���ջ���а����е��쳣��Ϣд��writer��
            while (mThrowable != null) {
                mThrowable.printStackTrace(mPrintWriter);
                // ����  ÿ�����쳣ջ֮�任��
                mPrintWriter.append("\r\n");
                mThrowable = mThrowable.getCause();
            }
        }
        //�ǵùر�
        mPrintWriter.close();
        String mResult = mWriter.toString();
        mStringBuffer.append(mResult);
        // �����ļ��������ļ���
        String mTime = mSimpleDateFormat.format(new Date());
        final String mFileName = "CrashLog-" + mTime + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File mDirectory = new File(Environment.getExternalStorageDirectory()
                        + "/CrashInfos");
                Log.v(TAG, mDirectory.toString());
                if (!mDirectory.exists())
                    mDirectory.mkdir();
                FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory + "/"
                        + mFileName);
                mFileOutputStream.write(mStringBuffer.toString().getBytes());
                mFileOutputStream.close();
                //�ϴ�������
                new Thread(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        HttpAssistFile httpAssistFile = new HttpAssistFile();
                        httpAssistFile.uploadFile(new File(Environment.getExternalStorageDirectory() + "/CrashInfos" + "/" + mFileName), "txt");
                    }
                }).start();
                return mFileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
