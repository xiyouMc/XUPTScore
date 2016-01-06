
package com.mc.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class H5Log {
    public static final String TAG = "H5Log";
    public final static String CURRENT_DEVICE_SPEC = Build.MANUFACTURER + "-"
            + Build.MODEL + "-" + Build.CPU_ABI + "-api" + Build.VERSION.SDK_INT;

    private static LogListener logListener;

    public interface LogListener {
        public void onLog(String tag, String log);
    }

    public static void setListener(LogListener listener) {
        synchronized (LogListener.class) {
            logListener = listener;
        }
    }

    public static void d(Context context,String log) {
        d(context,TAG, log);
    }

    public static void d(Context context,String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(context,tag, log);
        Log.d(tag, log);
    }

    public static void dWithDeviceInfo(Context context,String tag, String log) {
        final String appended = " on device: " + H5Log.CURRENT_DEVICE_SPEC;
        d(context,tag, (log == null ? appended : log + appended));
    }

    public static void w(Context context,String log) {
        w(context,TAG, log);
    }

    public static void w(Context context,String tag, String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        sendLog(context,tag, log);
        Log.w(tag, log);
    }

    public static void e(Context context,String log) {
        e(context,TAG, log, null);
    }

    public static void e(Context context,String tag, String log) {
        e(context,tag, log, null);
    }

    public static void eWithDeviceInfo(Context context,String tag, String log) {
        final String appended = " on device: " + H5Log.CURRENT_DEVICE_SPEC;
        e(context,tag, (log == null ? appended : log + appended), null);
    }

    public static void e(Context context,String log, Exception e) {
        e(context,TAG, log, e);
    }

    public static void e(Context context,String tag, String log, Exception e) {
        sendLog(context,tag, log);
        Log.e(tag, log, e);
    }

    private static void sendLog(Context context,String tag, String log) {
        if (!Util.isDebugable(context)) {
            return;
        }

        synchronized (LogListener.class) {
            if (logListener != null) {
                logListener.onLog(tag, log);
            }
        }
    }
}
