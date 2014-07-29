package com.xy.fy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 图片下载类，会异步更新图片，如果对图片有什么操作，圆角，缩小之类的，不仅要在ImageDownloader中操作，还要这里操作一次
 * 
 * @author Administrator
 * 
 */
@SuppressLint("HandlerLeak")
public class ImageThread implements Runnable {

	private ImageView image;
	private Context context;
	private String strUrl;
	public static final int END = 1;
	private Bitmap bitmap = null;

	public ImageThread(ImageView image, Context context, String strUrl) {
		this.image = image;
		this.context = context;
		this.strUrl = strUrl;
	}

	@Override
	public void run() {
		HttpUtil http = new HttpUtil();
		bitmap = http.getBitmapFromUrl(strUrl);

		// 先通知他更改
		Message msg = new Message();
		msg = new Message();
		msg.what = END;
		handler.sendMessage(msg);

		// 软引用缓存一份，硬引用缓存一份，文件缓存一份
		saveCache();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case END:
				image.setImageBitmap(bitmap);// 设置图片
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 软引用缓存一份，硬引用缓存一份，文件缓存一份
	 */
	private void saveCache() {
		// 内存缓存，硬引用缓存，软引用缓存
		MemoryCache memory = MemoryCache.getInstance(context);
		memory.saveCache(bitmap, strUrl);
		// 文件缓存
		FileCache file = new FileCache();
		file.saveCache(bitmap, strUrl);
	}
}
