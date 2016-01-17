package com.mc.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.xy.fy.main.R;


/**
 * @author Administrator
 * @description 记得修改代码，当和服务器响应的时候需要 fall back 代码
 */

public class HttpUtilMc {
  // 基础URL
  public static final String IP = "http://222.24.63.101";
  public static final String BASE_URL = IP + "/xuptqueryscore/";
  public static final String LIB_URL = IP + "/xuptlibrary/";
  public static String SERVER_ADDRESS = "222.24.63.101";
  public static final String XUPT_IP1 = "222.24.63.101";
  public static final String XUPT_IP2 = "222.24.63.101";
  public static final String xiyouMC_IP = "http://222.24.63.101";
  public static final String XIYOUMC_BASE_IP = xiyouMC_IP + "/xuptqueryscore/";
  
  public static String libURL = "http://222.24.63.101/XiYouLibrary/login";
  public static String RENEW_URL = "http://222.24.63.101/XiYouLibrary/renew";
  /*
   * public static String SERVER_ADDRESS="192.168.11.1"; public static int SERVER_PORT = 8080;
   */

  public static String CONNECT_EXCEPTION = Util.getContext().getResources().getString(R.string.repeat_login);
  public static String CONNECT_REPEAT_EXCEPTION = Util.getContext().getResources().getString(R.string.repeating_login);

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

  // 根据请求获得响应对象response
  public static HttpResponse getHttpResponse(HttpGet request)
      throws ClientProtocolException, IOException {
    HttpClient client = new DefaultHttpClient();
    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
    // 读取超时
    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
    HttpResponse response = client.execute(request);
    return response;
  }

  /**
   * 根据请求获得响应对象response
   * 
   * @param request
   *          request
   * @return httpResponse
   * @throws ClientProtocolException
   * @throws IOException
   */
  public static HttpResponse getHttpResponse(HttpPost request)
      throws ClientProtocolException, IOException {
    HttpClient client = new DefaultHttpClient();
    client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
    // 读取超时
    client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
    HttpResponse response = client.execute(request);
    return response;
  }

  // 发送Post请求，获得响应查询结果
  public static String queryStringForPost(String url) {
    // 根据url获得HttpPost对象
    // for test remove , if run server ,need fall back
    HttpPost request = HttpUtilMc.getHttpPost(url);
    String result = null;
    // for test remove , if run server ,need fall back
    try {
      // 获得响应对象
      HttpResponse response = HttpUtilMc.getHttpResponse(request);
      // 判断是否请求成功
      if (response.getStatusLine().getStatusCode() == 200) {

        // 获得响应
        result = EntityUtils.toString(response.getEntity(), "utf-8");// 防止中文乱码
        // result=new String(result.getBytes("ISO-8859-1"),"utf-8"); //
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

  /**
   * 验证学校服务器是否可以ping通
   * 
   * @param timeout
   * @return
   */
  private static boolean IsReachIP(String ip) {
    try {
      return Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip).waitFor() == 0 ? true : false;
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false;
  }

  public static boolean IsReachIP() {
    return true/* IsReachIP(XUPT_IP1) ? true : IsReachIP(XUPT_IP2) */;
  }
}
