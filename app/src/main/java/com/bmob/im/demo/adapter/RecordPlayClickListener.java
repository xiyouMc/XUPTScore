package com.bmob.im.demo.adapter;

import com.xy.fy.main.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import cn.bmob.im.BmobPlayManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnPlayChangeListener;
import cn.bmob.im.util.BmobLog;

/**
 * ����¼���ļ�--���ã�����ֲ��Ŵ�λ����ʱ��δ���.
 *
 * @author smile
 * @ClassName: RecordPlayClickListener
 * @Description: TODO
 * @date 2014-7-2 ����4:19:35
 */
public class RecordPlayClickListener implements View.OnClickListener {

    public static RecordPlayClickListener currentPlayListener = null;
    static BmobMsg currentMsg = null;// �������������ͬ�����Ĳ���
    BmobMsg message;
    ImageView iv_voice;
    BmobPlayManager playMananger;
    Context context;

    String currentObjectId = "";
    private AnimationDrawable anim = null;

    public RecordPlayClickListener(Context context, BmobMsg msg, ImageView voice) {
        this.iv_voice = voice;
        this.message = msg;
        this.context = context;
        currentMsg = msg;
        currentPlayListener = this;
        currentObjectId = BmobUserManager.getInstance(context)
                .getCurrentUserObjectId();
        playMananger = BmobPlayManager.getInstance(context);
        playMananger.setOnPlayChangeListener(new OnPlayChangeListener() {

            @Override
            public void onPlayStop() {
                // TODO Auto-generated method stub
                currentPlayListener.stopRecordAnimation();
            }

            @Override
            public void onPlayStart() {
                // TODO Auto-generated method stub
                currentPlayListener.startRecordAnimation();
            }
        });
    }

    /**
     * �������Ŷ���
     *
     * @return void
     * @Title: startRecordAnimation
     * @Description: TODO
     */
    public void startRecordAnimation() {
        if (message.getBelongId().equals(currentObjectId)) {
            iv_voice.setImageResource(R.anim.anim_chat_voice_right);
        } else {
            iv_voice.setImageResource(R.anim.anim_chat_voice_left);
        }
        anim = (AnimationDrawable) iv_voice.getDrawable();
        anim.start();
    }

    /**
     * ֹͣ���Ŷ���
     *
     * @return void
     * @Title: stopRecordAnimation
     * @Description: TODO
     */
    public void stopRecordAnimation() {
        if (message.getBelongId().equals(currentObjectId)) {
            iv_voice.setImageResource(R.drawable.voice_left3);
        } else {
            iv_voice.setImageResource(R.drawable.voice_right3);
        }
        if (anim != null) {
            anim.stop();
        }
    }

    @Override
    public void onClick(View arg0) {
        if (playMananger.isPlaying()) {
            playMananger.stopPlayback();
            if (currentMsg != null
                    && currentMsg.hashCode() == message.hashCode()) {// �Ƿ���ͬ��������Ϣ
                currentMsg = null;
                return;
            }
        } else {
            String localPath = message.getContent().split("&")[0];
            BmobLog.i("voice", "���ص�ַ:" + localPath);
            if (message.getBelongId().equals(currentObjectId)) {// ������Լ����͵�������Ϣ���򲥷ű��ص�ַ
                playMananger.playRecording(localPath, true);
            } else {// ������յ�����Ϣ������Ҫ�����غ󲥷�

            }
        }

    }

}