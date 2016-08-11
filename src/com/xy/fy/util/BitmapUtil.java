package com.xy.fy.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * 图片更改类，更改圆角图片，更改图片大小
 * 
 * @author 王玉超
 * 
 */
public class BitmapUtil {

	/**
	 * 圆角图片,所有的图片源自于网络上，只要将网络上的图片进行更改保存以后就行
	 * 
	 * @param bitmap
	 *            源文件
	 * @param pixels
	 *            圆角像素大小
	 * @return 带圆角的Bitmap
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 缩小图片为指定大小
	 * 
	 * @param bitmap
	 * @param w
	 *            宽度
	 * @param h
	 *            高度
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return resizedBitmap;
	}

	/**
	 * 缩小图片为指定高度的图片
	 * 
	 * @param bitmap
	 *            源文件
	 * @param h
	 *            高度
	 * @return
	 */
	public static Bitmap resizeBitmapHeight(Bitmap bitmap, int newWidth) {
		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = scaleWidth;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return resizedBitmap;
	}

	/**
	 * 缩小图片为指定宽度的图片
	 * 
	 * @param bitmap
	 *            源文件
	 * @param w
	 *            高度
	 * @return
	 */
	public static Bitmap resizeBitmapWidth(Bitmap bitmap, int newWidth) {
		// load the origial Bitmap
		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();

		// calculate the scale
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = scaleWidth;

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		// if you want to rotate the Bitmap
		// matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
				height, matrix, true);

		// make a Drawable from Bitmap to allow to set the Bitmap
		// to the ImageView, ImageButton or what ever
		return resizedBitmap;
	}

	/**
	 * 保存文件，并加入到 数据库
	 * 
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public static void saveFileAndDB(Context context, Bitmap bm, String fileName) {
		try {
			File dirFile = new File(StaticVarUtil.PATH);
			if (!dirFile.exists()) {
				dirFile.mkdir();
			}
			File myCaptureFile = new File(StaticVarUtil.PATH + fileName);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			updateGallery(context, StaticVarUtil.PATH + fileName);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(

		drawable.getIntrinsicWidth(),

		drawable.getIntrinsicHeight(),

		drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

		: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);

		// canvas.setBitmap(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		drawable.draw(canvas);

		return bitmap;

	}

	/**
	 * 将图片加入数据库
	 * 
	 * @param filename
	 */
	private static void updateGallery(Context context, String filename)// filename是我们的文件全名，包括后缀哦
	{
		MediaScannerConnection.scanFile(context, new String[] { filename },
				null, new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});
	}

	/**
	 * Save image to the SD card
	 * 
	 * @param photoBitmap
	 *            源文件
	 * @param path
	 *            保存文件路径
	 * @param fileName
	 *            保存文件名
	 */
	public static void saveBitmapToFile(Bitmap photoBitmap, String path,
			String fileName) {
		File photoFile = new File(path + "/" + fileName);
		if (!photoFile.exists()) {
			try {
				photoFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(photoFile);
			if (photoBitmap != null) {
				if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream)) {
					fileOutputStream.flush();
				}
			}
		} catch (Exception e) {
			photoFile.delete();
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
