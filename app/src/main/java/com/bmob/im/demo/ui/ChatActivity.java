package com.bmob.im.demo.ui;

import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.adapter.EmoViewPagerAdapter;
import com.bmob.im.demo.adapter.EmoteAdapter;
import com.bmob.im.demo.adapter.MessageChatAdapter;
import com.bmob.im.demo.adapter.NewRecordPlayClickListener;
import com.bmob.im.demo.bean.FaceText;
import com.bmob.im.demo.config.BmobConstants;
import com.bmob.im.demo.util.CommonUtils;
import com.bmob.im.demo.util.FaceTextUtils;
import com.bmob.im.demo.view.EmoticonsEditText;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.bmob.im.demo.view.xlist.XListView;
import com.bmob.im.demo.view.xlist.XListView.IXListViewListener;
import com.xy.fy.main.R;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.PushListener;

/**
 * �������
 *
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-3 ����4:33:11
 */

/**
 * @ClassName: ChatActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-23 ����3:28:49
 */
public class ChatActivity extends ActivityBase implements OnClickListener,
        IXListViewListener, EventListener {

    public static final int NEW_MESSAGE = 0x001;// �յ���Ϣ
    private static int MsgPagerNum;
    XListView mListView;
    EmoticonsEditText edit_user_comment;
    String targetId = "";
    BmobChatUser targetUser;
    // �����й�
    RelativeLayout layout_record;
    TextView tv_voice_tips;
    ImageView iv_record;
    BmobRecordManager recordManager;
    Toast toast;
    List<FaceText> emos;
    MessageChatAdapter mAdapter;
    NewBroadcastReceiver receiver;
    private Button btn_chat_emo, btn_chat_send, btn_chat_add, btn_chat_keyboard, btn_speak, btn_chat_voice;
    private LinearLayout layout_more, layout_emo, layout_add;
    private ViewPager pager_emo;
    private TextView tv_picture, tv_camera;
    private Drawable[] drawable_Anims;// ��Ͳ����
    private String localCameraPath = "";// ���պ�õ���ͼƬ��ַ
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NEW_MESSAGE) {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))// ����ǵ�ǰ��������������Ϣ��������
                    return;
                mAdapter.add(m);
                // ��λ
                mListView.setSelection(mAdapter.getCount() - 1);
                //ȡ��ǰ��������δ����ʾ
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        manager = BmobChatManager.getInstance(this);
        MsgPagerNum = 0;
        // ��װ�������
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();
//		BmobLog.i("�������" + targetUser.getUsername() + ",targetId = "
//				+ targetId);
        //ע��㲥������
        initNewMessageBroadCast();
        initView();
    }

    private void initRecordManager() {
        // ������ع�����
        recordManager = BmobRecordManager.getInstance(this);
        // ����������С����--�����￪���߿����Լ�ʵ�֣���ʣ��10������µĸ��û�����ʾ������΢�ŵ���������
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

            @Override
            public void onVolumnChanged(int value) {
                // TODO Auto-generated method stub
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                // TODO Auto-generated method stub
                BmobLog.i("voice", "��¼������:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1���ӽ�������Ϣ
                    // ��Ҫ���ð�ť
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    // ȡ��¼����
                    layout_record.setVisibility(View.INVISIBLE);
                    // ����������Ϣ
                    sendVoiceMessage(localPath, recordTime);
                    //��Ϊ�˷�ֹ����¼��ʱ��󣬻�෢һ��������ȥ�������
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                } else {

                }
            }
        });
    }

    private void initView() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mListView = (XListView) findViewById(R.id.mListView);
        initTopBarForLeft("��" + targetUser.getUsername() + "�Ի�");
        initBottomView();
        initXListView();
        initVoiceView();
    }

    /**
     * ��ʼ����������
     *
     * @Title: initVoiceView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initVoiceView() {
        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }

    /**
     * ����������Ϣ
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendVoiceMessage(String local, int length) {
        manager.sendVoiceMessage(targetUser, local, length,
                new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                        refreshMessage(msg);
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int error, String arg1) {
                        // TODO Auto-generated method stub
                        ShowLog("�ϴ�����ʧ�� -->arg1��" + arg1);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    /**
     * ��ʾ¼��ʱ���̵�Toast
     *
     * @Title: showShortToast
     * @return void
     * @throws
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(50);
        return toast;
    }

    /**
     * ��ʼ������������Դ
     * @Title: initVoiceAnimRes
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                getResources().getDrawable(R.drawable.chat_icon_voice2),
                getResources().getDrawable(R.drawable.chat_icon_voice3),
                getResources().getDrawable(R.drawable.chat_icon_voice4),
                getResources().getDrawable(R.drawable.chat_icon_voice5),
                getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    /**
     * ������Ϣ��ʷ������ݿ��ж���
     */
    private List<BmobMsg> initMsgData() {
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, MsgPagerNum);
        return list;
    }

    /**
     * ����ˢ��
     * @Title: initOrRefresh
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initOrRefresh() {
        if (mAdapter != null) {
            if (MyMessageReceiver.mNewNum != 0) {// ���ڸ��µ���������������ڼ�������Ϣ����ʱ�ٻص�����ҳ���ʱ����Ҫ��ʾ��������Ϣ
                int news = MyMessageReceiver.mNewNum;//�п��������ڼ䣬����N����Ϣ,�����Ҫ������ʾ�ڽ�����
                int size = initMsgData().size();
                for (int i = (news - 1); i >= 0; i--) {
                    mAdapter.add(initMsgData().get(size - (i + 1)));// ������һ����Ϣ��������ʾ
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }

    private void initAddView() {
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_camera = (TextView) findViewById(R.id.tv_camera);
        tv_picture.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
    }

    private void initBottomView() {
        // �����
        btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
        btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
        btn_chat_add.setOnClickListener(this);
        btn_chat_emo.setOnClickListener(this);
        // ���ұ�
        btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
        btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
        btn_chat_voice.setOnClickListener(this);
        btn_chat_keyboard.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        // ������
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (LinearLayout) findViewById(R.id.layout_add);
        initAddView();
        initEmoView();

        // ���м�
        // ������
        btn_speak = (Button) findViewById(R.id.btn_speak);
        // �����
        edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
        edit_user_comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(s)) {
                    btn_chat_send.setVisibility(View.VISIBLE);
                    btn_chat_keyboard.setVisibility(View.GONE);
                    btn_chat_voice.setVisibility(View.GONE);
                } else {
                    if (btn_chat_voice.getVisibility() != View.VISIBLE) {
                        btn_chat_voice.setVisibility(View.VISIBLE);
                        btn_chat_send.setVisibility(View.GONE);
                        btn_chat_keyboard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

    }

    /**
     * ��ʼ�����鲼��
     * @Title: initEmoView
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    private void initEmoView() {
        pager_emo = (ViewPager) findViewById(R.id.pager_emo);
        emos = FaceTextUtils.faceTexts;

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < 2; ++i) {
            views.add(getGridView(i));
        }
        pager_emo.setAdapter(new EmoViewPagerAdapter(views));
    }

    private View getGridView(final int i) {
        View view = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        if (i == 0) {
            list.addAll(emos.subList(0, 21));
        } else if (i == 1) {
            list.addAll(emos.subList(21, emos.size()));
        }
        final EmoteAdapter gridAdapter = new EmoteAdapter(ChatActivity.this,
                list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                FaceText name = (FaceText) gridAdapter.getItem(position);
                String key = name.text.toString();
                try {
                    if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
                        int start = edit_user_comment.getSelectionStart();
                        CharSequence content = edit_user_comment.getText()
                                .insert(start, key);
                        edit_user_comment.setText(content);
                        // ��λ���λ��
                        CharSequence info = edit_user_comment.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText,
                                    start + key.length());
                        }
                    }
                } catch (Exception e) {

                }

            }
        });
        return view;
    }

    private void initXListView() {
        // ���Ȳ�������ظ��
        mListView.setPullLoadEnable(false);
        // ��������
        mListView.setPullRefreshEnable(true);
        // ���ü�����
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mListView.setDividerHeight(0);
        // �������
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.VISIBLE);
                btn_chat_keyboard.setVisibility(View.GONE);
                btn_chat_send.setVisibility(View.GONE);
                return false;
            }
        });

        // �ط���ť�ĵ���¼�
        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
                new MessageChatAdapter.onInternalClickListener() {

                    @Override
                    public void OnClickListener(View parentV, View v,
                                                Integer position, Object values) {
                        // �ط���Ϣ
                        showResendDialog(parentV, v, values);
                    }
                });
    }

    /**
     * ��ʾ�ط���ť showResendDialog
     * @Title: showResendDialog
     * @Description: TODO
     * @param @param recent
     * @return void
     * @throws
     */
    public void showResendDialog(final View parentV, View v, final Object values) {
        DialogTips dialog = new DialogTips(this, "ȷ���ط�����Ϣ", "ȷ��", "ȡ��", "��ʾ",
                true);
        // ���óɹ��¼�
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE
                        || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// ͼƬ���������͵Ĳ���
                    resendFileMsg(parentV, values);
                } else {
                    resendTextMsg(parentV, values);
                }
                dialogInterface.dismiss();
            }
        });
        // ��ʾȷ�϶Ի���
        dialog.show();
        dialog = null;
    }

    /**
     * �ط��ı���Ϣ
     */
    private void resendTextMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(
                targetUser, (BmobMsg) values, new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ShowLog("���ͳɹ�");
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.VISIBLE);
                        ((TextView) parentV.findViewById(R.id.tv_send_status))
                                .setText("�ѷ���");
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ShowLog("����ʧ��:" + arg1);
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * �ط�ͼƬ��Ϣ
     * @Title: resendImageMsg
     * @Description: TODO
     * @param @param parentV
     * @param @param values
     * @return void
     * @throws
     */
    private void resendFileMsg(final View parentV, final Object values) {
        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(
                targetUser, (BmobMsg) values, new UploadListener() {

                    @Override
                    public void onStart(BmobMsg msg) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.INVISIBLE);
                        if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.GONE);
                            parentV.findViewById(R.id.tv_voice_length)
                                    .setVisibility(View.VISIBLE);
                        } else {
                            parentV.findViewById(R.id.tv_send_status)
                                    .setVisibility(View.VISIBLE);
                            ((TextView) parentV
                                    .findViewById(R.id.tv_send_status))
                                    .setText("�ѷ���");
                        }
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ((BmobMsg) values)
                                .setStatus(BmobConfig.STATUS_SEND_FAIL);
                        parentV.findViewById(R.id.progress_load).setVisibility(
                                View.INVISIBLE);
                        parentV.findViewById(R.id.iv_fail_resend)
                                .setVisibility(View.VISIBLE);
                        parentV.findViewById(R.id.tv_send_status)
                                .setVisibility(View.INVISIBLE);
                    }
                });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.edit_user_comment:// ����ı������
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_chat_emo:// ���Ц��ͼ��
                if (layout_more.getVisibility() == View.GONE) {
                    showEditState(true);
                } else {
                    if (layout_add.getVisibility() == View.VISIBLE) {
                        layout_add.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_add:// ��Ӱ�ť-��ʾͼƬ�����ա�λ��
                if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    hideSoftInputView();
                } else {
                    if (layout_emo.getVisibility() == View.VISIBLE) {
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_voice:// ������ť
                edit_user_comment.setVisibility(View.GONE);
                layout_more.setVisibility(View.GONE);
                btn_chat_voice.setVisibility(View.GONE);
                btn_chat_keyboard.setVisibility(View.VISIBLE);
                btn_speak.setVisibility(View.VISIBLE);
                hideSoftInputView();
                break;
            case R.id.btn_chat_keyboard:// ���̰�ť������͵������̲����ص�������ť
                showEditState(false);
                break;
            case R.id.btn_chat_send:// �����ı�
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals("")) {
                    ShowToast("�����뷢����Ϣ!");
                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected) {
                    ShowToast(R.string.network_tips);
                    // return;
                }
                // ��װBmobMessage����
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                // Ĭ�Ϸ�����ɣ�����ݱ��浽������Ϣ������Ự����
                manager.sendTextMessage(targetUser, message);
                // ˢ�½���
                refreshMessage(message);

                break;
            case R.id.tv_camera:// ����
                selectImageFromCamera();
                break;
            case R.id.tv_picture:// ͼƬ
                selectImageFromLocal();
                break;
            default:
                break;
        }
    }

    /**
     * ����������� startCamera
     *
     * @Title: startCamera
     * @throws
     */
    public void selectImageFromCamera() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis())
                + ".jpg");
        localCameraPath = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent,
                BmobConstants.REQUESTCODE_TAKE_CAMERA);
    }

    /**
     * ѡ��ͼƬ
     * @Title: selectImage
     * @Description: TODO
     * @param
     * @return void
     * @throws
     */
    public void selectImageFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BmobConstants.REQUESTCODE_TAKE_CAMERA:// ��ȡ��ֵ��ʱ����ϴ�path·���µ�ͼƬ��������
                    ShowLog("����ͼƬ�ĵ�ַ��" + localCameraPath);
                    sendImageMessage(localCameraPath);
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCAL:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(
                                    selectedImage, null, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex("_data");
                            String localSelectPath = cursor.getString(columnIndex);
                            cursor.close();
                            if (localSelectPath == null
                                    || localSelectPath.equals("null")) {
                                ShowToast("�Ҳ�������Ҫ��ͼƬ");
                                return;
                            }
                            sendImageMessage(localSelectPath);
                        }
                    }
                    break;
                case BmobConstants.REQUESTCODE_TAKE_LOCATION:// ����λ��
                    double latitude = data.getDoubleExtra("x", 0);// ά��
                    double longtitude = data.getDoubleExtra("y", 0);// ����
                    String address = data.getStringExtra("address");
                    if (address != null && !address.equals("")) {
                        sendLocationMessage(address, latitude, longtitude);
                    } else {
                        ShowToast("�޷���ȡ�����λ����Ϣ!");
                    }

                    break;
            }
        }
    }

    /**
     * ����λ����Ϣ
     * @Title: sendLocationMessage
     * @Description: TODO
     * @param @param address
     * @param @param latitude
     * @param @param longtitude
     * @return void
     * @throws
     */
    private void sendLocationMessage(String address, double latitude,
                                     double longtitude) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        // ��װBmobMessage����
        BmobMsg message = BmobMsg.createLocationSendMsg(this, targetId,
                address, latitude, longtitude);
        // Ĭ�Ϸ�����ɣ�����ݱ��浽������Ϣ������Ự����
        manager.sendTextMessage(targetUser, message);
        // ˢ�½���
        refreshMessage(message);
    }

    /**
     * Ĭ�����ϴ�����ͼƬ��֮�����ʾ���� sendImageMessage
     * @Title: sendImageMessage
     * @Description: TODO
     * @param @param localPath
     * @return void
     * @throws
     */
    private void sendImageMessage(String local) {
        if (layout_more.getVisibility() == View.VISIBLE) {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        manager.sendImageMessage(targetUser, local, new UploadListener() {

            @Override
            public void onStart(BmobMsg msg) {
                // TODO Auto-generated method stub
                ShowLog("��ʼ�ϴ�onStart��" + msg.getContent() + ",״̬��"
                        + msg.getStatus());
                refreshMessage(msg);
            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String arg1) {
                // TODO Auto-generated method stub
                ShowLog("�ϴ�ʧ�� -->arg1��" + arg1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * ����Ƿ���Ц������ʾ�ı�������״̬
     * @Title: showEditState
     * @Description: TODO
     * @param @param isEmo: ����������ֺͱ���
     * @return void
     * @throws
     */
    private void showEditState(boolean isEmo) {
        edit_user_comment.setVisibility(View.VISIBLE);
        btn_chat_keyboard.setVisibility(View.GONE);
        btn_chat_voice.setVisibility(View.VISIBLE);
        btn_speak.setVisibility(View.GONE);
        edit_user_comment.requestFocus();
        if (isEmo) {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            hideSoftInputView();
        } else {
            layout_more.setVisibility(View.GONE);
            showSoftInputView();
        }
    }

    // ��ʾ�����
    public void showSoftInputView() {
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .showSoftInput(edit_user_comment, 0);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // ����Ϣ�������ˢ�½���
        initOrRefresh();
        MyMessageReceiver.ehList.add(this);// �������͵���Ϣ
        // �п��������ڼ䣬������������֪ͨ������ʱ����Ҫ���֪ͨ�����δ����Ϣ��
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        //�����Ϣδ����-���Ҫ��ˢ��֮��
        MyMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// �������͵���Ϣ
        // ֹͣ¼��
        if (recordManager.isRecording()) {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        // ֹͣ����¼��
        if (NewRecordPlayClickListener.isPlaying
                && NewRecordPlayClickListener.currentPlayListener != null) {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }

    }

    private void initNewMessageBroadCast() {
        // ע�������Ϣ�㲥
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        //���ù㲥�����ȼ������Mainacitivity,���������Ϣ����ʱ�������chatҳ�棬ֱ����ʾ��Ϣ��������ʾ��Ϣδ��
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * ˢ�½���
     * @Title: refreshMessage
     * @Description: TODO
     * @param @param message
     * @return void
     * @throws
     */
    private void refreshMessage(BmobMsg msg) {
        // ���½���
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (!isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation invite) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
        // �˴�Ӧ�ù��˵����Ǻ͵�ǰ�û�������Ļ�ִ��Ϣ�����ˢ��
        if (conversionId.split("&")[1].equals(targetId)) {
            // �޸Ľ�����ָ����Ϣ���Ķ�״̬
            for (BmobMsg msg : mAdapter.getList()) {
                if (msg.getConversationId().equals(conversionId)
                        && msg.getMsgTime().equals(msgTime)) {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onRefresh() {
        // TODO Auto-generated method stub
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                MsgPagerNum++;
                int total = BmobDB.create(ChatActivity.this).queryChatTotalCount(targetId);
                BmobLog.i("��¼����" + total);
                int currents = mAdapter.getCount();
                if (total <= currents) {
                    ShowToast("�����¼��������Ŷ!");
                } else {
                    List<BmobMsg> msgList = initMsgData();
                    mAdapter.setList(msgList);
                    mListView.setSelection(mAdapter.getCount() - currents - 1);
                }
                mListView.stopRefresh();
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_more.getVisibility() == 0) {
                layout_more.setVisibility(View.GONE);
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        hideSoftInputView();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
        }

    }

    /**
     * ����˵��
     * @ClassName: VoiceTouchListen
     * @Description: TODO
     * @author smile
     * @date 2014-7-1 ����6:10:16
     */
    class VoiceTouchListen implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSdCard()) {
                        ShowToast("����������Ҫsdcard֧�֣�");
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        // ��ʼ¼��
                        recordManager.startRecording(targetId);
                    } catch (Exception e) {
                    }
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        tv_voice_tips
                                .setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try {
                        if (event.getY() < 0) {// ����¼��
                            recordManager.cancelRecording();
                            BmobLog.i("voice", "����������");
                        } else {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // ���������ļ�
                                BmobLog.i("voice", "��������");
                                sendVoiceMessage(
                                        recordManager.getRecordFilePath(targetId),
                                        recordTime);
                            } else {// ¼��ʱ���̣�����ʾ¼����̵���ʾ
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                default:
                    return false;
            }
        }
    }

    /**
     * ����Ϣ�㲥������
     *
     */
    private class NewBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            // �յ�����㲥��ʱ��message�Ѿ�����Ϣ���У���ֱ�ӻ�ȡ
            BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
            if (!from.equals(targetId))// ����ǵ�ǰ��������������Ϣ��������
                return;
            //��ӵ���ǰҳ��
            mAdapter.add(msg);
            // ��λ
            mListView.setSelection(mAdapter.getCount() - 1);
            //ȡ��ǰ��������δ����ʾ
            BmobDB.create(ChatActivity.this).resetUnread(targetId);
            // �ǵðѹ㲥���ս��
            abortBroadcast();
        }
    }

}
