package com.xy.fy.adapter;

import com.xy.fy.main.R;

import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.singleton.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    private ArrayList<Comment> allComments = null;
    private LayoutInflater inflater = null;

    /**
     * adapter�Ĺ��췽��
     *
     * @param allComments ���Դ
     * @param context     ������
     */
    public CommentAdapter(ArrayList<Comment> allComments, Context context) {
        this.allComments = allComments;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return allComments.size();
    }

    @Override
    public Object getItem(int arg0) {
        return allComments.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.message_comment_item, null);
        }
        ViewHolder holder = new ViewHolder();
        holder.nickname = (TextView) view.findViewById(R.id.nickname);
        holder.content = (TextView) view.findViewById(R.id.content);
        holder.floor = (TextView) view.findViewById(R.id.floor);
        holder.time = (TextView) view.findViewById(R.id.time);
        holder.date = (TextView) view.findViewById(R.id.date);

        holder.nickname.setText(allComments.get(position).getName());
        holder.date.setText(allComments.get(position).getDate());
        holder.floor.setText((position + 1) + "¥");
        holder.time.setText(allComments.get(position).getTime());
        holder.content.setText(allComments.get(position).getContent());
        return view;
    }

    /**
     * ����Comment
     *
     * @param jsonData Ҫ��ӵ����Դ
     */
    public void addComments(String jsonData) {
        if (allComments != null) {
            allComments.addAll(StaticVarUtil.getAllComments(jsonData));
        }
    }

    class ViewHolder {
        TextView nickname;// �ǳ�
        TextView date;// ����
        TextView time;// ʱ��
        TextView content;// ����
        TextView floor;// ¥���
    }
}
