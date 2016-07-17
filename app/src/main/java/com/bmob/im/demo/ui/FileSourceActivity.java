package com.bmob.im.demo.ui;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.bmob.im.demo.adapter.FileResourceAdapter;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.HeaderLayout.HeaderStyle;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.mc.util.H5Toast;
import com.xy.fy.main.R;
import com.xy.fy.util.OpenFileDialog;
import com.xy.fy.util.OpenFileDialog.CallbackBundle;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobFile;

public class FileSourceActivity extends Activity implements OnItemLongClickListener {

    private static int openfileDialogId = 1;
    ListView listview;
//  private String path = "";
//  private final int FILE_SELECT_CODE = -1;
    FileResourceAdapter adapter;
    ProgressDialog dialog1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_resource);
        initView();
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.list_file);
        listview.setOnItemLongClickListener(this);

        List<String> listFilename = new ArrayList<String>();
        listFilename.add("04113129.JPEG");
        listFilename.add("dslog2.txt");
        listFilename.add("78817953cfa14bbebd423769621d419d.pdf");
        adapter = new FileResourceAdapter(this, listFilename);
        listview.setAdapter(adapter);
        HeaderLayout headerLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        headerLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        headerLayout.setTitleAndRightImageButton("�ļ�����", R.drawable.iconfont_uploading,
                new onRightImageButtonClickListener() {

                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        // intent.setType("*/*");
                        // intent.addCategory(Intent.CATEGORY_OPENABLE);
                        // startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                        // StaticVarUtil.FILE_SELECT);
                        showDialog(openfileDialogId);
                    }
                });
    }

//  @Override
//  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    // TODO Auto-generated method stub
//    switch (requestCode)
//    // TODO Auto-generated method stub
//    {
//    case StaticVarUtil.FILE_SELECT:
//      if (resultCode == Activity.RESULT_OK) {
//        // Get the Uri of the selected file 2796192985
//        H5Toast.showToast(getApplicationContext(), "�ɹ���");
//        Uri uri = data.getData();
//        upload(FileUtils.getPath(this, uri));
//      }
//      break;
//    }
//    super.onActivityResult(requestCode, resultCode, data);
//  }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == openfileDialogId) {
            Map<String, Integer> images = new HashMap<String, Integer>();
            // ���漸�����ø��ļ����͵�ͼ�꣬ ��Ҫ���Ȱ�ͼ����ӵ���Դ�ļ���
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // ��Ŀ¼ͼ��
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // ������һ���ͼ��
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // �ļ���ͼ��
            images.put("wav", R.drawable.filedialog_wavfile); // wav�ļ�ͼ��
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "���ļ�", new CallbackBundle() {
                @Override
                public void callback(Bundle bundle) {
                    String filepath = bundle.getString("path");
                    setTitle(filepath); // ���ļ�·����ʾ�ڱ�����
                    upload(filepath);
                }
            }, null, images);
            return dialog;
        }
        return null;
    }

    private void upload(String filePath) {
        dialog1 = new ProgressDialog(this);
        dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog1.setTitle("�����ϴ�...");
        dialog1.setIndeterminate(false);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        BmobProFile.getInstance(this).upload(filePath, new UploadListener() {

            @Override
            public void onSuccess(String fileName, String url, BmobFile file) {
                // TODO Auto-generated method stub
                Log.i("bmob", "�ļ��ϴ��ɹ���" + fileName + ",�ɷ��ʵ��ļ���ַ��" + file.getUrl());
                dialog1.dismiss();
                H5Toast.showToast(getApplicationContext(),
                        "�ļ��ϴ��ɹ�");
                Log.i("main", ",�ɷ��ʵ��ļ���ַ��" + file.getUrl());
                //+ ",�ɷ��ʵ��ļ���ַ��" + file.getUrl()
            }

            @Override
            public void onProgress(int progress) {
                // TODO Auto-generated method stub
                Log.i("bmob", "onProgress :" + progress);
                dialog1.setProgress(progress);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                // TODO Auto-generated method stub
                Log.i("bmob", "�ļ��ϴ�ʧ�ܣ�" + errormsg);
                dialog1.dismiss();
            }
        });
    }

}
