package com.xy.fy.util;

import com.xy.fy.main.R;

import android.content.Context;
import android.content.Intent;

public class ShortcutUtils {
  /**
   * 
   * 返回添加到桌面快捷方式的Intent：
   * 
   * 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
   * 
   * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有)
   * 
   * 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
   * 
   */

  public static Intent getShortcutToDesktopIntent(Context context) {
    Intent intent = new Intent();
    intent.setClass(context, context.getClass());
    /* 以下两句是为了在卸载应用的时候同时删除桌面快捷方式 */
    intent.setAction("android.intent.action.MAIN");
    intent.addCategory("android.intent.category.LAUNCHER");

    Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    // 不允许重建
    shortcut.putExtra("duplicate", false);
    // 设置名字
    shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
    // 设置图标
    shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
        Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
    // 设置意图和快捷方式关联程序
    shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

    return shortcut;

  }
}
