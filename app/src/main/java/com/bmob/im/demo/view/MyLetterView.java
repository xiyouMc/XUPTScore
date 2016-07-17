package com.bmob.im.demo.view;

import com.bmob.im.demo.util.PixelUtil;
import com.xy.fy.main.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * ͨѶ¼�Ҳ���ٹ�����
 *
 * @author smile
 * @ClassName: MyLetterView
 * @Description: TODO
 * @date 2014-6-7 ����1:20:33
 */
public class MyLetterView extends View {
    // 26����ĸ
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    // �����¼�
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private int choose = -1;// ѡ��
    private Paint paint = new Paint();

    private TextView mTextDialog;

    public MyLetterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLetterView(Context context) {
        super(context);
    }

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    /**
     * ��д�������
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // ��ȡ����ı䱳����ɫ.
        int height = getHeight();// ��ȡ��Ӧ�߶�
        int width = getWidth(); // ��ȡ��Ӧ���
        int singleHeight = height / b.length;// ��ȡÿһ����ĸ�ĸ߶�

        for (int i = 0; i < b.length; i++) {
            paint.setColor(getResources().getColor(R.color.color_bottom_text_normal));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(PixelUtil.sp2px(12));
            // ѡ�е�״̬
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            // x�������м�-�ַ��ȵ�һ��.
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();// ���û���
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// ���y���
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);// ���y�����ռ�ܸ߶ȵı���*b����ĳ��Ⱦ͵��ڵ��b�еĸ���.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                //�����Ҳ���ĸ�б�[A,B,C,D,E....]�ı�����ɫ
                setBackgroundResource(R.drawable.v2_sortlistview_sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }

                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * ���⹫���ķ���
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * �ӿ�
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
