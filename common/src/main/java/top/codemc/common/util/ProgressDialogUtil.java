package top.codemc.common.util;

import android.app.Activity;
import android.app.ProgressDialog;

import top.codemc.common.R;

public class ProgressDialogUtil {

    public static ProgressDialog dialog;
    private static Activity mContext;
    private static String msg = Util.getContext().getResources().getString(R.string.querying);
    private static ProgressDialogUtil instance;

    private ProgressDialogUtil() {

        dialog = ViewUtil.getProgressDialog(mContext, msg);
        dialog.setCancelable(false);
    }

    public static ProgressDialogUtil getInstance(Activity context) {
        if (instance == null || context != mContext) {
            mContext = context;
            instance = new ProgressDialogUtil();
        }
        return instance;
    }

    private void setMsg(String msg) {
        this.msg = msg;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
