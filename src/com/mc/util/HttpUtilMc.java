package com.mc.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author Administrator
 * @description 记得修改代码，当和服务器响应的时候需要 fall back 代码
 */

public class HttpUtilMc {
	// 基础URL
	// public static final String BASE_URL="http://10.0.2.2:8080/ShopServer/";
	// public static final String BASE_URL = "http://192.168.137.1:8080/TuoC/";
	public static final String IP = "http://192.168.191.1:8080";
	public static final String BASE_URL = IP + "/xuptqueryscore/";
	// public static String SERVER_ADDRESS="192.168.1.103";

	/*
	 * public static String SERVER_ADDRESS="192.168.11.1"; public static int
	 * SERVER_PORT = 8080;
	 */

	public static String CONNECT_EXCEPTION = "服务器维护中。。。";

	// 获得Get请求对象request
	public static HttpGet getHttpGet(String url) {
		HttpGet request = new HttpGet(url);
		return request;
	}

	// 获得Post请求对象request
	public static HttpPost getHttpPost(String url) {
		HttpPost request = new HttpPost(url);
		return request;
	}
    

	//根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpGet request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	//根据请求获得响应对象response
	public static HttpResponse getHttpResponse(HttpPost request)
			throws ClientProtocolException, IOException {
		HttpResponse response = new DefaultHttpClient().execute(request);
		return response;
	}

	// 发送Post请求，获得响应查询结果
	public static String queryStringForPost(String url) {
		// 根据url获得HttpPost对象
		// for test remove , if run server ,need fall back
		HttpPost request = HttpUtilMc.getHttpPost(url);
		System.out.println("request==========" + request);
		String result = null;
		// for test remove , if run server ,need fall back
		try {
			// 获得响应对象
			HttpResponse response = HttpUtilMc.getHttpResponse(request);

			System.out.println("response==========" + response);
			System.out
					.println("response.getStatusLine().getStatusCode()=========="
							+ response.getStatusLine().getStatusCode());
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				System.out.println("响应成功");
				System.out.println("response.getEntity()=========="
						+ response.getEntity());

				// 获得响应
				result = EntityUtils.toString(response.getEntity(), "utf-8");// 防止中文乱码
				// result=new String(result.getBytes("ISO-8859-1"),"utf-8"); //
				System.out.println("result==========" + result);
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;

		// for test add , if run server ,need remove
		// return String.valueOf(1);
	}

	// 获得响应查询结果
	public static String queryStringForPost(HttpPost request) {
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtilMc.getHttpResponse(request);
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;
	}

	// 发送Get请求，获得响应查询结果
	public static String queryStringForGet(String url) {
		// 获得HttpGet对象
		HttpGet request = HttpUtilMc.getHttpGet(url);
		String result = null;
		try {
			// 获得响应对象
			HttpResponse response = HttpUtilMc.getHttpResponse(request);
			// 判断是否请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获得响应
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result = HttpUtilMc.CONNECT_EXCEPTION;
			return result;
		}
		return null;
	}
}
