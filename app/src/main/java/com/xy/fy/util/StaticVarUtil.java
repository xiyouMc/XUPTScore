package com.xy.fy.util;

import com.xy.fy.singleton.BookList;
import com.xy.fy.singleton.Comment;
import com.xy.fy.singleton.Message;
import com.xy.fy.singleton.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * �ͻ��˾�̬������
 *
 * @author ����
 */
public class StaticVarUtil {

    /**
     * listHerf��tittle
     */
    public static final String QUERY_SCORE = "�ɼ���ѯ";
    public static final String CHANGE_PW = "�����޸�";
    /**
     * handler�е�ֵ
     */
    public static final int JUDGE_ACCOUNT_END = 1;
    public static final int JUDGE_NICKNAME_END = 2;
    public static final int JUDGE_EMAIL_END = 3;
    public static final int INTERNET_ERROR = 4;
    public static final int REGISTER_START = 5;
    public static final int REGISTER_END = 6;
    public static final int GET_PASSWORD_BACK1_START = 7;
    public static final int GET_PASSWORD_BACK1_END = 8;
    public static final int GET_PASSWORD_BACK2_START = 9;
    public static final int GET_PASSWORD_BACK2_END = 10;
    public static final int LOGIN_START = 11;
    public static final int LOGIN_END = 12;
    public static final int MENU_BANG = 1;
    public static final int MENU_BUKAO = 2;
    public static final int MENU_PAIMING = 4;
    public static final int MENU_CJ_TJ = 3;
    public static final int MENU_IDEA_BACK = 5;
    public static final int MENU_SETTING = 6;
    public static final int MENU_ABOUT = 7;
    public static final int FILE_SELECT = 1;
    public static final int SHARE = 8;
    public static final int IDEA_BACK_TOAST = 9;
    public static final int CHECK_VERSION = 10;
    public static final int BMOB_CHAT = 11;
    // һ�鳣�õ�handler���
    public static final int START = 13;
    public static final int END_SUCCESS = 14;
    public static final int END_FAIL = 15;
    public static final int MENU_CET = 16;
    public static final int MENU_LIB = 17;
    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";
    public static final String USER_INFO = "userinfo";
    public static final String IS_REMEMBER = "isRemember";
    public static final String SESSION = "session";
    public static final String LANGUAGE_INFO = "languageInfo";
    public static final String LANGUAGE = "language";
    public static String PHOTOFILENAME;
    public static HashMap<String, Object> kcdmList = new HashMap<String, Object>();// �γ̴���list
    /**
     * �ṩ�������ѧ�ں�ѧ��
     */
    public static TreeMap<String, String> list_Rank_xnAndXq = new TreeMap<String, String>();
    public static ArrayList<Activity> activities = new ArrayList<Activity>();

    public static Student student = new Student();

    public static String PATH = null;// Ӧ�õ�Ŀ¼"/sdcard/xuptscore"

    public static String response = null;// ���������ص����

    public static String largePicPath = null;// ��ͼƬ·��

    public static String smallPicPath = null;// СͼƬ·��

    public static Message message = null;// �����˵˵

    public static ArrayList<BookList> allBookList = null;
    public static String LIB_NAME = "";
    public static int requestTimes = 0;

    public static int loginTimes = 0;
    public static ExecutorService executorService = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);// ���ϵͳ��Դ�����̳߳ش�С

    public static String lastMessageTime = null;// ���һ��˵˵�ķ���ʱ��
    public static String session = null;// ������Ҫʹ�õĵ���session
    public static String fileName = null;// ������ļ���
    public static String data = null;
    public static String viewstate = null;

    public static String libData = null;
    public static String libViewstate = null;

    public static String renewData = null;
    public static String renewViewstate = null;

    public static String accountData = null;
    public static String accountViewstate = null;
    public static String libNameViewstate = null;

    public static ArrayList<HashMap<String, Object>> listItem;
    public static String bindLibName = null;
    public static long time;
    public static String content = null;
    public static String cet_account = null;
    public static String cet_data = null;
    public static String cet_viewstate = null;
    // ���е�herf
    public static List<HashMap<String, String>> listHerf = new ArrayList<HashMap<String, String>>();

    public static Student parseJsonToStudent(String strJson) {
        Student student = null;
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            student = new Student();
            student.setAccount(jsonObject.optString("account"));
            student.setPassword(jsonObject.optString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
            student = null;
        }
        return student;
    }

    /**
     * ����ջ��˳�������˳�
     */
    public static void quit() {
        for (int i = StaticVarUtil.activities.size() - 1; i >= 0; i--) {
            if (StaticVarUtil.activities.get(i) != null) {
                StaticVarUtil.activities.get(i).finish();
            }
        }
    }

    /**
     * �õ�һ��Message
     *
     * @return Message
     */
    public static Message getItemMessage(String jsonStr) {
        Message message = null;
        try {
            message = new Message();
            JSONObject json = new JSONObject(jsonStr);
            message.setAccount(json.optInt("account"));
            message.setCollegeId(json.optInt("collegeId"));
            message.setColNum(json.optInt("colNum"));
            message.setComNum(json.optInt("comNum"));
            message.setDate(json.optString("date"));
            message.setKind(json.optInt("kind"));
            message.setLarPic(json.optString("larPic"));
            message.setMsgId(json.optInt("msgId"));
            message.setPraNum(json.optInt("praNum"));
            message.setSmaPic(json.optString("smaPic"));
            message.setText(json.optString("text"));
            message.setTime(json.optString("time"));
            message.setNickname(json.optString("nickname"));
            message.setHeadPhoto(json.optString("headPhoto"));
        } catch (Exception e) {
            System.out.println("StaticVarUtil.getItemMessage()����");
            e.printStackTrace();
        }
        return message;
    }

    /**
     * �õ����е���Ϣ���,���ұ������һ��˵˵��ʱ��
     */
    public static ArrayList<Message> getAllMessage(String jsonStr) {
        ArrayList<Message> allMessage = null;
        try {
            allMessage = new ArrayList<Message>();
            JSONArray allJson = new JSONArray(jsonStr);
            for (int i = 0; i < allJson.length(); i++) {
                allMessage.add(getItemMessage(allJson.getString(i)));
            }
            StaticVarUtil.lastMessageTime = allMessage.get(allMessage.size() - 1).getDate() + " "
                    + allMessage.get(allMessage.size() - 1).getTime();
        } catch (Exception e) {
            System.out.println("StaticVarUtil.getAllMessage()����");
            e.printStackTrace();
        }
        return allMessage;
    }

    /**
     * �����ղ������Ϣ
     */
    public static void collect(int messageId, Context context) {
        SharedPreferences share = context.getSharedPreferences("messageIsCollect",
                Activity.MODE_PRIVATE);
        Editor editor = share.edit();
        editor.putBoolean(messageId + "", true);
        editor.commit();
    }

    /**
     * �����Ƿ��ղ�
     *
     * @param messageId ��Ϣ����
     */
    public static boolean isCollect(int messageId, Context context) {
        if (StaticVarUtil.student == null) {
            return false;
        }
        SharedPreferences share = context.getSharedPreferences("messageIsCollect",
                Activity.MODE_PRIVATE);
        return share.getBoolean(messageId + "", false);
    }

    /**
     * �Ƿ��޹� ȡ������ݿ⣬���ж�Ӧ�������ڣ���ô����true�� ������뱾����ݿ⣬Ȼ�󷵻�false
     *
     * @param position λ��
     */
    public static boolean isPraised(int messageId, Context context) {
        SharedPreferences share = context.getSharedPreferences("messageIsPraise",
                Activity.MODE_PRIVATE);
        // �����ݿ����������ֵ��
        if (share.getBoolean(messageId + "", false) == true) {
            return true;
        }
        return false;
    }

    /**
     * �����˵˵�ޣ����б���
     */
    public static void praise(int messageId, Context context) {
        SharedPreferences share = context.getSharedPreferences("messageIsPraise",
                Activity.MODE_PRIVATE);
        Editor editor = share.edit();
        editor.putBoolean(messageId + "", true);
        editor.commit();
    }

    /**
     * �õ����е�Comment
     *
     * @param jsonData json���Դ
     */
    public static ArrayList<Comment> getAllComments(String jsonData) {
        ArrayList<Comment> allComments = null;
        try {
            allComments = new ArrayList<Comment>();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Comment comment = new Comment();
                comment.setContent(jsonObject.getString("content"));
                comment.setDate(jsonObject.getString("date"));
                comment.setName(jsonObject.getString("name"));
                comment.setTime(jsonObject.getString("time"));
                allComments.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allComments;
    }
}
