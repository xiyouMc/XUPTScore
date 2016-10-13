package com.xy.fy.main;

import com.util.ShareUtil;

import java.io.FileOutputStream;

import top.codemc.common.util.FileUtils;
import top.codemc.common.util.H5Log;
import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.Util;

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

public class ShowScoreActivity extends Activity {

    private static String TAG = "ShowScoreActivity";
    /** Called when the activity is first created. */
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private final int FP = ViewGroup.LayoutParams.MATCH_PARENT;
    // private Button back;
    private TextView XNandXQ;
    private LinearLayout layout;// 为了实现点击就取消
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
        String _row = i.getStringExtra("col");// 列数
        int row_int = Integer.valueOf(_row);
        score = new String[row_int][];
        for (int j = 0; j < row_int; j++) {
            score[j] = i.getStringArrayExtra("score" + j);
        }
        // 新建TableLayout01的实例
        TableLayout tableLayout = (TableLayout) findViewById(R.id.score);
        // 全部列自动填充空白处
        tableLayout.setStretchAllColumns(true);
        // 生成4行， _col 列的表格
        for (int row = 0; row < row_int; row++) {
            TableRow tableRow = new TableRow(this);
            for (int col = 0; col < 4; col++) {

                TextView tv = new TextView(this);
                // tv用于显示
                tv.setText(score[row][col]);
                tv.setTextSize(20);
                if (col == 0) {// 课程
                    tv.setTextColor(Color.CYAN);
                }
                tableRow.addView(tv);
            }
            // 新建的TableRow添加到TableLayout
            tableLayout.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        // back = (Button)findViewById(R.id.butBack);
        // 分享按钮
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
     * 截取客户端整个页面 ，去掉工具栏
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
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bitmap;
    }

    private synchronized String saveImage(Bitmap bitmap, String format) {
        if (!TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            return "";
        }
        FileOutputStream fos;
        final String imagePath = Environment.getExternalStorageDirectory() + "/xuptscore/"
                + StaticVarUtil.student.getAccount() + " _"+ xn_xq + "." + format;
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
                MediaScannerConnection.scanFile(this, new String[] { imagePath },
                        new String[] { "image/*" }, null);
            }
        } catch (Exception e) {
            H5Log.e(TAG, "saveImage exception.", e);
            return "";
        }
        return imagePath;
    }
}
