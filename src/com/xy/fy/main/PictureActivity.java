package com.xy.fy.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.xy.fy.util.FileCache;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

@SuppressLint("FloatMath")
public class PictureActivity extends Activity implements OnTouchListener {
	// 放大缩小
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// 控件
	private Button download = null;// 下载按钮
	private ProgressBar progress = null;// 进度条
	private ImageView image = null;// 下载图片

	private Bitmap bitmap = null;// 要显示的图片

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_picture);
		download = (Button) findViewById(R.id.butDownLoad);
		download.setEnabled(false);// 开始时不可用
		progress = (ProgressBar) findViewById(R.id.progress);
		progress.setMax(100);
		image = (ImageView) findViewById(R.id.imageLargePic);

		FileCache fileCache = new FileCache();
		bitmap = fileCache.getBitmap(StaticVarUtil.largePicPath);
		if (bitmap != null) {
			image.setImageBitmap(bitmap);
			download.setEnabled(true);
			progress.setVisibility(View.GONE);
			image.setOnTouchListener(this);// 设置监听
		} else {
			bitmap = fileCache.getBitmap(StaticVarUtil.smallPicPath);
			progress.setVisibility(View.VISIBLE);
			image.setImageBitmap(bitmap);
			// 后台自动下载
			try {
				new ImageDownLoadAsyncTask().execute(new URL(StaticVarUtil.largePicPath));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 保存到本地 下载按钮
		 */
		download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FileCache fileCache = new FileCache();
				fileCache.savePictureToLocal(bitmap, StaticVarUtil.largePicPath);
				ViewUtil.toastShort("文件保存在sdcard/FengYun/save/目录下", PictureActivity.this);
			}
		});
	}

	/**
	 * 三个参数分别是Params，启动任务执行的输入参数
	 * 
	 * 　　2. Progress，后台任务执行的百分比
	 * 
	 * 　　3. Result，后台计算的结果类型
	 * 
	 * @author Administrator
	 * 
	 */
	private class ImageDownLoadAsyncTask extends AsyncTask<URL, Integer, Bitmap> {

		Bitmap bitmap = null;

		/**
		 * 更新progressBar，总是取得传过来的第一个值
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			progress.setProgress(values[0]);// 取得第一个值
		}

		/**
		 * 后台下载
		 */
		@Override
		protected Bitmap doInBackground(URL... params) {
			// 开始下载
			InputStream inputStream = null;
			try {
				URLConnection conn = params[0].openConnection();
				conn.connect();
				// 获得图像的字符流
				inputStream = conn.getInputStream();
				int length = conn.getContentLength();// 得到长度，发送一个消息
				if (length != -1) {
					byte[] imgData = new byte[length];
					byte[] temp = new byte[1024 * 10];// 每次下载10k，发送一个消息
					int readLen = 0;
					int destPos = 0;
					while ((readLen = inputStream.read(temp)) > 0) {
						System.arraycopy(temp, 0, imgData, destPos, readLen);
						/****************** 下面这句是重点 *****************/
						int pro = (int) ((float) destPos * 100 / length);// 百分比值
						publishProgress(pro);// 百分比
						destPos += readLen;
					}
					bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			FileCache fileCache = new FileCache();
			fileCache.saveCache(bitmap, StaticVarUtil.largePicPath);
			return bitmap;
		}

		/**
		 * 结束以后
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			PictureActivity.this.bitmap = result;
			image.setImageBitmap(PictureActivity.this.bitmap);
			download.setEnabled(true);// 按钮可用
			progress.setVisibility(View.GONE);
			image.setScaleType(ScaleType.MATRIX);
			image.setOnTouchListener(PictureActivity.this);// 设置监听
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		image.setScaleType(ScaleType.MATRIX);// 现在设置这个触摸模式

		ImageView myImageView = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置拖拉模式
		case MotionEvent.ACTION_DOWN:
			matrix.set(myImageView.getImageMatrix());
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;

		// 设置多点触摸模式
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		// 若为DRAG模式，则点击移动图片
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			}
			// 若为ZOOM模式，则点击触摸缩放
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// 设置硕放比例和图片的中点位置
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		myImageView.setImageMatrix(matrix);
		return true;
	}

	/**
	 * 计算移动距离
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 计算中点位置
	 * 
	 * @param point
	 * @param event
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}
