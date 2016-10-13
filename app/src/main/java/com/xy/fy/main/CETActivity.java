package com.xy.fy.main;

import top.codemc.common.util.StaticVarUtil;
import top.codemc.common.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CETActivity extends Activity {

    private TextView name;
    private TextView school;
    private TextView kaohao;

    private TextView score;
    private TextView tingli;
    private TextView yuedu;
    private TextView xiezuo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_cet);
        Util.setContext(getApplicationContext());
        Intent i = getIntent();
        String[] data = i.getStringExtra("data").split(",");
        if (data.length != 7) {
            return;
        }
        name = (TextView) findViewById(R.id.name);
        name.setText(data[6]);
        school = (TextView) findViewById(R.id.school);
        school.setText(data[5]);
        kaohao = (TextView) findViewById(R.id.account);
        kaohao.setText(StaticVarUtil.cet_account);

        score = (TextView) findViewById(R.id.score);
        score.setText(data[4]);
        tingli = (TextView) findViewById(R.id.tingli);
        tingli.setText(data[1]);
        yuedu = (TextView) findViewById(R.id.yuedu);
        yuedu.setText(data[2]);
        xiezuo = (TextView) findViewById(R.id.xiezuo);
        xiezuo.setText(data[3]);

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }


}
