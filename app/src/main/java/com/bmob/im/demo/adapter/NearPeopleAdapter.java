package com.bmob.im.demo.adapter;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xy.fy.main.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * �������
 *
 * @author smile
 * @ClassName: BlackListAdapter
 * @Description: TODO
 * @date 2014-6-24 ����5:27:14
 */
public class NearPeopleAdapter extends BaseListAdapter<User> {

    private static final double EARTH_RADIUS = 6378137;

    public NearPeopleAdapter(Context context, List<User> list) {
        super(context, list);
        // TODO Auto-generated constructor stub
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * �������侭γ����꣨doubleֵ���������������룬
     *
     * @return ���룺��λΪ��
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1,
                                             double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    @Override
    public View bindView(int arg0, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_near_people, null);
        }
        final User contract = getList().get(arg0);
        TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
        TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
        TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        String avatar = contract.getAvatar();
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar,
                    ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }
        BmobGeoPoint location = contract.getLocation();
        String currentLat = CustomApplcation.getInstance().getLatitude();
        String currentLong = CustomApplcation.getInstance().getLongtitude();
        if (location != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat), Double.parseDouble(currentLong), contract.getLocation().getLatitude(),
                    contract.getLocation().getLongitude());
            tv_distance.setText(String.valueOf(distance) + "��");
        } else {
            tv_distance.setText("δ֪");
        }
        tv_name.setText(contract.getUsername());
        tv_logintime.setText("����¼ʱ��:" + contract.getUpdatedAt());
        return convertView;
    }

}
