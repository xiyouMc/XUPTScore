package com.xy.fy.view;

import com.xy.fy.main.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * �Զ���ListView
 *
 * @author Administrator
 */
public class CustomListView extends ListView implements OnScrollListener {

    private final static int RELEASE_To_REFRESH = 0; // �ͷ�ˢ��״̬
    private final static int PULL_To_REFRESH = 1; // ����ˢ��״̬
    private final static int REFRESHING = 2; // ����ˢ��״̬
    private final static int DONE = 3; // ���״̬
    private final static int LOADING = 4; // ���ڼ������״̬
    private final static int RATIO = 3; // ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
    private LinearLayout headView;// ���ͷ����ͼ
    private ImageView imageArrow;// ����ͷ
    private ProgressBar progressHead;// ͷ�����Ȧ
    private ProgressBar progressFoot;// β�����Ȧ
    private int headContentHeight;
    private TextView butMore;// ���ظ��
    private RotateAnimation animation;// ����Ч��
    private RotateAnimation reverseAnimation;
    private int state;// ״̬
    private boolean isRefreshable;// �Ƿ����ˢ��
    private int firstItemIndex; // ListView�ĵ�һ��Item
    private boolean isRecored;// ���ڱ�֤startY��ֵ��һ�������touch�¼���ֻ����¼һ��
    private int startY; // ��ʼ��Y���
    private boolean isBack; // �Ƿ񷵻�
    private OnRefreshListener refreshListener;
    @SuppressWarnings("unused")
    private LayoutInflater inflater;

    public CustomListView(Context context) {
        super(context);
        this.init(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    /**
     * ��ʼ�����
     *
     * @param context ������
     */
    public void init(Context context) {
        inflater = LayoutInflater.from(context);
        this.headView = (LinearLayout) inflate(context, R.layout.pull_down_head_refresh, null);
        this.imageArrow = (ImageView) headView.findViewById(R.id.imageHeadArrow);
        this.progressHead = (ProgressBar) headView.findViewById(R.id.progressBarHead);

        measureView(headView); // ����headView�Ĵ�С
        headContentHeight = headView.getMeasuredHeight(); // headView�ĸ߶�
        headView.setPadding(0, -1 * headContentHeight, 0, 0); // ��headView����ListView���Ϸ�-
        headView.invalidate(); // �ػ����
        addHeaderView(headView, null, false); // ��headView��ӵ�ListView���Ϸ�

        LinearLayout footView = (LinearLayout) inflate(context, R.layout.pull_up_foot_more, null);
        this.butMore = (TextView) footView.findViewById(R.id.butMore);
        this.progressFoot = (ProgressBar) footView.findViewById(R.id.progressBarFoot);
        this.progressFoot.setVisibility(View.GONE);
        addFooterView(footView);

        setOnScrollListener(this); // ���ü�����

        animation = new RotateAnimation(0, -180, // ����Ч��
                RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE; // ��ʼ��state��isRefreshable
        isRefreshable = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isRefreshable) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN: // ����ָ����ʱ����¼startY,��isRecored��־Ϊ�Ѽ�¼
                    if (firstItemIndex == 0 && !isRecored) {
                        isRecored = true;
                        startY = (int) ev.getY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int temY = (int) ev.getY(); // ��¼��ǰ��Y���
                    if (!isRecored && firstItemIndex == 0) { // ����û��¼startY�������¼
                        isRecored = true;
                        startY = temY;
                    }
                    if (state != REFRESHING && state != LOADING && isRecored) {

                        if (state == DONE) { // ��ǰ״̬ΪDONE
                            if ((temY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                        }
                        if (state == PULL_To_REFRESH) {
                            setSelection(0);

                            // ������headView��ʾ�����������ͷ�ˢ��״̬
                            if ((temY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                            }
                            // ���Ƶ�����
                            else if (temY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                        if (state == RELEASE_To_REFRESH) {
                            setSelection(0);
                            // �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
                            if (((temY - startY) / RATIO < headContentHeight) && (temY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                            }
                            // һ�����Ƶ�����
                            else if (temY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                            }
                        }
                    }
                    // ����headView��size
                    if (state == PULL_To_REFRESH) {
                        headView.setPadding(0, -1 * headContentHeight + (temY - startY) / RATIO, 0, 0);
                    }
                    // ����headView��paddingTop
                    if (state == RELEASE_To_REFRESH) {
                        headView.setPadding(0, (temY - startY) / RATIO - headContentHeight, 0, 0);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            // ʲô������
                        }
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                        }
                    }
                    isRecored = false;
                    isBack = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * �������
     */
    public void refreshComplete() {
        state = DONE;
        changeHeaderViewByState();
    }

    /**
     * ˢ�¼�����
     */
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    /**
     * ��������
     */
    public void setOnMoreListener(OnMoreButtonListener moreButtonListener) {
        butMore.setOnClickListener(moreButtonListener);
    }

    /**
     * ��ʼ���
     */
    public void start() {
        butMore.setVisibility(View.GONE);
        progressFoot.setVisibility(View.VISIBLE);
    }

    /**
     * ������
     */
    public void finish() {
        butMore.setVisibility(View.VISIBLE);
        progressFoot.setVisibility(View.GONE);
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    // ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                imageArrow.setVisibility(View.VISIBLE);
                progressHead.setVisibility(View.GONE);
                imageArrow.clearAnimation();
                imageArrow.startAnimation(animation);
                break;
            case PULL_To_REFRESH:
                progressHead.setVisibility(View.GONE);
                imageArrow.clearAnimation();
                imageArrow.setVisibility(View.VISIBLE);
                // ����RELEASE_To_REFRESH״̬ת������
                if (isBack) {
                    isBack = false;
                    imageArrow.clearAnimation();
                    imageArrow.startAnimation(reverseAnimation);
                }
                break;
            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                progressHead.setVisibility(View.VISIBLE);
                imageArrow.clearAnimation();
                imageArrow.setVisibility(View.GONE);
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                progressHead.setVisibility(View.GONE);
                imageArrow.clearAnimation();
                imageArrow.setImageResource(R.drawable.pull_down_refresh_arrow);
                break;
        }
    }

    /**
     * ����ͷ������
     */
    @SuppressWarnings("deprecation")
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstItemIndex = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public interface OnMoreButtonListener extends OnClickListener {
    } // ��ఴť������

    public interface OnRefreshListener { // ˢ�¼�����
        public void onRefresh();
    }
}
