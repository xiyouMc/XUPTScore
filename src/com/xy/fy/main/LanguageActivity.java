package com.xy.fy.main;

import java.util.ArrayList;
import java.util.Locale;

import com.bmob.im.demo.ui.BaseActivity;
import com.mc.db.DBConnection;
import com.mc.db.DBConnection.UserSchema;
import com.mc.util.Util;
import com.xy.fy.adapter.LanguageAdapter;
import com.xy.fy.main.R.color;
import com.xy.fy.singleton.Language;
import com.xy.fy.util.StaticVarUtil;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class LanguageActivity extends BaseActivity {

  Button save;
  ListView languageListView;
  ArrayList<Language> list;
  LanguageAdapter languageAdapter;

  int postion = 3;

  String optionType = "Login";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_language);
    Util.setContext(getApplicationContext());
    save = (Button) findViewById(R.id.save);
    languageListView = (ListView) findViewById(R.id.select_language);

    Intent intent = getIntent();
    optionType = intent.getStringExtra("optionType");

    Resources resources = getResources();
    list = new ArrayList<Language>();
    Language chinese = new Language();
    chinese.setLanguage("简体中文");
    chinese.setSelect(true);
    list.add(chinese);
    Language english = new Language();
    english.setLanguage("English");
    english.setSelect(false);
    list.add(english);

    SharedPreferences preferences = getSharedPreferences(StaticVarUtil.LANGUAGE_INFO, MODE_PRIVATE);
    postion = preferences.getInt(StaticVarUtil.LANGUAGE, 3);
    if (postion != 3) {
      for (int i = 0; i < list.size(); i++) {
        if (i == postion) {
          list.get(postion).setSelect(true);
        } else {
          list.get(i).setSelect(false);
        }
      }
    }

    languageAdapter = new LanguageAdapter(list, getApplicationContext());
    languageListView.setAdapter(languageAdapter);

    languageListView.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        for (int i = 0; i < list.size(); i++) {
          if (i == position) {
            list.get(position).setSelect(true);
          } else {
            list.get(i).setSelect(false);
          }
        }
        languageAdapter.notifyDataSetChanged();

        postion = position;
      }
    });

    save.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        if (postion == 1) {// English
          config.locale = Locale.ENGLISH;
        } else {
          config.locale = Locale.CHINESE;
        }
        rememberPassword(postion);
        DisplayMetrics dm = res.getDisplayMetrics();
        res.updateConfiguration(config, dm);

        Intent intent = new Intent();
        intent.setClass(LanguageActivity.this,
            optionType.equals("Login") ? LoginActivity.class : MainActivity.class);
        StaticVarUtil.quit();
        startActivity(intent);
        finish();
      }
    });
  }

  /**
   * 记住密码
   */
  private void rememberPassword(int position) {
    SharedPreferences preferences = getSharedPreferences(StaticVarUtil.LANGUAGE_INFO, MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.putInt(StaticVarUtil.LANGUAGE, position);

    editor.commit();
  }
}
