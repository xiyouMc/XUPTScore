package com.mc.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

public class ScrollViewLinearLayout extends LinearLayout implements
        OnTouchListener {

    private LinearLayout top;
    // private LinearLayout.LayoutParams top_lp ;
    private ScrollView sv;
    private boolean isfrist = true;
    private float y1, y2;
    private int hight = 60;
    private Scroller mScroller;

    public ScrollViewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setClickable(true);
        setLongClickable(true);
        mScroller = new Scroller(context);

    }

    protected void smoothScrollBy(int dx, int dy) {
        // ����mScroller�Ĺ���ƫ����
        mScroller.startScroll(0, mScroller.getFinalY(), 0, dy);
        invalidate();// ����������invalidate()���ܱ�֤computeScroll()�ᱻ���ã�����һ����ˢ�½��棬����������Ч��
    }

    protected void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(0, dy);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && isfrist) {// ֻ��ʵ��һ��
            sv = (ScrollView) getChildAt(0);// ���Զ��岼��д��xml�ļ�ʱ�����Ӳ��ֵĵ�һ��������ScrollViewʱ���������getChildAt(0����ʵ��ScrollView
            sv.setOverScrollMode(View.OVER_SCROLL_NEVER);// ȥ��ScrollView
            // �������ײ��򶥲�
            // ����ʱ����ֽ������ɫ��ɫ��

            sv.setOnTouchListener(this);
            isfrist = false;

        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // �ж�mScroller�����Ƿ����
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY()); // �������View��scrollTo()���ʵ�ʵĹ���
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                y2 = event.getY();
                int scrollY = v.getScrollY();
                int height = v.getHeight();
                int scrollViewMeasuredHeight = sv.getChildAt(0).getMeasuredHeight();
                if (y2 - y1 > 0 && v.getScrollY() <= 0) {// ͷ���ص�Ч��
                    smoothScrollTo(0, -(int) ((y2 - y1) / 2));
                    System.out.println("topMargin=" + ((int) ((y2 - y1) / 2)));
                    return false;
                }

                if (y2 - y1 < 0 && (scrollY + height) == scrollViewMeasuredHeight) {// �ײ��ص�Ч��
                    smoothScrollTo(0, -(int) ((y2 - y1) / 2));
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                smoothScrollTo(0, 0); // �ɿ���ָ���Զ��ع�
                break;
            default:
                break;
        }
        return false;
    }

}
