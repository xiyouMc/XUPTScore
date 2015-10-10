package com.mc.util;

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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mc.db.DBConnection;
import com.nrs.utils.HttpAssistFile;
import com.xy.fy.util.StaticVarUtil;

@SuppressLint("SimpleDateFormat")
public class Util {

  // 二维码文件
  public static final String QRCODE_FILENAME = "/西邮成绩.png";
  private final static String TAG = "util";
  public final static String infosFloder = "/xuptscore/devInfos";
  public final static String LOGINFILE = "login.log";

  public static boolean isDebug(Context context) {
    try {
      ApplicationInfo e = context.getPackageManager().getApplicationInfo(context.getPackageName(),
          16384);
      return (e.flags & 2) != 0;
    } catch (Exception var2) {
      return false;
    }
  }
  private static Context context;
  public static void setContext(Context ctx) {
    if (context == null && ctx != null) {
        context = ctx.getApplicationContext();
    }
}

public static Context getContext() {
    if (context == null) {
        // TODO
    }
    return context;
}

  public static void getRequestParmas(Context context, String data) {
    long time = System.currentTimeMillis();
    // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
    try {
      String time_s = Passport.jiami(String.valueOf(time),
          String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
      String realData = Passport.jiami(data, String.valueOf(time));
      String imei = "none";
      if (context != null) {
        imei = ((TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE))
            .getDeviceId();
        imei = Passport.jiami(imei, String.valueOf(time));
      }

      String checkData = Util.checkRankRequestData(realData, time_s);

      if (!checkData.equals(data)) {
        getRequestParmas(context, data);// 递归再次计算，直到计算出正确的
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
          String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
      String realData = Passport.jiami(libName, String.valueOf(time));

      String checkData = Util.checkRankRequestData(realData, time_s);
      if (!checkData.equals(libName)) {
        getBindLibParmas(context, libName);// 递归再次计算，直到计算出正确的
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
          String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
      String realData = Passport.jiami(account, String.valueOf(time));

      String checkData = Util.checkRankRequestData(realData, time_s);

      if (!checkData.equals(account)) {
        getAccountParmas(context, account);// 递归再次计算，直到计算出正确的
      }
      StaticVarUtil.libNameViewstate = time_s;//为了校验 libname,使用同一个密钥
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
        getBindLibNameParmas(context, libName);// 递归再次计算，直到计算出正确的
      }
      realData = URLEncoder.encode(realData);

      StaticVarUtil.bindLibName = realData;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void getRenewParmas(Context context, String renewData) {

    long time = System.currentTimeMillis();
    // String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
    try {
      String time_s = Passport.jiami(String.valueOf(time),
          String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
      String realData = Passport.jiami(renewData, String.valueOf(time));

      String checkData = Util.checkRankRequestData(realData, time_s);

      if (!checkData.equals(renewData)) {
        getRenewParmas(context, renewData);// 递归再次计算，直到计算出正确的
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
   * 用来存储设备信息和异常信息 Map<String,String> : mLogInfo
   * 
   * @since 2013-3-21下午8:46:15
   */
  /**
   * getDeviceInfo:{获取设备参数信息} ──────────────────────────────────
   * 
   * @param paramContext
   * @throws @since
   *           I used to be a programmer like you, then I took an arrow in the knee Ver 1.0
   *           ──────────────────────────────────────────────────────
   *           ──────────────────────────────────────────────── 2013-3-24下午12:30:02 Modified By
   *           Norris ──────────────────────────── ───────────────────────────────────────
   *           ───────────────────────────────────
   */
  @SuppressWarnings("static-access")
  public static void saveDeviceInfo(Context paramContext) {

    Map<String, String> mLogInfo = new HashMap<String, String>();
    try {
      // 获得包管理器
      PackageManager mPackageManager = paramContext.getPackageManager();
      // 得到该应用的信息，即主Activity
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
    // 反射机制
    Field[] mFields = Build.class.getDeclaredFields();
    // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
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

  public static boolean sendMail(final String msg) {
    MultiPartEmail email = new MultiPartEmail();
    /*
     * EmailAttachment attachment = new EmailAttachment();
     * 
     * String pathAll = email_attach .getText().toString(); attachment.setPath(pathAll);
     * 
     * attachment .setDisposition(EmailAttachment.ATTACHMENT); attachment.setDescription("不错！");
     * 
     * //解决附件名中文乱码 String fujian = pathAll.substring(pathAll .lastIndexOf("/") + 1);
     * System.out.println("附件名:"+fujian ); try { attachment.setName(MimeUtility.encodeText(fujian));
     * } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     */
    try {

      // 设置发送主机的服务器地址
      email.setHostName("smtp.126.com");
      // 设置收件人邮箱
      email.addTo("ideaback_mc@126.com");
      // 发件人邮箱
      email.setFrom("uu9923@126.com");
      // 如果要求身份验证，设置用户名、密码，分别为发件人在邮件服务器上注册的用户名和密码
      email.setAuthentication("uu9923@126.com", "uz31415926");
      // 设置邮件的主题
      email.setSubject("xupcScore ideaback");
      // 邮件正文消息
      email.setMsg(msg);
      // email.attach(attachment);
      email.send();
    } catch (EmailException e1) {
      // TODO Auto-generated catch
      // block
      e1.printStackTrace();
      return false;
    }
    return true;

  }

  private static long lastClickTime;

  /**
   * 防止按钮被点击很多次
   * 
   * @return
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
   * 返回是否将 客户端信息上传至服务器 并且将 登录信息 加入到 文件中
   * 
   * @param paramContext
   * @return
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
   * 将用户登录信息写入日志
   * 
   * @param loginMsgLogFile
   * @param msg
   * @param isAppend
   * @throws FileNotFoundException
   * @throws IOException
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
   * 上传用户登录信息
   * 
   * @param paramContext
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
   * 上传 客户端手机信息
   * 
   * @param paramContext
   */
  public static void uploadDevInfos(final Context paramContext) {
    // 上传服务器

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
      // 获得包管理器
      PackageManager mPackageManager = paramContext.getPackageManager();
      // 得到该应用的信息，即主Activity
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
      version = Integer.valueOf(android.os.Build.VERSION.SDK);
    } catch (NumberFormatException e) {
      Log.e("getAndroidSDKVersion", e.toString());
    }
    return version;
  }

  /**
   * 将图片保存到本地
   * 
   * @param bmp
   * @param username
   * @return
   */
  public static boolean saveBitmap2file(Bitmap bmp, String photoFileName, Context context) {
    CompressFormat format = Bitmap.CompressFormat.JPEG;
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

  // 从网络获取图片,并保存找到本地
  public static Bitmap getBitmap(String pictureUrl) {
    URL url = null;
    Bitmap bitmap = null;

    InputStream in = null;
    try {
      if (pictureUrl != null) {
        url = new URL(pictureUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(6000);// 最大延迟6000毫秒
        httpURLConnection.setDoInput(true);// 连接获取数据流
        httpURLConnection.setUseCaches(true);// 用缓存
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

  // 将文件转化为图片
  public static Bitmap convertToBitmap(String path, int w, int h) {
    if (!new File(path).exists()) {
      return null;
    }
    BitmapFactory.Options opts = new BitmapFactory.Options();
    // 设置为ture只获取图片大小
    opts.inJustDecodeBounds = true;
    opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
    // 返回为空
    BitmapFactory.decodeFile(path, opts);
    int width = opts.outWidth;
    int height = opts.outHeight;
    float scaleWidth = 0.f, scaleHeight = 0.f;
    if (width > w || height > h) {
      // 缩放
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
      Integer.valueOf(str);// 把字符串强制转换为数字
      return false;// 如果是数字，返回True
    } catch (Exception e) {
      return true;// 如果抛出异常，返回False
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
   * 
   * @param data
   * @param viewstate
   * @return
   */
  public static String checkRankRequestData(String data, String viewstate) {
    // TODO Auto-generated method stub
    String realXh = "";
    String realTime = Passport.jiemi(viewstate, String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
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
   * 从listHerf中获取具体 tittle中的herf
   * 
   * @param tittle
   * @return
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
   * 获取软件版本号
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
