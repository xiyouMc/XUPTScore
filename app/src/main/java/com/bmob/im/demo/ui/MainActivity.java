package com.bmob.im.demo.ui;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.ui.fragment.SettingsFragment;
import com.xy.fy.main.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

/**
 * ��½
 *
 * @author smile
 * @ClassName: MainActivity
 * @Description: TODO
 * @date 2014-5-29 ����2:45:35
 */
public class MainActivity extends ActivityBase implements EventListener {

    private static long firstTime;
    ImageView iv_recent_tips, iv_contact_tips, iv_bukao_tips;// ��Ϣ��ʾ
    NewBroadcastReceiver newReceiver;
    TagBroadcastReceiver userReceiver;
    private Button[] mTabs;
    private ContactFragment contactFragment;
    private RecentFragment recentFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private View menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        BmobConstants.IS_STARTED = 1;
        // ������ʱ�����񣨵�λΪ�룩-���������̨�Ƿ���δ������Ϣ���еĻ���ȡ����
        BmobChat.getInstance(this).startPollService(30);
        // �����㲥������
        initNewMessageBroadCast();
        initTagMessageBroadCast();
        initView();
        initTab();
    }

    private void initView() {
        mTabs = new Button[3];
        mTabs[0] = (Button) findViewById(R.id.btn_message);
        mTabs[1] = (Button) findViewById(R.id.btn_contract);
        mTabs[2] = (Button) findViewById(R.id.btn_set);
        iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
        iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);

        menuView = View.inflate(getApplicationContext(), R.layout.menu, null);

        iv_bukao_tips = (ImageView) menuView.findViewById(R.id.iv_bukao_tips);
        // �ѵ�һ��tab��Ϊѡ��״̬
        mTabs[0].setSelected(true);
    }

    private void initTab() {
        contactFragment = new ContactFragment();
        recentFragment = new RecentFragment();
        settingFragment = new SettingsFragment();
        fragments = new Fragment[]{recentFragment, contactFragment, settingFragment};
        // �����ʾ��һ��fragment
        try {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment)
                    .add(R.id.fragment_container, contactFragment).hide(contactFragment).show(recentFragment)
                    .commitAllowingStateLoss();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    /**
     * button����¼�
     */
    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_message:
                index = 0;
                break;
            case R.id.btn_contract:
                index = 1;
                break;
            case R.id.btn_set:
                index = 2;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // �ѵ�ǰtab��Ϊѡ��״̬
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // СԲ����ʾ
        if (BmobDB.create(this).hasUnReadMsg()) {
            iv_recent_tips.setVisibility(View.VISIBLE);
            iv_bukao_tips.setVisibility(View.VISIBLE);
        } else {
            iv_bukao_tips.setVisibility(View.GONE);
            iv_recent_tips.setVisibility(View.GONE);
        }
        if (BmobDB.create(this).hasNewInvite()) {
            iv_contact_tips.setVisibility(View.VISIBLE);
            iv_recent_tips.setVisibility(View.VISIBLE);
        } else {
            iv_contact_tips.setVisibility(View.GONE);
            iv_recent_tips.setVisibility(View.GONE);
        }
        MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
        menuView.invalidate();
        // ���
        MyMessageReceiver.mNewNum = 0;

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// ȡ��������͵���Ϣ
    }

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        refreshNewMsg(message);
    }

    /**
     * ˢ�½���
     *
     * @Title: refreshNewMsg @Description: TODO @param @param message @return void @throws
     */
    private void refreshNewMsg(BmobMsg message) {
        // ������ʾ
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);
        iv_recent_tips.setVisibility(View.VISIBLE);
        // ҲҪ�洢����
        if (message != null) {
            BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true, message);
        }
        if (currentTabIndex == 0) {
            // ��ǰҳ�����Ϊ�Ựҳ�棬ˢ�´�ҳ��
            if (recentFragment != null) {
                recentFragment.refresh();
                menuView.invalidate();
            }
        }
    }

    private void initNewMessageBroadCast() {
        // ע�������Ϣ�㲥
        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        // ���ȼ�Ҫ����ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    private void initTagMessageBroadCast() {
        // ע�������Ϣ�㲥
        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        // ���ȼ�Ҫ����ChatActivity
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        // TODO Auto-generated method stub
        refreshInvite(message);
    }

    /**
     * ˢ�º�������
     *
     * @Title: notifyAddUser @Description: TODO @param @param message @return void @throws
     */
    private void refreshInvite(BmobInvitation message) {
        boolean isAllow = CustomApplcation.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplcation.getInstance().getMediaPlayer().start();
        }
        iv_contact_tips.setVisibility(View.VISIBLE);
        iv_recent_tips.setVisibility(View.VISIBLE);
        if (currentTabIndex == 1) {
            if (contactFragment != null) {
                contactFragment.refresh();
                menuView.invalidate();
            }
        } else {
            // ͬʱ����֪ͨ
            String tickerText = message.getFromname() + "������Ӻ���";
            boolean isAllowVibrate = CustomApplcation.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate,
                    R.drawable.default_head_photo, tickerText, message.getFromname(), tickerText.toString(),
                    NewFriendActivity.class);
        }
    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
    }

    /**
     * �������η��ؼ���˳�
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // if (firstTime + 2000 > System.currentTimeMillis()) {
        super.onBackPressed();

    /*
     * } else { ShowToast("�ٰ�һ���˳�����"); } firstTime = System.currentTimeMillis();
     */
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            unregisterReceiver(newReceiver);
        } catch (Exception e) {
        }
        try {
            unregisterReceiver(userReceiver);
        } catch (Exception e) {
        }
        // ȡ��ʱ������
        BmobChat.getInstance(this).stopPollService();
    }

    /**
     * ����Ϣ�㲥������
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // ˢ�½���
            refreshNewMsg(null);
            // �ǵðѹ㲥���ս��
            abortBroadcast();
        }
    }

    /**
     * ��ǩ��Ϣ�㲥������
     */
    private class TagBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            // �ǵðѹ㲥���ս��
            abortBroadcast();
        }
    }

}
