package top.codemc.common.util;

import android.content.Context;

public class MyPreferences {
    // ƫ���ļ���
    public static final String SHAREDPREFERENCES_NAME = "activity_pref";
    // �����KEY
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";

    /**
     * �ж�activity�Ƿ����
     *
     * @return �Ƿ��Ѿ���� true����� falseδ��
     */
    public static boolean activityIsGuided(Context context, String className) {
        if (context == null || className == null || "".equalsIgnoreCase(className))
            return false;
        String[] classNames = context
                .getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                .getString(KEY_GUIDE_ACTIVITY, "").split("\\|");// ȡ���������� �� com.my.MainActivity
        for (String string : classNames) {
            if (className.equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ���ø�activity������ˡ� �������� |a|b|c������ʽ����Ϊvalue����Ϊƫ����ֻ�ܱ����ֵ��
     */
    public static void setIsGuided(Context context, String className) {
        if (context == null || className == null || "".equalsIgnoreCase(className))
            return;
        String classNames = context.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_WORLD_READABLE).getString(KEY_GUIDE_ACTIVITY, "");
        StringBuilder sb = new StringBuilder(classNames).append("|").append(className);// ���ֵ
        context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)// �����޸ĺ��ֵ
                .edit().putString(KEY_GUIDE_ACTIVITY, sb.toString()).commit();
    }
}
