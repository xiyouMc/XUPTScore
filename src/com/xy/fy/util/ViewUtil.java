package com.xy.fy.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.widget.Toast;

/**
 * view帮助类
 */
public class ViewUtil {


	/**
	 * Toast
	 */
	private static Toast mToast;

	public static void showToast(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	public static void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

	public static void toastLength(String string, Activity activity) {
		Toast toast = Toast.makeText(activity, string, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		// LayoutInflater inflater = LayoutInflater.from(activity);
		// View view = inflater.inflate(R.layout, null);
		// toast.setView(view);
		toast.show();
	}

	public static void showDialog(String string, String title,
			final Activity activity) {
		Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title);
		builder.setMessage(string);
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	public static ProgressDialog getProgressDialog(Activity activity,
			String title) {
		ProgressDialog progressDialog = new ProgressDialog(activity);// 实例化
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条风格，风格为圆形，旋转的
		progressDialog.setTitle(title);// 设置ProgressDialog 标题
		progressDialog.setMessage("请稍后...");// 设置ProgressDialog 提示信息
		progressDialog.setIndeterminate(false);// 设置ProgressDialog
		progressDialog.setCancelable(false);// 是否可以按退回按键取消
		return progressDialog;
	}
}
