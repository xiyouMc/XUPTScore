package com.xy.fy.util;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.xy.fy.main.R;

import android.content.Context;

public class ShareUtil {

  private Context context;

  public ShareUtil(Context context) {
    this.context = context;
  }

  /**
   * 分享模块
   */
  public OnekeyShare showShare() {
    ShareSDK.initSDK(context);
    OnekeyShare oks = new OnekeyShare();
    // 关闭sso授权
    // oks.disableSSOWhenAuthorize();

    // 分享时Notification的图标和文字

    /*
     * oks.setNotification(R.drawable.default_head_photo, context.getString(R.string.app_name));
     */

    // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
    oks.setTitle(context.getString(R.string.app_name));
    // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
    oks.setTitleUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
    // text是分享文本，所有平台都需要这个字段
    oks.setText(
        "我正在使用《西邮成绩》查询平时成绩和卷面成绩，还可以查询专业排名奥，你快下载试试吧。下载链接:http://www.xiyoumobile.com/wechat_app/xiyouscore/");
    // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
    // oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
    // url仅在微信（包括好友和朋友圈）中使用
    // oks.setImagePath("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
    oks.setUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
    // comment是我对这条分享的评论，仅在人人网和QQ空间使用
    oks.setComment("《西邮成绩》分享功能,下载地址:");
    // site是分享此内容的网站名称，仅在QQ空间使用
    oks.setSite(context.getString(R.string.app_name));
    // siteUrl是分享此内容的网站地址，仅在QQ空间使用
    oks.setSiteUrl("http://www.xiyoumobile.com/wechat_app/xiyouscore/");
    return oks;
  }

  /**
   * 分享模块
   */
  public OnekeyShare showShare(String filePath) {
    ShareSDK.initSDK(context);
    OnekeyShare oks = new OnekeyShare();
    // 关闭sso授权
    // oks.disableSSOWhenAuthorize();

    // 分享时Notification的图标和文字

    /*
     * oks.setNotification(R.drawable.default_head_photo, context.getString(R.string.app_name));
     */

    // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//    oks.setTitle(context.getString(R.string.app_name));
    // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
    oks.setImagePath(filePath);// 确保SDcard下面存在此张图片
    return oks;
  }

  public void showShareUI(OnekeyShare oks) {
    // 启动分享GUI
    oks.show(context);
  }
}
