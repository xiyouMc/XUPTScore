package com.xy.fy.util;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

/**
 * 参数名 说明 urlKind 代表url种类（1-16可用）其余不可用 account 账号 password 密码 email 邮箱 nickname 昵称 collegeId 大学ID（西邮3728，0代表所有大学） messageId 消息ID messageKind 消息种类（0代表所有消息，1-9代表9类不同的消息） sortKind 排序种类（1代表7日关注，2代表总排行） page 分页技术（从0开始） lastMessageTime 用于时间排序：最后一个说说的时间(格式为"2013-04-02 13:36:29"[yyyy-MM-dd HH:mm:SS]) commentContentText 评论内容 messageContentText 说说内容 messageContentPic 说说图片
 * 
 * @author Administrator
 * 
 */
public class DownLoadThread implements Runnable {

	private Handler handler;

	private HashMap<String, String> allParams = null;

	/**
	 * 进行赞的接口
	 * 
	 * @param handler
	 */
	public DownLoadThread(Handler handler, int messageId) {
		this.handler = handler;
		allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.PRAISE + "");
		allParams.put(HttpUtil.MESSAGE_ID, messageId + "");
	}

	/**
	 * 我发表的说说，我评论的说说，我收藏的说说
	 * 
	 * @param handler
	 */
	public DownLoadThread(int account, int urlKind) {
		allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, urlKind + "");
		allParams.put(HttpUtil.ACCOUNT, account + "");
	}

	/**
	 * 设置page,专门用于设置我的发表，我的评论，我的收藏的
	 * 
	 * @param page
	 */
	public void setPageAndHanlder(int page, Handler handler) {
		this.handler = handler;
		allParams.put(HttpUtil.PAGE, page + "");
	}

	/**
	 * 查看所有评论的接口
	 * 
	 * @param handler
	 */
	public DownLoadThread(Handler handler, String messageId, String page) {
		this.handler = handler;
		allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.MESSAGE_COMMENTS + "");
		allParams.put(HttpUtil.MESSAGE_ID, messageId);
		allParams.put(HttpUtil.PAGE, page);
	}

	/**
	 * 进行收藏的接口
	 * 
	 * @param handler
	 */
	public DownLoadThread(Handler handler, int messageId, int account) {
		this.handler = handler;
		allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.COLLECT + "");
		allParams.put(HttpUtil.ACCOUNT, account + "");
		allParams.put(HttpUtil.MESSAGE_ID, messageId + "");
	}

	/**
	 * 进行评论的接口
	 * 
	 * @param handler
	 */
	public DownLoadThread(Handler handler, int messageId, int account, String comment) {
		this.handler = handler;
		allParams = new HashMap<String, String>();
		allParams.put(HttpUtil.URL_KIND, HttpUtil.PUBLISH_COMMENT + "");
		allParams.put(HttpUtil.ACCOUNT, account + "");
		allParams.put(HttpUtil.MESSAGE_ID, messageId + "");
		allParams.put(HttpUtil.COMMENT_CONTENT_TEXT, comment);
	}

	/**
	 * 
	 * @param handler
	 *        一个是风云榜，一个是近期
	 * @param collegeId
	 *        第9第10接口都用到
	 * @param messageKind
	 *        第9第10接口都用到
	 * @param messageSort
	 *        第9接口用到
	 * @param page
	 *        第9接口用到
	 * @param date
	 *        第十接口用到
	 */
	public DownLoadThread(Handler handler, int collegeId, int messageKind, int messageSort, int page, String lastMessageTime) {
		this.handler = handler;
		allParams = new HashMap<String, String>();// 所有的参数
		if (messageSort == 0) {// 调用第十接口,永远返回20条数据，近期
			allParams.put(HttpUtil.URL_KIND, HttpUtil.MESSAGE_TIME + "");
			allParams.put(HttpUtil.COLLEGE_ID, collegeId + "");
			allParams.put(HttpUtil.MESSAGE_KIND, messageKind + "");
			allParams.put(HttpUtil.LAST_MESSAGE_TIME, lastMessageTime);
		} else if (messageSort == 1 || messageSort == 2) {// 调用第九接口，根据page返回数据，排行旁
			allParams.put(HttpUtil.URL_KIND, HttpUtil.MESSAGE + "");
			allParams.put(HttpUtil.COLLEGE_ID, collegeId + "");
			allParams.put(HttpUtil.MESSAGE_KIND, messageKind + "");
			allParams.put(HttpUtil.PAGE, page + "");
			allParams.put(HttpUtil.SORT_KIND, messageSort + "");
		}
	}

	@Override
	public void run() {
		Message msg = new Message();
		msg.what = StaticVarUtil.START;
		this.handler.sendMessage(msg);

		System.out.println("所有的参数如下");
		for (Map.Entry<String, String> param : allParams.entrySet()) {
			System.out.println(param.getKey() + "-->" + param.getValue());
		}

		HttpUtil http = new HttpUtil();
		try {
			StaticVarUtil.response = http.downLoad(HttpUtil.URL1, allParams);
		} catch (Exception e) {
			System.out.println("DownLoadThread.run()出错了");
			msg = new Message();
			msg.what = StaticVarUtil.INTERNET_ERROR;
			this.handler.sendMessage(msg);
			e.printStackTrace();
			return;// 返回程序的被调用处
		}

		if (StaticVarUtil.response.equals(HttpUtil.FAIL)) {
			msg = new Message();
			msg.what = StaticVarUtil.END_FAIL;
			this.handler.sendMessage(msg);
		} else {
			msg = new Message();
			msg.what = StaticVarUtil.END_SUCCESS;
			this.handler.sendMessage(msg);
		}

	}
}
