package com.mc.util;

import java.util.HashMap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.xy.fy.util.StaticVarUtil;

public class Util {

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
