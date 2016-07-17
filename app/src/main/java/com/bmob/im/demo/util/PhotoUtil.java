package com.bmob.im.demo.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtil {

    /**
     * �������� recycle
     */
    public static void recycle(Bitmap bitmap) {
        // ���ж��Ƿ��Ѿ�����
        if (bitmap != null && !bitmap.isRecycled()) {
            // ���ղ�����Ϊnull
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**
     * ��ȡָ��·���µ�ͼƬ��ָ����С������ͼ getImageThumbnail
     *
     * @return Bitmap
     */
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // ��ȡ���ͼƬ�Ŀ�͸ߣ�ע��˴���bitmapΪnull
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // ��Ϊ false
        // �������ű�
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // ���¶���ͼƬ����ȡ���ź��bitmap��ע�����Ҫ��options.inJustDecodeBounds ��Ϊ false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // ����ThumbnailUtils����������ͼ������Ҫָ��Ҫ�����ĸ�Bitmap����
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * saveBitmap
     *
     * @param @param filename---�����·����ʽ-��Ŀ¼�Լ��ļ���
     * @param @param bitmap
     * @param @param isDelete --�Ƿ�ֻ��һ��
     * @return void
     */
    public static void saveBitmap(String dirpath, String filename,
                                  Bitmap bitmap, boolean isDelete) {
        File dir = new File(dirpath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dirpath, filename);
        // �����ڼ�ɾ��-Ĭ��ֻ����һ��
        if (isDelete) {
            if (file.exists()) {
                file.delete();
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static File getFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    /**
     * ��ȡͼƬ���ԣ���ת�ĽǶ�
     *
     * @param path ͼƬ���·��
     * @return degree��ת�ĽǶ�
     */

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    /**
     * ��תͼƬһ���Ƕ�
     * rotaingImageView
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // ��תͼƬ ����
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // �����µ�ͼƬ
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * ��ͼƬ��ΪԲ��
     *
     * @param bitmap ԭBitmapͼƬ
     * @param pixels ͼƬԲ�ǵĻ���(��λ:����(px))
     * @return ����Բ�ǵ�ͼƬ(Bitmap ����)
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
     * ��ͼƬת��ΪԲ��ͷ��
     *
     * @Title: toRoundBitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;

            left = 0;
            top = 0;
            right = width;
            bottom = width;

            height = width;

            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;

            float clip = (width - height) / 2;

            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;

            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);// ���û����޾��

        canvas.drawARGB(0, 0, 0, 0); // ������Canvas

        // ���������ַ�����Բ,drawRounRect��drawCircle
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ�������͵��������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
        // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // ��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle

        return output;
    }

}
