package com.xy.fy.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpUtil {
	// 路径
	private static final String strURL = HttpUtil.URLasdf + HttpUtil.URasd + HttpUtil.URasd + "." + HttpUtil.URLas + HttpUtil.URLaf + "." + HttpUtil.URLadf + HttpUtil.URLasf + "." + HttpUtil.URLasf + HttpUtil.URLasf + HttpUtil.URLasf;
	private static final String URL = HttpUtil.HTTP + new StringBuffer().append(strURL).reverse().substring(0, 11).toString() + "01" + HttpUtil.FENGYUN;
	public static final String URL1 = "/fengyun";

	/**
	 * 服务器用到的所有的URL
	 */
	public static final int URL_DEFAULT = 0;
	public static final int REGISTER_ACCOUNT_JUDGE = 1;
	public static final int REGISTER_EMAIL_JUDGE = 2;
	public static final int REGISTER_NICKNAME_JUDGE = 3;
	public static final int REGISTER = 4;
	public static final int LOGIN = 5;
	public static final int PUBLISH_COMMENT = 6;
	public static final int PRAISE = 7;
	public static final int COLLECT = 8;
	public static final int MESSAGE = 9;// 第9个URL按照不同要求返回数据
	public static final int MESSAGE_TIME = 10;// 按照时间返回
	public static final int MY_PUBLISH_MESSAGE = 11;
	public static final int MY_COMMENT_MESSAGE = 12;
	public static final int MY_COLLECT_MESSAGE = 13;
	public static final int FIND_PASSWORD_1 = 14;
	public static final int FIND_PASSWORD_2 = 15;
	public static final int MESSAGE_COMMENTS = 16;

	/**
	 * 服务器用到的所有的参数
	 */
	public static final String URL_KIND = "urlKind";
	public static final String ACCOUNT = "account";
	public static final String EMAIL = "email";
	public static final String NICKNAME = "nickname";
	public static final String COLLEGE_ID = "collegeId";
	public static final String PASSWORD = "password";
	public static final String MESSAGE_ID = "messageId";
	public static final String MESSAGE_KIND = "messageKind";
	public static final String SORT_KIND = "sortKind";// 排序种类
	public static final String PAGE = "page";
	public static final String COMMENT_CONTENT_TEXT = "commentContentText";
	public static final String LAST_MESSAGE_TIME = "lastMessageTime";
	public static final String MESSAGE_CONTENT_TEXT = "messageContentText";
	public static final String MESSAGE_CONTENT_PIC = "messageContentPic";
	public static final String HEAD_PHOTO = "headPhoto";
	/**
	 * 服务器所有的返回输出
	 */
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	public static final String ERROR_PARAMETER = "paramter_error";
	public static final String NOT_SUPPORT = "not_support_get_request";

	/**
	 * 客户端用到的所有的数据，为了防止被反编译
	 */
	public static final String URLasdf = "0";
	public static final String URasd = "1";
	public static final String URLasf = "2";
	public static final String URLas = "3";
	public static final String URLadf = "4";
	public static final String URLad = "5";
	public static final String URLaf = "6";
	public static final String URLa = "7";
	public static final String URLsdf = "8";
	public static final String URLsd = "9";

	public static final String FENGYUN = "/FengYun";
	public static final String HTTP = "http://";

	/**
	 * post your key and value to the strUrl receive the all message that service machine response to you
	 * 
	 * @param strUrl
	 * @param allParams
	 * @return
	 * @throws Exception
	 */
	public String downLoad(String strUrl, HashMap<String, String> allParams) throws Exception {
		strUrl = URL + strUrl;
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		setConnection(conn);// set the property of the urlConnection
		conn.connect();// url链接
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		// post your key and value to the DataOutPutStream
		for (Map.Entry<String, String> param : allParams.entrySet()) {
			dos.writeBytes(param.getKey() + "=" + URLEncoder.encode(param.getValue(), "UTF-8") + "&");
		}
		dos.flush();
		dos.close();
		StringBuffer result = getResult(conn);
		conn.disconnect();// disconnect the urlConnection
		System.out.println("返回数据：" + result.toString());
		return result.toString();
	}

	/**
	 * set the properties of the connection which was passed
	 * 
	 * @param conn
	 * @throws Exception
	 */
	private void setConnection(HttpURLConnection conn) throws Exception {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
	}

	/**
	 * receive the all message that service machine response to you based on the param 'conn'
	 * 
	 * @param conn
	 * @return service machine response to you based on the param 'conn'
	 * @throws Exception
	 */
	private StringBuffer getResult(HttpURLConnection conn) throws Exception {
		// 得到返回结果
		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuffer result = new StringBuffer();
		String readLine = null;
		while ((readLine = bufferedReader.readLine()) != null) {
			result.append(readLine);
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		return result;
	}

	/**
	 * imitate the form to post all message to the url 发表说说：模拟form表单提交
	 * 
	 * @author Administrator王玉超
	 * @param strUrl
	 * @param allParams
	 * @param fileParam
	 * @return receive the all message that service machine response to you based on the param 'strUrl'
	 * @throws Exception
	 */
	public String submitForm(String strUrl, HashMap<String, String> allParams, HashMap<String, File> fileParam) throws Exception {
		strUrl = URL + strUrl;
		PostMethod filePost = new PostMethod(strUrl);
		Part[] parts = null;
		int i = 0;

		// 这里一定要将全部参数传递过去
		parts = new Part[5];
		for (Map.Entry<String, String> part : allParams.entrySet()) {
			parts[i] = new StringPart(part.getKey(), URLEncoder.encode(part.getValue(), "UTF-8"));
			i++;
		}
		// 如果是空的，也要传递参数过去
		if (fileParam.isEmpty()) {
			parts[i] = new FilePart(HttpUtil.MESSAGE_CONTENT_PIC, fileParam.get(HttpUtil.MESSAGE_CONTENT_PIC));
		} else {// 如果不是空的
			for (Map.Entry<String, File> part : fileParam.entrySet()) {
				parts[i] = new FilePart(part.getKey(), part.getValue());
			}
		}
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient clients = new HttpClient();
		int status = clients.executeMethod(filePost);
		StringBuffer stringBuffer = null;
		if (status == 200) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filePost.getResponseBodyAsStream(), "UTF-8"));
			stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			bufferedReader.close();
		}
		return stringBuffer.toString();
	}

	/**
	 * alter personal info修改个人信息：模拟form表单提交
	 * 
	 * @author Administrator王玉超
	 * @param strUrl
	 * @param allParams
	 * @param fileParam
	 * @return receive the all message that service machine response to you based on the param 'strUrl'
	 * @throws Exception
	 */
	public String submitFormAlter(String strUrl, HashMap<String, String> allParams, HashMap<String, File> fileParam) throws Exception {
		strUrl = URL + strUrl;
		PostMethod filePost = new PostMethod(strUrl);
		Part[] parts = null;
		int i = 0;

		// 这里一定要将全部参数传递过去
		parts = new Part[3];
		for (Map.Entry<String, String> part : allParams.entrySet()) {
			parts[i] = new StringPart(part.getKey(), URLEncoder.encode(part.getValue(), "UTF-8"));
			i++;
		}
		// 如果是空的，也要传递参数过去
		if (fileParam.isEmpty()) {
			parts[i] = new FilePart(HttpUtil.HEAD_PHOTO, fileParam.get(HttpUtil.HEAD_PHOTO));
		} else {// 如果不是空的
			for (Map.Entry<String, File> part : fileParam.entrySet()) {
				parts[i] = new FilePart(part.getKey(), part.getValue());
			}
		}
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		HttpClient clients = new HttpClient();
		int status = clients.executeMethod(filePost);
		StringBuffer stringBuffer = null;
		if (status == 200) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(filePost.getResponseBodyAsStream(), "UTF-8"));
			stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			bufferedReader.close();
		}
		return stringBuffer.toString();
	}

	/**
	 * 根据URL下载bitmap图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromUrl(String url) {
		Bitmap bitmap = null;// 生成了一张bmp图像
		InputStream inputStream = null;
		BufferedInputStream buffer = null;
		try {
			URL iconurl = new URL(url);
			URLConnection conn = iconurl.openConnection();
			conn.connect();
			// 获得图像的字符流
			inputStream = conn.getInputStream();
			buffer = new BufferedInputStream(inputStream, 8 * 1024);
			bitmap = BitmapFactory.decodeStream(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
}
