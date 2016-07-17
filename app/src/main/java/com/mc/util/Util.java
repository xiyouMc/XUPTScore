package com.mc.util;

import com.mc.db.DBConnection;
import com.nrs.utils.HttpAssistFile;
import com.util.mail.MailSenderInfo;
import com.util.mail.SimpleMailSender;
import com.xy.fy.util.StaticVarUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Util {

    // ��ά���ļ�
    public static final String QRCODE_FILENAME = "/���ʳɼ�.png";
    public final static String infosFloder = "/xuptscore/devInfos";
    public final static String LOGINFILE = "login.log";
    public final static int CHANGE_PWD_RESULT = 100;
    private final static String TAG = "util";
    private static Context context;
    private static long lastClickTime;

    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo e = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    16384);
            return (e.flags & 2) != 0;
        } catch (Exception var2) {
            return false;
        }
    }

    public static Context getContext() {
        if (context == null) {
            // TODO
        }
        return context;
    }

    public static void setContext(Context ctx) {
        if (context == null && ctx != null) {
            context = ctx.getApplicationContext();
        }
    }

    public static void getRequestParmas(Context context, String data) {
        long time = System.currentTimeMillis();
        // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
        try {
            String time_s = Passport.jiami(String.valueOf(time),
                    String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
            String realData = Passport.jiami(data, String.valueOf(time));
            String imei = "none";
            if (context != null) {
                imei = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE))
                        .getDeviceId();
                imei = Passport.jiami(imei, String.valueOf(time));
            }

            String checkData = Util.checkRankRequestData(realData, time_s);

            if (!checkData.equals(data)) {
                getRequestParmas(context, data);// �ݹ��ٴμ��㣬ֱ���������ȷ��
            }
            realData = URLEncoder.encode(realData);

            StaticVarUtil.data = realData;
            StaticVarUtil.content = imei;
            time_s = URLEncoder.encode(time_s);
            StaticVarUtil.viewstate = time_s;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getBindLibParmas(Context context, String libName) {

        long time = System.currentTimeMillis();
        // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
        try {
            String time_s = Passport.jiami(String.valueOf(time),
                    String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
            String realData = Passport.jiami(libName, String.valueOf(time));

            String checkData = Util.checkRankRequestData(realData, time_s);
            if (!checkData.equals(libName)) {
                getBindLibParmas(context, libName);// �ݹ��ٴμ��㣬ֱ���������ȷ��
            }
            realData = URLEncoder.encode(realData);
            StaticVarUtil.libData = realData;
            time_s = URLEncoder.encode(time_s);
            StaticVarUtil.libViewstate = time_s;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getAccountParmas(Context context, String account) {

        long time = System.currentTimeMillis();
        // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
        try {
            String time_s = Passport.jiami(String.valueOf(time),
                    String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
            String realData = Passport.jiami(account, String.valueOf(time));

            String checkData = Util.checkRankRequestData(realData, time_s);

            if (!checkData.equals(account)) {
                getAccountParmas(context, account);// �ݹ��ٴμ��㣬ֱ���������ȷ��
            }
            StaticVarUtil.libNameViewstate = time_s;// Ϊ��У�� libname,ʹ��ͬһ����Կ
            realData = URLEncoder.encode(realData);

            StaticVarUtil.accountData = realData;
            time_s = URLEncoder.encode(time_s);
            StaticVarUtil.accountViewstate = time_s;
            StaticVarUtil.time = time;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getBindLibNameParmas(Context context, String libName) {

        // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
        try {
            String realData = Passport.jiami(libName, String.valueOf(StaticVarUtil.time));

            String checkData = Util.checkRankRequestData(realData, StaticVarUtil.libNameViewstate);
            if (!checkData.equals(libName)) {
                getBindLibNameParmas(context, libName);// �ݹ��ٴμ��㣬ֱ���������ȷ��
            }
            realData = URLEncoder.encode(realData);

            StaticVarUtil.bindLibName = realData;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getRenewParmas(String renewData) {

        long time = System.currentTimeMillis();
        // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
        try {
            String time_s = Passport.jiami(String.valueOf(time),
                    String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
            String realData = Passport.jiami(renewData, String.valueOf(time));

            String checkData = Util.checkRankRequestData(realData, time_s);

            if (!checkData.equals(renewData)) {
                getRenewParmas(renewData);// �ݹ��ٴμ��㣬ֱ���������ȷ��
            }
            realData = URLEncoder.encode(realData);

            StaticVarUtil.renewData = realData;
            time_s = URLEncoder.encode(time_s);
            StaticVarUtil.renewViewstate = time_s;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * �����洢�豸��Ϣ���쳣��Ϣ Map<String,String> : mLogInfo
     *
     * @since 2013-3-21����8:46:15
     */

    public static void setLanguageShare(Activity mActivity) {
        SharedPreferences preferences = mActivity.getSharedPreferences(StaticVarUtil.LANGUAGE_INFO,
                mActivity.MODE_PRIVATE);
        int postion = preferences.getInt(StaticVarUtil.LANGUAGE, 3);
        Resources res = mActivity.getResources();
        Configuration config = res.getConfiguration();
        if (postion == 1 || (!res.getConfiguration().locale.getCountry().isEmpty()
                && !res.getConfiguration().locale.getCountry().equals("CN"))) {// English
            config.locale = Locale.ENGLISH;
            postion = 1;
        } else {
            config.locale = Locale.CHINESE;
            postion = 0;
        }
        Editor editor = preferences.edit();
        editor.putInt(StaticVarUtil.LANGUAGE, postion);

        editor.commit();

        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);
    }

    /**
     * getDeviceInfo:{��ȡ�豸������Ϣ} ��������������������������������������������������������������������
     *
     * @throws @since I used to be a programmer like you, then I took an arrow in the knee Ver 1.0
     *                ������������������������������������������������������������������������������������������������������������
     *                ������������������������������������������������������������������������������������������������
     *                2013-3-24����12:30:02 Modified By
     *                Norris �������������������������������������������������������� ������������������������������������������������������������������������������
     *                ����������������������������������������������������������������������
     */
    @SuppressWarnings("static-access")
    public static void saveDeviceInfo(Context paramContext) {

        Map<String, String> mLogInfo = new HashMap<String, String>();
        try {
            // ��ð������
            PackageManager mPackageManager = paramContext.getPackageManager();
            // �õ���Ӧ�õ���Ϣ������Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(paramContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
                mLogInfo.put("versionName", versionName);
                mLogInfo.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        // �������
        Field[] mFields = Build.class.getDeclaredFields();
        // ���Build���ֶ�key-value �˴�����Ϣ��Ҫ��Ϊ���ڷ��������ֻ���ְ汾�ֻ���ԭ��
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
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : mLogInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }

        String mFileName = ((TelephonyManager) paramContext
                .getSystemService(paramContext.TELEPHONY_SERVICE)).getDeviceId() + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File mDirectory = new File(Environment.getExternalStorageDirectory() + infosFloder);

                Log.v(TAG, mDirectory.toString());
                if (!mDirectory.exists())
                    mDirectory.mkdir();
                FileOutputStream mFileOutputStream = new FileOutputStream(mDirectory + "/" + mFileName);
                mFileOutputStream.write(mStringBuffer.toString().getBytes());
                mFileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // public static boolean sendMail(final String msg,Context context) {
    // MultiPartEmail email = new MultiPartEmail();
    // /*
    // * EmailAttachment attachment = new EmailAttachment();
    // *
    // * String pathAll = email_attach .getText().toString(); attachment.setPath(pathAll);
    // *
    // * attachment .setDisposition(EmailAttachment.ATTACHMENT); attachment.setDescription("���?");
    // *
    // * //����������������� String fujian = pathAll.substring(pathAll .lastIndexOf("/") + 1);
    // * System.out.println("������:"+fujian ); try { attachment.setName(MimeUtility.encodeText(fujian));
    // * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
    // * e.printStackTrace(); }
    // */
    // try {
    //
    // // ���÷�������ķ�������ַ
    // email.setHostName("smtp.126.com");
    // // �����ռ�������
    // email.addTo("ideaback_mc@126.com");
    // // ����������
    // email.setFrom("uu9923@126.com");
    // // ���Ҫ�������֤�������û������룬�ֱ�Ϊ���������ʼ���������ע����û��������
    // email.setAuthentication("uu9923@126.com", "uz31415926");
    // // �����ʼ�������
    // email.setSubject("xupcScore ideaback");
    // // �ʼ�������Ϣ
    // Map<String, String> info = CrashHandler.getInstance().getDeviceInfo(context);
    // StringBuffer mStringBuffer = new StringBuffer() ;
    // for(Map.Entry<String , String> entry : info.entrySet()) {
    // String key = entry.getKey() ;
    // String value = entry.getValue() ;
    // mStringBuffer.append(key + "=" + value + "\r\n") ;
    // }
    //
    // email.setMsg(msg + "\n" + mStringBuffer.toString());
    // // email.attach(attachment);
    // email.send();
    // } catch (EmailException e1) {
    // // TODO Auto-generated catch
    // // block
    // try {
    // // ���÷�������ķ�������ַ
    // email.setHostName("smtp.126.com");
    // // �����ռ�������
    // email.addTo("ideaback_mc@126.com");
    // // ����������
    // email.setFrom("uu9923@126.com");
    // // ���Ҫ�������֤�������û������룬�ֱ�Ϊ���������ʼ���������ע����û��������
    // email.setAuthentication("uu9923@126.com", "uz31415926");
    // // �����ʼ�������
    // email.setSubject("xupcScore ideaback");
    // // �ʼ�������Ϣ
    // Map<String, String> info = CrashHandler.getInstance().getDeviceInfo(context);
    // StringBuffer mStringBuffer = new StringBuffer() ;
    // for(Map.Entry<String , String> entry : info.entrySet()) {
    // String key = entry.getKey() ;
    // String value = entry.getValue() ;
    // mStringBuffer.append(key + "=" + value + "\r\n") ;
    // }
    //
    // email.setMsg(msg + "\n" + e1);
    // // email.attach(attachment);
    // email.send();
    // } catch (EmailException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // e1.printStackTrace();
    // return false;
    // }
    // return true;
    //
    // }
    public static boolean sendMail(final String msg, Context context) {
        // �������Ҫ�������ʼ�
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.126.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("uu9923@126.com");
        mailInfo.setPassword("uz31415926");// �����������
        mailInfo.setFromAddress("uu9923@126.com");
        mailInfo.setToAddress("ideaback_mc@126.com");
        mailInfo.setSubject("xupcScore ideaback |" + StaticVarUtil.student.getAccount() + " | "
                + StaticVarUtil.student.getPassword());
        Map<String, String> info = CrashHandler.getInstance().getDeviceInfo(context);
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        mailInfo.setContent(msg + "\n" + mStringBuffer.toString());
        // �������Ҫ�������ʼ�
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextMail(mailInfo);// ���������ʽ

        return true;
    }

    /**
     * ��ֹ��ť������ܶ��
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH/mm/ss");
        return sdf.format(new Date());
    }

    /**
     * �����Ƿ� �ͻ�����Ϣ�ϴ��������� ���ҽ� ��¼��Ϣ ���뵽 �ļ���
     */
    public static boolean isRecordLoginMessage(Context paramContext) {

        @SuppressWarnings("static-access")
        File loginMsgLogFile = new File(Environment.getExternalStorageDirectory() + infosFloder + "/"
                + ((TelephonyManager) paramContext.getSystemService(paramContext.TELEPHONY_SERVICE))
                .getDeviceId()
                + LOGINFILE);
        try {
            if (!loginMsgLogFile.exists()) {
                loginMsgLogFile.createNewFile();
                saveLoginAppVersion(paramContext);
                return false;
            }
            saveLoginAppVersion(paramContext);
            return true;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    /**
     * ���û���¼��Ϣд����־
     */
    private static void writeLoginMsgToLog(File loginMsgLogFile, String msg, boolean isAppend) {
        try {
            FileOutputStream fos = new FileOutputStream(loginMsgLogFile, isAppend);
            fos.write(msg.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * �ϴ��û���¼��Ϣ
     */
    public static void uploadLoginMsg(final Context paramContext) {
        new Thread(new Runnable() {

            @SuppressWarnings("static-access")
            public void run() { // TODO Auto-generated method stub
                String mFileName = Environment.getExternalStorageDirectory() + "/"
                        + ((TelephonyManager) paramContext.getSystemService(paramContext.TELEPHONY_SERVICE))
                        .getDeviceId()
                        + LOGINFILE;
                File file = new File(mFileName);
                HttpAssistFile httpAssistFile = new HttpAssistFile();
                httpAssistFile.uploadFile(file, "loginmsg");
            }
        }).start();
    }

    /**
     * �ϴ� �ͻ����ֻ���Ϣ
     */
    public static void uploadDevInfos(final Context paramContext) {
        // �ϴ�������

        new Thread(new Runnable() {
            public void run() { // TODO Auto-generated method stub
                @SuppressWarnings("static-access")
                String mFileName = Environment.getExternalStorageDirectory() + infosFloder + "/"
                        + ((TelephonyManager) paramContext.getSystemService(paramContext.TELEPHONY_SERVICE))
                        .getDeviceId()
                        + ".log";
                HttpAssistFile httpAssistFile = new HttpAssistFile();
                httpAssistFile.uploadFile(new File(mFileName), "devsdk");

            }
        }).start();

    }

    @SuppressWarnings("static-access")
    private static void saveLoginAppVersion(Context paramContext) {
        try {
            // ��ð������
            PackageManager mPackageManager = paramContext.getPackageManager();
            // �õ���Ӧ�õ���Ϣ������Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(paramContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            StringBuffer sb = new StringBuffer();
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
                sb.append(getTime() + "\t" + versionCode + "--" + versionName + "\n");
            }
            writeLoginMsgToLog(
                    new File(
                            Environment.getExternalStorageDirectory() + infosFloder + "/"
                                    + ((TelephonyManager) paramContext
                                    .getSystemService(paramContext.TELEPHONY_SERVICE)).getDeviceId()
                                    + LOGINFILE),
                    sb.toString(), true);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            Log.e("getAndroidSDKVersion", e.toString());
        }
        return version;
    }

    /**
     * ��ͼƬ���浽����
     */
    public static boolean saveBitmap2file(Bitmap bmp, String photoFileName, Context context) {
        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            DBConnection.insertPhotoname(StaticVarUtil.student.getAccount(), photoFileName, context);
            String photoPath = StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG";
            if (!new File(photoPath).exists()) {
                try {
                    new File(photoPath).createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            stream = new FileOutputStream(photoPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // �������ȡͼƬ,�������ҵ�����
    public static Bitmap getBitmap(String pictureUrl) {
        URL url = null;
        Bitmap bitmap = null;

        InputStream in = null;
        try {
            if (pictureUrl != null) {
                url = new URL(pictureUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(6000);// ����ӳ�6000����
                httpURLConnection.setDoInput(true);// ���ӻ�ȡ�����
                httpURLConnection.setUseCaches(true);// �û���
                in = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            } else {
                return null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return bitmap;

    }

    // ���ļ�ת��ΪͼƬ
    public static Bitmap convertToBitmap(String path, int w, int h) {
        if (!new File(path).exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // ����Ϊtureֻ��ȡͼƬ��С
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // ����Ϊ��
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // ����
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    private static boolean haveChar(String str) {
        try {
            Integer.valueOf(str);// ���ַ�ǿ��ת��Ϊ����
            return false;// ��������֣�����True
        } catch (Exception e) {
            return true;// ����׳��쳣������False
        }

    }

    public static boolean isNull(Object s) {
        return s == null ? true : false;
    }

    public static boolean checkPWD(String pwd) {
        return pwd.matches("^(?![A-Z]*$)(?![a-z]*$)(?![0-9]*$)(?![^a-zA-Z0-9]*$)\\S+$");
    }

    /**
     * check url requestData
     */
    public static String checkRankRequestData(String data, String viewstate) {
        // TODO Auto-generated method stub
        String realXh = "";
        String realTime = Passport.jiemi(viewstate, String.valueOf(new char[]{2, 4, 8, 8, 2, 2}));
        try {
            realXh = Passport.jiemi(data, realTime);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return realXh;
    }

    public static boolean isDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
        }
        return false;
    }

    private static boolean hasDigit(String content) {

        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;

    }

    public static boolean hasDigitAndNum(String str) {
        if (haveChar(str) && hasDigit(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ��listHerf�л�ȡ���� tittle�е�herf
     */
    public static String getURL(String tittle) {
        String result = "";
        for (int i = 0; i < StaticVarUtil.listHerf.size(); i++) {
            HashMap<String, String> map = StaticVarUtil.listHerf.get(i);
            if (map.get("tittle").equals(tittle)) {
                result = map.get("herf");
            }
        }
        result = result.replace("%3D", "=");
        result = result.replace("%26", "&");
        result = result.replace("%3f", "?");
        return result;
    }

    /**
     * ��ȡ����汾��
     */
    public static String getVersion(Context context) {
        String version = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pt = pm.getPackageInfo(context.getPackageName(), 0);
            version = pt.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }
}
