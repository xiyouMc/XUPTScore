package com.bmob.im.demo.adapter.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.im.util.BmobLog;

/**
 * ���������
 *
 * @author smile
 * @ClassName: BaseListAdapter
 * @Description: TODO
 * @date 2014-6-19 ����11:04:01
 */
@SuppressLint("UseSparseArrays")
public abstract class BaseListAdapter<E> extends BaseAdapter {

    public List<E> list;

    public Context mContext;

    public LayoutInflater mInflater;
    // adapter�е��ڲ�����¼�
    public Map<Integer, onInternalClickListener> canClickItem;
    Toast mToast;

    public BaseListAdapter(Context context, List<E> list) {
        super();
        this.mContext = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void add(E e) {
        this.list.add(e);
        notifyDataSetChanged();
    }

    public void addAll(List<E> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = bindView(position, convertView, parent);
        // ���ڲ��������
        addInternalClickListener(convertView, position, list.get(position));
        return convertView;
    }

    public abstract View bindView(int position, View convertView,
                                  ViewGroup parent);

    private void addInternalClickListener(final View itemV, final Integer position, final Object valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemV.findViewById(key);
                final onInternalClickListener inviewListener = canClickItem.get(key);
                if (inView != null && inviewListener != null) {
                    inView.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {
                            inviewListener.OnClickListener(itemV, v, position,
                                    valuesMap);
                        }
                    });
                }
            }
        }
    }

    public void setOnInViewClickListener(Integer key,
                                         onInternalClickListener onClickListener) {
        if (canClickItem == null)
            canClickItem = new HashMap<Integer, onInternalClickListener>();
        canClickItem.put(key, onClickListener);
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (mToast == null) {
                        mToast = Toast.makeText(mContext, text,
                                Toast.LENGTH_SHORT);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                }
            });

        }
    }

    /**
     * ��Log ShowLog
     *
     * @return void
     */
    public void ShowLog(String msg) {
        BmobLog.i(msg);
    }

    public interface onInternalClickListener {
        public void OnClickListener(View parentV, View v, Integer position,
                                    Object values);
    }

}
