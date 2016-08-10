package com.mc.util;

import com.xy.fy.main.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


/**
 * @author Administrator
 * @description HttpUtil
 */

public class HttpUtilMc {
    // ��URL
    private static final String HOST = "http://www.codemc.top:8080";
    public static final String IP = HOST;
    public static final String BASE_URL = IP + "/xuptqueryscore/";
    public static final String LIB_URL = IP + "/xuptlibrary/";
    public static final String XUPT_IP1 = HOST;
    public static final String XUPT_IP2 = HOST;
    public static final String xiyouMC_IP = HOST;
    public static final String XIYOUMC_BASE_IP = xiyouMC_IP + "/xuptqueryscore/";
    public static String SERVER_ADDRESS = HOST;
    public static String libURL = "http://www.codemc.top:8080/XiYouLibrary/login";
    public static String RENEW_URL = "http://www.codemc.top:8080/XiYouLibrary/renew";
  /*
   * public static String SERVER_ADDRESS="192.168.11.1"; public static int SERVER_PORT = 8080;
   */

    public static String CONNECT_EXCEPTION = Util.getContext().getResources().getString(R.string.repeat_login);
    public static String CONNECT_REPEAT_EXCEPTION = Util.getContext().getResources().getString(R.string.repeating_login);

    public static HttpGet getHttpGet(String url) {
        HttpGet request = new HttpGet(url);
        return request;
    }

    public static HttpPost getHttpPost(String url) {
        HttpPost request = new HttpPost(url);
        return request;
    }

    public static HttpResponse getHttpResponse(HttpGet request)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 40000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 40000);
        HttpResponse response = client.execute(request);
        return response;
    }

    /**
     * �����������Ӧ����response
     *
     * @param request request
     * @return httpResponse
     */
    public static HttpResponse getHttpResponse(HttpPost request)
            throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 40000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 40000);
        HttpResponse response = client.execute(request);
        return response;
    }

    public static String queryStringForPost(String url) {
        // for test remove , if run server ,need fall back
        HttpPost request = HttpUtilMc.getHttpPost(url);
        String result = null;
        // for test remove , if run server ,need fall back
        try {
            // �����Ӧ����
            HttpResponse response = HttpUtilMc.getHttpResponse(request);
            // �ж��Ƿ�����ɹ�
            if (response.getStatusLine().getStatusCode() == 200) {

                // �����Ӧ
                result = EntityUtils.toString(response.getEntity(), "utf-8");// ��ֹ��������
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

    public static String queryStringForPost(HttpPost request) {
        String result = null;
        try {
            HttpResponse response = HttpUtilMc.getHttpResponse(request);
            if (response.getStatusLine().getStatusCode() == 200) {
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

    public static String queryStringForGet(String url) {
        HttpGet request = HttpUtilMc.getHttpGet(url);
        String result = null;
        try {
            HttpResponse response = HttpUtilMc.getHttpResponse(request);
            if (response.getStatusLine().getStatusCode() == 200) {
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
