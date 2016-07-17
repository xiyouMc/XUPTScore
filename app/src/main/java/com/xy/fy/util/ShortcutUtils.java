package com.xy.fy.util;

import com.xy.fy.main.R;

import android.content.Context;
import android.content.Intent;

public class ShortcutUtils {
    /**
     * ������ӵ������ݷ�ʽ��Intent��
     *
     * 1.��Intentָ��action="com.android.launcher.INSTALL_SHORTCUT"
     *
     * 2.����ΪIntent.EXTRA_SHORTCUT_INENT��Intent�����밲װʱһ�µ�action(����Ҫ��)
     *
     * 3.���Ȩ��:com.android.launcher.permission.INSTALL_SHORTCUT
     */

    public static Intent getShortcutToDesktopIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, context.getClass());
    /* ����������Ϊ����ж��Ӧ�õ�ʱ��ͬʱɾ�������ݷ�ʽ */
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // �������ؽ�
        shortcut.putExtra("duplicate", false);
        // ��������
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        // ����ͼ��
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
        // ������ͼ�Ϳ�ݷ�ʽ��������
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        return shortcut;

    }
}
