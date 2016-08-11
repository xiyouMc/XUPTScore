package com.xy.fy.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mc.util.H5Log;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class FileUtils {

  private static final String TAG = "FileUtils";

  public static String getPath(Context context, Uri uri) {
    if ("content".equalsIgnoreCase(uri.getScheme())) {
      String[] projection = { "_data" };
      Cursor cursor = null;

      try {
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow("_data");
        if (cursor.moveToFirst()) {
          return cursor.getString(column_index);
        }
      } catch (Exception e) {
        // Eat it
      }
    }

    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }

    return null;
  }

  public static void copyFile(String oldPath, String newPath, String filename) {
    try {
      int bytesum = 0;
      int byteread = 0;
      File dir = new File(newPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      newPath = newPath + "/" + filename;
      File newFile = new File(newPath);
      if (!newFile.exists()) {
        newFile.createNewFile();
      }
      File oldfile = new File(oldPath);
      if (oldfile.exists()) { // 文件存在时
        InputStream inStream = new FileInputStream(oldPath); // 读入原文件
        FileOutputStream fs = new FileOutputStream(newPath);
        byte[] buffer = new byte[1444];
        int length;
        while ((byteread = inStream.read(buffer)) != -1) {
          bytesum += byteread; // 字节数 文件大小
          System.out.println(bytesum);
          fs.write(buffer, 0, byteread);
        }
        inStream.close();
      }
    } catch (Exception e) {
      System.out.println("复制单个文件操作出错");
      e.printStackTrace();

    }

  }

  private static final int IO_BUFFER_SIZE = 16384;

  public static boolean create(String absPath) {
    return create(absPath, false);
  }

  public static boolean create(String absPath, boolean force) {
    if (TextUtils.isEmpty(absPath)) {
      return false;
    }

    if (exists(absPath)) {
      return true;
    }

    String parentPath = getParent(absPath);
    mkdirs(parentPath, force);

    try {
      File file = new File(absPath);
      return file.createNewFile();
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
    }
    return false;
  }

  public static boolean mkdirs(String absPath) {
    return mkdirs(absPath, false);
  }

  public static boolean mkdirs(String absPath, boolean force) {
    File file = new File(absPath);
    if (exists(absPath) && !isFolder(absPath)) {
      if (!force) {
        return false;
      }

      delete(file);
    }
    try {
      file.mkdirs();
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
    }
    return exists(file);
  }

  public static boolean move(String srcPath, String dstPath) {
    return move(srcPath, dstPath, false);
  }

  public static boolean move(String srcPath, String dstPath, boolean force) {
    if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
      return false;
    }

    if (!exists(srcPath)) {
      return false;
    }

    if (exists(dstPath)) {
      if (!force) {
        return false;
      } else {
        delete(dstPath);
      }
    }

    try {
      File srcFile = new File(srcPath);
      File dstFile = new File(dstPath);
      return srcFile.renameTo(dstFile);
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
    }
    return false;
  }

  public static boolean delete(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return false;
    }

    File file = new File(absPath);
    return delete(file);
  }

  public static boolean delete(File file) {
    if (!exists(file)) {
      return true;
    }

    if (file.isFile()) {
      return file.delete();
    }

    boolean result = true;
    File files[] = file.listFiles();
    for (int index = 0; index < files.length; index++) {
      result |= delete(files[index]);
    }
    result |= file.delete();

    return result;
  }

  public static boolean exists(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return false;
    }
    File file = new File(absPath);
    return exists(file);
  }

  public static boolean exists(File file) {
    return file != null && file.exists();
  }

  public static String getParent(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return null;
    }
    absPath = cleanPath(absPath);
    File file = new File(absPath);
    return getParent(file);
  }

  public static String getParent(File file) {
    if (file == null) {
      return null;
    } else {
      return file.getParent();
    }
  }

  public static boolean childOf(String childPath, String parentPath) {
    if (TextUtils.isEmpty(childPath) || TextUtils.isEmpty(parentPath)) {
      return false;
    }
    childPath = cleanPath(childPath);
    parentPath = cleanPath(parentPath);
    return childPath.startsWith(parentPath + File.separator);
  }

  public static int childCount(String absPath) {
    if (!exists(absPath)) {
      return 0;
    }
    File file = new File(absPath);
    File[] children = file.listFiles();
    if (children == null || children.length == 0) {
      return 0;
    }
    return children.length;
  }

  public static String cleanPath(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return absPath;
    }
    try {
      File file = new File(absPath);
      absPath = file.getCanonicalPath();
    } catch (Exception e) {
      H5Log.e(TAG, "Exception", e);
    }
    return absPath;
  }

  public static long size(String absPath) {
    if (absPath == null) {
      return 0;
    }
    File file = new File(absPath);
    return size(file);
  }

  public static long size(File file) {
    if (!exists(file)) {
      return 0;
    }

    long length = 0;
    if (isFile(file)) {
      length = file.length();
      return length;
    }

    File files[] = file.listFiles();
    if (files == null || files.length == 0) {
      return length;
    }

    int size = files.length;
    for (int index = 0; index < size; index++) {
      File child = files[index];
      length += size(child);
    }
    return length;
  }

  public static boolean copy(String srcPath, String dstPath) {
    return copy(srcPath, dstPath, false);
  }

  public static boolean copy(String srcPath, String dstPath, boolean force) {
    if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
      return false;
    }

    // check if copy source equals destination
    if (srcPath.equals(dstPath)) {
      return true;
    }

    // check if source file exists or is a directory
    if (!exists(srcPath) || !isFile(srcPath)) {
      return false;
    }

    // delete old content
    if (exists(dstPath)) {
      if (!force) {
        return false;
      } else {
        delete(dstPath);
      }
    }
    if (!create(dstPath)) {
      return false;
    }

    FileInputStream in = null;
    FileOutputStream out = null;

    // get streams
    try {
      in = new FileInputStream(srcPath);
      out = new FileOutputStream(dstPath);
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
      return false;
    }

    try {
      byte[] buffer = new byte[IO_BUFFER_SIZE];

      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
      out.flush();
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
      return false;
    } finally {
      try {
        in.close();
        out.close();
      } catch (Exception e) {
        H5Log.e(TAG, "exception detail", e);
      }
    }
    return true;
  }

  public static String fileName(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return null;
    }

    absPath = cleanPath(absPath);
    File file = new File(absPath);
    return file.getName();
  }

  public static String getExtension(File file) {
    if (file == null) {
      return null;
    }
    return getExtension(file.getName());
  }

  public static String getExtension(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return "";
    }

    int index = fileName.lastIndexOf('.');
    if (index < 0 || index >= (fileName.length() - 1)) {
      return "";
    }
    return fileName.substring(index + 1);
  }

  public static String getMimeType(File file) {
    if (file == null) {
      return "*/*";
    }
    String fileName = file.getName();
    return getMimeType(fileName);
  }

  public static String getMimeType(String url) {
    String type = null;
    String extension = MimeTypeMap.getFileExtensionFromUrl(url);
    // http://androidxref.com/5.1.0_r1/xref/libcore/luni/src/main/java/libcore/net/MimeUtils.java
    // not contains js file extension
    if ("js".equalsIgnoreCase(extension)) {
      return "application/javascript";
    }
    if (extension != null) {
      type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    return type;
  }

  public static boolean isFile(String absPath) {
    boolean exists = exists(absPath);
    if (!exists) {
      return false;
    }

    File file = new File(absPath);
    return isFile(file);
  }

  public static boolean isFile(File file) {
    return null != file && file.isFile();

  }

  public static boolean isFolder(String absPath) {
    boolean exists = exists(absPath);
    if (!exists) {
      return false;
    }

    File file = new File(absPath);
    return file.isDirectory();
  }

  public static String getFileName(String filePath) {
    if (TextUtils.isEmpty(filePath)) {
      return null;
    }
    int index = filePath.lastIndexOf("/");
    if (index > 0 && index < (filePath.length() - 1)) {
      filePath = filePath.substring(index + 1, filePath.length());
    }
    return filePath;
  }

  public static String getFileStem(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return null;
    }

    int index = fileName.lastIndexOf(".");
    if (index > 0) {
      return fileName.substring(0, index);
    } else {
      return "";
    }
  }

  public static String getFileSHA1(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return null;
    }
    File file = new File(absPath);

    if (!file.exists() || file.isDirectory()) {
      return null;
    }
    String fileHash = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
    } catch (FileNotFoundException e1) {
      H5Log.e(TAG, "exception detail.", e1);
    }
    if (fis == null) {
      return null;
    }

    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("SHA-1");
      byte[] buffer = new byte[IO_BUFFER_SIZE];
      int length = 0;
      while ((length = fis.read(buffer)) > 0) {
        messageDigest.update(buffer, 0, length);
      }
      fis.close();
      fileHash = H5SecurityUtil.bytes2Hex(messageDigest.digest());
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
      try {
        fis.close();
      } catch (IOException ioe) {
        H5Log.e(TAG, "exception detail", ioe);
      }
    }
    if (!TextUtils.isEmpty(fileHash)) {
      fileHash = fileHash.trim();
    }
    return fileHash;
  }

  public static String getFileMD5(String absPath) {
    if (TextUtils.isEmpty(absPath)) {
      return null;
    }
    File file = new File(absPath);
    if (!file.exists() || file.isDirectory()) {
      return null;
    }
    String fileHash = null;
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
    } catch (FileNotFoundException e1) {
      H5Log.e(TAG, "exception detail.", e1);
    }

    if (fis == null) {
      return null;
    }

    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
      byte[] buffer = new byte[IO_BUFFER_SIZE];
      int length = 0;
      while ((length = fis.read(buffer)) > 0) {
        messageDigest.update(buffer, 0, length);
      }
      fis.close();
      fileHash = H5SecurityUtil.bytes2Hex(messageDigest.digest());
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
      try {
        fis.close();
      } catch (IOException ioe) {
        H5Log.e(TAG, "IOException", ioe);
      }
    }
    if (!TextUtils.isEmpty(fileHash)) {
      fileHash = fileHash.trim();
    }
    return fileHash;
  }

  public static String getText(String absPath) {
    if (!exists(absPath)) {
      return null;
    }

    File file = new File(absPath);
    int length = (int) file.length();
    if (length > 20480) {
      return null;
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(file));
      int bufferSize = 1024;
      byte[] buffer = new byte[bufferSize];
      int len = 0;
      while (-1 != (len = in.read(buffer, 0, bufferSize))) {
        bos.write(buffer, 0, len);
      }
      in.close();
      bos.close();
      return bos.toString();
    } catch (Exception e) {
      H5Log.e(TAG, "exception detail", e);
    }
    return null;
  }

  public static final String read(String absPath) {
    String text = null;
    InputStream ips = null;
    try {
      ips = new FileInputStream(absPath);
      text = read(ips);
    } catch (Exception e) {
      H5Log.e(TAG, "Exception", e);
    } finally {
      if (ips != null) {
        try {
          ips.close();
        } catch (Exception e) {
          H5Log.e(TAG, "exception detail", e);
        }
      }
    }
    return text;
  }

  public static final String read(InputStream inputStream) {
    if (inputStream == null) {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    final char buffer[] = new char[IO_BUFFER_SIZE];
    try {
      InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
      for (;;) {
        int size = reader.read(buffer, 0, buffer.length);
        if (size < 0) {
          break;
        }
        builder.append(buffer, 0, size);
      }
    } catch (Exception e) {
      H5Log.e(TAG, "Exception", e);
    }
    return builder.toString();
  }

  public static String getLastModified(String absPath) {
    if (!exists(absPath)) {
      return null;
    }

    File file = new File(absPath);
    long lastModified = file.lastModified();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date(lastModified);
    return sdf.format(date);
  }
}