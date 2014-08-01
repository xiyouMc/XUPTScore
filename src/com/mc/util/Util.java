package com.mc.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.xy.fy.util.StaticVarUtil;

public class Util {

	private static boolean haveChar(String str) {
		try {
			int num = Integer.valueOf(str);// 把字符串强制转换为数字
			return false;// 如果是数字，返回True
		} catch (Exception e) {
			return true;// 如果抛出异常，返回False
		}

	}
	private static boolean hasDigit(String content) {

		boolean flag = false;

		Pattern p = Pattern.compile(".*\\d+.*");

		Matcher m = p.matcher(content);

		if (m.matches())

		flag = true;

		return flag;

		}
	public static boolean hasDigitAndNum(String str){
		if (haveChar(str)&hasDigit(str)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 从listHerf中获取具体 tittle中的herf
	 * @param tittle
	 * @return
	 */
	public static String getURL(String tittle){
		String result = "";
		for (int i = 0; i < StaticVarUtil.listHerf.size(); i++) {
			HashMap<String, String> map = StaticVarUtil.listHerf.get(i);
			if (map.get("tittle").equals(tittle)) {
					result =map.get("herf");
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
	public static String getVersion(Context context){
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
