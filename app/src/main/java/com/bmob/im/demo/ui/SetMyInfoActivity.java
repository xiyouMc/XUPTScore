package com.bmob.im.demo.ui;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.DialogTips;
import com.mc.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xy.fy.asynctask.UploadFileAsytask;
import com.xy.fy.main.MainActivity;
import com.xy.fy.main.R;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.StaticVarUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * ��������ҳ��
 *
 * @author smile
 * @ClassName: SetMyInfoActivity
 * @Description: TODO
 * @date 2014-6-10 ����2:55:19
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("SimpleDateFormat")
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {

    private static final int PIC = 11;// ͼƬ
    private static final int PHO = 22;// ����
    private static final int RESULT = 33;// ���ؽ��
    public String filePath = "";
    TextView tv_set_name, tv_set_nick, tv_set_gender;
    ImageView iv_set_avator, iv_arraw, iv_nickarraw;
    LinearLayout layout_all;
    Button btn_chat, btn_back, btn_add_friend;
    RelativeLayout layout_head, layout_nick, layout_gender, layout_black_tips;
    String from = "";
    String username = "";
    User user;
    String[] sexs = new String[]{"��", "Ů"};
    RelativeLayout layout_choose;
    RelativeLayout layout_photo;
    PopupWindow avatorPop;
    Bitmap newBitmap;
    boolean isFromCamera = false;// ���������ת
    int degree = 0;
    String path;
    private Bitmap bitmap = null;// �޸�ͷ��

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // ��Ϊ�����ֻ��������������ĵ�����ť����Ҫ�������ص�����Ȼ���ڵ����պ����������ť������setContentView֮ǰ���ò�����Ч
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);
        from = getIntent().getStringExtra("from");// me add other
        username = getIntent().getStringExtra("username");
        initView();
    }

    private void initView() {
        layout_all = (LinearLayout) findViewById(R.id.layout_all);
        iv_set_avator = (ImageView) findViewById(R.id.iv_set_avator);
        iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
        iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
        layout_head = (RelativeLayout) findViewById(R.id.layout_head);
        layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
        layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
        // ������ʾ��
        layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
        tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
        btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_add_friend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);
        if (from.equals("me")) {
            initTopBarForLeft("��������");
            layout_head.setOnClickListener(this);
            layout_nick.setOnClickListener(this);
            layout_gender.setOnClickListener(this);
            iv_nickarraw.setVisibility(View.VISIBLE);
            iv_arraw.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
        } else {
            initTopBarForLeft("��ϸ����");
            iv_nickarraw.setVisibility(View.INVISIBLE);
            iv_arraw.setVisibility(View.INVISIBLE);
            // ���ܶԷ��ǲ�����ĺ��ѣ�����Է�����Ϣ--BmobIM_V1.1.2�޸�
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
            if (from.equals("add")) {// �Ӹ�������б���Ӻ���--��Ϊ��ȡ������˵ķ����������Ƿ���ʾ���ѵ�����������������Ҫ�ж�������û��Ƿ����Լ��ĺ���
                if (mApplication.getContactList().containsKey(username)) {// �Ǻ���
                    // btn_chat.setVisibility(View.VISIBLE);
                    // btn_chat.setOnClickListener(this);
                    btn_back.setVisibility(View.VISIBLE);
                    btn_back.setOnClickListener(this);
                } else {
                    // btn_chat.setVisibility(View.GONE);
                    btn_back.setVisibility(View.GONE);
                    btn_add_friend.setVisibility(View.VISIBLE);
                    btn_add_friend.setOnClickListener(this);
                }
            } else {// �鿴����
                // btn_chat.setVisibility(View.VISIBLE);
                // btn_chat.setOnClickListener(this);
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            initOtherData(username, false);
        }
    }

    private void initMeData() {
        User user = userManager.getCurrentUser(User.class);
        initOtherData(user.getUsername(), true);
    }

    private void initOtherData(String name, final boolean isInit) {
        userManager.queryUser(name, new FindListener<User>() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowLog("onError onError:" + arg1);
            }

            @Override
            public void onSuccess(List<User> arg0) {
                // TODO Auto-generated method stub
                if (arg0 != null && arg0.size() > 0) {
                    user = arg0.get(0);
                    btn_chat.setEnabled(true);
                    btn_back.setEnabled(true);
                    btn_add_friend.setEnabled(true);
                    updateUser(user, isInit);
                } else {
                    ShowLog("onSuccess ���޴���");
                }
            }
        });
    }

    private void updateUser(User user, boolean isInit) {
        // ���
        if (isInit) {
            refreshAvatar();
        } else {
            refreshAvatar(user.getAvatar());
        }

        tv_set_name.setText(user.getUsername());
        tv_set_nick.setText(user.getNick());
        tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
        // ����Ƿ�Ϊ�����û�
        if (from.equals("other")) {
            if (BmobDB.create(this).isBlackUser(user.getUsername())) {
                btn_back.setVisibility(View.GONE);
                layout_black_tips.setVisibility(View.VISIBLE);
            } else {
                btn_back.setVisibility(View.VISIBLE);
                layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ����ͷ�� refreshAvatar
     *
     * @return void @throws
     */
    private void refreshAvatar() {

        // �ж� ͷ���ļ������Ƿ�� ���û���ͷ��
        File file = new File(StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG");
        if (file.exists()) {// ������
            Bitmap bitmap = Util.convertToBitmap(
                    StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG", 240, 240);
            if (bitmap != null) {
                iv_set_avator.setImageBitmap(bitmap);
            } else {
                file.delete();
            }

        } else {// ����ļ����в��������ͷ��
            iv_set_avator.setImageResource(R.drawable.person);
        }
    }

    /**
     * ����ͷ�� refreshAvatar
     *
     * @return void @throws
     */
    private void refreshAvatar(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoader.getInstance().displayImage(avatar, iv_set_avator, ImageLoadOptions.getOptions());
        } else {
            iv_set_avator.setImageResource(R.drawable.person);
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (from.equals("me")) {
            initMeData();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_chat:// ��������
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startAnimActivity(intent);
                finish();
                break;
            case R.id.layout_head:
                File file = new File(StaticVarUtil.PATH);
                if (!file.exists()) {
                    file.mkdirs();// �����ļ�
                }
                chooseHeadPhoto();// �ı�ͷ��
                break;
            case R.id.layout_nick:
                startAnimActivity(UpdateInfoActivity.class);
                break;
            case R.id.layout_gender:// �Ա�
                showSexChooseDialog();
                break;
            case R.id.btn_back:// ����
                showBlackDialog(user.getUsername());
                break;
            case R.id.btn_add_friend:// ��Ӻ���
                addFriend();
                break;
        }
    }

    private void showSexChooseDialog() {
        new AlertDialog.Builder(this).setTitle("��ѡ��").setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(sexs, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BmobLog.i("�������" + sexs[which]);
                        updateInfo(which);
                        dialog.dismiss();
                    }
                }).setNegativeButton("ȡ��", null).show();
    }

    /**
     * �޸����� updateInfo @Title: updateInfo @return void @throws
     */
    private void updateInfo(int which) {
        final User user = userManager.getCurrentUser(User.class);
        BmobLog.i("updateInfo �Ա�" + user.getSex());
        if (which == 0) {
            user.setSex(true);
        } else {
            user.setSex(false);
        }
        user.update(this, new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ShowToast("�޸ĳɹ�");
                final User u = userManager.getCurrentUser(User.class);
                BmobLog.i("�޸ĳɹ����sex = " + u.getSex());
                tv_set_gender.setText(user.getSex() == true ? "��" : "Ů");
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                ShowToast("onFailure:" + arg1);
            }
        });
    }

    /**
     * ��Ӻ�������
     *
     * @Title: addFriend @Description: TODO @param @return void @throws
     */
    private void addFriend() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("�������...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // ����tag����
        BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT, user.getObjectId(),
                new PushListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        ShowToast("��������ɹ����ȴ�Է���֤��");
                    }

                    @Override
                    public void onFailure(int arg0, final String arg1) {
                        // TODO Auto-generated method stub
                        progress.dismiss();
                        ShowToast("��������ɹ����ȴ�Է���֤��");
                        ShowLog("��������ʧ��:" + arg1);
                    }
                });
    }

    /**
     * ��ʾ������ʾ��
     *
     * @Title: showBlackDialog @Description: TODO @param @return void @throws
     */
    private void showBlackDialog(final String username) {
        DialogTips dialog = new DialogTips(this, "�������", "��������㽫�����յ��Է�����Ϣ��ȷ��Ҫ������", "ȷ��", true,
                true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                // ��ӵ������б�
                userManager.addBlack(username, new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        ShowToast("������ӳɹ�!");
                        btn_back.setVisibility(View.GONE);
                        layout_black_tips.setVisibility(View.VISIBLE);
                        // �����������ڴ��б���ĺ����б�
                        CustomApplcation.getInstance().setContactList(
                                CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        // TODO Auto-generated method stub
                        ShowToast("�������ʧ��:" + arg1);
                    }
                });
            }
        });
        // ��ʾȷ�϶Ի���
        dialog.show();
        dialog = null;
    }

    /*
     * �ü�ͼƬ����ʵ��
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");// ����ϵͳ�Ľ�ͼ���ܡ�
        intent.setDataAndType(uri, "image/*");
        // ���òü�
        intent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("scale", true);// �ڱ�
        intent.putExtra("scaleUpIfNeeded", true);// �ڱ�
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ������벻��ȡ���ʱ��
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case PHO:
                    File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
                    startPhotoZoom(Uri.fromFile(tempFile));
                    break;
                case PIC:
                    // ��Ƭ��ԭʼ��Դ��ַ
                    Uri originalUri = data.getData();
                    startPhotoZoom(originalUri);
                    break;
                case RESULT:
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            bitmap = extras.getParcelable("data");
                        }
                        bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 240);// �����Ϊ240���ص�ͼƬ
                        BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
                                StaticVarUtil.student.getAccount() + ".JPEG");
                        iv_set_avator.setImageBitmap(bitmap);
                        MainActivity.updataPhoto(bitmap);
                        // �ϴ�ͷ��
                        UploadFileAsytask uploadFileAsytask = new UploadFileAsytask(SetMyInfoActivity.this,
                                bitmap);
                        uploadFileAsytask.execute(new String[]{
                                StaticVarUtil.PATH + "/" + StaticVarUtil.student.getAccount() + ".JPEG"});
                    }
                    break;
                default:
                    break;
            }
        } else {
            bitmap = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void chooseHeadPhoto() {
        String[] items = new String[]{
                Util.getContext().getResources().getString(R.string.select_picture),
                Util.getContext().getResources().getString(R.string.photo)};
        new AlertDialog.Builder(this)
                .setTitle(Util.getContext().getResources().getString(R.string.setPhoto))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// ѡ�񱾵�ͼƬ
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, PIC);
                                break;
                            case 1:// ����
                                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                Uri imageUri = Uri.fromFile(new File(StaticVarUtil.PATH, "temp.JPEG"));
                                // ָ����Ƭ����·����SD������image.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
                                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent2, PHO);
                                break;
                        }
                    }
                }).setNegativeButton(Util.getContext().getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
