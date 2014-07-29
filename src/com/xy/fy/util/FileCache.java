package com.xy.fy.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 文件缓存，strUrl截取最后一段字符串，当做文件名， 用到的知识点有LRU算法
 * 
 * @author 王玉超
 */
public class FileCache {

	private static final int FILE_CACHE = 50 * 1024 * 1024;// 文件缓存50MB
	private static final String FILE_CACHE_EXT = ".cach";// 文件后缀名(扩展名)
	private static final String dir = getRealPath() + HttpUtil.FENGYUN + "/fileCache";// 得到真实路径

	/**
	 * @param strUrl
	 *        图片路径
	 * @return Bitmap图片
	 */
	public Bitmap getBitmap(String strUrl) {
		String fileName = convertUrlToFileName(strUrl);
		String path = dir + "/" + fileName;
		File file = new File(path);
		if (file.exists()) {
			System.out.println("文件缓存有数据：");
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			if (bitmap == null) {
				file.delete();
			} else {
				updateFileTime(path);// 更新最后修改时间
			}
			return bitmap;
		} else {
			System.out.println("文件缓存中没有");
			return null;
		}
	}

	/**
	 * 保存缓存，如果缓存多了以后，那么清理缓存
	 * 
	 * @param bitmap
	 *        要保存的Bitmap类型的图片
	 * @param strUrl
	 *        要保存的图片URL路径
	 */
	public void saveCache(Bitmap bitmap, String strUrl) {
		String fileName = convertUrlToFileName(strUrl);
		// 文件缓存目录

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		// 先判断是否该清理了一部分文件缓存了
		isTimeToClear(dir);

		File file = new File(dir + "/" + fileName);
		OutputStream outputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 系统自动判断文件缓存文件夹是否超过50MB
	 * 
	 * @param dir
	 *        缓存路径
	 */
	private void isTimeToClear(String dir) {
		File file = new File(dir);
		File[] allFile = file.listFiles();
		int allSize = 0;
		for (int i = 0; i < allFile.length; i++) {
			allSize += allFile[i].length();
		}
		if (allSize >= FILE_CACHE) {
			Arrays.sort(allFile, new FileLastModifSort());
			// 清除最前面一半的缓存
			int clearFlag = allFile.length / 2;
			for (int i = 0; i < clearFlag; i++) {
				if (allFile[i].getName().contains(FILE_CACHE_EXT)) {
					allFile[i].delete();
				}
			}
		}
	}

	/**
	 * 根据文件的最后修改时间进行前后排序
	 */
	private class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	/**
	 * 清除全部缓存
	 */
	public void clearCache() {
		File file = new File(dir);
		if (file.exists()) {
			File[] allFile = file.listFiles();
			for (int i = 0; i < allFile.length; i++) {
				allFile[i].delete();
			}
		}

	}

	/**
	 * 将url转成文件名，这个方法不通用，只是适用于我的这个服务器，因为我知道服务器的地址规律
	 * 
	 * @param url
	 *        图片地址
	 * @return 文件名
	 */
	private String convertUrlToFileName(String url) {
		// 如果想通用的话，直接改成url.split("/");即可
		String[] strs = url.substring(36).split("/");

		StringBuffer string = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			string.append(strs[i]);
		}
		return string.toString() + FILE_CACHE_EXT;// 返回文件名（扩展名为.cache）
	}

	/**
	 * @return 真实路径
	 */
	private static String getRealPath() {
		File sdDir = null;
		boolean isSDcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (isSDcardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根路径
			return sdDir.toString();
		} else {
			System.out.println("没有SDcard");
			return null;
		}
	}

	/**
	 * 当重复文件缓存已经存在的时候，修改文件缓存的时间
	 * 
	 * @param path
	 * 
	 */
	public void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/**
	 * 更新json数据源
	 * 
	 * @param jsonData
	 *        json数据
	 * @param isAppend
	 *        是否追加
	 */
	public void updateJsonCache(String jsonData, Boolean isAppend, String fileName) {
		String path = getRealPath() + HttpUtil.FENGYUN + "/jsonCache/" + fileName;
		File file = new File(path);
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file, isAppend);
			fileWriter.write(jsonData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取历史json数据 jsonCache.txt,jsonCacheMyPublish.txt,jsonCacheMyCollect.txt,jsonCacheMyComment.txt
	 * 
	 * @return
	 */
	public String readHistoryJsonData(String fileName) {
		String path = getRealPath() + HttpUtil.FENGYUN + "/jsonCache/" + fileName;
		StringBuffer stringBuffer = new StringBuffer();
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 保存在本地
	 * 
	 * @param fileUrlPath
	 *        图片的URL路径
	 * @param bitmap
	 *        要保存的Bitmap图片
	 */
	public void savePictureToLocal(Bitmap bitmap, String fileUrlPath) {
		// 如果想通用的话，直接改成url.split("/");即可
		String[] strs = fileUrlPath.substring(36).split("/");
		StringBuffer string = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			string.append(strs[i]);
		}
		String fileName = string.toString();// 得到文件名
		String path = getRealPath() + HttpUtil.FENGYUN + "/save/" + fileName;// 得到本地路径
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			if (bitmap != null) {
				// 要求无损格式
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
					fileOutputStream.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
