package com.mc.request.asyn;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.xy.fy.util.ViewUtil;

public class CheckVersionAsyntask extends AsyncTask<String, String, String> {

	private Context context;
	public CheckVersionAsyntask(Context context){
		this.context = context;
	}
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		return HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL
				+ "checkversion.jsp?version="
				+ Util.getVersion(context));

	}
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		try {
			if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
				if (!result.equals("no")) {
					String[] str = result.split("\\|");
					String apk_url = str[0];
					String new_version = str[1];
					String update_content = str[2];
					VersionUpdate versionUpdate = new VersionUpdate(
							context);
					versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
					versionUpdate.updateMsg = new_version + "\n\n"
							+ update_content;
					versionUpdate.checkUpdateInfo();
					return;
				}
				ViewUtil.showToast(context, "×îÐÂ°æ±¾£¡");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Log.i("LoginActivity", e.toString());
		}

	}

}
