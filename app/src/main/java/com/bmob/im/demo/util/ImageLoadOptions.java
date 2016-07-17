package com.bmob.im.demo.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.graphics.Bitmap;

public class ImageLoadOptions {

    /** �����б����õ���ͼƬ�������� */
    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                // // ����ͼƬ�������ڼ���ʾ��ͼƬ
                // .showImageOnLoading(R.drawable.small_image_holder_listpage)
                // // ����ͼƬUriΪ�ջ��Ǵ����ʱ����ʾ��ͼƬ
                // .showImageForEmptyUri(R.drawable.small_image_holder_listpage)
                // // ����ͼƬ����/�������д���ʱ����ʾ��ͼƬ
                // .showImageOnFail(R.drawable.small_image_holder_listpage)
                .cacheInMemory(true)
                // �������ص�ͼƬ�Ƿ񻺴����ڴ���
                .cacheOnDisc(true)
                // �������ص�ͼƬ�Ƿ񻺴���SD����
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)// ����ͼƬ����εı��뷽ʽ��ʾ
                .bitmapConfig(Bitmap.Config.RGB_565)// ����ͼƬ�Ľ�������
                // .decodingOptions(android.graphics.BitmapFactory.Options
                // decodingOptions)//����ͼƬ�Ľ�������
                .considerExifParams(true)
                // ����ͼƬ����ǰ���ӳ�
                // .delayBeforeLoading(int delayInMillis)//int
                // delayInMillisΪ�����õ��ӳ�ʱ��
                // ����ͼƬ���뻺��ǰ����bitmap��������
                // ��preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)// ����ͼƬ������ǰ�Ƿ����ã���λ
                // .displayer(new RoundedBitmapDisplayer(20))//�Ƿ�����ΪԲ�ǣ�����Ϊ����
                .displayer(new FadeInBitmapDisplayer(100))// ����
                .build();

        return options;
    }

}
