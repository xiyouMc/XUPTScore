package com.mc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

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
}
