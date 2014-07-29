package com.xy.fy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 图片下载类，返回bitmap类型图片或者Drawable图片，用到异步处理，图片缓存
 * 
 * @author Administrator 1.首先从内存中读取数据 2.如果内存中没有缓存，那么从硬盘缓存读取数据 3.如果硬盘缓存也没有数据，那么从给定的URL中开启线程下载数据
 */
public class ImageDownloader {

	private Context context;

	/**
	 * 使用单例模式，使得程序只存在一个线程池和一份内存缓存和文件缓存
	 */
	public static ImageDownloader instance;

	public static ImageDownloader getInstance(Context context) {
		if (instance == null) {
			instance = new ImageDownloader(context);
		}
		return instance;
	}

	private ImageDownloader(Context context) {
		this.context = context;
	}

	
	/**
	 * 1.首先从内存中读取数据 2.如果内存中没有缓存，那么从硬盘缓存读取数据 3.如果硬盘缓存也没有数据，那么从给定的URL中开启线程下载数据
	 */
	public Bitmap getBitmap(ImageView image, String strUrl) {
		MemoryCache memory = MemoryCache.getInstance(context);
		Bitmap bitmap = memory.getBitmap(strUrl);
		if (bitmap == null) {
			FileCache file = new FileCache();
			bitmap = file.getBitmap(strUrl);
			if (bitmap == null) {
				StaticVarUtil.executorService.submit(new ImageThread(image, context, strUrl));
			}
		} else {
			System.out.println("内存中有呀。。。。。。。。。。");
		}
		return bitmap;
	}

}
