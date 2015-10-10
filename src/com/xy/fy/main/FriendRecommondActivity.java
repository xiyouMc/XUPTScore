package com.xy.fy.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mc.util.ScoreUtil;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 好友推荐
 * 
 * @author Administrator 2015-3-21
 */
public class FriendRecommondActivity extends Activity {

  private ListView kecheng_Listview;
  private ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_friend_recommond);
    Util.setContext(getApplicationContext());
    init();
    calcul();
    if (list_data.size() != 0) {
      setListView();
    }
    click();
  }

  private void click() {
    // TODO Auto-generated method stub
    kecheng_Listview.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        // 获得选中项的HashMap对象
        @SuppressWarnings("unchecked")
        HashMap<String, String> map = (HashMap<String, String>) kecheng_Listview
            .getItemAtPosition(position);
        String kecheng = map.get("kecheng");
        String score = map.get("score");
        // 查询课程代码
        ViewUtil.showToast(getApplicationContext(), StaticVarUtil.kcdmList.get(kecheng) + " "
            + score);
      }
    });
  }

  /**
   * 获取所有成绩 得到 未通过的 课程
   */
  private void calcul() {
    // TODO Auto-generated method stub
    Resources resources = getResources();
    String[] xn = resources.getStringArray(R.array.xn);
    for (int i = 0; i < xn.length; i++) {
      addData(xn[i], ScoreUtil.mapScoreOne);
      addData(xn[i], ScoreUtil.mapScoreTwo);
    }
  }

  private boolean addData(String xn, HashMap<String, String> data) {
    String score = data.get(xn) == null ? "" : data.get(xn);
    String[] s = score.split("\n");

    for (int i = 0; i < s.length; i++) {
      HashMap<String, String> firstscoreAllMap = new HashMap<String, String>();
      String first_score_all = s[i];
      if (first_score_all == "") {
        return false;
      }
      String now_score = first_score_all.split("--")[1].trim();
      if (now_score.equals("\\")) {
        return false;
      }
      if (now_score.contains("(")) {// 说明补考过
        float bukaoScore = Float.parseFloat(now_score.substring(now_score.lastIndexOf("(") + 1,
            now_score.length() - 1).trim());
        Log.d("FriendRecommond", "" + bukaoScore);
        if (bukaoScore < 60) {
          firstscoreAllMap.put("kecheng", first_score_all.split("--")[0]);
          firstscoreAllMap.put("score", now_score);
        }
      } else {
        if (isNumeric(now_score) && Float.parseFloat(now_score) < 60) {
          firstscoreAllMap.put("kecheng", first_score_all.split("--")[0]);
          firstscoreAllMap.put("score", now_score);
        }
      }
      if (firstscoreAllMap.size() != 0) {
        list_data.add(firstscoreAllMap);
      }

    }
    return true;
  }

  public boolean isNumeric(String str) {
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(str);
    if (!isNum.matches()) {
      return false;
    }
    return true;
  }

  private void init() {
    // TODO Auto-generated method stub
    kecheng_Listview = (ListView) findViewById(R.id.recommond_list);
  }

  // 显示 排名的listview
  private void setListView() {
    // TODO Auto-generated method stub
    SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), list_data,
        R.layout.activity_add_friend_recommand_listview_item, new String[] { "kecheng", "score" },
        new int[] { R.id.kecheng, R.id.score });
    kecheng_Listview.setAdapter(simpleAdapter);

  }

}
