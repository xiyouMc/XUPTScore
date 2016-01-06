package com.xy.fy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class FileUtils {
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

  public static void copyFile(String oldPath, String newPath,String filename) {
    try {
      int bytesum = 0;
      int byteread = 0;
      File dir = new File(newPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      newPath = newPath+"/"+filename;
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
}