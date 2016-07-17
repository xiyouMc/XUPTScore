package com.bmob.im.demo.adapter;

import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
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

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;

/**
 * @author smile
 * @ClassName: AddFriendAdapter
 * @Description: TODO
 * @date 2014-6-25 ����10:56:33
 */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {

    public AddFriendAdapter(Context context, List<BmobChatUser> list) {
        super(context, list);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View bindView(int arg0, View convertView, ViewGroup arg2) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }
        final BmobChatUser contract = getList().get(arg0);
        TextView name = ViewHolder.get(convertView, R.id.name);
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

        final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

        String avatar = contract.getAvatar();

        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
        } else {
            iv_avatar.setImageResource(R.drawable.default_head);
        }

        name.setText(contract.getUsername());
        btn_add.setText("���");
        btn_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                final ProgressDialog progress = new ProgressDialog(mContext);
                progress.setMessage("�������...");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
                //����tag����
                BmobChatManager.getInstance(mContext).sendTagMessage(MsgTag.ADD_CONTACT, contract.getObjectId(), new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        btn_add.setText("�����");
                        ShowToast("��������ɹ����ȴ�Է���֤!");
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        ShowToast("��������ʧ�ܣ����������!");
                        ShowLog("��������ʧ��:" + arg1);
                    }
                });
            }
        });
        return convertView;
    }

}
