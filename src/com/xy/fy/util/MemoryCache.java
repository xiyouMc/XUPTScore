package com.xy.fy.util;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 内存缓存,软引用，当内存不足的时候会回收内存,硬引用，一般不会轻易回收
 */
public class MemoryCache {

	private static final int SOFT_CACHE_SIZE = 20; // 软引用缓存容量
	private static LruCache<String, Bitmap> allLruCache;// 硬引用缓存
	private static LinkedHashMap<String, SoftReference<Bitmap>> allSoftCache;// 软引用缓存

	public static MemoryCache instance = null;

	public static MemoryCache getInstance(Context context) {
		if (instance == null) {
			instance = new MemoryCache(context);
		}
		return instance;
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	private MemoryCache(Context context) {
		// this.strUrl = convertUrlToFileName(strUrl);// 把路径改了
		int memoryMB = ((ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memoryMB / 10;// 硬引用容量大小为总容量的十分之一
		allLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null) {
					return value.getRowBytes() * value.getHeight();
				} else {
					return 0;
				}
			}

			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null)
					// 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存,用于随时回收内存
					allSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		allSoftCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
				SOFT_CACHE_SIZE, 0.75f, true) {
			private static final long serialVersionUID = 6040103833179403725L;

			@Override
			protected boolean removeEldestEntry(
					Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 在内存中，根据URL得到对应的Bitmap图片
	 * 
	 * @param url
	 *            图片URL
	 * @return Bitmap图片
	 */
	public Bitmap getBitmap(String url) {
		String strUrl = convertUrlToFileName(url);
		Bitmap bitmap;

		// 先硬引用缓存中获取
		synchronized (allLruCache) {
			bitmap = allLruCache.get(strUrl);
			if (bitmap != null) {
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				allLruCache.remove(strUrl);
				allLruCache.put(strUrl, bitmap);
				System.out.println("存在硬引用");
				return bitmap;
			}
		}

		// 硬缓存没有，那么再在软引用缓存中找
		synchronized (allSoftCache) {
			SoftReference<Bitmap> bitmapReference = allSoftCache.get(strUrl);
			if (bitmapReference != null) {
				bitmap = bitmapReference.get();
				if (bitmap != null) {
					// 将图片移回硬缓存
					allLruCache.put(strUrl, bitmap);
					allSoftCache.remove(strUrl);
					System.out.println("存在软引用");
					return bitmap;
				} else {
					allSoftCache.remove(strUrl);
				}
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存
	 * 
	 * @param bitmap
	 *            要缓存的图片
	 * @param url
	 *            缓存图片的URL
	 */
	public void saveCache(Bitmap bitmap, String url) {
		String strUrl = convertUrlToFileName(url);
		if (bitmap != null) {
			synchronized (allLruCache) {
				allLruCache.put(strUrl, bitmap);
			}
		}
	}

	/**
	 * 将url转成文件名，这个方法不通用，只是适用于我的这个服务器，因为我知道服务器的地址规律
	 * 
	 * @param url
	 *            图片地址
	 * @return 文件名
	 */
	private String convertUrlToFileName(String url) {
		// 如果想通用的话，直接改成url.split("/");即可
		String[] strs = url.substring(36).split("/");

		StringBuffer string = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			string.append(strs[i]);
		}
		return string.toString();// 返回文件名
	}

	/**
	 * 清理缓存
	 */
	public void clearCache() {
		allSoftCache.clear();
	}
}
