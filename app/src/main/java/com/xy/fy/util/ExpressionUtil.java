package com.xy.fy.util;

import com.xy.fy.main.R;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpressionUtil {

    public static final HashMap<String, Integer> all = getAllHashMap();
    private Context context;

    /**
     * ����Adapter�Ĺ��췽��
     */
    public ExpressionUtil(Context context) {
        this.context = context;
    }

    /**
     * �õ���������е�ӳ��
     */
    public static HashMap<String, Integer> getAllHashMap() {
        Class<com.xy.fy.main.R.drawable> cls = R.drawable.class;
        HashMap<String, Integer> nameToId = new HashMap<String, Integer>();
        // �õ���ǰҳ�����е�ӳ��
        for (int i = 1; i <= 140; i++) {
            String name = "expression" + i;// Ū���������ֻ��Ϊ�˵õ�Id���������
            try {
                int id = cls.getDeclaredField(name).getInt(null);
                nameToId.put(name, id);// ע�������ŵĲ���name��ΪҪ�ļ�ֵ��
            } catch (Exception e) {
                System.out.println("�Ҳ���Idѽ");
                e.printStackTrace();
            }
        }
        return nameToId;
    }

    /**
     * ͨ�������ҵ���Դ��ID
     */
    @SuppressWarnings("unused")
    private int getResourseIdByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "drawable",
                "com.xy.fy.main");
        return id;
    }

    /**
     * �õ���ǰ����ҳ��������
     */
    public ArrayList<HashMap<String, Integer>> getCurrentPageAdapter(
            int pageNumber) {
        Class<com.xy.fy.main.R.drawable> cls = R.drawable.class;
        int start = pageNumber * 35;
        // �����￪ʼ��ÿҳ��35�����飬5��7�У�allNameToId���һ��
        ArrayList<HashMap<String, Integer>> allNameToId = new ArrayList<HashMap<String, Integer>>();
        // �õ���ǰҳ�����е�ӳ��
        for (int i = 1; i <= 35; i++) {
            HashMap<String, Integer> nameToId = new HashMap<String, Integer>();
            String name = "expression" + (start + i);// Ū���������ֻ��Ϊ�˵õ�Id���������
            try {
                int id = cls.getDeclaredField(name).getInt(null);
                nameToId.put("expression", id);// ע�������ŵĲ���name��ΪҪ�ļ�ֵ��
            } catch (Exception e) {
                System.out.println("�Ҳ���Idѽ");
                e.printStackTrace();
            }
            allNameToId.add(nameToId);
        }
        return allNameToId;
    }

    /**
     * �õ�������
     */
    public SimpleAdapter getAdapter(int pageNumber) {
        SimpleAdapter simpleAdapter = null;
        simpleAdapter = new SimpleAdapter(context,
                getCurrentPageAdapter(pageNumber), R.layout.view_paper_item,
                new String[]{"expression"}, new int[]{R.id.item_image});
        return simpleAdapter;
    }
}
