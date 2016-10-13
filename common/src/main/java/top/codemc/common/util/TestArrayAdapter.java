package top.codemc.common.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TestArrayAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String[] mStringArray;
    private int resource;
    private int width;

    public TestArrayAdapter(int resource, Context context,
                            String[] stringArray, int width) {
        super(context, android.R.layout.simple_spinner_item, stringArray);
        mContext = context;
        mStringArray = stringArray;
        this.resource = resource;
        this.width = width;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // �޸�Spinnerչ�����������ɫ
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(resource, parent, false);
            convertView.setBackgroundColor(Color.WHITE);
        }

        // �˴�text1��SpinnerĬ�ϵ�������ʾ���ֵ�TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(14f);
        tv.setTextColor(Color.GRAY);

        return convertView;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // �޸�Spinnerѡ������������ɫ
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        // �˴�text1��SpinnerĬ�ϵ�������ʾ���ֵ�TextView
        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText(mStringArray[position]);
        tv.setTextSize(14f);
        tv.setTextColor(Color.GRAY);
        return convertView;
    }

}
