package com.bmob.im.demo.adapter;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xy.fy.main.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

/**
 * �µĺ�������
 *
 * @author smile
 * @ClassName: NewFriendAdapter
 * @Description: TODO
 * @date 2014-6-9 ����1:26:12
 */
public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {

    public NewFriendAdapter(Context context, List<BmobInvitation> list) {
        super(context, list);
    }

    @Override
    public View bindView(int arg0, View convertView, ViewGroup arg2) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }
        final BmobInvitation msg = getList().get(arg0);
        TextView name = ViewHolder.get(convertView, R.id.name);
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

        final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

        String avatar = msg.getAvatar();

        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }

        int status = msg.getStatus();
        if (status == BmobConfig.INVITE_ADD_NO_VALIDATION
                || status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED) {
            // btn_add.setText("ͬ��");
            // btn_add.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_login_selector));
            // btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_white));
            btn_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    BmobLog.i("���ͬ�ⰴť:" + msg.getFromid());
                    agressAdd(btn_add, msg);
                }
            });
        } else if (status == BmobConfig.INVITE_ADD_AGREE) {
            btn_add.setText("��ͬ��");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
        name.setText(msg.getFromname());

        return convertView;
    }

    /**
     * ��Ӻ��� agressAdd @Title: agressAdd @Description: TODO @param @param btn_add @param @param
     * msg @return void @throws
     */
    private void agressAdd(final Button btn_add, final BmobInvitation msg) {
        final ProgressDialog progress = new ProgressDialog(mContext);
        progress.setMessage("�������...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        try {
            // ͬ����Ӻ���
            BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    btn_add.setText("��ͬ��");
                    btn_add.setBackgroundDrawable(null);
                    btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
                    btn_add.setEnabled(false);
                    // ���浽application�з���Ƚ�
                    CustomApplcation.getInstance()
                            .setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));
                }

                @Override
                public void onFailure(int arg0, final String arg1) {
                    // TODO Auto-generated method stub
                    progress.dismiss();
                    ShowToast("���ʧ��: " + arg1);
                }
            });
        } catch (final Exception e) {
            progress.dismiss();
            ShowToast("���ʧ��: " + e.getMessage());
        }
    }
}
