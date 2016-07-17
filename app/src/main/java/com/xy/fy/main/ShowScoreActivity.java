package com.xy.fy.main;

import com.mc.util.H5Log;
import com.mc.util.Util;
import com.xy.fy.util.FileUtils;
import com.xy.fy.util.ShareUtil;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.FileOutputStream;

public class ShowScoreActivity extends Activity {

    private static String TAG = "ShowScoreActivity";
    /** Called when the activity is first created. */
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
    // private Button back;
    private TextView XNandXQ;
    private LinearLayout layout;// Ϊ��ʵ�ֵ����ȡ��
    private String xn_xq;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.card_score);
        Util.setContext(getApplicationContext());
        init();
        String[][] score = null;
        Intent i = getIntent();
        xn_xq = i.getStringExtra("xn_and_xq");
        XNandXQ.setText(xn_xq);
        String _row = i.getStringExtra("col");// ����
        int row_int = Integer.valueOf(_row);
        score = new String[row_int][];
        for (int j = 0; j < row_int; j++) {
            score[j] = i.getStringArrayExtra("score" + j);
        }
        // �½�TableLayout01��ʵ��
        TableLayout tableLayout = (TableLayout) findViewById(R.id.score);
        // ȫ�����Զ����հ״�
        tableLayout.setStretchAllColumns(true);
        // ���4�У� _col �еı��
        for (int row = 0; row < row_int; row++) {
            TableRow tableRow = new TableRow(this);
            for (int col = 0; col < 4; col++) {

                TextView tv = new TextView(this);
                // tv������ʾ
                tv.setText(score[row][col]);
                tv.setTextSize(20);
                if (col == 0) {// �γ�
                    tv.setTextColor(Color.CYAN);
                }
                tableRow.addView(tv);
            }
            // �½���TableRow��ӵ�TableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        // back = (Button)findViewById(R.id.butBack);
        // ���?ť
        Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // ViewUtil.showShare(getApplicationContext());

                String filePath = saveImage(captureScreen(), "jpg");
                ShareUtil shareUtil = new ShareUtil(getApplicationContext());
                shareUtil.showShareUI(shareUtil.showShare(filePath));
            }
        });

        layout = (LinearLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        XNandXQ = (TextView) findViewById(R.id.XNandXQ);
    /*
     * back.setOnClickListener(new OnClickListener() {
     * 
     * @Override public void onClick(View v) { // TODO Auto-generated method stub finish(); } });
     */
    }

    /**
     * ��ȡ�ͻ������ҳ�� ��ȥ��������
     */
    public Bitmap captureScreen() {

        View view = this.getWindow().getDecorView();
        view.buildDrawingCache();
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = this.getWindowManager().getDefaultDisplay();
        int widths = display.getWidth();
        int heights = display.getHeight();
        view.setDrawingCacheEnabled(true);

        if (heights > view.getDrawingCache().getHeight()) {
            return view.getDrawingCache();
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeights, widths,
                heights - statusBarHeights);
        // ��ٻ�����Ϣ
        view.destroyDrawingCache();
        return bitmap;
    }

    private synchronized String saveImage(Bitmap bitmap, String format) {
        if (!TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            return "";
        }
        FileOutputStream fos;
        final String imagePath = Environment.getExternalStorageDirectory() + "/xuptscore/"
                + StaticVarUtil.student.getAccount() + " _" + xn_xq + "." + format;
        try {
            // file or folder may not exist, cause file not found exception
            if (FileUtils.exists(imagePath)) {
                return imagePath;
            }
            FileUtils.create(imagePath);
            fos = new FileOutputStream(imagePath);
            if (fos == null) {
                return "";
            }
            Bitmap.CompressFormat cf = Bitmap.CompressFormat.JPEG;
            final boolean success = bitmap.compress(cf, 100, fos);
            fos.close();
            if (!success) {
                return "";
            } else {
                MediaScannerConnection.scanFile(this, new String[]{imagePath},
                        new String[]{"image/*"}, null);
            }
        } catch (Exception e) {
            H5Log.e(TAG, "saveImage exception.", e);
            return "";
        }
        return imagePath;
    }
}
